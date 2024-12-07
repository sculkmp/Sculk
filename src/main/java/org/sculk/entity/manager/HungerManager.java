package org.sculk.entity.manager;


import lombok.Getter;
import org.sculk.entity.Attribute;
import org.sculk.entity.AttributeFactory;
import org.sculk.entity.Entity;
import org.sculk.entity.HumanEntity;
import org.sculk.event.player.PlayerExhaustEvent;

/*
 *   ____             _ _
 *  / ___|  ___ _   _| | | __
 *  \___ \ / __| | | | | |/ /
 *   ___) | (__| |_| | |   <
 *  |____/ \___|\__,_|_|_|\_\
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author: SculkTeams
 * @link: http://www.sculkmp.org/
 */
public class HungerManager {

    private Attribute hungerAttribute;
    private Attribute saturationAttribute;
    private Attribute exhaustionAttrbute;

    @Getter
    private int foodTickTimer = 0;
    @Getter
    private boolean enabled = true;
    private final HumanEntity humanEntity;

    public HungerManager(HumanEntity humanEntity) {
        this.humanEntity = humanEntity;
        this.hungerAttribute = fetchAttribute(humanEntity, Attribute.HUNGER);
        this.saturationAttribute = fetchAttribute(humanEntity, Attribute.SATURATION);
        this.exhaustionAttrbute = fetchAttribute(humanEntity, Attribute.EXHAUSTION);
    }

    private static Attribute fetchAttribute(Entity entity, String attributeId) {
        Attribute attribute = AttributeFactory.getINSTANCE().mustGet(attributeId);
        // TODO next step add attribute to entity
        return attribute;
    }

    public float getFood() {
        return this.hungerAttribute.getCurrentValue();
    }

    public void setFood(float newFood) {
        float oldFood = this.hungerAttribute.getCurrentValue();
        this.hungerAttribute.setValue(newFood, true, true);
        // Ranges 18-20 (regen), 7-17 (none) 1-6 (no sprint), 0 (health depletion)
        for(int bound : new int[]{17, 6, 0}) {
            if((oldFood > bound) != (newFood > bound)) {
                foodTickTimer = 0;
                break;
            }
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public float getMaxFood() {
        return this.hungerAttribute.getMaxValue();
    }

    public void addFood(float amount) {
        float newAmount = Math.max(Math.min(amount + this.hungerAttribute.getCurrentValue(), this.hungerAttribute.getMaxValue()), this.hungerAttribute.getMinValue());
        setFood(newAmount);
    }

    public boolean isHungry() {
        return this.getFood() < this.getMaxFood();
    }

    public float getSaturation() {
        return this.saturationAttribute.getCurrentValue();
    }

    public void setSaturation(float saturation) {
        this.saturationAttribute.setValue(saturation, true, true);
    }

    public void addSaturation(float amount) {
        this.saturationAttribute.setValue(this.saturationAttribute.getCurrentValue() + amount, true, true);
    }

    public float getExhaustion() {
        return this.exhaustionAttrbute.getCurrentValue();
    }

    public void setExhaustion(float exhaustion) {
        this.exhaustionAttrbute.setValue(exhaustion, true, true);
    }

    public float exhaust(float amount, int cause) {
        if(!this.enabled) {
            return 0;
        }
        float eventAmount = amount;
        PlayerExhaustEvent playerExhaustEvent = new PlayerExhaustEvent(this.humanEntity, amount, cause);
        playerExhaustEvent.call();
        if(playerExhaustEvent.isCancelled()) {
            return 0.0f;
        }
        eventAmount = playerExhaustEvent.getAmount();

        float exhaustion = this.getExhaustion() + eventAmount;
        while(exhaustion >= 4.0f) {
            exhaustion -= 4.0f;
            float saturation = this.getSaturation();
            if(saturation > 0) {
                saturation = Math.max(0, saturation - 1.0f);
                this.setSaturation(saturation);
            } else {
                float food = this.getFood();
                if(food > 0) {
                    food--;
                    this.setFood(Math.max(food, 0));
                }
            }
        }
        this.setExhaustion(exhaustion);
        return eventAmount;
    }

    public void setFoodTickTimer(int foodTickTimer) {
        if(foodTickTimer < 0) {
            throw new IllegalArgumentException("Expected a non-negative value");
        }
        this.foodTickTimer = foodTickTimer;
    }

    // TODO To be put in the Player::onUpdate()
    public void tick(int tickDiff) {
        float food = this.getFood();

        foodTickTimer += tickDiff;
        if(foodTickTimer >= 80) {
            foodTickTimer = 0;
        }
        if(foodTickTimer == 0) {
            if(food >= 18) {
                // TODO Next step: heal entity
                exhaust(6.0f, PlayerExhaustEvent.CAUSE_HEALTH_REGEN);
            } else if(food <= 0) {
                // TODO soon
            }
        }
        if(food <= 6) {
            // TODO Disable sprinting
        }
    }

}

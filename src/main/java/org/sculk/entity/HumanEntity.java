package org.sculk.entity;


import org.sculk.entity.manager.ExperienceManager;
import org.sculk.entity.manager.HungerManager;
import org.sculk.nbt.CompoundTag;
import org.sculk.player.Player;
import org.sculk.player.skin.Skin;

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
public class HumanEntity extends Living {

    protected HungerManager hungerManager;
    protected ExperienceManager experienceManager;

    @Override
    public void initEntity() {
        super.initEntity();
        this.hungerManager = new HungerManager(this);
        this.experienceManager = new ExperienceManager(this);

        if(!(this instanceof Player)) {
            if(this.namedTag.contains("Skin") && this.namedTag.get("Skin") instanceof CompoundTag) {
                CompoundTag skinTag = this.namedTag.getCompound("Skin");
                if(!skinTag.contains("Transparent")) {
                    skinTag.putBoolean("Transparent", false);
                }
                Skin newSkin = new Skin();
                if(skinTag.contains("ModelId")) {
                    //newSkin.setSkinId(skinTag.get);
                }
            }
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
    }

    public HungerManager getHungerManager() {
        return hungerManager;
    }

    public ExperienceManager getExperienceManager() {
        return experienceManager;
    }

}

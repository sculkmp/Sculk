package org.sculk.entity;

import lombok.Getter;

import javax.annotation.Nullable;

public class EntitySizeInfo {
    @Getter
    private float height;
    @Getter
    private float width;
    @Getter
    private float eyeHeight;

    public  EntitySizeInfo(
            float height,
            float width,
            float eyeHeight
    ){
        this.height = height;
        this.width = width;
        this.eyeHeight = eyeHeight;
    }
    public  EntitySizeInfo(
            float height,
            float width
    ){
        this.height = height;
        this.width = width;
        this.eyeHeight = (float) Math.min(height / 2 + 0.1, height);
    }

    public EntitySizeInfo scale(float $newScale){
        return new EntitySizeInfo(
                this.height * $newScale,
                this.width * $newScale,
                this.eyeHeight * $newScale
        );
    }
}

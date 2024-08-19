package org.sculk.player.skin;


import lombok.Data;
import lombok.ToString;
import org.sculk.player.skin.data.ImageData;
import org.sculk.player.skin.data.PersonaPiece;
import org.sculk.player.skin.data.PersonaPieceTint;
import org.sculk.player.skin.data.SkinAnimation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*
 *   ____             _ _              __  __ ____
 *  / ___|  ___ _   _| | | __         |  \/  |  _ \
 *  \___ \ / __| | | | | |/ /  _____  | |\/| | |_) |
 *   ___) | (__| |_| | |   <  |_____| | |  | |  __/
 *  |____/ \___|\__,_|_|_|\_\         |_|  |_|_|
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author: SculkTeams
 * @link: http://www.sculkmp.org/
 */
@Data
@ToString(exclude = {"geometryData", "animationData"})
public class Skin {

    private final String fullSkinId;
    private String skinId;
    private String playFabId;
    private String skinResourcePatch = GEOMETRY_CUSTOM;
    private ImageData skinData;
    private List<SkinAnimation> animations;
    private final List<PersonaPiece> personaPieces = new ArrayList<>();
    private final List<PersonaPieceTint> tintColors = new ArrayList<>();
    private ImageData capeData;
    private String geometryData;
    private String animationData;
    private boolean premium;
    private boolean persona;
    private boolean capeOnClassic;
    private String capeId;
    private String skinColor = "#0";
    private String armSize = "wide";
    private boolean trusted = false;

    public static final String GEOMETRY_CUSTOM = convertLegacyGeometryName("geometry.humanoid.custom");
    public static final String GEOMETRY_CUSTOM_SLIM = convertLegacyGeometryName("geometry.humanoid.customSlim");

    private static String convertLegacyGeometryName(String geometryName) {
        return "{\"geometry\" : {\"default\" : \"" + geometryName + "\"}}";
    }

    public Skin(String fullSkinId) {
        this.fullSkinId = fullSkinId;
    }

    public Skin() {
        this(UUID.randomUUID().toString());
    }

    public boolean isValid() {
        return skinId != null && !skinId.trim().isEmpty() && skinData != null && skinData.getWidth() >= 64 && skinData.getHeight() >= 32;
    }

}

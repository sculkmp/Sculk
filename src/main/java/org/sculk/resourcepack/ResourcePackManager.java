package org.sculk.resourcepack;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.common.io.Files;
import org.sculk.Server;
import org.sculk.lang.LanguageKeys;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
public class ResourcePackManager {

    private final Map<UUID, ResourcePack> resourcePacks = new HashMap<>();

    public void loadResourcePacks() {
        File resourcePacksPath = new File(System.getProperty("user.dir") + "/resource_packs");
        if (!resourcePacksPath.exists()) {
            resourcePacksPath.mkdirs();
        }
        if (!resourcePacksPath.isDirectory()) {
            return;
        }
        Server.getInstance().getLogger().info(Server.getInstance().getLanguage().translate(LanguageKeys.SCULK_SERVER_RESOURCE_PACK_LOADING));
        for (File file : Objects.requireNonNull(resourcePacksPath.listFiles())) {
            if (!file.isDirectory()) {
                String fileEnding = Files.getFileExtension(file.getName());
                if (fileEnding.equalsIgnoreCase("zip") || fileEnding.equalsIgnoreCase("mcpack")) {
                    try (ZipFile zipFile = new ZipFile(file)) {
                        String manifestFileName = "manifest.json";
                        ZipEntry manifestEntry = zipFile.getEntry(manifestFileName);

                        if (manifestEntry == null) {
                            manifestEntry = zipFile.stream().filter(zipEntry -> !zipEntry.isDirectory() && zipEntry.getName().toLowerCase().endsWith(manifestFileName))
                                    .filter(zipEntry -> {
                                        File zipEntryFile = new File(zipEntry.getName());
                                        if (!zipEntryFile.getName().equalsIgnoreCase(manifestFileName)) {
                                            return false;
                                        }
                                        return zipEntryFile.getParent() == null || zipEntryFile.getParentFile().getParent() == null;
                                    }).findFirst()
                                    .orElseThrow(() -> new IllegalArgumentException("The " + manifestFileName + " file could not be found"));
                        }

                        JsonObject manifest = JsonParser.parseReader(new InputStreamReader(zipFile.getInputStream(manifestEntry), StandardCharsets.UTF_8)).getAsJsonObject();
                        if (!isManifestValid(manifest)) {
                            throw new IllegalArgumentException("The " + manifestFileName + " file is invalid");
                        }

                        JsonObject manifestHeader = manifest.getAsJsonObject("header");
                        String resourcePackName = manifestHeader.get("name").getAsString();
                        String resourcePackUuid = manifestHeader.get("uuid").getAsString();
                        String resourcePackVersion = manifestHeader.getAsJsonArray("version").toString().replace("[", "").replace("]", "").replace(",", ".");
                        int resourcePackSize = (int) file.length();
                        byte[] resourcePackSha256 = MessageDigest.getInstance("SHA-256").digest(java.nio.file.Files.readAllBytes(file.toPath()));

                        Server.getInstance().getLogger().info(Server.getInstance().getLanguage().translate(LanguageKeys.SCULK_SERVER_RESOURCE_PACK_LOADED, List.of(resourcePackName)));
                        ResourcePack resourcePack = new ResourcePack(file, resourcePackName, resourcePackUuid, resourcePackVersion, (long) resourcePackSize, resourcePackSha256, new byte[0]);
                        resourcePacks.put(UUID.fromString(resourcePackUuid), resourcePack);
                    } catch (IOException | NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private boolean isManifestValid(JsonObject manifest) {
        if (manifest.has("format_version") && manifest.has("header") && manifest.has("modules")) {
            JsonObject manifestHeader = manifest.getAsJsonObject("header");
            if (manifestHeader.has("description") && manifestHeader.has("name") && manifestHeader.has("uuid") && manifestHeader.has("version")) {
                return manifestHeader.getAsJsonArray("version").size() == 3;
            }
        }
        return false;
    }

    public ResourcePack getResourcePack(UUID uuid) {
        return this.resourcePacks.get(uuid);
    }

    public Collection<ResourcePack> getResourcePacks() {
        return this.resourcePacks.values();
    }
}

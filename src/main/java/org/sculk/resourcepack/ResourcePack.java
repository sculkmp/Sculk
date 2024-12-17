package org.sculk.resourcepack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

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

public class ResourcePack {
    private final File file;
    private final String name;
    private final String uuid;
    private final String version;
    private final long size;
    private final byte[] hash;
    private byte[] chunk;

    public ResourcePack(File file, String name, String uuid, String version, long size, byte[] hash, byte[] chunk) {
        this.file = file;
        this.name = name;
        this.uuid = uuid;
        this.version = version;
        this.size = size;
        this.hash = hash;
        this.chunk = chunk;
    }

    /**
     * Returns a chunk of bytes from the resource pack file starting at the specified offset
     * and with the specified length. If the remaining data in the file starting from the offset
     * is less than the specified length, the size of the chunk will be adjusted accordingly.
     *
     * @param offset the starting position, in bytes, from where to read the chunk
     * @param length the number of bytes to be read starting from the offset
     * @return a byte array containing the requested chunk of data
     */
    public byte[] getChunk(int offset, int length) {
        if (size - offset > length) {
            chunk = new byte[length];
        } else {
            chunk = new byte[(int) (size - offset)];
        }
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            fileInputStream.skip(offset);
            fileInputStream.read(chunk);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return chunk;
    }

    /**
     * Retrieves the name of the resource pack.
     *
     * @return the name of the resource pack as a String.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Retrieves the UUID of the resource pack.
     *
     * @return the UUID associated with the resource pack.
     */
    public UUID getUuid() {
        return UUID.fromString(this.uuid);
    }

    /**
     * Retrieves the version information.
     *
     * @return the current version as a string.
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Retrieves the size of the resource pack in bytes.
     *
     * @return the size of the resource pack as a long value.
     */
    public long getSize() {
        return this.size;
    }

    /**
     * Returns the hash value of the resource pack. The hash is represented as a byte array,
     * commonly used for verifying the integrity of the resource pack contents.
     *
     * @return a byte array containing the SHA-256 hash of the resource pack.
     */
    public byte[] getHash() {
        return this.hash;
    }
}

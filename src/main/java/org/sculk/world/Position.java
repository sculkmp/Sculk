package org.sculk.world;

import lombok.Getter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.cloudburstmc.math.GenericMath;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.math.vector.Vector3i;

import javax.annotation.Nonnull;

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
public class Position extends Vector3f{
    public float x;
    public float y;
    public float z;
    @Getter
    public @Nullable World world;
    public Position(float x, float y, float z, World world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
    }
    public Position(double x, double y, double z, World world) {
        this.x = (float) x;
        this.y = (float) y;
        this.z = (float) z;
        this.world = world;
    }
    public Position(Vector3f vector3, World world) {
        this.x = vector3.getX();
        this.y = vector3.getY();
        this.z = vector3.getZ();
        this.world = world;
    }
    public Position(Vector3i vector3, World world) {
        this.x = vector3.getX();
        this.y = vector3.getY();
        this.z = vector3.getZ();
        this.world = world;
    }

    public static Position from(World world) {
        return from(ZERO, world);
    }

    public static Position from(Vector3i position, World world) {
        return from(position.getX(), position.getY(), position.getZ(), world);
    }

    public static Position from(Vector3f position, World world) {
        return from(position.getX(), position.getY(), position.getZ(), world);
    }

    public static Position from(float x, float y, float z, World world) {
        return new Position(from(x, y, z), world);
    }
    public static Position from(double x, double y, double z, World world) {
        return new Position(from(x, y, z), world);
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getZ() {
        return this.z;
    }

    @Nonnull
    @Override
    public Position add(float x, float y, float z) {
        return from(this.getX() + x, this.getY() + y, this.getZ() + z, this.world);
    }

    @Nonnull
    @Override
    public Position sub(float x, float y, float z) {
        return from(this.getX() - x, this.getY() - y, this.getZ() - z, this.world);
    }

    @Nonnull
    @Override
    public Position mul(float x, float y, float z) {
        return from(this.getX() * x, this.getY() * y, this.getZ() * z, this.world);
    }

    @Nonnull
    @Override
    public Position div(float x, float y, float z) {
        return from(this.getX() / x, this.getY() / y, this.getZ() / z, this.world);
    }

    @Nonnull
    @Override
    public Position project(float x, float y, float z) {
        float lengthSquared = x * x + y * y + z * z;
        if (Math.abs(lengthSquared) < GenericMath.FLT_EPSILON) {
            throw new ArithmeticException("Cannot project onto the zero vector");
        } else {
            float a = this.dot(x, y, z) / lengthSquared;
            return from(a * x, a * y, a * z, this.world);
        }
    }

    @Nonnull
    @Override
    public Position cross(float x, float y, float z) {
        return from(this.getY() * z - this.getZ() * y, this.getZ() * x - this.getX() * z, this.getX() * y - this.getY() * x, this.world);
    }

    @Nonnull
    @Override
    public Position pow(float power) {
        return from(Math.pow((double)this.x, (double)power), Math.pow((double)this.y, (double)power), Math.pow((double)this.z, (double)power), this.world);
    }

    @Nonnull
    @Override
    public Position ceil() {
        return from(Math.ceil((double)this.getX()), Math.ceil((double)this.getY()), Math.ceil((double)this.getZ()), this.world);
    }

    @Nonnull
    @Override
    public Position floor() {
        return from((float)GenericMath.floor(this.getX()), (float)GenericMath.floor(this.getY()), (float)GenericMath.floor(this.getZ()), this.world);
    }

    @Nonnull
    @Override
    public Position round() {
        return from((float)Math.round(this.getX()), (float)Math.round(this.getY()), (float)Math.round(this.getZ()), this.world);
    }

    @Nonnull
    @Override
    public Position abs() {
        return from(Math.abs(this.getX()), Math.abs(this.getY()), Math.abs(this.getZ()), this.world);
    }

    @Nonnull
    @Override
    public Position negate() {
        return from(-this.getX(), -this.getY(), -this.getZ(), this.world);
    }

    @Nonnull
    @Override
    public Position min(float x, float y, float z) {
        return from(Math.min(this.getX(), x), Math.min(this.getY(), y), Math.min(this.getZ(), z), this.world);
    }

    @Nonnull
    @Override
    public Position max(float x, float y, float z) {
        return from(Math.max(this.getX(), x), Math.max(this.getY(), y), Math.max(this.getZ(), z), this.world);
    }

    @Nonnull
    @Override
    public Position up(float v) {
        return from(this.getX(), this.getY() + v, this.getZ(), this.world);
    }

    @Nonnull
    @Override
    public Position down(float v) {
        return from(this.getX(), this.getY() - v, this.getZ(), this.world);
    }

    @Nonnull
    @Override
    public Position north(float v) {
        return from(this.getX(), this.getY(), this.getZ() - v, this.world);
    }

    @Nonnull
    @Override
    public Position south(float v) {
        return from(this.getX(), this.getY(), this.getZ() + v, this.world);
    }

    @Nonnull
    @Override
    public Position east(float v) {
        return from(this.getX() + v, this.getY(), this.getZ(), this.world);
    }

    @Nonnull
    @Override
    public Position west(float v) {
        return from(this.getX() - v, this.getY(), this.getZ(), this.world);
    }

    @Nonnull
    @Override
    public Position normalize() {
        float length = this.length();
        if (Math.abs(length) < GenericMath.FLT_EPSILON) {
            throw new ArithmeticException("Cannot normalize the zero vector");
        } else {
            return from(this.getX() / length, this.getY() / length, this.getZ() / length, this.world);
        }
    }

    @Nonnull
    @Override
    public Position clone() {
        return new Position(super.clone(), this.world);
    }
}

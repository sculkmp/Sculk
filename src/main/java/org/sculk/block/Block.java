package org.sculk.block;

import com.dynatrace.hash4j.hashing.XXH3_64;
import jline.internal.Nullable;
import lombok.SneakyThrows;
import org.sculk.data.runtime.RuntimeDataDescriber;
import org.sculk.data.runtime.RuntimeDataReader;
import org.sculk.data.runtime.RuntimeDataSizeCalculator;
import org.sculk.data.runtime.RuntimeDataWriter;
import org.sculk.world.Position;
import lombok.Getter;
import org.sculk.utils.Binary;
import org.sculk.world.World;

import java.util.ArrayList;
import java.util.List;

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
public class Block implements Cloneable{
     static final long  INTERNAL_STATE_DATA_BITS = 11;
     static final long  INTERNAL_STATE_DATA_MASK = ~(~0 << Block.INTERNAL_STATE_DATA_BITS);
     static final long  EMPTY_STATE_ID  = computeStateIdXorMask(BlockTypeIds.AIR);

     private static long computeStateIdXorMask(long typeId){
          return typeId << Block.INTERNAL_STATE_DATA_BITS | (XXH3_64.create().hashBytesToLong(Binary.writeLLong(typeId)) & Block.INTERNAL_STATE_DATA_MASK);
     }

     @Getter
     private BlockIdentifier identifier;
     @Getter
     private String fallbackName;
     @Getter
     private BlockTypeInfo typeInfo;
     @Getter
     private Position position;
     private final long requiredBlockItemStateDataBits;
     private final long requiredBlockOnlyStateDataBits;
     @Nullable
     private List<Object> collisionBoxes;

     private final long stateIdXorMask;

     private Block defaultState;

     public Block(BlockIdentifier identifier, String name, BlockTypeInfo blockTypeInfo){
          this.identifier = identifier;
          this.fallbackName = name;
          this.typeInfo = blockTypeInfo;
          this.position = new Position(0,0,0, null);
          RuntimeDataSizeCalculator calculator = new RuntimeDataSizeCalculator();
          this.describeBlockItemState(calculator);
          this.requiredBlockItemStateDataBits = calculator.getBitsUsed();
          calculator.reset();
          this.describeBlockOnlyState(calculator);
          this.requiredBlockOnlyStateDataBits = calculator.getBitsUsed();
          this.stateIdXorMask = Block.computeStateIdXorMask(identifier.getBlockTypeId());
          Block defaultState = this.clone();
          this.defaultState = defaultState;
          defaultState.defaultState = defaultState;
     }

     public final void position(World world, long x, long y, long z) {
          this.position = new Position((double)x, (double)y, (double)z, world);
          this.collisionBoxes = null;
     }

     public long getTypeId() {
          return identifier.getBlockTypeId();
     }

     /**
      * @internal
      *
      * Returns the full blockstate ID of this block. This is a compact way of representing a blockstate used to store
      * blocks in chunks at runtime.
      *
      * This usually encodes all properties of the block, such as facing, open/closed, powered/unpowered, colour, etc.
      * State ID may change depending on the properties of the block (e.g. a torch facing east will have a different
      * state ID to one facing west).
      *
      * Some blocks (such as signs and chests) may store additional properties in an associated "tile" if they
      * have too many possible values to be encoded into the state ID. These extra properties are **NOT** included in
      * this function's result.
      *
      * This ID can be used to later obtain a copy of the block with the same state properties by using
      * {@link RuntimeBlockStateRegistry::fromStateId()}.
      */
     @SneakyThrows
     public long getStateId() {
          return encodeFullState() ^ stateIdXorMask;
     }
     /**
      * Returns whether the given block has the same type and properties as this block.
      * <p>
      * Note: Tile data (e.g. sign text, chest contents) are not compared here.
      */
     public boolean isSameState(Block other){
          return this.getStateId() == other.getStateId();
     }

     public final boolean hasSameTypeId(Block other) {
          return this.getTypeId() == other.getTypeId();
     }

     public final List<String> getTypeTags() {
          return typeInfo.getTypeTags();
     }

     public final boolean  hasTypeTag(String tag)
     {
          return typeInfo.hasTypeTag(tag);
     }

     /**
      * Describes properties of this block which apply to both the block and item form of the block.
      * Examples of suitable properties include colour, skull type, and any other information which **IS** kept when the
      * block is mined or block-picked.
      *
      * The method implementation must NOT use conditional logic to determine which properties are written. It must
      * always write the same properties in the same order, regardless of the current state of the block.
      */
     @SneakyThrows
     public void describeBlockItemState(RuntimeDataDescriber w) {
          //NOOP
     }

     /**
      * Describes properties of this block which apply only to the block form of the block.
      * Examples of suitable properties include facing, open/closed, powered/unpowered, on/off, and any other information
      * which **IS NOT** kept when the block is mined or block-picked.
      *
      * The method implementation must NOT use conditional logic to determine which properties are written. It must
      * always write the same properties in the same order, regardless of the current state of the block.
      */
     @SneakyThrows
     protected void describeBlockOnlyState(RuntimeDataDescriber w) {
          //NOOP
     }

     private void decodeBlockItemState(long data) throws InvalidSerializedRuntimeDataException {
          RuntimeDataReader reader = new RuntimeDataReader(this.requiredBlockItemStateDataBits, data);
          this.describeBlockItemState(reader);
          long readBits = reader.getOffset();
          if(this.requiredBlockItemStateDataBits != readBits){
               throw new InvalidSerializedRuntimeDataException(this.getClass() + ": Exactly " + this.requiredBlockItemStateDataBits + " bits of block-item state data were provided, but " + readBits + " were read");
          }
     }

     private void decodeBlockOnlyState(long data) throws InvalidSerializedRuntimeDataException {
          RuntimeDataReader reader = new RuntimeDataReader(this.requiredBlockOnlyStateDataBits, data);
          this.describeBlockOnlyState(reader);
          long readBits = reader.getOffset();
          if(this.requiredBlockOnlyStateDataBits != readBits) {
               throw new InvalidSerializedRuntimeDataException(this.getClass() + ": Exactly " + this.requiredBlockOnlyStateDataBits + " bits of block-only state data were provided, but " + readBits + " were read");
          }
     }

     @SneakyThrows
     public long encodeBlockItemState() {
          RuntimeDataWriter writer = new RuntimeDataWriter(this.requiredBlockItemStateDataBits);
          this.describeBlockItemState(writer);
          long writtenBits = writer.getOffset();
          if(this.requiredBlockItemStateDataBits != writtenBits){
               throw new InvalidSerializedRuntimeDataException(this.getClass() + ": Exactly " + this.requiredBlockItemStateDataBits + " bits of block-item state data were expected, but " + writtenBits + " were written");
          }
          return writer.getValue();
     }

     @SneakyThrows
     public long encodeBlockOnlyState() {
          RuntimeDataWriter writer = new RuntimeDataWriter(this.requiredBlockOnlyStateDataBits);
          this.describeBlockOnlyState(writer);
          long writtenBits = writer.getOffset();
          if(this.requiredBlockOnlyStateDataBits != writtenBits){
               throw new InvalidSerializedRuntimeDataException(this.getClass() + ": Exactly " + this.requiredBlockOnlyStateDataBits + " bits of block-only state data were expected, but " + writtenBits + " were written");
          }
          return writer.getValue();
     }

     @SneakyThrows
     private long encodeFullState() {
          long blockItemBits = this.requiredBlockItemStateDataBits;
          long blockOnlyBits = this.requiredBlockOnlyStateDataBits;

          if (blockOnlyBits == 0 && blockItemBits == 0) {
               return 0;
          }

          long result = 0;
          if (blockItemBits > 0) {
               result |= encodeBlockItemState();
          }
          if (blockOnlyBits > 0) {
               result |= encodeBlockOnlyState() << blockItemBits;
          }

          return result;
     }

     @SneakyThrows
     public List<Block> generateStatePermutations() {
          List<Block> permutations = new ArrayList<>();
          long totalBits = this.requiredBlockItemStateDataBits + this.requiredBlockOnlyStateDataBits;
          if (totalBits > INTERNAL_STATE_DATA_BITS) {
               throw new IllegalStateException("Block state data cannot use more than " + INTERNAL_STATE_DATA_BITS + " bits");
          }

          for (long blockItemStateData = 0; blockItemStateData < (1L << this.requiredBlockItemStateDataBits); blockItemStateData++) {
               Block withType = this.clone();
               try {
                    withType.decodeBlockItemState(blockItemStateData);
                    long encoded = withType.encodeBlockItemState();
                    if (encoded != blockItemStateData) {
                         throw new IllegalStateException(this.getClass().getSimpleName() + "::decodeBlockItemState() accepts invalid inputs (returned " + encoded + " for input " + blockItemStateData + ")");
                    }
               } catch (InvalidSerializedRuntimeDataException e) {
                    continue;
               }

               for (long blockOnlyStateData = 0; blockOnlyStateData < (1L << this.requiredBlockOnlyStateDataBits); ++blockOnlyStateData) {
                    Block withState = withType.clone();
                    try {
                         withState.decodeBlockOnlyState(blockOnlyStateData);
                         long encoded = withState.encodeBlockOnlyState();
                         if (encoded != blockOnlyStateData) {
                              throw new IllegalStateException(this.getClass().getSimpleName() + "::decodeBlockOnlyState() accepts invalid inputs (returned " + encoded + " for input " + blockOnlyStateData + ")");
                         }
                    } catch (InvalidSerializedRuntimeDataException e) {
                         continue;
                    }
                    permutations.add(withState);
               }
          }

          return permutations;
     }

     public Block clone() {
          Block o = null;
          try {
               o = (Block) super.clone();
               o.position = o.getPosition().clone();
          } catch(CloneNotSupportedException cnse) {
               // Ne devrait jamais arriver, car nous implémentons
               // l'interface Cloneable
               cnse.printStackTrace(System.err);
          }
          return o;
     }

     public int getLightLevel() {
          return 0;
     }

     public int getLightFilter() {
          return this.isTransparent() ? 0 : 15;
     }

     public boolean blocksDirectSkyLight() {
          return getLightFilter() > 0;
     }

     public boolean isTransparent() {
          return false;
     }

     @Deprecated
     public boolean isSolid() {
          return true;
     }

     public BlockBreakInfo getBreakInfo() {
          return typeInfo.getBreakInfo();
     }
}

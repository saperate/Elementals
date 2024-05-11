package dev.saperate.elementals.elements.earth;

import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.Upgrade;
import dev.saperate.elementals.elements.fire.*;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.joml.Vector3f;

import static dev.saperate.elementals.misc.ElementalsCustomTags.EARTH_BENDABLE_BLOCKS;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;


public class EarthElement extends Element {
    public EarthElement() {
        super("Earth", new Upgrade[]{
                new Upgrade("shrapnel", new Upgrade[]{
                        new Upgrade("earthWall"),
                        new Upgrade("chunkPickup")
                },true)
        });
        addAbility(new AbilityEarth1(), true);
        addAbility(new AbilityEarthBlockPickup());
        addAbility(new AbilityEarthWall());
        addAbility(new AbilityEarthChunkPickup());
        addAbility(new AbilityEarth2(), true);
        addAbility(new AbilityEarthMine());
        addAbility(new AbilityEarthTrap());
    }

    public static Element get() {
        return elementList.get(3);
    }

    /**
     * Calculates if the block can be bent, if it can, we return an array of Object which contains in this order:
     * <br>0 - Vec3d -> the position where the hit intercepted
     * <br>1 - BlockState -> the block state before we consumed it
     * <br>2 - BlockPos -> the block pos where the raycast hit
     * @return Array of calculated items
     */
    public static Object[] canBend(PlayerEntity player, boolean consumeBlock) {
        BlockHitResult hit = (BlockHitResult) player.raycast(5, 1, false);

        BlockState blockState = player.getEntityWorld().getBlockState(hit.getBlockPos());

        if (hit.getType() == HitResult.Type.BLOCK && isBlockBendable(hit.getBlockPos(), player.getWorld())) {
            if (consumeBlock) {
                player.getWorld().setBlockState(hit.getBlockPos(), Blocks.AIR.getDefaultState());
            }
            return new Object[]{hit.getPos(),blockState, hit.getBlockPos()};
        }
        return null;
    }

    public static boolean isBlockBendable(BlockPos pos, World world) {
        BlockState bState = world.getBlockState(pos);
        return bState.isIn(EARTH_BENDABLE_BLOCKS);
    }

    public static boolean isBlockBendable(BlockState bState) {
        System.out.println(bState);
        return bState.isIn(EARTH_BENDABLE_BLOCKS);
    }
}

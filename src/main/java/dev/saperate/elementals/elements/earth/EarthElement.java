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

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;


public class EarthElement extends Element {
    public EarthElement() {
        super("Earth", new Upgrade[]{

        });
        addAbility(new AbilityEarth1(), true);
        addAbility(new AbilityEarthBlockPickup());
    }

    public static Element get() {
        return elementList.get(3);
    }

    /**
     * Calculates if the block can be bent, if it can, we return an array of Object which contains in this order:
     * <br>1 - Vec3d -> the position where we checked
     * <br>2 - BlockState -> the block state before we consumed it
     * @return Array of calculated items
     */
    public static Object[] canBend(PlayerEntity player, boolean consumeBlock) {
        BlockHitResult hit = (BlockHitResult) player.raycast(5, 1, false);

        BlockState blockState = player.getEntityWorld().getBlockState(hit.getBlockPos());

        if (hit.getType() == HitResult.Type.BLOCK && isBlockBendable(hit.getBlockPos(), player.getWorld())) {
            if (consumeBlock) {
                player.getWorld().setBlockState(hit.getBlockPos(), Blocks.AIR.getDefaultState());
            }
            return new Object[]{hit.getPos(),blockState};
        }
        return null;
    }

    public static boolean isBlockBendable(BlockPos pos, World world) {
        //TODO eventually make this into a config file
        BlockState bState = world.getBlockState(pos);
        Block block = bState.getBlock();


        return (block.equals(Blocks.DIRT)
                || block.equals(Blocks.GRASS_BLOCK)
                || block.equals(Blocks.SAND)
                || block.equals(Blocks.COARSE_DIRT)
                || block.equals(Blocks.ROOTED_DIRT)
                || block.equals(Blocks.COBBLESTONE)
                || block.equals(Blocks.STONE)
                || block.equals(Blocks.DIORITE)
                || block.equals(Blocks.MUD)
        );
    }
}

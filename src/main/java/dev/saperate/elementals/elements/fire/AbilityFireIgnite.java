package dev.saperate.elementals.elements.fire;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.fire.FireBlockEntity;
import dev.saperate.elementals.entities.water.WaterArcEntity;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3f;

import static dev.saperate.elementals.elements.fire.FireElement.placeFire;
import static dev.saperate.elementals.utils.SapsUtils.*;

public class AbilityFireIgnite extends Ability {
    @Override
    public void onCall(Bender bender) {
        PlayerEntity player = bender.player;
        BlockHitResult hit = (BlockHitResult) player.raycast(5, 0, true);
        BlockState blockState = player.getEntityWorld().getBlockState(hit.getBlockPos());
        BlockPos bPos = hit.getBlockPos();

        if (hit.getType() == HitResult.Type.BLOCK
                && AbstractFireBlock.canPlaceAt(player.getWorld(),bPos.up(),hit.getSide())) {
            //placeFire(hit.getBlockPos(), hit.getSide(), player, blockState);
            Entity entity = new FireBlockEntity(player.getWorld(), player, bPos.getX() + 0.5f, bPos.getY() + 1, bPos.getZ() + 0.5f);
            player.getWorld().spawnEntity(entity);
        }
    }

    @Override
    public void onLeftClick(Bender bender) {

    }

    @Override
    public void onMiddleClick(Bender bender) {

    }

    @Override
    public void onRightClick(Bender bender) {

    }

    @Override
    public void onTick(Bender bender) {

    }
}

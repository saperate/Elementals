package dev.saperate.elementals.elements.fire;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.data.StateDataSaverAndLoader;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.fire.FireBlockEntity;
import dev.saperate.elementals.entities.water.WaterArcEntity;
import dev.saperate.elementals.mixin.FurnaceBlockEntityAccessor;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;
import org.joml.Vector3f;

import static dev.saperate.elementals.elements.fire.FireElement.placeFire;
import static dev.saperate.elementals.utils.SapsUtils.*;

public class AbilityFireIgnite implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerEntity player = bender.player;
        BlockHitResult hit = (BlockHitResult) player.raycast(5, 0, true);
        BlockState blockState = player.getEntityWorld().getBlockState(hit.getBlockPos());
        BlockPos bPos = hit.getBlockPos();

        if (hit.getType() == HitResult.Type.BLOCK) {
            if(blockState.getProperties().contains(Properties.LIT)){

                BlockEntity blockEntity = player.getWorld().getBlockEntity(bPos);

                if (blockEntity instanceof AbstractFurnaceBlockEntity furnace){
                    ((FurnaceBlockEntityAccessor) furnace).setBurnTime(100);
                    ((FurnaceBlockEntityAccessor) furnace).setFuelTime(100);
                }

                player.getWorld().setBlockState(bPos, blockState.with(Properties.LIT, true), 11);
                player.getWorld().emitGameEvent(player, GameEvent.BLOCK_CHANGE, bPos);
                return;
            }

            if(AbstractFireBlock.canPlaceAt(player.getWorld(),bPos.up(),hit.getSide())){
                if(PlayerData.get(player).canUseUpgrade("flareUp")){
                    FireBlockEntity entity = new FireBlockEntity(player.getWorld(), player, bPos.getX() + 0.5f, bPos.getY() + 1, bPos.getZ() + 0.5f);
                    entity.setIsBlue(PlayerData.get(player).canUseUpgrade("blueFire"));
                    player.getWorld().spawnEntity(entity);
                }else{
                    placeFire(hit.getBlockPos(), hit.getSide(), player, blockState);
                }
            }
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

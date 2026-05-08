package dev.saperate.elementals.elements.fire;

import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.fire.FireBlockEntity;
import dev.saperate.elementals.mixin.FurnaceBlockEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import static dev.saperate.elementals.elements.fire.FireElement.placeFire;

public class AbilityFireIgnite implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        Player player = bender.player;
        PlayerData playerData = PlayerData.get(player);

        if(!playerData.canUseUpgrade("fireIgnition")){
            onRemove(bender);
            return;
        }

        if (!bender.reduceChi(2.5f)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }

        BlockHitResult hit = (BlockHitResult) player.pick(5, 0, true);
        BlockState blockState = player.level().getBlockState(hit.getBlockPos());
        BlockPos bPos = hit.getBlockPos();

        boolean hasFlareUp = PlayerData.get(player).canUseUpgrade("fireFlareUp");


        if (hit.getType() == HitResult.Type.BLOCK) {
            if(blockState.getProperties().contains(BlockStateProperties.LIT)){

                BlockEntity blockEntity = player.level().getBlockEntity(bPos);

                if (blockEntity instanceof AbstractFurnaceBlockEntity furnace){
                    Elementals.USED_ABILITY.trigger((ServerPlayer) player, "ignite/furnace");
                    ((FurnaceBlockEntityAccessor) furnace).setBurnTime(hasFlareUp ? 225 : 100);
                    ((FurnaceBlockEntityAccessor) furnace).setFuelTime(hasFlareUp ? 225 : 100);
                }

                player.level().setBlockAndUpdate(bPos, blockState.setValue(BlockStateProperties.LIT, true));
                player.level().gameEvent(player, GameEvent.BLOCK_CHANGE, bPos);
                return;
            }

            if(BaseFireBlock.canBePlacedAt(player.level(),bPos.above(),hit.getDirection())){
                if(hasFlareUp){
                    FireBlockEntity entity = new FireBlockEntity(player.level(), player, bPos.getX() + 0.5f, bPos.getY() + 1, bPos.getZ() + 0.5f);
                    entity.setIsBlue(PlayerData.get(player).canUseUpgrade("blueFire"));
                    player.level().addFreshEntity(entity);
                }
                placeFire(hit.getBlockPos(), hit.getDirection(), player, blockState);
            }

        }
    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
    }
}

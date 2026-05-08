package dev.saperate.elementals.elements.fire;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.fire.FireBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.phys.BlockHitResult;


public class AbilityFireWall implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        if (!bender.reduceChi(15)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }
        Player player = bender.player;
        PlayerData plrData = PlayerData.get(player);
        BlockHitResult hit = (BlockHitResult) player.pick(5, 0, true);
        BlockPos bPos = hit.getBlockPos();


        placeFire(bender,bPos,hit, plrData);
        double dx = -Math.sin(Math.toRadians(player.getYRot() - 90));//Side to side
        double dz = Math.cos(Math.toRadians(player.getYRot() - 90));

        for (int i = 1; i <= (plrData.canUseUpgrade("fireWallWideI") ? 8 : 4); i++) {
            int dxScaled = (int) Math.round(dx * i);
            int dzScaled = (int) Math.round(dz * i);

            placeFire(bender,bPos.offset(dxScaled, 0, dzScaled), hit, plrData);
            placeFire(bender,bPos.offset(-dxScaled, 0, -dzScaled), hit, plrData);
        }
    }

    public void placeFire(Bender bender, BlockPos bPos, BlockHitResult hit, PlayerData plrData){
        Player player = bender.player;

        if(BaseFireBlock.canBePlacedAt(player.level(),bPos.above(),hit.getDirection())) {
            FireBlockEntity entity = new FireBlockEntity(player.level(), player, bPos.getX() + 0.5f, bPos.getY() + 1, bPos.getZ() + 0.5f);
            entity.setFinalFireHeight(plrData.canUseUpgrade("fireWallTallI") ? 2.5f : 1.5f );
            player.level().addFreshEntity(entity);
            FireElement.placeFire(hit.getBlockPos(), hit.getDirection(), player, player.level().getBlockState(bPos));

        }
    }
    
    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
    }
}

package dev.saperate.elementals.elements.fire;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.fire.FireBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.phys.BlockHitResult;

public class AbilityFireSpikes implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        if (!PlayerData.get(bender.player).canUseUpgrade("fireSpikes")) {
            bender.setCurrAbility(null);
            return;
        }
        if (!bender.reduceChi(15)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }
        Player player = bender.player;
        RandomSource rnd = player.getRandom();
        PlayerData plrData = PlayerData.get(player);
        BlockHitResult hit = (BlockHitResult) player.pick(5, 0, true);
        BlockPos bPos = hit.getBlockPos();


        placeFire(bender, bPos, hit, plrData);
        int dx = (int) Math.round(-Math.sin(Math.toRadians(player.getYRot())));
        int dz = (int) Math.round(Math.cos(Math.toRadians(player.getYRot())));

        int range = PlayerData.get(player).canUseUpgrade("fireSpikesRangeI") ? 10 : 6;

        int countMod = PlayerData.get(player).canUseUpgrade("fireSpikesCountI") ? 2 : 1;

        for (int i = 1; i <= range; i++) {

            if(rnd.nextInt(0,6 / countMod) == 0){
                placeFire(bender, bPos.offset(dx * i, 0, dz * i), hit, plrData);
            }

            for (int j = -i; j < i; j++) {
                if(rnd.nextInt(0,4 / countMod) == 0) {
                    placeFire(bender, bPos.offset(dz * j + (i * dx), 0,  - (dx * j - (i * dz))), hit, plrData);
                }
            }

        }
    }

    public void placeFire(Bender bender, BlockPos bPos, BlockHitResult hit, PlayerData plrData) {
        Player player = bender.player;

        if (FireBlock.canBePlacedAt(player.level(), bPos.above(), hit.getDirection())) {
            FireBlockEntity entity = new FireBlockEntity(player.level(), player, bPos.getX() + 0.5f, bPos.getY() + 1, bPos.getZ() + 0.5f);
            entity.setFinalFireHeight(1.5f);
            player.level().addFreshEntity(entity);
            FireElement.placeFire(hit.getBlockPos(), hit.getDirection(), player, player.level().getBlockState(bPos));
        }
    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
    }
}
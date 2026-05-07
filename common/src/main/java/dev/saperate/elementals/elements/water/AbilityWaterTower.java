package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import static dev.saperate.elementals.utils.SapsUtils.*;

public class AbilityWaterTower implements Ability {

    //TODO add a method which is like on keypress or smth so when one of the 4 key is pressed it calls here
    //TODO could be useful for more intuitive uncast
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


        if (bender.isAbilityInBackground(this)) {
            onRemove(bender);
        } else {
            bender.setCurrAbility(null);
            Player player = bender.player;

            if (player.isInWaterOrRain()) {
                player.addDeltaMovement(new Vec3(0, 1, 0));
                player.hurtMarked = true; //TODO Verify this works
                player.move(MoverType.PLAYER, player.getDeltaMovement());
            }

            WaterTowerEntity entity = new WaterTowerEntity(player.level(), player);
            entity.setOwnerCouldFly(player.getAbilities().mayfly);
            bender.abilityData = entity;
            bender.addBackgroundAbility(this, makeAbilityData(entity, player.getAbilities().mayfly));

            int height = 10;
            PlayerData plrData = PlayerData.get(player);
            if (plrData.canUseUpgrade("waterTowerRangeI")) {
                height = 15;
            }
            entity.setMaxTowerHeight(height);

            player.level().addFreshEntity(entity);
        }
    }

    @Override
    public void onBackgroundTick(Bender bender, Object data) {
        if (!bender.reduceChi(0.15f)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }

        Player player = bender.player;
        WaterTowerEntity entity = getEntity(bender);

        int height = 10;
        PlayerData plrData = PlayerData.get(player);
        if (plrData.canUseUpgrade("waterTowerRangeI")) {
            height = 15;
        }

        BlockHitResult hit = raycastBlockCustomRotation(player, height, true, new Vec3(0, -1, 0));

        boolean isAir = hit == null;

        if (!player.isUnderWater() && (player.onGround()
                || entity.getY() - 0.25f > player.getY()
                || (!isAir && !WaterElement.isBlockBendable(hit.getBlockPos(), player.level(), false, plrData.canUseUpgrade("waterPickupEfficiencyI"))))) {
            onRemove(bender);
            return;
        }

        if (entity == null) {
            return;
        }
        entity.setPosition(hit.getLocation());
    }

    @Override
    public void onRemove(Bender bender) {
        WaterTowerEntity entity = getEntity(bender);
        bender.removeAbilityFromBackground(this);
        if (entity == null) {
            return;
        }
        entity.discard();
    }

    public WaterTowerEntity getEntity(Bender bender) {
        return ((WaterTowerEntity) ((Object[]) bender.getBackgroundAbilityData(this))[0]);
    }

    public boolean getFlyAbility(Bender bender) {
        return ((boolean) ((Object[]) bender.getBackgroundAbilityData(this))[1]);
    }

    public Object[] makeAbilityData(WaterTowerEntity entity, boolean flyAbility) {
        return new Object[]{entity, flyAbility};
    }
}

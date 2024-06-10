package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.*;

public class AbilityWater3 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerData playerData = PlayerData.get(bender.player);

        if (bender.player.isSprinting() && playerData.canUseUpgrade("waterSurf")
                && (bender.player.isOnGround() || bender.player.isSubmergedInWater())) {
            WaterElement.get().abilityList.get(13).onCall(bender, deltaT);
            return;
        }

        bender.setCurrAbility(this);
        bender.abilityData = -1;
    }

    @Override
    public void onLeftClick(Bender bender, boolean started) {
        PlayerEntity player = bender.player;
        PlayerData playerData = PlayerData.get(player);
        if (!playerData.canUseUpgrade("waterJump")) {
            bender.setCurrAbility(null);
            return;
        }

        if (player.isTouchingWaterOrRain()) {
            if (!bender.reduceChi(15)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }
            bender.abilityData = 1;

            float power = 1;
            PlayerData plrData = PlayerData.get(player);
            if (plrData.canUseUpgrade("waterJumpRangeII")) {
                power = 2;
            } else if (plrData.canUseUpgrade("waterJumpRangeI")) {
                power = 1.5f;
            }

            launchPlayer(player,power);
        }
    }

    @Override
    public void onMiddleClick(Bender bender, boolean started) {

    }

    @Override
    public void onRightClick(Bender bender, boolean started) {

    }

    @Override
    public void onTick(Bender bender) {
        PlayerEntity player = bender.player;
        serverSummonParticles((ServerWorld) player.getWorld(),
                ParticleTypes.SPLASH, player, player.getRandom(),
                0, 0.1f, 0,
                0.1f, 4,
                0, -0.5f, 0, 0);

        if (bender.abilityData == null || (player.isSneaking() && ((int) bender.abilityData) < 0)) {
            onRemove(bender);
            return;
        }


        if (!bender.player.isOnGround() && ((int) bender.abilityData) < 0 && PlayerData.get(player).canUseUpgrade("waterTower")) {
            WaterElement.get().abilityList.get(16).onCall(bender, -1);//dT doesn't matter here
        } else if (bender.abilityData.equals(1) && (bender.player.isOnGround() || bender.player.isSubmergedInWater())) {
            //Trigger when jump touches the ground, use for abilities
            onRemove(bender);
        }
    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
        bender.abilityData = null;
    }

}

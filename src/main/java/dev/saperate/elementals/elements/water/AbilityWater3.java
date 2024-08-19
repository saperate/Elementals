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
        PlayerEntity player = bender.player;

        if (player.isSprinting() && playerData.canUseUpgrade("waterSurf")
                && player.isSubmergedInWater()) {
            WaterElement.get().abilityList.get(13).onCall(bender, deltaT);
            return;

        }else if (deltaT >= 200 && !player.isOnGround() && PlayerData.get(player).canUseUpgrade("waterTower")){
            WaterElement.get().abilityList.get(16).onCall(bender, deltaT);
            return;

        } else if (player.getRootVehicle().isTouchingWaterOrRain() && playerData.canUseUpgrade("waterJump") || WaterElement.canBend(player,true) != null) {
            WaterElement.get().abilityList.get(17).onCall(bender, deltaT);
            return;
        }

        bender.setCurrAbility(null);
    }

    @Override
    public void onLeftClick(Bender bender, boolean started) {

    }

    @Override
    public void onMiddleClick(Bender bender, boolean started) {

    }

    @Override
    public void onRightClick(Bender bender, boolean started) {

    }

    @Override
    public void onTick(Bender bender) {

    }

    @Override
    public void onRemove(Bender bender) {

    }

}

package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import net.minecraft.world.entity.player.Player;

public class AbilityWater3 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerData playerData = PlayerData.get(bender.player);
        Player player = bender.player;
        bender.setCurrAbility(null);
        
        if (player.isSprinting() && playerData.canUseUpgrade("waterSurf")
                && player.isUnderWater()) {
            WaterElement.get().getAbility(13).onCall(bender, deltaT);

        }else if (deltaT >= 500 && !player.onGround() && PlayerData.get(player).canUseUpgrade("waterTower")){
            WaterElement.get().getAbility(16).onCall(bender, deltaT);

        } else if (player.getRootVehicle().isInWaterOrRain() && playerData.canUseUpgrade("waterJump") && WaterElement.canBend(player,true) != null) {
            WaterElement.get().getAbility(17).onCall(bender, deltaT);
        }
    }
    @Override
    public void onRemove(Bender bender) {

    }

}

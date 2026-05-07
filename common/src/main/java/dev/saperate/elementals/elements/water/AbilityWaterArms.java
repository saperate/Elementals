package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Ability;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3f;

public class AbilityWaterArms implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        Player player = bender.player;
        Vector3f pos = WaterElement.canBend(player,true);

        if (pos != null) {
            WaterArmEntity entity = new WaterArmEntity(player.level(), player, pos.x, pos.y, pos.z);
            bender.abilityData = entity;
            entity.createChain(player);
            player.level().addFreshEntity(entity);

            bender.setCurrAbility(this);
        } else {
            bender.setCurrAbility(null);
        }
    }

    @Override
    public void onLeftClick(Bender bender, boolean started) {
        WaterArmEntity entity = (WaterArmEntity) bender.abilityData;
        if(entity == null){
            throw new RuntimeException("Elementals: Tried to launch entity while having none!");
        }
        onRemove(bender);

        entity.setVelocity(bender.player, bender.player.getXRot(), bender.player.getYRot(), 0, 1, 0);
    }
    @Override
    public void onRightClick(Bender bender, boolean started) {
        onRemove(bender);
    }
    

    @Override
    public void onRemove(Bender bender) {
        WaterArmEntity entity = (WaterArmEntity) bender.abilityData;
        if (entity == null) {
            return;
        }
        entity.setControlled(false);
        bender.setCurrAbility(null);
    }

}

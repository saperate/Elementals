package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.water.WaterArcEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.joml.Vector3f;

public class AbilityWaterArc extends Ability {
    public WaterArcEntity entity;
    @Override
    public void onCall(PlayerEntity player) {
        Vector3f pos = WaterElement.canBend(player);

        if (pos != null) {
            entity = new WaterArcEntity(player.getWorld(), player, pos.x, pos.y, pos.z);
            entity.createChain(player);
            player.getWorld().spawnEntity(entity);

            Bender.getBender(player).setCurrAbility(this);
        }
    }

    @Override
    public void onLeftClick() {

    }

    @Override
    public void onMiddleClick() {

    }

    @Override
    public void onRightClick() {

    }
}

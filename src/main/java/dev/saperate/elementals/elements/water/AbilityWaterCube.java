package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.earth.EarthBlockEntity;
import dev.saperate.elementals.entities.water.WaterArcEntity;
import dev.saperate.elementals.entities.water.WaterCubeEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;

public class AbilityWaterCube implements Ability {

    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerEntity player = bender.player;
        Vector3f pos = WaterElement.canBend(player,true);

        if (pos != null) {
            bender.reduceChi(10);
            WaterCubeEntity entity = new WaterCubeEntity(player.getWorld(), player, pos.x, pos.y, pos.z);
            bender.abilityData = entity;
            player.getWorld().spawnEntity(entity);

            bender.setCurrAbility(this);
        }else{
            bender.setCurrAbility(null);
        }
    }


    @Override
    public void onLeftClick(Bender bender, boolean started) {
        WaterCubeEntity entity = (WaterCubeEntity) bender.abilityData;
        onRemove(bender);
        if(entity == null){
            return;
        }

        entity.setVelocity(bender.player, bender.player.getPitch(), bender.player.getYaw(), 0, 1, 0);
    }

    @Override
    public void onMiddleClick(Bender bender, boolean started) {

    }

    @Override
    public void onRightClick(Bender bender, boolean started) {
        onRemove(bender);
    }

    @Override
    public void onTick(Bender bender) {

    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
        WaterCubeEntity entity = (WaterCubeEntity) bender.abilityData;
        bender.abilityData = null;
        if (entity == null) {
            return;
        }
        entity.setControlled(false);
    }

}

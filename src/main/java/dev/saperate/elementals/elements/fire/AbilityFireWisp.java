package dev.saperate.elementals.elements.fire;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.fire.FireBallEntity;
import dev.saperate.elementals.entities.fire.FireWispEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;

public class AbilityFireWisp implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        bender.setCurrAbility(null);

        if(bender.isAbilityInBackground(this)){
            ((FireWispEntity) bender.getBackgroundAbilityData(this)).discard();
            bender.removeAbilityFromBackground(this);
            return;
        }

        if (!bender.reduceChi(10)) {
            return;
        }
        PlayerEntity player = bender.player;

        Vector3f pos = player.getEyePos().subtract(0,0.5f,0).toVector3f();

        FireWispEntity entity = new FireWispEntity(player.getWorld(), player, pos.x, pos.y, pos.z);
        bender.abilityData = entity;
        entity.setIsBlue(PlayerData.get(player).canUseUpgrade("blueFire"));
        player.getWorld().spawnEntity(entity);
        bender.addBackgroundAbility(this,entity);
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
    public void onBackgroundTick(Bender bender, Object data) {
        FireWispEntity wisp = (FireWispEntity) data;
        if(!bender.reduceChi(0.075f) || wisp.isTouchingWater()){
            wisp.discard();
            bender.removeAbilityFromBackground(this);
        }
    }

    @Override
    public void onRemove(Bender bender) {

    }
}

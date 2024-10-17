package dev.saperate.elementals.elements.fire;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.fire.FireBallEntity;
import dev.saperate.elementals.entities.fire.FireWispEntity;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.raycastFull;

public class AbilityFireWisp implements Ability {
    @Override
    public void onCall(Bender originalBender, long deltaT) {
        Bender bender = originalBender;
        originalBender.setCurrAbility(null);

        if(originalBender.player.isSneaking()){
            PlayerEntity other = (PlayerEntity) SapsUtils.entityFromHitResult(
                    raycastFull(originalBender.player,5,true, entity -> entity instanceof PlayerEntity));
            if(other != null){
                bender = Bender.getBender((ServerPlayerEntity) other);
            }
        }


        if(bender.isAbilityInBackground(this)){
            ((FireWispEntity) bender.getBackgroundAbilityData(this)).discard();
            bender.removeAbilityFromBackground(this);
            return;
        }

        if (!originalBender.reduceChi(10)) {
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
        if(!bender.reduceChi(0.075f, false) || wisp.isTouchingWater()){
            wisp.discard();
            bender.removeAbilityFromBackground(this);
        }
    }

    @Override
    public void onRemove(Bender bender) {

    }
}

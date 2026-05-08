package dev.saperate.elementals.elements.fire;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.fire.FireWispEntity;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.raycastFull;

public class AbilityFireWisp implements Ability {
    @Override
    public void onCall(Bender originalBender, long deltaT) {
        Bender bender = originalBender;
        originalBender.setCurrAbility(null);

        if(originalBender.player.isCrouching()){
            Player other = (Player) SapsUtils.entityFromHitResult(
                    raycastFull(originalBender.player,5,true, entity -> entity instanceof Player));
            if(other != null){
                bender = Bender.getBender((ServerPlayer) other);
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
        Player player = bender.player;

        Vector3f pos = player.getEyePosition().subtract(0,0.5f,0).toVector3f();

        FireWispEntity entity = new FireWispEntity(player.level(), player, pos.x, pos.y, pos.z);
        bender.abilityData = entity;
        entity.setIsBlue(PlayerData.get(player).canUseUpgrade("blueFire"));
        player.level().addFreshEntity(entity);
        bender.addBackgroundAbility(this,entity);
    }


    @Override
    public void onBackgroundTick(Bender bender, Object data) {
        FireWispEntity wisp = (FireWispEntity) data;
        if(!bender.reduceChi(0.075f, false) || wisp.isInWater()){
            wisp.discard();
            bender.removeAbilityFromBackground(this);
        }
    }

    @Override
    public void onRemove(Bender bender) {

    }
}

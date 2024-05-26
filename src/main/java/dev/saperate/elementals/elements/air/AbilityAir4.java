package dev.saperate.elementals.elements.air;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.common.DecoyPlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

import static dev.saperate.elementals.effects.SpiritProjectionStatusEffect.SPIRIT_PROJECTION_EFFECT;

public class AbilityAir4 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        ServerPlayerEntity plr = (ServerPlayerEntity) bender.player;

        DecoyPlayerEntity decoy = new DecoyPlayerEntity(plr.getWorld(),plr);
        plr.getWorld().spawnEntity(decoy);

        bender.abilityData = new Object[]{plr.getPos(), plr.interactionManager.getGameMode(), decoy};
        plr.changeGameMode(GameMode.SPECTATOR);

        bender.player.addStatusEffect(new StatusEffectInstance(SPIRIT_PROJECTION_EFFECT,-1,0,false,false,false));
        bender.setCurrAbility(this);
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
        if(bender.player.isSneaking()){
            onRemove(bender);
        }
    }

    @Override
    public void onRemove(Bender bender) {
        //TODO add upgrade to uncast before the timer, otherwise they have to punch their bodies or smth

        Object[] data = (Object[]) bender.abilityData;

        Vec3d pos = (Vec3d) data[0];
        if(pos != null){
            bender.player.setPos(pos.x,pos.y,pos.z);
        }
        GameMode gm = (GameMode) data[1];
        if(gm != null){
            ((ServerPlayerEntity) bender.player).changeGameMode(gm);
        }

        DecoyPlayerEntity decoy = (DecoyPlayerEntity) data[2];
        if(decoy != null){
            decoy.discard();
        }

        bender.player.removeStatusEffect(SPIRIT_PROJECTION_EFFECT);
        bender.setCurrAbility(null);
        bender.abilityData = null;
    }
}

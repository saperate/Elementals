package dev.saperate.elementals.elements.lightning;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.fire.FireArcEntity;
import dev.saperate.elementals.entities.lightning.LightningArcEntity;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.serverSummonParticles;

public class AbilityLightningBolt implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerEntity player = bender.player;

        Vector3f pos = getEntityLookVector(player, 2).toVector3f();

        LightningArcEntity entity = new LightningArcEntity(player.getWorld(), player, pos.x, pos.y, pos.z);
        entity.makeChild();

        bender.abilityData = entity;
        player.getWorld().spawnEntity(entity);


        bender.setCurrAbility(this);
    }

    @Override
    public void onLeftClick(Bender bender, boolean started) {
        LightningArcEntity e = (LightningArcEntity) bender.abilityData;
        if(e.getTail().chainLength == LightningArcEntity.MAX_CHAIN_LENGTH){
            Vec3d pos = SapsUtils.raycastFull(bender.player, 20,true).getPos();
            LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT,e.getWorld());
            lightning.setPos(pos.x,pos.y,pos.z);
            e.getWorld().spawnEntity(lightning);
            onRemove(bender);
        }
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
        LightningArcEntity e = (LightningArcEntity) bender.abilityData;
        e.remove();
        bender.setCurrAbility(null);
    }
}

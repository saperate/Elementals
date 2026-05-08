package dev.saperate.elementals.elements.metal;

import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.ElementalConfig;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.metal.MetalLanceEntity;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class AbilityMetalLance implements Ability {

    @Override
    public void onCall(Bender bender, long deltaT) {//TODO add chargeup after casting
        if (!bender.reduceChi(15)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }
        Player player = bender.player;
        Vec3 pos = SapsUtils.getEntityLookVector(player,1);
        
        MetalLanceEntity entity = new MetalLanceEntity(player.level(), player, pos.x, pos.y, pos.z);
        entity.setControlled(true);
        bender.abilityData = packAbilityData(entity,-1,null);
        player.level().addFreshEntity(entity);

        bender.setCurrAbility(this);
    }

    @Override
    public void onLeftClick(Bender bender, boolean started) {
        MetalLanceEntity lance = getLanceEntity(bender);
        if(lance == null 
                || (!lance.getIsControlled() && !bender.plrData.canUseUpgrade("metalLanceRedirectI")))
            return;
        lance.setControlled(false);
        HitResult hitResult = SapsUtils.raycastFull(bender.player, 150, false);
        if(hitResult.getType() != HitResult.Type.MISS){
            bender.abilityData = packAbilityData(lance,getHoldTime(bender),hitResult.getLocation());
            Vec3 dirToTarget = hitResult.getLocation().subtract(lance.position()).normalize().scale(2);
            lance.setDeltaMovement(dirToTarget);
        }else {
            lance.setDeltaMovement(bender.player, bender.player.getXRot(), bender.player.getYRot(), 0, 4, 0);
        }
        lance.move(MoverType.SELF,lance.getDeltaMovement());
        if(!bender.plrData.canUseUpgrade("metalLanceRedirectI")){
            onRemove(bender);
        }
    }

    @Override
    public void onRightClick(Bender bender, boolean started) {
        MetalLanceEntity lance = getLanceEntity(bender);
        if(lance.getIsControlled()){
            lance.remove(Entity.RemovalReason.KILLED);//TODO make a synced data to transfer whether or not to explode
        }
        onRemove(bender);
    }

    @Override
    public void onTick(Bender bender) {
        MetalLanceEntity lance = getLanceEntity(bender);
        Vec3 thrownPos = getThrownPos(bender);
        
        if(thrownPos != null && lance.position().distanceTo(thrownPos) < 2){
            lance.discard();
            
            if(bender.plrData.canUseUpgrade("metalLanceDamageII") 
                    && lance.level().getGameRules().getBoolean(Elementals.BENDING_GRIEFING)) {
                Explosion explosion = new Explosion(lance.level(), lance, lance.getX(), lance.getY(), lance.getZ(), 1, false, Explosion.BlockInteraction.DESTROY);
                explosion.explode();
                explosion.finalizeExplosion(true);
            }
            SapsUtils.serverSummonParticles(
                    (ServerLevel) lance.level(), 
                    ParticleTypes.EXPLOSION, 
                    lance,lance.level().getRandom(),
                    0,0,0,
                    0,4, 
                    0, -0.5f, 0, 
                    0
            );
            
            List<LivingEntity> entityList = SapsUtils.getEntitiesInRadius(
                    getThrownPos(bender),1,
                    lance.level(),lance
            );
            if(entityList.isEmpty()){
                onRemove(bender);
                return;
            }
            int damage = 8;
            if(bender.plrData.canUseUpgrade("metalLanceDamageII"))
                damage = 14;
            else if(bender.plrData.canUseUpgrade("metalLanceDamageI"))
                damage = 12;
            

            for (LivingEntity living: entityList) {
                living.hurt(bender.player.damageSources().playerAttack(bender.player), damage * ElementalConfig.get().BENDING_DAMAGE_MULTIPLIER);
            }
            onRemove(bender);
            return;
        }
        if(lance.isRemoved()){
            onRemove(bender);
        }
    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
    }

    //ABILITY DATA STUFF
    
    public Object packAbilityData(MetalLanceEntity lance, int holdTime, Vec3 thrownPos){
        return new Object[]{lance,holdTime,thrownPos};
    }
    
    public MetalLanceEntity getLanceEntity(Bender bender){
        return (MetalLanceEntity) ((Object[])bender.abilityData)[0];
    }

    public int getHoldTime(Bender bender){
        if (bender.abilityData != null) {
            return (int) ((Object[])bender.abilityData)[1];
        }
        return -1;
    }

    public Vec3 getThrownPos(Bender bender){
        if (bender.abilityData != null) {
            return (Vec3) ((Object[])bender.abilityData)[2];
        }
        return null;
    }
}

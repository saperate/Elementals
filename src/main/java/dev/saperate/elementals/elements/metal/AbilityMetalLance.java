package dev.saperate.elementals.elements.metal;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.elements.water.WaterElement;
import dev.saperate.elementals.entities.metal.MetalLanceEntity;
import dev.saperate.elementals.entities.water.WaterBladeEntity;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.List;

public class AbilityMetalLance implements Ability {

    @Override
    public void onCall(Bender bender, long deltaT) {
        if (!bender.reduceChi(15)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }
        PlayerEntity player = bender.player;
        Vec3d pos = SapsUtils.getEntityLookVector(player,1);
        
        MetalLanceEntity entity = new MetalLanceEntity(player.getWorld(), player, pos.x, pos.y, pos.z);
        entity.setControlled(true);
        bender.abilityData = packAbilityData(entity,-1,null);
        player.getWorld().spawnEntity(entity);

        bender.setCurrAbility(this);
    }

    @Override
    public void onLeftClick(Bender bender, boolean started) {
        MetalLanceEntity lance = getLanceEntity(bender);
        if(lance == null)
            return;
        lance.setControlled(false);
        HitResult hitResult = SapsUtils.raycastFull(bender.player, 150, false);
        if(hitResult.getType() != HitResult.Type.MISS){//TODO store entity in ability data
            bender.abilityData = packAbilityData(lance,getHoldTime(bender),hitResult.getPos());
            Vec3d dirToTarget = hitResult.getPos().subtract(lance.getPos()).normalize().multiply(2);
            lance.setVelocity(dirToTarget);
        }else {
            lance.setVelocity(bender.player, bender.player.getPitch(), bender.player.getYaw(), 0, 4, 0);
        }
        lance.move(MovementType.SELF,lance.getVelocity());
    }

    @Override
    public void onRightClick(Bender bender, boolean started) {
        //TODO be able to right click to dismiss accuracy, would remove little explosion and lower damage
    }

    @Override
    public void onTick(Bender bender) {
        MetalLanceEntity lance = getLanceEntity(bender);
        Vec3d thrownPos = getThrownPos(bender);
        
        if(thrownPos != null){
            System.out.println(lance.getPos().distanceTo(thrownPos));
        }
        
        if(thrownPos != null && lance.getPos().distanceTo(thrownPos) < 2){
            lance.discard();
            List<LivingEntity> entityList = SapsUtils.getEntitiesInRadius(
                    getThrownPos(bender),1,
                    lance.getWorld(),lance
            );
            if(entityList.isEmpty()){
                onRemove(bender);
                return;
            }
            LivingEntity closest = entityList.get(0);
            float closestDist = closest.distanceTo(lance);
            entityList.remove(0);
            while (!entityList.isEmpty()){
                if(entityList.get(0).distanceTo(lance) < closestDist){
                    closest = entityList.get(0);
                    closestDist = closest.distanceTo(lance);
                }
                entityList.remove(0);
            }
            closest.damage(lance.getDamageSources().playerAttack(bender.player),5);
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
    
    public Object packAbilityData(MetalLanceEntity lance, int holdTime, Vec3d thrownPos){
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

    public Vec3d getThrownPos(Bender bender){
        if (bender.abilityData != null) {
            return (Vec3d) ((Object[])bender.abilityData)[2];
        }
        return null;
    }
}

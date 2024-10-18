package dev.saperate.elementals.elements.blood;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.HitResult;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;


public class AbilityBloodControl implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerEntity player = bender.player;
        if(!BloodElement.isNight(player.getWorld()) && !bender.plrData.canUseUpgrade("bloodControlPrecisionII")){
            bender.setCurrAbility(null);
            return;
        }

        HitResult hit = SapsUtils.raycastFull(
                player,
                bender.plrData.canUseUpgrade("bloodControlPower") ? 40 : 20,
                false,
                (entity -> entity instanceof LivingEntity)
        );

        LivingEntity living = (LivingEntity) SapsUtils.entityFromHitResult(hit);
        if (living != null && bender.reduceChi(25)) {

            bender.setCurrAbility(this);
            setAbilityData(bender,living,10);
            return;
        }
        bender.setCurrAbility(null);
    }

    @Override
    public void onLeftClick(Bender bender, boolean started) {
        PlayerEntity player = bender.player;
        LivingEntity living = getVictim(bender);
        if(living == null || living.isRemoved()){
            bender.setCurrAbility(null);
            return;
        }
        float power = player.isSneaking() ? -3 : 3;
        if(bender.plrData.canUseUpgrade("bloodControlPower")){
            power *= 1.5f;
        }

        Vector3f velocity = getEntityLookVector(player, 1)
                .subtract(player.getEyePos())
                .normalize().multiply(power, power * 0.5f, power).toVector3f();
        //returns the root vehicle or itself if there are none
        Entity vehicle = living.getRootVehicle();

        vehicle.setVelocity(velocity.x,
                velocity.y,
                velocity.z);
        vehicle.velocityModified = true;
        vehicle.move(MovementType.PLAYER, vehicle.getVelocity());
        bender.setCurrAbility(null);
    }

    @Override
    public void onMiddleClick(Bender bender, boolean started) {
        if(bender.player.isSneaking()){
            decrementDistance(bender);
        }else {
            incrementDistance(bender);
        }
    }

    @Override
    public void onRightClick(Bender bender, boolean started) {
        onRemove(bender);
    }

    @Override
    public void onTick(Bender bender) {
        if (!bender.reduceChi(0.25f)) {
            bender.setCurrAbility(null);
            return;
        }
        LivingEntity living = getVictim(bender);
        if(living == null || living.isRemoved()){
            bender.setCurrAbility(null);
            return;
        }

        if(living.getVelocity().y <= -0.020f){
            living.fallDistance = 0;
        }

        HitResult hit = bender.player.raycast(getDistance(bender),1, !bender.player.isSubmergedInWater());

        Vector3f direction = hit.getPos().toVector3f().sub(0, 0.5f, 0)
                .sub(living.getPos().toVector3f())
                .mul(0.05f)
                .mul(1,1,1)
                ;

        if(!SapsUtils.isLookingAt(bender.player,living,-1,0.775f)){
            onRemove(bender);
        }

        living.addVelocity(direction.x, direction.y, direction.z);
        living.velocityModified = true;
        living.move(MovementType.PLAYER, living.getVelocity());
    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
    }

    @Override
    public boolean shouldImmobilizePlayer(PlayerEntity player) {
        return !Bender.getBender((ServerPlayerEntity) player).getData().canUseUpgrade("bloodControlPrecisionI");
    }

    public LivingEntity getVictim(Bender bender){
        return ((LivingEntity)((Object[])bender.abilityData)[0]);
    }

    public int getDistance(Bender bender){
        if(bender.abilityData == null){
            return 10;
        }
        return ((int)((Object[])bender.abilityData)[1]);
    }

    public void incrementDistance(Bender bender){
        setAbilityData(bender,getVictim(bender),Math.min(getDistance(bender) + 5, bender.plrData.canUseUpgrade("bloodControlPower") ? 40 : 20));
    }

    public void decrementDistance(Bender bender){
        setAbilityData(bender,getVictim(bender),Math.max(getDistance(bender) - 5, 5));
    }

    public void setAbilityData(Bender bender, LivingEntity victim, int distance){
        bender.abilityData = new Object[]{victim, distance};
    }
}

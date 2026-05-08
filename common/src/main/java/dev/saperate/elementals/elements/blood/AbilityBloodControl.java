package dev.saperate.elementals.elements.blood;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;


public class AbilityBloodControl implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        Player player = bender.player;
        if(!BloodElement.isNight(player.level()) && !bender.plrData.canUseUpgrade("bloodControlPrecisionII")){
            bender.setCurrAbility(null);
            return;
        }

        HitResult hit = SapsUtils.raycastFull(
                player,
                bender.plrData.canUseUpgrade("bloodControlPowerI") ? 40 : 20,
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
        Player player = bender.player;
        LivingEntity living = getVictim(bender);
        if(living == null || living.isRemoved()){
            bender.setCurrAbility(null);
            return;
        }
        float power = player.isCrouching() ? -3 : 3;
        if(bender.plrData.canUseUpgrade("bloodControlPowerI")){
            power *= 1.5f;
        }

        Vector3f velocity = getEntityLookVector(player, 1)
                .subtract(player.getEyePosition())
                .normalize().multiply(power, power * 0.5f, power).toVector3f();
        //returns the root vehicle or itself if there are none
        Entity vehicle = living.getRootVehicle();

        vehicle.setDeltaMovement(velocity.x,
                velocity.y,
                velocity.z);
        vehicle.hasImpulse = true;
        vehicle.move(MoverType.PLAYER, vehicle.getDeltaMovement());
        bender.setCurrAbility(null);
    }

    @Override
    public void onMiddleClick(Bender bender, boolean started) {
        if(bender.player.isCrouching()){
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

        if(living.getDeltaMovement().y <= -0.020f){
            living.fallDistance = 0;
        }

        HitResult hit = bender.player.pick(getDistance(bender),1, !bender.player.isUnderWater());

        Vector3f direction = hit.getLocation().toVector3f().sub(0, 0.5f, 0)
                .sub(living.position().toVector3f())
                .mul(0.05f)
                .mul(1,1,1)
                ;

        if(!SapsUtils.isLookingAt(bender.player,living,-1,0.775f)){
            onRemove(bender);
        }

        living.addDeltaMovement(new Vec3(direction.x, direction.y, direction.z));
        living.hasImpulse = true;
        living.move(MoverType.PLAYER, living.getDeltaMovement());
    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
    }

    @Override
    public boolean shouldImmobilizePlayer(Player player) {
        return !Bender.getBender((ServerPlayer) player).getData().canUseUpgrade("bloodControlPrecisionI");
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
        setAbilityData(bender,getVictim(bender),Math.min(getDistance(bender) + 5, bender.plrData.canUseUpgrade("bloodControlPowerI") ? 40 : 20));
    }

    public void decrementDistance(Bender bender){
        setAbilityData(bender,getVictim(bender),Math.max(getDistance(bender) - 5, 5));
    }

    public void setAbilityData(Bender bender, LivingEntity victim, int distance){
        bender.abilityData = new Object[]{victim, distance};
    }
}

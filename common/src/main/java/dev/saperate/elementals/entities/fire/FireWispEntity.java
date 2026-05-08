package dev.saperate.elementals.entities.fire;

import dev.saperate.elementals.blocks.blockEntities.LitAirBlockEntity;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.elements.fire.AbilityFireWisp;
import dev.saperate.elementals.elements.fire.FireElement;
import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import dev.saperate.elementals.misc.FireExplosion;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.SynchedEntityData;
import net.minecraft.entity.data.EntityDataAccessor;
import net.minecraft.entity.data.EntityDataSerializers;
import net.minecraft.entity.player.Player;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.Level;
import net.minecraft.world.explosion.Explosion;


import static dev.saperate.elementals.entities.ElementalEntities.FIREWISP;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class FireWispEntity extends AbstractElementalsEntity<Player> {
    private static final EntityDataAccessor<Boolean> IS_BLUE = SynchedEntityData.defineId(FireWispEntity.class, EntityDataSerializers.BOOLEAN);
    public FireWispEntity(EntityType<FireWispEntity> type, Level world) {
        super(type, world, Player.class);
    }

    public FireWispEntity(Level world, Player owner) {
        super(FIREWISP, world, Player.class);
        setOwner(owner);
        setPos(owner.getX(), owner.getY(), owner.getZ());
    }

    public FireWispEntity(Level world, Player owner, double x, double y, double z) {
        super(FIREWISP, world, Player.class);
        setOwner(owner);
        setPos(x, y, z);
        setControlled(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_BLUE, false);
    }

    @Override
    public void tick() {
        super.tick();

        if (touchingWater && !level().isClientSide) {
            remove();
            return;
        }

        if (random.nextBetween(0, 20) == 6) {
            summonParticles(this, random,
                    isBlue() ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME,
                    0, 1);
            //playSound(SoundEvents.BLOCK_FIRE_AMBIENT, 1, 0);

        }

        Entity owner = getOwner();
        if(isRemoved()){
            return;
        }

        if (owner == null) {
            discard();
            return;
        }

        if (!owner.isCrouching()) {
            moveEntity();
        }



    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        remove();
        return true;
    }


    private void moveEntity() {
        if (getIsControlled() ) {
            float yaw = getOwner().getHeadYaw() % 360;
            if(yaw < 0){
                yaw = 360 + yaw;
            }
            moveEntityTowardsGoal(getOwner().getEyePos()
                    .add(-.75 * Math.cos(Math.toRadians(yaw)),0.5,-.75 * Math.sin(Math.toRadians(yaw))).toVector3f());
        }else {
            discard();
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    public void remove(){
        discard();

        Player owner = getOwner();
        if(owner != null && !level().isClientSide){
            Bender bender = Bender.getBender((ServerPlayerEntity) owner);
            bender.removeAbilityFromBackground(FireElement.get().getAbility(11));
        }
    }

    @Override
    public float getMovementSpeed() {
        return 0.1f;
    }

    @Override
    public boolean emitsLight() {
        return true;
    }

    @Override
    public void onClientRemoval() {
        summonParticles(this, random,
                isBlue() ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME,
                0.25f, 25);
    }

    public boolean isBlue() {
        return this.entityData.get(IS_BLUE);
    }

    public void setIsBlue(boolean val) {
        this.getEntityData().set(IS_BLUE, val);
    }

    @Override
    public float touchGroundFrictionMultiplier() {
        return -1;
    }

    @Override
    public boolean pushesEntitiesAway() {
        return false;
    }
}

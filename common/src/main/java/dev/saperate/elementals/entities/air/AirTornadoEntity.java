package dev.saperate.elementals.entities.air;

import dev.saperate.elementals.data.ElementalConfig;
import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import dev.saperate.elementals.misc.ElementalsSounds;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import static dev.saperate.elementals.entities.ElementalEntities.AIRTORNADO;
import static dev.saperate.elementals.utils.SapsUtils.*;

public class AirTornadoEntity extends AbstractElementalsEntity<Player> {
    private static final EntityDataAccessor<Float> RANGE = SynchedEntityData.defineId(AirTornadoEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(AirTornadoEntity.class, EntityDataSerializers.FLOAT);

    public AirTornadoEntity(EntityType<AirTornadoEntity> type, Level world) {
        super(type, world, Player.class);
    }

    public AirTornadoEntity(Level world, Player owner) {
        this(world, owner, owner.getX(), owner.getY(), owner.getZ());
    }

    public AirTornadoEntity(Level world, Player owner, double x, double y, double z) {
        super(AIRTORNADO.get(), world, Player.class);
        setOwner(owner);
        setPos(x, y, z);
        setNoGravity(true);
        maxLifeTime = 100;
    }


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(RANGE, 20f);
        builder.define(SPEED, 0.001f);
    }

    @Override
    public void tick() {
        super.tick();
        if (random.nextInt(0, 40) == 6) {
            summonParticles(this, random,
                    ParticleTypes.POOF,
                    0, 1);
            playSound(ElementalsSounds.WIND_SOUND_EVENT, 1, (1.0f + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2f) * 0.7f);
        }

        Player owner = getOwner();
        if (owner == null || isRemoved()) {
            return;
        }

        if (isUnderWater()) {
            discard();
            return;
        }

        if (getIsControlled()) {
            moveEntityTowardsGoal(getOwner().pick(getRange(),1,true).getLocation().toVector3f());
            setDeltaMovement(getDeltaMovement().multiply(1,0,1));
        }
        setDeltaMovement(getDeltaMovement().add(0, -0.4, 0));

        this.move(MoverType.SELF, this.getDeltaMovement());

        if (onGround() && level().isClientSide) {
            summonParticles(this, random, new BlockParticleOption(ParticleTypes.BLOCK, level().getBlockState(getOnPos().below())), 0, 5);
        }
    }

    @Override
    public float getMovementSpeed() {
        return getSpeed();
    }

    @Override
    public void onTouchEntity(Entity entity) {
        if(getOwner() == entity){
            return;
        }
        entity.hurt(this.damageSources().playerAttack((Player) getOwner()), 5 * ElementalConfig.get().BENDING_DAMAGE_MULTIPLIER);//TODO maybe add a debris upgrade for more dmg
        entity.addDeltaMovement(new Vec3(0, 0.50f, 0));
        entity.hasImpulse = true;
        entity.move(MoverType.SELF, entity.getDeltaMovement());
    }

    @Override
    public boolean damagesOnTouch() {
        return true;
    }

    @Override
    public int getLifeTimeIncrement() {
        if (getIsControlled()) {
            return 0;
        }
        return 1;
    }

    public float getRange() {
        return getEntityData().get(RANGE);
    }

    public void setRange(float val) {
        this.getEntityData().set(RANGE, val);
    }

    public void setSpeed(float speed) {
        this.entityData.set(SPEED, speed);
    }

    public float getSpeed() {
        return this.entityData.get(SPEED);
    }

    @Override
    public boolean discardsOnNullOwner() {
        return true;
    }
    @Override
    public float maxUpStep() {
        return 2;
    }
}

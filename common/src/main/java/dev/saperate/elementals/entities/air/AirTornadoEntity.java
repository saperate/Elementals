package dev.saperate.elementals.entities.air;

import dev.saperate.elementals.data.ElementalConfig;
import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import dev.saperate.elementals.entities.water.WaterArcEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.data.SynchedEntityData;
import net.minecraft.entity.data.EntityDataAccessor;
import net.minecraft.entity.data.EntityDataSerializers;
import net.minecraft.entity.player.Player;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3;
import net.minecraft.world.Level;
import org.joml.Vector3f;

import static dev.saperate.elementals.Elementals.WIND_SOUND_EVENT;
import static dev.saperate.elementals.entities.ElementalEntities.AIRTORNADO;
import static dev.saperate.elementals.entities.ElementalEntities.WATERJET;
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
        super(AIRTORNADO, world, Player.class);
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
        if (random.nextBetween(0, 40) == 6) {
            summonParticles(this, random,
                    ParticleTypes.POOF,
                    0, 1);
            playSound(WIND_SOUND_EVENT, 1, (1.0f + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2f) * 0.7f);
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
            moveEntityTowardsGoal(getOwner().raycast(getRange(),1,true).getPos().toVector3f());
            setDeltaMovement(getDeltaMovement().multiply(1,0,1));
        }
        setDeltaMovement(getDeltaMovement().add(0, -0.4, 0));

        this.move(MoverType.SELF, this.getDeltaMovement());

        if (isOnGround() && level().isClientSide) {
            summonParticles(this, random, new BlockStateParticleEffect(ParticleTypes.BLOCK, level().getBlockState(getOnPos().down())), 0, 5);
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
        entity.addDeltaMovement(0, 0.50f, 0);
        entity.velocityModified = true;
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
    public float getStepHeight() {
        return 2;
    }
}

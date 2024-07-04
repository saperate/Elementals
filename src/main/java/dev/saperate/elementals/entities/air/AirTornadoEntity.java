package dev.saperate.elementals.entities.air;

import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import dev.saperate.elementals.entities.water.WaterArcEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

import static dev.saperate.elementals.Elementals.WIND_SOUND_EVENT;
import static dev.saperate.elementals.entities.ElementalEntities.AIRTORNADO;
import static dev.saperate.elementals.entities.ElementalEntities.WATERJET;
import static dev.saperate.elementals.utils.SapsUtils.*;

public class AirTornadoEntity extends AbstractElementalsEntity<PlayerEntity> {
    private static final TrackedData<Float> RANGE = DataTracker.registerData(AirTornadoEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> SPEED = DataTracker.registerData(AirTornadoEntity.class, TrackedDataHandlerRegistry.FLOAT);

    public AirTornadoEntity(EntityType<AirTornadoEntity> type, World world) {
        super(type, world, PlayerEntity.class);
    }

    public AirTornadoEntity(World world, PlayerEntity owner) {
        this(world, owner, owner.getX(), owner.getY(), owner.getZ());
    }

    public AirTornadoEntity(World world, PlayerEntity owner, double x, double y, double z) {
        super(AIRTORNADO, world, PlayerEntity.class);
        setOwner(owner);
        setPos(x, y, z);
        setNoGravity(true);
        setStepHeight(2f);
        maxLifeTime = 100;
    }


    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.getDataTracker().startTracking(RANGE, 20f);
        this.getDataTracker().startTracking(SPEED, 0.001f);
    }

    @Override
    public void tick() {
        super.tick();
        if (random.nextBetween(0, 40) == 6) {
            summonParticles(this, random,
                    ParticleTypes.POOF,
                    0, 1);
            playSound(WIND_SOUND_EVENT, 1, (1.0f + (this.getWorld().random.nextFloat() - this.getWorld().random.nextFloat()) * 0.2f) * 0.7f);
        }

        PlayerEntity owner = getOwner();
        if (owner == null || isRemoved()) {
            return;
        }

        if (isSubmergedInWater()) {
            discard();
            return;
        }

        if (getIsControlled()) {
            moveEntityTowardsGoal(getOwner().raycast(getRange(),1,true).getPos().toVector3f());
            setVelocity(getVelocity().multiply(1,0,1));
        }
        setVelocity(getVelocity().add(0, -0.4, 0));

        this.move(MovementType.SELF, this.getVelocity());

        if (isOnGround() && getWorld().isClient) {
            summonParticles(this, random, new BlockStateParticleEffect(ParticleTypes.BLOCK, getWorld().getBlockState(getBlockPos().down())), 0, 3);
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
        entity.damage(this.getDamageSources().playerAttack((PlayerEntity) getOwner()), 5);//TODO maybe add a debris upgrade for more dmg
        entity.addVelocity(0, 0.75f, 0);
        entity.velocityModified = true;
        entity.move(MovementType.SELF, entity.getVelocity());
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
        return getDataTracker().get(RANGE);
    }

    public void setRange(float val) {
        this.getDataTracker().set(RANGE, val);
    }

    public void setSpeed(float speed) {
        this.dataTracker.set(SPEED, speed);
    }

    public float getSpeed() {
        return this.dataTracker.get(SPEED);
    }

    @Override
    public boolean discardsOnNullOwner() {
        return true;
    }
}

package dev.saperate.elementals.entities.water;

import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static dev.saperate.elementals.entities.ElementalEntities.WATERBULLET;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class WaterBulletEntity extends AbstractElementalsEntity {
    private static final TrackedData<Integer> ARRAY_ID = DataTracker.registerData(WaterBulletEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> ARRAY_SIZE = DataTracker.registerData(WaterBulletEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public Vector3f lastCenterPos;


    public WaterBulletEntity(EntityType<WaterBulletEntity> type, World world) {
        super(type, world);
    }

    public WaterBulletEntity(World world, PlayerEntity owner) {
        super(WATERBULLET, world);
        setOwner(owner);
        setPos(owner.getX(), owner.getY(), owner.getZ());
    }

    public WaterBulletEntity(World world, PlayerEntity owner, double x, double y, double z) {
        super(WATERBULLET, world);
        setOwner(owner);
        setPos(x, y, z);
        setControlled(true);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.getDataTracker().startTracking(ARRAY_ID, 0);
        this.getDataTracker().startTracking(ARRAY_SIZE, 1);
    }

    @Override
    public void tick() {
        super.tick();

        PlayerEntity owner = (PlayerEntity) getOwner();
        if (owner == null || isRemoved()) {
            return;
        }

        moveEntity(owner);
    }


    private void moveEntity(Entity owner) {
        if (getIsControlled()) {
            controlEntity(owner);
        }

        this.move(MovementType.SELF, this.getVelocity());
    }

    private void controlEntity(Entity owner) {
        Vector3f direction;
        if (!owner.isSneaking() || lastCenterPos == null) {
            direction = getEntityLookVector(owner, 3)
                    .subtract(0, 1, 0)
                    .subtract(getPos()).toVector3f();
            lastCenterPos = direction;
        } else {
            direction = lastCenterPos;
        }

        double angle = ((2 * Math.PI) / getArraySize()) * getArrayId() + Math.toRadians(age * 2);

        direction = direction.add(
                new Vector3f(0, 1, 0).add(new Vector3f(1, 0, 0).mul((float) Math.sin(angle)).add(new Vector3f(0, 0, 1).mul((float) Math.cos(angle))))
        );

        moveEntityTowardsGoal(direction);
    }

    @Override
    public void collidesWithGround() {
        discard();
    }

    @Override
    public void onHitEntity(Entity entity) {
        entity.damage(this.getDamageSources().playerAttack((PlayerEntity) getOwner()), 2);
        entity.addVelocity(this.getVelocity().multiply(0.8f));
        discard();
    }

    @Override
    public void onRemoved() {
        summonParticles(this, random, ParticleTypes.SPLASH, 10, 100);
    }

    public void setArrayId(int val) {
        this.getDataTracker().set(ARRAY_ID, val);
    }

    public int getArrayId() {
        return this.getDataTracker().get(ARRAY_ID);
    }

    /**
     * @param val The amount of entities in the same batch + 1
     */
    public void setArraySize(int val) {
        this.getDataTracker().set(ARRAY_SIZE, val);
    }

    public int getArraySize() {
        return this.getDataTracker().get(ARRAY_SIZE);
    }

    @Override
    public void setOwner(LivingEntity owner) {
        if(owner instanceof PlayerEntity){
            super.setOwner(owner);
        }
    }


}

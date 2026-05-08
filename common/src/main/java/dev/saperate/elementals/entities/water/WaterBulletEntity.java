package dev.saperate.elementals.entities.water;

import dev.saperate.elementals.data.ElementalConfig;
import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

import static dev.saperate.elementals.entities.ElementalEntities.WATERBULLET;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class WaterBulletEntity extends AbstractElementalsEntity<Player> {
    private static final EntityDataAccessor<Integer> ARRAY_ID = SynchedEntityData.defineId(WaterBulletEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ARRAY_SIZE = SynchedEntityData.defineId(WaterBulletEntity.class, EntityDataSerializers.INT);
    public Vector3f lastCenterPos;


    public WaterBulletEntity(EntityType<WaterBulletEntity> type, Level world) {
        super(type, world, Player.class);
    }

    public WaterBulletEntity(Level world, Player owner) {
        super(WATERBULLET, world, Player.class);
        setOwner(owner);
        setPos(owner.getX(), owner.getY(), owner.getZ());
    }

    public WaterBulletEntity(Level world, Player owner, double x, double y, double z) {
        super(WATERBULLET, world, Player.class);
        setOwner(owner);
        setPos(x, y, z);
        setControlled(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ARRAY_ID, 0);
        builder.define(ARRAY_SIZE, 1);
    }

    @Override
    public void tick() {
        super.tick();

        Player owner = (Player) getOwner();
        if (owner == null || isRemoved()) {
            return;
        }

        moveEntity(owner);
    }


    private void moveEntity(Entity owner) {
        if (getIsControlled()) {
            controlEntity(owner);
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    private void controlEntity(Entity owner) {
        Vector3f direction;
        if (!owner.isCrouching() || lastCenterPos == null) {
            direction = getEntityLookVector(owner, 3)
                    .subtract(0, 1, 0)
                    .subtract(position()).toVector3f();
            lastCenterPos = direction;
        } else {
            direction = lastCenterPos;
        }

        double angle = ((2 * Math.PI) / getArraySize()) * getArrayId() + Math.toRadians(tickCount * 2);

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
        entity.hurt(this.damageSources().playerAttack((Player) getOwner()), 2 * ElementalConfig.get().BENDING_DAMAGE_MULTIPLIER);
        entity.addDeltaMovement(this.getDeltaMovement().scale(0.8f));
        discard();
    }

    @Override
    public void onClientRemoval() {
        summonParticles(this, random, ParticleTypes.SPLASH, 10, 100);
    }

    public void setArrayId(int val) {
        this.getEntityData().set(ARRAY_ID, val);
    }

    public int getArrayId() {
        return this.getEntityData().get(ARRAY_ID);
    }

    /**
     * @param val The amount of entities in the same batch + 1
     */
    public void setArraySize(int val) {
        this.getEntityData().set(ARRAY_SIZE, val);
    }

    public int getArraySize() {
        return this.getEntityData().get(ARRAY_SIZE);
    }



}

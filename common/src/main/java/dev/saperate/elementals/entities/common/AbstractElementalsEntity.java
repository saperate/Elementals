package dev.saperate.elementals.entities.common;

import dev.saperate.elementals.blocks.ElementalsBlocks;
import dev.saperate.elementals.blocks.blockEntities.LitAirBlockEntity;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;


public abstract class AbstractElementalsEntity<OwnerType extends Entity> extends Entity {
    private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(AbstractElementalsEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_CONTROLLED = SynchedEntityData.defineId(AbstractElementalsEntity.class, EntityDataSerializers.BOOLEAN);

    private final Class<OwnerType> ownerClass;
    public int lifeTime = 0, maxLifeTime = -1;

    public AbstractElementalsEntity(EntityType<?> type, Level world, Class<OwnerType> ownerClass) {
        super(type, world);
        this.ownerClass = ownerClass;
        setNoGravity(false);
    }

    @Override
    public void tick() {
        super.tick();

        if(emitsLight()){
            BlockPos pos = getOnPos().above();
            BlockState state = level().getBlockState(pos);
            if (tickCount % 2 == 0 && state.getBlock().equals(ElementalsBlocks.LIT_AIR)
                    && level().getBlockEntity(pos) instanceof LitAirBlockEntity litAirBlockEntity) {
                litAirBlockEntity.resetTimer();
            } else if (state.isAir()) {
                level().setBlockAndUpdate(pos, ElementalsBlocks.LIT_AIR.defaultBlockState());
            }
        }

        if(getDeltaMovement().x == 0 || getDeltaMovement().z == 0 || getDeltaMovement().y == 0){
            addDeltaMovement(new Vec3(0.0001,0.0001,0.0001));
        }

        Entity owner = getOwner();
        if (owner == null) {
            if(discardsOnNullOwner()){
                discard();
                return;
            }
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.02, 0.0));
            this.move(MoverType.SELF, this.getDeltaMovement());
            if (SapsUtils.checkBlockCollision(this, 0.05f, false, true) != null) {
                collidesWithGround();
            }
            return;
        }

        if(!isNoGravity() && !getIsControlled()){
            this.addDeltaMovement(new Vec3(0,-0.02f,0));
        }

        if (projectileDeflectionRange() > 0) {

            List<Projectile> projectiles = level().getEntitiesOfClass(Projectile.class,
                    level().isClientSide ? getBoundingBox().inflate(projectileDeflectionRange()) : getBoundingBox().move(position()).inflate(projectileDeflectionRange()),
                    Projectile::isAlive);

            for (Projectile e : projectiles) {
                Vec3 direction = e.position().add(0,1.7f,0).subtract(position()).scale(0.1f);
                e.setDeltaMovement(getDeltaMovement().add(direction));
            }
        }

        if (maxLifeTime != -1) {
            if (lifeTime >= maxLifeTime) {
                onLifeTimeEnd();
            }
            lifeTime += getLifeTimeIncrement();
        }

        if (damagesOnTouch() && !level().isClientSide) {
            AABB boundingBox = getBoundingBox();

            //if the bounding box doesn't offset by the entity position for some reason,
            // we need to add it manually for collisions to work properly
            if(!SapsUtils.isAboutEquals(boundingBox.getCenter(),getEyePosition(), 4)){
                boundingBox =  boundingBox.move(position());
            }

            List<LivingEntity> entities = level().getEntitiesOfClass(LivingEntity.class,
                    boundingBox.inflate(0.25f),
                    LivingEntity::isAlive);

            for (LivingEntity e : entities) {
                onTouchEntity(e);
            }
        }

        if (!getIsControlled() && !level().isClientSide) {
            HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, entity -> entity instanceof LivingEntity);
            if (hit.getType() == HitResult.Type.ENTITY) {
                onHitEntity(((EntityHitResult) hit).getEntity());
                return;
            } else if (SapsUtils.checkBlockCollision(this, collisionSensitivity(), false) != null) {
                if (getDeltaMovement().lengthSqr() > 0.3) {
                    setDeltaMovement(getDeltaMovement().add(getDeltaMovement().scale(touchGroundFrictionMultiplier())));
                } else {
                    collidesWithGround();
                }
            }

        }

        if(pushesEntitiesAway()){
            this.level().getEntities(EntityTypeTest.forClass(Player.class), this.getBoundingBox(), EntitySelector.pushableBy(this)).forEach(this::pushAway);
        }

    }

    /**
     * The range that will be added to the hit box to detect projectiles.
     * If set to 0 or lower, projectiles will not be deflected
     */
    public float projectileDeflectionRange() {
        return 0;
    }

    public int getLifeTimeIncrement(){
        return 1;
    }

    public float touchGroundFrictionMultiplier(){
        return -0.5f;
    }

    public void onLifeTimeEnd() {
        discard();
    }

    /**
     * This method is called when the entity <b>touches</b> another entity.
     * This may be called when the entity is still controlled.
     * This also includes the owner, so be wary of that.
     * @param entity The entity being touched
     */
    public void onTouchEntity(Entity entity){

    }

    /**
     * This method is called when the entity <b>hits</b> another entity.
     * This may only be called when the entity is no longer controlled.
     * This also includes the owner, so be wary of that.
     * @param entity The entity being hit
     */
    public void onHitEntity(Entity entity){

    }

    public boolean damagesOnTouch() {
        return false;
    }

    public boolean pushesEntitiesAway(){
        return true;
    }

    public void moveEntityTowardsGoal(Vector3f goal) {
       moveEntityTowardsGoal(goal,getMovementSpeed());
    }

    public void moveEntityTowardsGoal(Vector3f goal, float speed) {
        if (teleportsToGoal()) {
            this.setPos(goal.x, goal.y, goal.z);
            return;
        }
        Vector3f direction = goal.sub(0, 0.5f, 0)
                .sub(position().toVector3f())
                .mul(speed);
        this.setDeltaMovement(direction.x, direction.y, direction.z);
    }

    public boolean teleportsToGoal() {
        return false;
    }

    public float getMovementSpeed() {
        return 0.2f;
    }

    public boolean discardsOnNullOwner(){
        return false;
    }

    public boolean emitsLight(){
        return false;
    }

    /**
     * Called when the entity collides with the ground, this is only called if the entity is no longer controlled
     */
    public void collidesWithGround() {

    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(OWNER_ID, 0);
        builder.define(IS_CONTROLLED, false);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {

    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {

    }

    public void setControlled(boolean val) {
        this.getEntityData().set(IS_CONTROLLED, val);
    }

    public boolean getIsControlled() {
        return this.getEntityData().get(IS_CONTROLLED);
    }

    public OwnerType getOwner() {
        Entity owner = this.level().getEntity(this.getEntityData().get(OWNER_ID));
        return (owner != null && ownerClass.isAssignableFrom(owner.getClass())) ? ownerClass.cast(owner) : null;
    }

    public void setOwner(OwnerType owner) {
        this.getEntityData().set(OWNER_ID, owner.getId());
    }

    protected void pushAway(Entity entity) {
        entity.push(this);
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }


    /**
     * @author Mojang
     */
    public void setDeltaMovement(double x, double y, double z, float speed, float divergence) {
        Vec3 vec3d = (new Vec3(x, y, z)).normalize().add(this.random.triangle(0.0, 0.0172275 * (double)divergence), this.random.triangle(0.0, 0.0172275 * (double)divergence), this.random.triangle(0.0, 0.0172275 * (double)divergence)).scale((double)speed);
        this.setDeltaMovement(vec3d);
        double d = vec3d.horizontalDistance();
        this.setYRot((float)(Mth.atan2(vec3d.x, vec3d.z) * 57.2957763671875));
        this.setXRot((float)(Mth.atan2(vec3d.y, d) * 57.2957763671875));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    /**
     * @author Mojang
     */
    public void setDeltaMovement(Entity shooter, float pitch, float yaw, float roll, float speed, float divergence) {
        float f = -Mth.sin(yaw * 0.017453292F) * Mth.cos(pitch * 0.017453292F);
        float g = -Mth.sin((pitch + roll) * 0.017453292F);
        float h = Mth.cos(yaw * 0.017453292F) * Mth.cos(pitch * 0.017453292F);
        this.setDeltaMovement((double)f, (double)g, (double)h, speed, divergence);
        Vec3 vec3d = shooter.getDeltaMovement();
        this.setDeltaMovement(this.getDeltaMovement().add(vec3d.x, shooter.onGround() ? 0.0 : vec3d.y, vec3d.z));
    }

    public float collisionSensitivity(){
        return 0.1f;
    }
}

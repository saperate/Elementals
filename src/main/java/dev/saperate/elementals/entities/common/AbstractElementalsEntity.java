package dev.saperate.elementals.entities.common;

import dev.saperate.elementals.entities.earth.EarthBlockEntity;
import dev.saperate.elementals.utils.SapsUtils;
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
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Interface;

import java.util.List;

public abstract class AbstractElementalsEntity extends Entity {
    private static final TrackedData<Integer> OWNER_ID = DataTracker.registerData(AbstractElementalsEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> IS_CONTROLLED = DataTracker.registerData(AbstractElementalsEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public int lifeTime = 0, maxLifeTime = -1;

    public AbstractElementalsEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public void tick() {
        super.tick();
        Entity owner = getOwner();
        if (owner == null) {
            if(discardsOnNullOwner()){
                discard();
                return;
            }
            this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
            this.move(MovementType.SELF, this.getVelocity());
            if (SapsUtils.checkBlockCollision(this, 0.05f, false, true) != null) {
                collidesWithGround();
            }
            return;
        }

        if (projectileDeflectionRange() > 0) {

            List<ProjectileEntity> projectiles = getWorld().getEntitiesByClass(ProjectileEntity.class,
                    getWorld().isClient ? getBoundingBox().expand(projectileDeflectionRange()) : getBoundingBox().offset(getPos()).expand(projectileDeflectionRange()),
                    ProjectileEntity::isAlive);

            for (ProjectileEntity e : projectiles) {
                Vec3d direction = e.getPos().add(0,1.7f,0).subtract(getPos()).multiply(0.1f);
                e.setVelocity(getVelocity().add(direction));
            }
        }

        if (maxLifeTime != -1) {
            if (lifeTime >= maxLifeTime) {
                onLifeTimeEnd();
            }
            lifeTime += getLifeTimeIncrement();
        }

        if (damagesOnTouch() && !getWorld().isClient) {
            List<LivingEntity> entities = getWorld().getEntitiesByClass(LivingEntity.class,
                    getBoundingBox().expand(0.25f),
                    LivingEntity::isAlive);

            for (LivingEntity e : entities) {
                onTouchEntity(e);
            }
        }

        if (!getIsControlled() && !getWorld().isClient) {
            HitResult hit = ProjectileUtil.getCollision(this, entity -> entity instanceof LivingEntity);
            if (hit.getType() == HitResult.Type.ENTITY) {
                onHitEntity(((EntityHitResult) hit).getEntity());
                return;
            }

            if (SapsUtils.checkBlockCollision(this, 0.05f, false) != null) {
                if (getVelocity().lengthSquared() > 0.3) {
                    setVelocity(getVelocity().add(getVelocity().multiply(touchGroundFrictionMultiplier())));
                } else {
                    collidesWithGround();
                }
            }
        }

        if(pushesEntitiesAway()){
            this.getWorld().getEntitiesByType(TypeFilter.instanceOf(PlayerEntity.class), this.getBoundingBox(), EntityPredicates.canBePushedBy(this)).forEach(this::pushAway);
        }

        if(!hasNoGravity()){
            this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
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
        if (teleportsToGoal()) {
            this.setPos(goal.x, goal.y, goal.z);
            return;
        }
        Vector3f direction = goal.sub(0, 0.5f, 0)
                .sub(getPos().toVector3f())
                .mul(getMovementSpeed());
        this.setVelocity(direction.x, direction.y, direction.z);
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

    public void collidesWithGround() {

    }


    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(OWNER_ID, 0);
        this.getDataTracker().startTracking(IS_CONTROLLED, false);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        int ownerId = nbt.getInt("OwnerID");
        this.getDataTracker().set(OWNER_ID, ownerId);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        if (getOwner() != null) {
            nbt.putInt("OwnerID", this.getOwner().getId());
        }
    }

    public void setControlled(boolean val) {
        this.getDataTracker().set(IS_CONTROLLED, val);
    }

    public boolean getIsControlled() {
        return this.getDataTracker().get(IS_CONTROLLED);
    }

    public LivingEntity getOwner() {
        Entity owner = this.getWorld().getEntityById(this.getDataTracker().get(OWNER_ID));
        return (owner instanceof LivingEntity) ? (LivingEntity) owner : null;
    }

    public void setOwner(LivingEntity owner) {
        this.getDataTracker().set(OWNER_ID, owner.getId());
    }

    protected void pushAway(Entity entity) {
        entity.pushAwayFrom(this);
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Override
    public boolean doesRenderOnFire() {
        return false;
    }

    /**
     * @author Mojang
     */
    public void setVelocity(double x, double y, double z, float speed, float divergence) {
        Vec3d vec3d = (new Vec3d(x, y, z)).normalize().add(this.random.nextTriangular(0.0, 0.0172275 * (double)divergence), this.random.nextTriangular(0.0, 0.0172275 * (double)divergence), this.random.nextTriangular(0.0, 0.0172275 * (double)divergence)).multiply((double)speed);
        this.setVelocity(vec3d);
        double d = vec3d.horizontalLength();
        this.setYaw((float)(MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875));
        this.setPitch((float)(MathHelper.atan2(vec3d.y, d) * 57.2957763671875));
        this.prevYaw = this.getYaw();
        this.prevPitch = this.getPitch();
    }

    /**
     * @author Mojang
     */
    public void setVelocity(Entity shooter, float pitch, float yaw, float roll, float speed, float divergence) {
        float f = -MathHelper.sin(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
        float g = -MathHelper.sin((pitch + roll) * 0.017453292F);
        float h = MathHelper.cos(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
        this.setVelocity((double)f, (double)g, (double)h, speed, divergence);
        Vec3d vec3d = shooter.getVelocity();
        this.setVelocity(this.getVelocity().add(vec3d.x, shooter.isOnGround() ? 0.0 : vec3d.y, vec3d.z));
    }

}

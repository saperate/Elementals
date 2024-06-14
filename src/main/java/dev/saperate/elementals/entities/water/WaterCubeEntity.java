package dev.saperate.elementals.entities.water;

import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.List;

import static dev.saperate.elementals.entities.ElementalEntities.WATERCUBE;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class WaterCubeEntity extends ProjectileEntity {
    private static final TrackedData<Integer> OWNER_ID = DataTracker.registerData(WaterCubeEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> IS_CONTROLLED = DataTracker.registerData(WaterCubeEntity.class, TrackedDataHandlerRegistry.BOOLEAN);


    public WaterCubeEntity(EntityType<WaterCubeEntity> type, World world) {
        super(type, world);
    }

    public WaterCubeEntity(World world, LivingEntity owner) {
        super(WATERCUBE, world);
        setOwner(owner);
        setPos(owner.getX(), owner.getY(), owner.getZ());
    }

    public WaterCubeEntity(World world, LivingEntity owner, double x, double y, double z) {
        super(WATERCUBE, world);
        setOwner(owner);
        setPos(x, y, z);
        setControlled(true);
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(OWNER_ID, 0);
        this.getDataTracker().startTracking(IS_CONTROLLED, false);
    }

    @Override
    public void tick() {
        super.tick();

        if (random.nextBetween(0, 40) == 6) {
            summonParticles(this, random,
                    ParticleTypes.SPLASH,
                    0, 1);
            playSound(SoundEvents.ENTITY_PLAYER_SWIM,0.25f,0);
        }

        Entity owner = getOwner();
        if (owner == null) {
            this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
            this.move(MovementType.SELF, this.getVelocity());
            if (SapsUtils.checkBlockCollision(this,0.1f) != null) {
                collidesWithGround();
            }

            return;
        }

        List<ProjectileEntity> projectiles = getWorld().getEntitiesByClass(ProjectileEntity.class,
                getWorld().isClient ? getBoundingBox().expand(.25f) : getBoundingBox().offset(getPos()).expand(.25f),
                ProjectileEntity::isAlive);

        for (ProjectileEntity e : projectiles) {
            if (!(e instanceof WaterCubeEntity)) {
                e.discard();
            }
        }


        if (!getIsControlled()) {
            HitResult hit = ProjectileUtil.getCollision(this, entity -> entity instanceof LivingEntity);
            if (hit.getType() == HitResult.Type.ENTITY) {
                LivingEntity entity = (LivingEntity) ((EntityHitResult) hit).getEntity();
                entity.damage(this.getDamageSources().playerAttack((PlayerEntity) owner), 2);
                entity.addVelocity(this.getVelocity().multiply(0.8f));
                discard();
            }
        }

        this.getWorld().getEntitiesByType(TypeFilter.instanceOf(PlayerEntity.class), this.getBoundingBox(), EntityPredicates.canBePushedBy(this)).forEach(this::pushAway);
        if (!owner.isSneaking()) {
            moveEntity(owner);
        }

    }

    private void moveEntity(Entity owner) {


        //gravity
        this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));

        if (getIsControlled()) {
            controlEntity(owner);
        } else if (!getWorld().isClient && SapsUtils.checkBlockCollision(this, 0.1f, false) != null) {
            collidesWithGround();
        }

        this.move(MovementType.SELF, this.getVelocity());
    }

    private void controlEntity(Entity owner) {
        Vector3f direction = getEntityLookVector(owner, 3)
                .subtract(0, 0.5f, 0)
                .subtract(getPos()).toVector3f();
        direction.mul(0.1f);

        if (direction.length() < 0.4f) {
            this.setVelocity(0, 0, 0);
        }


        this.addVelocity(direction.x, direction.y, direction.z);
    }

    public void collidesWithGround() {
        if(!getEntityWorld().getRegistryKey().equals(World.NETHER)){
            getWorld().setBlockState(getBlockPos(), Blocks.WATER.getDefaultState());
        }
        discard();
    }

    @Override
    public boolean canHit() {
        return true;
    }
    @Override
    public void onRemoved() {
        summonParticles(this, random, ParticleTypes.SPLASH, 0, 10);
        this.getWorld().playSound(getX(), getY(), getZ(), SoundEvents.ENTITY_PLAYER_SPLASH, SoundCategory.BLOCKS, 0.25f, (1.0f + (this.getWorld().random.nextFloat() - this.getWorld().random.nextFloat()) * 0.2f) * 0.7f, false);

    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        if (getOwner() != null) {
            super.writeCustomDataToNbt(nbt);
            nbt.putInt("OwnerID", this.getOwner().getId());
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        int ownerId = nbt.getInt("OwnerID");
        this.getDataTracker().set(OWNER_ID, ownerId);
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
}

package dev.saperate.elementals.entities.fire;

import dev.saperate.elementals.data.FireExplosion;
import dev.saperate.elementals.utils.SapsUtils;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
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
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class FireBallEntity extends ProjectileEntity {
    private static final TrackedData<Integer> OWNER_ID = DataTracker.registerData(FireBallEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> IS_CONTROLLED = DataTracker.registerData(FireBallEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_BLUE = DataTracker.registerData(FireBallEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public static final EntityType<FireBallEntity> FIREBALL = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "fire_ball"),
            FabricEntityTypeBuilder.<FireBallEntity>create(SpawnGroup.MISC, FireBallEntity::new)
                    .dimensions(EntityDimensions.fixed(1, 1)).build());


    public FireBallEntity(EntityType<FireBallEntity> type, World world) {
        super(type, world);
    }

    public FireBallEntity(World world, LivingEntity owner) {
        super(FIREBALL, world);
        setOwner(owner);
        setPos(owner.getX(), owner.getY(), owner.getZ());
    }

    public FireBallEntity(World world, LivingEntity owner, double x, double y, double z) {
        super(FIREBALL, world);
        setOwner(owner);
        setPos(x, y, z);
        setControlled(true);
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(OWNER_ID, 0);
        this.getDataTracker().startTracking(IS_CONTROLLED, false);
        this.getDataTracker().startTracking(IS_BLUE, false);
    }

    @Override
    public void tick() {
        if (random.nextBetween(0, 20) == 6) {
            summonParticles(this, random,
                    isBlue() ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME,
                    0, 1);
        }

        Entity owner = getOwner();
        if (owner == null) {
            this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
            this.move(MovementType.SELF, this.getVelocity());
            collidesWithGround();
            return;
        }
        if (!getIsControlled()) {
            HitResult hit = ProjectileUtil.getCollision(this, entity -> entity instanceof LivingEntity);
            if (hit.getType() == HitResult.Type.ENTITY) {
                LivingEntity entity = (LivingEntity) ((EntityHitResult) hit).getEntity();
                entity.damage(this.getDamageSources().playerAttack((PlayerEntity) owner), 2);
                entity.addVelocity(this.getVelocity().multiply(0.8f));
                discard();
                return;
            }
            if (getVelocity().length() < 0.1) {
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
        } else {
            onCollision();
        }

        this.move(MovementType.SELF, this.getVelocity());
    }

    private void controlEntity(Entity owner) {
        Vector3f direction = getEntityLookVector(owner, 3)
                .sub(0, 0.5f, 0)
                .sub(getPos().toVector3f());
        direction.mul(0.05f);

        if (direction.length() < 0.4f) {
            this.setVelocity(0, 0, 0);
        }


        this.addVelocity(direction.x, direction.y, direction.z);
    }

    public void onCollision() {
        if(getWorld().isClient){
            this.getWorld().playSound(getX(),getY(),getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0f, (1.0f + (this.getWorld().random.nextFloat() - this.getWorld().random.nextFloat()) * 0.2f) * 0.7f, false);
            return;
        }

        getWorld().setBlockState(getBlockPos(), AbstractFireBlock.getState(getWorld(), getBlockPos()));
        FireExplosion explosion = new FireExplosion(getWorld(), getOwner(), getX(), getY(), getZ(), 2.5f, true, Explosion.DestructionType.KEEP, 6);
        explosion.collectBlocksAndDamageEntities();
        explosion.affectWorld(true);
        discard();

    }

    @Override
    protected void onCollision(HitResult hitResult) {
        System.out.println("collided");
        super.onCollision(hitResult);
    }

    @Override
    protected void onBlockCollision(BlockState state) {

        if (!state.isAir()) {
            System.out.println("collided 2");
        }
        super.onBlockCollision(state);
    }

    @Override
    public void onRemoved() {
        summonParticles(this, random,
                isBlue() ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME,
                0.25f, 25);
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

    public boolean isBlue() {
        return this.dataTracker.get(IS_BLUE);
    }

    public void setIsBlue(boolean val) {
        this.getDataTracker().set(IS_BLUE, val);
    }
}

package dev.saperate.elementals.entities.water;

import dev.saperate.elementals.utils.SapsUtils;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.OperatorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class WaterBulletEntity extends ProjectileEntity {
    private static final TrackedData<Integer> OWNER_ID = DataTracker.registerData(WaterBulletEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> IS_CONTROLLED = DataTracker.registerData(WaterBulletEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> ARRAY_ID = DataTracker.registerData(WaterBulletEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> ARRAY_SIZE = DataTracker.registerData(WaterBulletEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final EntityType<WaterBulletEntity> WATERBULLET = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "water_bullet"),
            FabricEntityTypeBuilder.<WaterBulletEntity>create(SpawnGroup.MISC, WaterBulletEntity::new)
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build());

    public Vector3f lastCenterPos;

    private BlockPos currMiningPos = null;
    private int startMiningAge = -1;


    public WaterBulletEntity(EntityType<WaterBulletEntity> type, World world) {
        super(type, world);
    }

    public WaterBulletEntity(World world, LivingEntity owner) {
        super(WATERBULLET, world);
        setOwner(owner);
        setPos(owner.getX(), owner.getY(), owner.getZ());
    }

    public WaterBulletEntity(World world, LivingEntity owner, double x, double y, double z) {
        super(WATERBULLET, world);
        setOwner(owner);
        setPos(x, y, z);
        setControlled(true);
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(OWNER_ID, 0);
        this.getDataTracker().startTracking(IS_CONTROLLED, false);
        this.getDataTracker().startTracking(ARRAY_ID, 0);
        this.getDataTracker().startTracking(ARRAY_SIZE, 1);
    }

    @Override
    public void tick() {
        super.tick();

        BlockPos blockHit = SapsUtils.checkBlockCollision(this);

        PlayerEntity owner = getOwner();
        if (owner == null) {
            this.setVelocity(this.getVelocity().add(0.0, -0.02, 0.0));
            this.move(MovementType.SELF, this.getVelocity());
            if (blockHit != null) {
                collidesWithGround();
            }
            return;
        }


        if (blockHit != null && !getIsControlled()){
            collidesWithGround();
            return;
        }


        HitResult hit = ProjectileUtil.getCollision(this, entity -> entity instanceof LivingEntity);
        if (hit.getType() == HitResult.Type.ENTITY) {
            LivingEntity entity = (LivingEntity) ((EntityHitResult) hit).getEntity();
            entity.damage(this.getDamageSources().playerAttack(owner), 2);
            if (!getIsControlled()) {
                entity.addVelocity(this.getVelocity().multiply(0.8f));
                discard();
            }
        }

        moveEntity(owner);
    }


    private void moveEntity(Entity owner) {


        //gravity
        this.setVelocity(this.getVelocity().add(0.0, -0.02, 0.0));

        if (getIsControlled()) {
            controlEntity(owner);
        }


        this.move(MovementType.SELF, this.getVelocity());
    }

    private void controlEntity(Entity owner) {
        Vector3f direction;
        if(!owner.isSneaking() || lastCenterPos == null) {
            direction = getEntityLookVector(owner, 3)
                    .subtract(getPos()).toVector3f();
            lastCenterPos = direction;
        }else{
            direction = lastCenterPos;
        }

        double angle =  ((2 * Math.PI) / getArraySize()) * getArrayId()  + Math.toRadians(age * 2);
        double yaw = Math.toRadians(owner.getYaw() + 90);
        double pitch = Math.toRadians(owner.getPitch());

        double dx = -Math.sin(yaw);
        double dy = Math.cos(pitch);
        double dz = Math.cos(yaw);



        direction = direction.add((float) (Math.cos(angle) * dx), (float) (Math.sin(angle) * dy), (float) (Math.cos(angle) * dz));

        direction.mul(0.25f);

        if (direction.length() < 0.6f) {
            this.setVelocity(0, 0, 0);
        }


        this.addVelocity(direction.x, direction.y, direction.z);
    }

    public void collidesWithGround() {
        //getWorld().setBlockState(getBlockPos(), Blocks.WATER.getDefaultState());
        discard();
    }

    @Override
    public void onRemoved() {
        summonParticles(this, random, ParticleTypes.SPLASH, 10, 100);
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

    public PlayerEntity getOwner() {
        Entity owner = this.getWorld().getEntityById(this.getDataTracker().get(OWNER_ID));
        return (owner instanceof PlayerEntity) ? (PlayerEntity) owner : null;
    }

    public void setOwner(LivingEntity owner) {
        this.getDataTracker().set(OWNER_ID, owner.getId());
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


    protected void pushAway(Entity entity) {
        entity.pushAwayFrom(this);
    }


}

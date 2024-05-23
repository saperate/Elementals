package dev.saperate.elementals.entities.air;

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

import static dev.saperate.elementals.entities.ElementalEntities.AIRTORNADO;
import static dev.saperate.elementals.entities.ElementalEntities.WATERJET;
import static dev.saperate.elementals.utils.SapsUtils.*;

public class AirTornadoEntity extends ProjectileEntity {
    //TODO fix entities like this from getting on fire
    private static final TrackedData<Float> RANGE = DataTracker.registerData(AirTornadoEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Integer> OWNER_ID = DataTracker.registerData(AirTornadoEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> IS_CONTROLLED = DataTracker.registerData(AirTornadoEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private int lifeTime = 100; // 5 seconds

    public AirTornadoEntity(EntityType<AirTornadoEntity> type, World world) {
        super(type, world);
    }

    public AirTornadoEntity(World world, LivingEntity owner) {
        this(world, owner, owner.getX(), owner.getY(), owner.getZ());
    }

    public AirTornadoEntity(World world, LivingEntity owner, double x, double y, double z) {
        super(AIRTORNADO, world);
        setOwner(owner);
        setPos(x, y, z);
        setNoGravity(false);
        setStepHeight(2f);

    }


    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(RANGE, 20f);
        this.getDataTracker().startTracking(OWNER_ID, 0);
        this.getDataTracker().startTracking(IS_CONTROLLED, true);
    }

    @Override
    public void tick() {
        super.tick();
        PlayerEntity owner = getOwner();
        if (owner == null || isSubmergedInWater()) {
            discard();
            return;
        }

        damageEntities(owner);
        handleMovements(owner);

        if (isOnGround() && getWorld().isClient) {
            summonParticles(this, random, new BlockStateParticleEffect(ParticleTypes.BLOCK, getWorld().getBlockState(getBlockPos().down())), 0, 3);
        }
    }

    public void damageEntities(PlayerEntity owner) {
        //TODO add config to see if we target owner too
        HitResult hit = ProjectileUtil.getCollision(this, entity -> entity instanceof LivingEntity);
        if (hit.getType() == HitResult.Type.ENTITY) {
            LivingEntity entity = (LivingEntity) ((EntityHitResult) hit).getEntity();
            entity.damage(this.getDamageSources().playerAttack(owner), 0.5f);//TODO maybe add a debris upgrade for more dmg
            entity.addVelocity(0, 0.75, 0);//TODO add upgrade for this
            entity.move(MovementType.SELF, entity.getVelocity());
        }
    }

    public void handleMovements(PlayerEntity owner) {
        setVelocity(getVelocity().add(0, -0.01, 0));

        if (getIsControlled()) {
            HitResult hit = raycastFull(owner, getRange(), true);
            if (hit instanceof BlockHitResult bHit && !getWorld().getBlockState(bHit.getBlockPos()).isAir()
                    || hit instanceof EntityHitResult) {
                goTowardsGoal(hit.getPos());
            }
        } else {
            lifeTime--;
            if (lifeTime <= 0) {
                discard();
                return;
            }
        }

        this.move(MovementType.SELF, this.getVelocity());
    }

    private void goTowardsGoal(Vec3d target) {
        Vector3f direction = target
                .subtract(getPos()).toVector3f();

        if (direction.length() <= 1f) {
            this.setVelocity(0, 0, 0);
        }
        direction.mul(0.001f);


        this.addVelocity(direction.x, direction.y, direction.z);
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


    public PlayerEntity getOwner() {
        Entity owner = this.getWorld().getEntityById(this.getDataTracker().get(OWNER_ID));
        return (owner instanceof PlayerEntity) ? (PlayerEntity) owner : null;
    }

    public void setOwner(LivingEntity owner) {
        this.getDataTracker().set(OWNER_ID, owner.getId());
    }

    public void setControlled(boolean val) {
        this.getDataTracker().set(IS_CONTROLLED, val);
    }

    public boolean getIsControlled() {
        return this.getDataTracker().get(IS_CONTROLLED);
    }


    public float getRange() {
        return getDataTracker().get(RANGE);
    }

    public void setRange(float val) {
        this.getDataTracker().set(RANGE, val);
    }


    protected void pushAway(Entity entity) {
        entity.pushAwayFrom(this);
    }


}

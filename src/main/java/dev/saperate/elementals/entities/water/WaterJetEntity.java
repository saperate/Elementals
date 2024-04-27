package dev.saperate.elementals.entities.water;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.Block;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.*;

public class WaterJetEntity extends ProjectileEntity {
    private static final TrackedData<Float> STREAM_SIZE = DataTracker.registerData(WaterJetEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> RANGE = DataTracker.registerData(WaterJetEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Integer> OWNER_ID = DataTracker.registerData(WaterJetEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> CHILD_ID = DataTracker.registerData(WaterJetEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final EntityType<WaterJetEntity> WATERJET = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "water_jet"),
            FabricEntityTypeBuilder.<WaterJetEntity>create(SpawnGroup.MISC, WaterJetEntity::new)
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build());


    public WaterJetEntity(EntityType<WaterJetEntity> type, World world) {
        super(type, world);
    }

    public WaterJetEntity(World world, LivingEntity owner) {
        this(world, owner, owner.getX(), owner.getY(), owner.getZ());
    }

    public WaterJetEntity(World world, LivingEntity owner, double x, double y, double z) {
        super(WATERJET, world);
        setOwner(owner);
        setPos(x, y, z);
        setNoGravity(false);
    }


    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(STREAM_SIZE, 1f);
        this.getDataTracker().startTracking(RANGE, 10f);
        this.getDataTracker().startTracking(CHILD_ID, 0);
        this.getDataTracker().startTracking(OWNER_ID, 0);
    }

    @Override
    public void tick() {
        super.tick();
        Entity owner = getOwner();
        if (owner == null) {
            discard();
            return;
        }

        if (getChild() != null) {
            setPosition(getEntityLookVector(owner, 0.5f).subtract(0,0.5f,0));
        } else {
            if(getWorld().isClient){
                summonParticles(this, random, ParticleTypes.SPLASH, 10, 20);
                summonParticles(this, random, ParticleTypes.CLOUD, 0, 1);
            }
            HitResult hit = raycastFull(owner, getRange(), true);
            if (hit instanceof BlockHitResult bHit) {
                BlockState bState = getWorld().getBlockState(bHit.getBlockPos());
                Block bBlock = bState.getBlock();
                if (bBlock.equals(Blocks.TALL_GRASS) || bBlock.equals(Blocks.SHORT_GRASS)) {
                    getWorld().setBlockState(bHit.getBlockPos(),Blocks.AIR.getDefaultState());
                }
            } else if (hit instanceof EntityHitResult eHit) {
                Entity victim = eHit.getEntity();
                Vec3d direction = getOwner().getEyePos().subtract(victim.getPos()).normalize().multiply(-0.075f);
                victim.addVelocity(direction);

                victim.damage(getDamageSources().inFire(), 1 * getStreamSize());

            }
            setPosition(hit.getPos());
        }

        this.move(MovementType.SELF, this.getVelocity());
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


    public LivingEntity getOwner() {
        Entity owner = this.getWorld().getEntityById(this.getDataTracker().get(OWNER_ID));
        return (owner instanceof LivingEntity) ? (LivingEntity) owner : null;
    }

    public void setOwner(LivingEntity owner) {
        this.getDataTracker().set(OWNER_ID, owner.getId());
    }

    public WaterJetEntity getChild() {
        Entity child = this.getWorld().getEntityById(this.getDataTracker().get(CHILD_ID));
        return (child instanceof WaterJetEntity) ? (WaterJetEntity) child : null;
    }

    public void setChild(WaterJetEntity child) {
        this.getDataTracker().set(CHILD_ID, child.getId());
    }

    public float getStreamSize() {
        return getDataTracker().get(STREAM_SIZE);
    }

    public void setStreamSize(float val) {
        this.getDataTracker().set(STREAM_SIZE, val);
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

package dev.saperate.elementals.entities.water;

import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static dev.saperate.elementals.entities.ElementalEntities.WATERJET;
import static dev.saperate.elementals.utils.SapsUtils.*;

public class WaterJetEntity extends AbstractElementalsEntity<PlayerEntity> {
    private static final TrackedData<Float> STREAM_SIZE = DataTracker.registerData(WaterJetEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> RANGE = DataTracker.registerData(WaterJetEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Integer> CHILD_ID = DataTracker.registerData(WaterJetEntity.class, TrackedDataHandlerRegistry.INTEGER);


    public WaterJetEntity(EntityType<WaterJetEntity> type, World world) {
        super(type, world, PlayerEntity.class);
    }

    public WaterJetEntity(World world, PlayerEntity owner) {
        this(world, owner, owner.getX(), owner.getY(), owner.getZ());
    }

    public WaterJetEntity(World world, PlayerEntity owner, double x, double y, double z) {
        super(WATERJET, world, PlayerEntity.class);
        setOwner(owner);
        setPos(x, y, z);
        setNoGravity(true);
    }


    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.getDataTracker().startTracking(STREAM_SIZE, 1f);
        this.getDataTracker().startTracking(RANGE, 10f);
        this.getDataTracker().startTracking(CHILD_ID, 0);
    }

    @Override
    public void tick() {
        super.tick();
        if (random.nextBetween(0, 20) == 6) {
            playSound(SoundEvents.ENTITY_PLAYER_SPLASH,0.25f,0);
        }

        Entity owner = getOwner();
        if (owner == null) {
            return;
        }

        if (getChild() != null) {
            setPosition(getEntityLookVector(owner, 0.5f).subtract(0,0.5f,0));
        } else {
            if(getWorld().isClient){
                summonParticles(this, random, ParticleTypes.SPLASH, 0, 10);
                summonParticles(this, random, ParticleTypes.CLOUD, 0, 1);
            }
            HitResult hit = raycastFull(owner, getRange(), true);
            if (hit instanceof BlockHitResult bHit) {
                BlockState bState = getWorld().getBlockState(bHit.getBlockPos());
                Block bBlock = bState.getBlock();
                if (bBlock.equals(Blocks.TALL_GRASS) || bBlock.equals(Blocks.GRASS)) {
                    getWorld().setBlockState(bHit.getBlockPos(),Blocks.AIR.getDefaultState());
                }
            } else if (hit instanceof EntityHitResult eHit) {
                Entity victim = eHit.getEntity();
                Vec3d direction = getOwner().getEyePos().subtract(victim.getPos()).normalize().multiply(-0.075f);
                victim.addVelocity(direction);

                victim.damage(getDamageSources().playerAttack(getOwner()), 1.5f * getStreamSize());

            }
            setPosition(hit.getPos());
        }

        this.move(MovementType.SELF, this.getVelocity());
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

    @Override
    public boolean discardsOnNullOwner() {
        return true;
    }

}

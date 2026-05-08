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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import static dev.saperate.elementals.entities.ElementalEntities.WATERJET;
import static dev.saperate.elementals.utils.SapsUtils.*;
import static net.minecraft.sounds.SoundEvents.*;

public class WaterJetEntity extends AbstractElementalsEntity<Player> {
    private static final EntityDataAccessor<Float> STREAM_SIZE = SynchedEntityData.defineId(WaterJetEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> RANGE = SynchedEntityData.defineId(WaterJetEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> CHILD_ID = SynchedEntityData.defineId(WaterJetEntity.class, EntityDataSerializers.INT);


    public WaterJetEntity(EntityType<WaterJetEntity> type, Level world) {
        super(type, world, Player.class);
    }

    public WaterJetEntity(Level world, Player owner) {
        this(world, owner, owner.getX(), owner.getY(), owner.getZ());
    }

    public WaterJetEntity(Level world, Player owner, double x, double y, double z) {
        super(WATERJET, world, Player.class);
        setOwner(owner);
        setPos(x, y, z);
        setNoGravity(true);
    }


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(STREAM_SIZE, 1f);
        builder.define(RANGE, 10f);
        builder.define(CHILD_ID, 0);
    }

    @Override
    public void tick() {
        super.tick();
        if (random.nextInt(0, 20) == 6) {
            playSound(PLAYER_SPLASH,0.25f,0);
        }

        Entity owner = getOwner();
        if (owner == null) {
            return;
        }

        if (getChild() != null) {
            setPos(getEntityLookVector(owner, 0.5f).subtract(0,0.5f,0));
        } else {
            if(level().isClientSide){
                summonParticles(this, random, ParticleTypes.SPLASH, 0, 10);
                summonParticles(this, random, ParticleTypes.CLOUD, 0, 1);
            }
            HitResult hit = raycastFull(owner, getRange(), true);
            if (hit instanceof BlockHitResult bHit) {
                BlockState bState = level().getBlockState(bHit.getBlockPos());
                Block bBlock = bState.getBlock();
                if (bBlock.equals(Blocks.TALL_GRASS) || bBlock.equals(Blocks.SHORT_GRASS)) {
                    level().setBlockAndUpdate(bHit.getBlockPos(),Blocks.AIR.defaultBlockState());
                }
            } else if (hit instanceof EntityHitResult eHit) {
                Entity victim = eHit.getEntity();
                Vec3 direction = getOwner().getEyePosition().subtract(victim.position()).normalize().scale(-0.075f);
                victim.addDeltaMovement(direction);

                victim.hurt(damageSources().playerAttack(getOwner()), 1.5f * getStreamSize() * ElementalConfig.get().BENDING_DAMAGE_MULTIPLIER);

            }
            setPos(hit.getLocation());
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    public WaterJetEntity getChild() {
        Entity child = this.level().getEntity(this.getEntityData().get(CHILD_ID));
        return (child instanceof WaterJetEntity) ? (WaterJetEntity) child : null;
    }

    public void setChild(WaterJetEntity child) {
        this.getEntityData().set(CHILD_ID, child.getId());
    }

    public float getStreamSize() {
        return getEntityData().get(STREAM_SIZE);
    }

    public void setStreamSize(float val) {
        this.getEntityData().set(STREAM_SIZE, val);
    }

    public float getRange() {
        return getEntityData().get(RANGE);
    }

    public void setRange(float val) {
        this.getEntityData().set(RANGE, val);
    }

    @Override
    public boolean discardsOnNullOwner() {
        return true;
    }

}

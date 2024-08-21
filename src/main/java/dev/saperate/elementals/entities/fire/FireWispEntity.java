package dev.saperate.elementals.entities.fire;

import dev.saperate.elementals.blocks.blockEntities.LitAirBlockEntity;
import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import dev.saperate.elementals.misc.FireExplosion;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

import static dev.saperate.elementals.blocks.LitAir.LIT_AIR;
import static dev.saperate.elementals.entities.ElementalEntities.FIREBALL;
import static dev.saperate.elementals.entities.ElementalEntities.FIREWISP;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class FireWispEntity extends AbstractElementalsEntity<PlayerEntity> {
    private static final TrackedData<Boolean> IS_BLUE = DataTracker.registerData(FireWispEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public FireWispEntity(EntityType<FireWispEntity> type, World world) {
        super(type, world, PlayerEntity.class);
    }

    public FireWispEntity(World world, PlayerEntity owner) {
        super(FIREWISP, world, PlayerEntity.class);
        setOwner(owner);
        setPos(owner.getX(), owner.getY(), owner.getZ());
    }

    public FireWispEntity(World world, PlayerEntity owner, double x, double y, double z) {
        super(FIREWISP, world, PlayerEntity.class);
        setOwner(owner);
        setPos(x, y, z);
        setControlled(true);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.getDataTracker().startTracking(IS_BLUE, false);
    }

    @Override
    public void tick() {
        super.tick();
        if (random.nextBetween(0, 20) == 6) {
            summonParticles(this, random,
                    isBlue() ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME,
                    0, 1);
            playSound(SoundEvents.BLOCK_FIRE_AMBIENT, 1, 0);

        }

        Entity owner = getOwner();
        if(isRemoved()){
            return;
        }

        if (owner == null) {
            discard();
            return;
        }

        if (!owner.isSneaking()) {
            moveEntity();
        }

        BlockPos pos = getBlockPos().up();
        BlockState state = getWorld().getBlockState(pos);
        if (age % 2 == 0 && state.getBlock().equals(LIT_AIR)
                && getWorld().getBlockEntity(pos) instanceof LitAirBlockEntity litAirBlockEntity) {
            litAirBlockEntity.resetTimer();
        } else if (state.isAir()) {
            getWorld().setBlockState(pos, LIT_AIR.getDefaultState());
        }

    }

    private void moveEntity() {
        if (getIsControlled() ) {
            float yaw = getOwner().getHeadYaw() % 360;
            if(yaw < 0){
                yaw = 360 + yaw;
            }
            moveEntityTowardsGoal(getOwner().getEyePos()
                    .add(-.75 * Math.cos(Math.toRadians(yaw)),0.5,-.75 * Math.sin(Math.toRadians(yaw))).toVector3f());
        }else {
            discard();
        }

        this.move(MovementType.SELF, this.getVelocity());
    }

    @Override
    public float getMovementSpeed() {
        return 0.1f;
    }


    @Override
    public void onRemoved() {
        summonParticles(this, random,
                isBlue() ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME,
                0.25f, 25);
    }

    public boolean isBlue() {
        return this.dataTracker.get(IS_BLUE);
    }

    public void setIsBlue(boolean val) {
        this.getDataTracker().set(IS_BLUE, val);
    }

    @Override
    public float touchGroundFrictionMultiplier() {
        return -1;
    }

    @Override
    public boolean pushesEntitiesAway() {
        return false;
    }
}

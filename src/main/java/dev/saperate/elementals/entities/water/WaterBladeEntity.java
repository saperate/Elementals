package dev.saperate.elementals.entities.water;

import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.utils.SapsUtils;
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
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.joml.Vector3f;

import static dev.saperate.elementals.entities.ElementalEntities.WATERBLADE;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class WaterBladeEntity extends ProjectileEntity {
    private static final TrackedData<Integer> OWNER_ID = DataTracker.registerData(WaterBladeEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> DAMAGE = DataTracker.registerData(WaterBladeEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Boolean> IS_CONTROLLED = DataTracker.registerData(WaterBladeEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private BlockPos currMiningPos = null;
    private int startMiningAge = -1;


    public WaterBladeEntity(EntityType<WaterBladeEntity> type, World world) {
        super(type, world);
    }

    public WaterBladeEntity(World world, LivingEntity owner) {
        super(WATERBLADE, world);
        setOwner(owner);
        setPos(owner.getX(), owner.getY(), owner.getZ());
    }

    public WaterBladeEntity(World world, LivingEntity owner, double x, double y, double z) {
        super(WATERBLADE, world);
        setOwner(owner);
        setPos(x, y, z);
        setControlled(true);
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(OWNER_ID, 0);
        this.getDataTracker().startTracking(IS_CONTROLLED, false);
        this.getDataTracker().startTracking(DAMAGE, 1.5f);
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

        BlockPos blockHit = SapsUtils.checkBlockCollision(this, 0.1f, false, false);

        PlayerEntity owner = getOwner();
        if (owner == null) {
            this.setVelocity(this.getVelocity().add(0.0, -0.03, 0.0));
            this.move(MovementType.SELF, this.getVelocity());
            if (blockHit != null) {
                collidesWithGround();
            }
            return;
        }

        HitResult hit = ProjectileUtil.getCollision(this, entity -> entity instanceof LivingEntity);
        if (hit.getType() == HitResult.Type.ENTITY) {
            LivingEntity entity = (LivingEntity) ((EntityHitResult) hit).getEntity();
            entity.damage(this.getDamageSources().playerAttack(owner), getDamage());
            if (!getIsControlled()) {
                entity.addVelocity(this.getVelocity().multiply(0.8f));
                discard();
            }
        }

        moveEntity(owner);

        if (getWorld().isClient) {
            return;
        }

        if (currMiningPos == null || !currMiningPos.equals(blockHit)) {
            if (currMiningPos != null) {
                getWorld().setBlockBreakingInfo(getId(), currMiningPos, (0));
            }
            currMiningPos = blockHit;
            startMiningAge = age;
        }

        if (blockHit != null) {
            if (!getIsControlled()) {
                collidesWithGround();
            } else if (blockHit.getY() == getBlockY()) {
                float progress = calcBlockBreakingDelta(getWorld().getBlockState(blockHit), getWorld(), blockHit)
                        * (age - startMiningAge + 1);
                getWorld().setBlockBreakingInfo(getId(), blockHit, (int) (progress * 10));

                if (progress >= 1) {
                    getWorld().breakBlock(blockHit, true);
                }

                if (age % 5 == 0) {
                    summonParticles(this, random,
                            ParticleTypes.CLOUD,
                            0, 1, 0);
                }
            }
        }
    }

    public float calcBlockBreakingDelta(BlockState state, BlockView world, BlockPos pos) {
        float f = state.getHardness(world, pos);
        if (f == -1.0f) {
            return 0.0f;
        }
        int miningSpeed = 100;
        PlayerData plrData = PlayerData.get(getOwner());
        if(plrData.canUseUpgrade("waterBladeMiningII")){
            miningSpeed = 30;
        } else if(plrData.canUseUpgrade("waterBladeMiningI")){
            miningSpeed = 60;
        }
        return 1 / f / (float) miningSpeed;
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
        float distance = 3;
        if (owner.isSneaking()) {
            distance = 6;
        }
        Vector3f direction = getEntityLookVector(owner, distance)
                .subtract(getPos()).toVector3f();
        direction.mul(0.25f);

        if (direction.length() < 0.6f) {
            this.setVelocity(0, 0, 0);
        }


        this.addVelocity(direction.x, direction.y, direction.z);
    }

    public void collidesWithGround() {
        discard();
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

    public PlayerEntity getOwner() {
        Entity owner = this.getWorld().getEntityById(this.getDataTracker().get(OWNER_ID));
        return (owner instanceof PlayerEntity) ? (PlayerEntity) owner : null;
    }

    public void setOwner(LivingEntity owner) {
        this.getDataTracker().set(OWNER_ID, owner.getId());
    }

    protected void pushAway(Entity entity) {
        entity.pushAwayFrom(this);
    }


    public boolean tryBreakBlock(BlockPos pos) {
        BlockState blockState = getWorld().getBlockState(pos);

        BlockEntity blockEntity = getWorld().getBlockEntity(pos);
        Block block = blockState.getBlock();
        if (block instanceof OperatorBlock) {
            getWorld().updateListeners(pos, blockState, blockState, Block.NOTIFY_ALL);
            return false;
        }
        block.onBreak(getWorld(), pos, blockState, getOwner());
        boolean bl = getWorld().removeBlock(pos, false);
        if (bl) {
            block.onBroken(getWorld(), pos, blockState);
        }

        return true;
    }

    public void setDamage(float val) {
        getDataTracker().set(DAMAGE, val);
    }

    public float getDamage() {
        return getDataTracker().get(DAMAGE);
    }
}

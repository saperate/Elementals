package dev.saperate.elementals.entities.water;

import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
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

public class WaterBladeEntity extends AbstractElementalsEntity<PlayerEntity> {
    private static final TrackedData<Float> DAMAGE = DataTracker.registerData(WaterBladeEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private BlockPos currMiningPos = null;
    private int startMiningAge = -1;


    public WaterBladeEntity(EntityType<WaterBladeEntity> type, World world) {
        super(type, world, PlayerEntity.class);
    }

    public WaterBladeEntity(World world, PlayerEntity owner) {
        super(WATERBLADE, world, PlayerEntity.class);
        setOwner(owner);
        setPos(owner.getX(), owner.getY(), owner.getZ());
    }

    public WaterBladeEntity(World world, PlayerEntity owner, double x, double y, double z) {
        super(WATERBLADE, world, PlayerEntity.class);
        setOwner(owner);
        setPos(x, y, z);
        setControlled(true);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.getDataTracker().startTracking(DAMAGE, 1.5f);
    }

    @Override
    public void tick() {
        super.tick();

        if (random.nextBetween(0, 40) == 6) {
            summonParticles(this, random,
                    ParticleTypes.SPLASH,
                    0, 1);
            playSound(SoundEvents.ENTITY_PLAYER_SWIM, 0.25f, 0);
        }

        BlockPos blockHit = SapsUtils.checkBlockCollision(this, 0.1f, false, false);

        PlayerEntity owner = (PlayerEntity) getOwner();
        if (owner == null || isRemoved()) {
            return;
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
        if (plrData.canUseUpgrade("waterBladeMiningII")) {
            miningSpeed = 30;
        } else if (plrData.canUseUpgrade("waterBladeMiningI")) {
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

    @Override
    public void collidesWithGround() {
        discard();
    }

    @Override
    public void onHitEntity(Entity entity) {
        entity.damage(this.getDamageSources().playerAttack((PlayerEntity) getOwner()), getDamage());
        entity.addVelocity(this.getVelocity().multiply(0.8f));
        discard();
    }

    @Override
    public void onRemoved() {
        summonParticles(this, random, ParticleTypes.SPLASH, 0, 10);
        this.getWorld().playSound(getX(), getY(), getZ(), SoundEvents.ENTITY_PLAYER_SPLASH, SoundCategory.BLOCKS, 0.25f, (1.0f + (this.getWorld().random.nextFloat() - this.getWorld().random.nextFloat()) * 0.2f) * 0.7f, false);

    }

    public void setDamage(float val) {
        getDataTracker().set(DAMAGE, val);
    }

    public float getDamage() {
        return getDataTracker().get(DAMAGE);
    }

}

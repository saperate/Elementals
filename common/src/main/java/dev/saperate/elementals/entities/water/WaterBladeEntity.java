package dev.saperate.elementals.entities.water;



import dev.saperate.elementals.data.ElementalConfig;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import static dev.saperate.elementals.entities.ElementalEntities.WATERBLADE;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class WaterBladeEntity extends AbstractElementalsEntity<Player> {
    private static final EntityDataAccessor<Float> DAMAGE = SynchedEntityData.defineId(WaterBladeEntity.class, EntityDataSerializers.FLOAT);
    private BlockPos currMiningPos = null;
    private int startMiningAge = -1;


    public WaterBladeEntity(EntityType<WaterBladeEntity> type, Level world) {
        super(type, world, Player.class);
    }

    public WaterBladeEntity(Level world, Player owner) {
        super(WATERBLADE, world, Player.class);
        setOwner(owner);
        setPos(owner.getX(), owner.getY(), owner.getZ());
    }

    public WaterBladeEntity(Level world, Player owner, double x, double y, double z) {
        super(WATERBLADE, world, Player.class);
        setOwner(owner);
        setPos(x, y, z);
        setControlled(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DAMAGE, 7.5f);
    }

    @Override
    public void tick() {
        super.tick();

        if (random.nextInt(0, 40) == 6) {
            summonParticles(this, random,
                    ParticleTypes.SPLASH,
                    0, 1);
            playSound(SoundEvents.PLAYER_SWIM, 0.25f, 0);
        }

        BlockPos blockHit = SapsUtils.checkBlockCollision(this, 0.1f, false, false);

        Player owner = (Player) getOwner();
        if (owner == null || isRemoved()) {
            return;
        }


        moveEntity(owner);

        if (level().isClientSide) {
            return;
        }

        if (currMiningPos == null || !currMiningPos.equals(blockHit)) {
            if (currMiningPos != null) {
                level().destroyBlockProgress(getId(), currMiningPos, (0));
            }
            currMiningPos = blockHit;
            startMiningAge = tickCount;
        }

        if (blockHit != null) {
            if (!getIsControlled()) {
                collidesWithGround();
            } else if (blockHit.getY() == getBlockY()) {
                int miningSpeed = 100;
                PlayerData plrData = PlayerData.get(getOwner());
                if (plrData.canUseUpgrade("waterBladeMiningII")) {
                    miningSpeed = 30;
                } else if (plrData.canUseUpgrade("waterBladeMiningI")) {
                    miningSpeed = 60;
                }
                SapsUtils.mineBlock(blockHit, level(), getId(), tickCount, startMiningAge, miningSpeed);

                if (tickCount % 5 == 0) {
                    summonParticles(this, random,
                            ParticleTypes.CLOUD,
                            0, 1, 0);
                }
            }
        }
    }




    private void moveEntity(Entity owner) {


        //gravity
        this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.02, 0.0));

        if (getIsControlled()) {
            controlEntity(owner);
        }


        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    private void controlEntity(Entity owner) {
        float distance = 3;
        if (owner.isCrouching()) {
            distance = 6;
        }
        Vector3f direction = getEntityLookVector(owner, distance)
                .subtract(position()).toVector3f();
        direction.mul(0.25f);

        if (direction.length() < 0.6f) {
            this.setDeltaMovement(0, 0, 0);
        }


        this.addDeltaMovement(new Vec3(direction.x, direction.y, direction.z));
    }

    @Override
    public void collidesWithGround() {
        discard();
    }

    @Override
    public void onHitEntity(Entity entity) {
        entity.hurt(this.damageSources().playerAttack(getOwner()), getDamage() * ElementalConfig.get().BENDING_DAMAGE_MULTIPLIER);
        entity.addDeltaMovement(this.getDeltaMovement().scale(0.8f));
        discard();
    }

    @Override
    public void onTouchEntity(Entity entity) {
        if(tickCount % 10 == 0){
            entity.hurt(this.damageSources().playerAttack(getOwner()), getDamage() / 5 * ElementalConfig.get().BENDING_DAMAGE_MULTIPLIER);
        }

    }

    @Override
    public void onClientRemoval() {
        summonParticles(this, random, ParticleTypes.SPLASH, 0, 10);
        this.level().playSound(this, getOnPos(), SoundEvents.PLAYER_SPLASH, SoundSource.BLOCKS, 0.25f, (1.0f + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2f) * 0.7f);

    }

    public void setDamage(float val) {
        getEntityData().set(DAMAGE, val);
    }

    public float getDamage() {
        return getEntityData().get(DAMAGE);
    }

    @Override
    public boolean damagesOnTouch() {
        return true;
    }
}

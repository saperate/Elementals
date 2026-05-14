package dev.saperate.elementals.entities.fire;

import dev.saperate.elementals.data.ElementalConfig;
import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import static dev.saperate.elementals.entities.ElementalEntities.FIRESHIELD;

public class FireShieldEntity extends AbstractElementalsEntity<Player> {
    public static final int MAX_FLAME_SIZE = 3;
    private static final EntityDataAccessor<Float> FINAL_HEIGHT = SynchedEntityData.defineId(FireShieldEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> HEIGHT = SynchedEntityData.defineId(FireShieldEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> IS_BLUE = SynchedEntityData.defineId(FireShieldEntity.class, EntityDataSerializers.BOOLEAN);
    public float prevFlameSize = 0;
    public int heightAdjustSpeed = 10;//Smaller is faster

    public FireShieldEntity(EntityType<FireShieldEntity> type, Level world) {
        super(type, world, Player.class);
    }

    public FireShieldEntity(Level world, Player owner) {
        this(world, owner, owner.getX(), owner.getY(), owner.getZ());
    }

    public FireShieldEntity(Level world, Player owner, double x, double y, double z) {
        super(FIRESHIELD.get(), world, Player.class);
        setPos(x, y, z);
        setFireHeight(MAX_FLAME_SIZE);
        setOwner(owner);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HEIGHT, 0.1f);
        builder.define(FINAL_HEIGHT, 3f);
        builder.define(IS_BLUE, false);
    }

    @Override
    public void tick() {
        super.tick();

        if (random.nextInt(0, 20) == 6) {
            playSound(SoundEvents.FIRE_AMBIENT, 1, 0);
        }

        Entity owner = getOwner();
        if (owner == null || isRemoved() || owner.isRemoved()) {
            return;
        }

        moveEntityTowardsGoal(owner.position().toVector3f());


        float diff = (getFireHeight() - prevFlameSize) / heightAdjustSpeed;
        prevFlameSize += diff;
        if (prevFlameSize > MAX_FLAME_SIZE - 1) {
            setFireHeight(getFinalFireHeight());
            heightAdjustSpeed = 5;
        }
    }

    @Override
    public boolean emitsLight() {
        return true;
    }

    @Override
    public void onTouchEntity(Entity entity) {
        if (entity.getY() - getY() < getFireHeight() + 1
                && Math.abs(entity.position().subtract(position()).length()) > 2) {
            if (!entity.fireImmune()) {
                entity.igniteForSeconds(8);
                float damage = isBlue() ? 2.5f : 1.5f;
                if(SapsUtils.isBeingRainedOn(this)){
                    damage /= 2;
                }
                entity.hurt(damageSources().inFire(), damage * ElementalConfig.get().BENDING_DAMAGE_MULTIPLIER);
            }

            Vec3 direction = entity.position().add(0, 1.5f, 0).subtract(position()).scale(0.1f);
            entity.setDeltaMovement(getDeltaMovement().add(direction));
        }
    }

    @Override
    public boolean discardsOnNullOwner() {
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }


    public float getFireHeight() {
        return this.entityData.get(HEIGHT);
    }

    public void setFireHeight(float h) {
        this.getEntityData().set(HEIGHT, h);
    }

    public boolean isBlue() {
        return this.entityData.get(IS_BLUE);
    }

    public void setIsBlue(boolean val) {
        this.getEntityData().set(IS_BLUE, val);
    }


    @Override
    public boolean isPickable() {
        return true;
    }


    public float getFinalFireHeight() {
        return this.entityData.get(FINAL_HEIGHT);
    }

    public void setFinalFireHeight(float h) {
        this.getEntityData().set(FINAL_HEIGHT, h);
    }

    @Override
    public boolean teleportsToGoal() {
        return true;
    }

    @Override
    public float projectileDeflectionRange() {
        return 1.5f;
    }

    @Override
    public boolean damagesOnTouch() {
        return true;
    }
}

package dev.saperate.elementals.entities.fire;

import dev.saperate.elementals.data.ElementalConfig;
import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

import java.util.List;

import static dev.saperate.elementals.entities.ElementalEntities.FIREBLOCK;

public class FireBlockEntity extends AbstractElementalsEntity<Player> {
    public static final int MAX_FLAME_SIZE = 5;
    private static final EntityDataAccessor<Float> FINAL_HEIGHT = SynchedEntityData.defineId(FireBlockEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> HEIGHT = SynchedEntityData.defineId(FireBlockEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> IS_BLUE = SynchedEntityData.defineId(FireBlockEntity.class, EntityDataSerializers.BOOLEAN);
    public float prevFlameSize = 0;
    public int heightAdjustSpeed = 10;//Smaller is faster


    public FireBlockEntity(EntityType<FireBlockEntity> type, Level world) {
        super(type, world, Player.class);
    }

    public FireBlockEntity(Level world, Player owner) {
        this(world, owner, owner.getX(), owner.getY(), owner.getZ());
    }

    public FireBlockEntity(Level world, Player owner, double x, double y, double z) {
        super(FIREBLOCK, world, Player.class);
        setOwner(owner);
        setPos(x, y, z);
        lifeTime = 200;
        setFireHeight(MAX_FLAME_SIZE);
    }


    @Override
    public boolean hurt(DamageSource source, float amount) {
        Entity entity = source.getDirectEntity();
        if (entity instanceof Projectile) {
            entity.discard();
        }
        return super.hurt(source, amount);

    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(HEIGHT, 1f);
        builder.define(FINAL_HEIGHT, 1.5f);
        builder.define(IS_BLUE, false);
        super.defineSynchedData(builder);
    }

    @Override
    public void tick() {
        super.tick();
        if (random.nextInt(0, 20) == 6) {
            playSound(SoundEvents.FIRE_AMBIENT, 1, 0);
        }

        lifeTime--;
        if (lifeTime <= 0 && !level().isClientSide) {
            discard();
            return;
        }
        float diff = (getFireHeight() - prevFlameSize) / heightAdjustSpeed;
        prevFlameSize += diff;
        if (prevFlameSize > MAX_FLAME_SIZE - 1) {
            setFireHeight(getFinalFireHeight());
            heightAdjustSpeed = 5;
        }

        if (isInWater()) {
            discard();
        }
    }

    @Override
    public void onTouchEntity(Entity entity) {
        if (!entity.fireImmune() && entity.getY() - getY() < getFireHeight()) {
            entity.igniteForSeconds(8);
            float damage = isBlue() ? 2.5f : 1.5f;
            if(SapsUtils.isBeingRainedOn(this)){
                damage /= 2;
            }
            entity.hurt(damageSources().inFire(), damage * ElementalConfig.get().BENDING_DAMAGE_MULTIPLIER);
        }
    }

    @Override
    public float projectileDeflectionRange() {
        return 1;
    }

    @Override
    public boolean emitsLight() {
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
    public boolean damagesOnTouch() {
        return true;
    }

    @Override
    public boolean pushesEntitiesAway() {
        return false;
    }

}

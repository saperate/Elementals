package dev.saperate.elementals.entities.fire;

import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import static dev.saperate.elementals.entities.ElementalEntities.FIREWHIP;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

/**
 * Purely cosmetic "whip crack" - a short-lived 3D slash that appears in front of the
 * player for a few ticks to sell the FireWhip animation. Damage for FireWhip is still
 * applied instantly by the ability itself (a fixed cone check); this entity never hits
 * or damages anything, it's just the visual artifice for the "cut" effect.
 */
public class FireWhipEntity extends AbstractElementalsEntity<Player> {
    private static final EntityDataAccessor<Boolean> IS_BLUE = SynchedEntityData.defineId(FireWhipEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> YAW = SynchedEntityData.defineId(FireWhipEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> PITCH = SynchedEntityData.defineId(FireWhipEntity.class, EntityDataSerializers.FLOAT);

    public FireWhipEntity(EntityType<FireWhipEntity> type, Level world) {
        super(type, world, Player.class);
    }

    public FireWhipEntity(Level world, Player owner, double x, double y, double z, float yaw, float pitch, boolean isBlue) {
        super(FIREWHIP.get(), world, Player.class);
        setOwner(owner);
        setPos(x, y, z);
        setNoGravity(true);
        setControlled(false);
        setIsBlue(isBlue);
        getEntityData().set(YAW, yaw);
        getEntityData().set(PITCH, pitch);
        maxLifeTime = 6;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_BLUE, false);
        builder.define(YAW, 0f);
        builder.define(PITCH, 0f);
    }

    @Override
    public void tick() {
        super.tick();
        summonParticles(this, random,
                isBlue() ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME,
                0.02f, 3);
    }

    @Override
    public boolean damagesOnTouch() {
        return false;
    }

    @Override
    public boolean pushesEntitiesAway() {
        return false;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean discardsOnNullOwner() {
        return true;
    }

    public boolean isBlue() {
        return getEntityData().get(IS_BLUE);
    }

    public void setIsBlue(boolean val) {
        getEntityData().set(IS_BLUE, val);
    }

    public float getSlashYaw() {
        return getEntityData().get(YAW);
    }

    public float getSlashPitch() {
        return getEntityData().get(PITCH);
    }
}
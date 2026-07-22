package dev.saperate.elementals.client.entities.air;

import dev.saperate.elementals.data.ElementalConfig;
import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;

import static dev.saperate.elementals.entities.ElementalEntities.AIRBLADE;
import static dev.saperate.elementals.misc.ElementalsSounds.WIND_BURST_SOUND_EVENT;
import static dev.saperate.elementals.misc.ElementalsSounds.WIND_SOUND_EVENT;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

/**
 * A real, physical "cut of air" - a thin horizontal blade fired forward in a straight
 * line. Unlike a normal projectile it doesn't stop on the first thing it hits: it slices
 * through every living entity in its path (piercing) before dissipating at max range or
 * on hitting a block, like a slashing blade rather than a single-target bullet.
 */
public class AirBladeEntity extends AbstractElementalsEntity<Player> {
    private static final EntityDataAccessor<Float> DAMAGE = SynchedEntityData.defineId(AirBladeEntity.class, EntityDataSerializers.FLOAT);
    private final Set<Integer> alreadyHit = new HashSet<>();

    public AirBladeEntity(EntityType<AirBladeEntity> type, Level world) {
        super(type, world, Player.class);
    }

    public AirBladeEntity(Level world, Player owner, double x, double y, double z) {
        super(AIRBLADE.get(), world, Player.class);
        setOwner(owner);
        setPos(x, y, z);
        setNoGravity(true);
        setControlled(false);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DAMAGE, 3f);
    }

    /**
     * Aims and launches the blade in a straight line, matching the caster's look direction.
     */
    public void launch(Vec3 direction, float speed) {
        Vec3 dir = direction.normalize();
        setDeltaMovement(dir.scale(speed));
        double horizontalDistance = dir.horizontalDistance();
        setYRot((float) (Math.toDegrees(Math.atan2(dir.x, dir.z))));
        setXRot((float) (Math.toDegrees(Math.atan2(dir.y, horizontalDistance))));
        yRotO = getYRot();
        xRotO = getXRot();
    }

    @Override
    public void tick() {
        //continuous, concentrated trail of white/cloud particles behind the blade so it
        //visually "cuts" the air as it travels, instead of relying only on the 3D model
        summonParticles(this, random, ParticleTypes.CLOUD, 0.01f, 2);
        if (random.nextInt(0, 6) == 0) {
            playSound(WIND_SOUND_EVENT, 0.15f, 1.4f + (random.nextFloat() * 0.2f));
        }

        //this calls the base class's own hit-detection every tick (onHitEntity is guarded below
        //against re-hitting the same target or the owner, since the blade pierces through targets
        //instead of stopping on the first one)
        super.tick();

        move(MoverType.SELF, getDeltaMovement());
    }

    @Override
    public void collidesWithGround() {
        discard();
    }

    @Override
    public void onHitEntity(Entity entity) {
        if (entity == getOwner() || !alreadyHit.add(entity.getId())) {
            //already sliced this target, or it's the caster - piercing blade, not a boomerang
            return;
        }
        entity.hurt(this.damageSources().playerAttack(getOwner()), getDamage() * ElementalConfig.get().BENDING_DAMAGE_MULTIPLIER);
        //light shove along the blade's direction of travel, precise rather than a big knockback
        entity.addDeltaMovement(this.getDeltaMovement().scale(0.4));
        entity.hurtMarked = true;
    }

    @Override
    public boolean discardsOnNullOwner() {
        return true;
    }

    @Override
    public void onClientRemoval() {
        summonParticles(this, random, ParticleTypes.CLOUD, 0.05f, 12);
        this.level().playSound(this, getOnPos(), WIND_BURST_SOUND_EVENT, SoundSource.BLOCKS, 0.2f,
                (1.0f + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2f) * 0.9f);
    }

    public float getDamage() {
        return getEntityData().get(DAMAGE);
    }

    public void setDamage(float val) {
        getEntityData().set(DAMAGE, val);
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean pushesEntitiesAway() {
        return false;
    }
}
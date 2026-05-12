package dev.saperate.elementals.entities.water;

import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import static dev.saperate.elementals.entities.ElementalEntities.WATERHELMET;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

/**
 * <b>IMPORTANT NOTICE</b> this entity also handles air bending's suffocate. To modify the air suffocate model,
 * go to the water helmet renderer
 */
public class WaterHelmetEntity extends AbstractElementalsEntity<LivingEntity> {
    private static final EntityDataAccessor<Integer> RANGE = SynchedEntityData.defineId(WaterHelmetEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CASTER_ID = SynchedEntityData.defineId(WaterHelmetEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> STEALTHY = SynchedEntityData.defineId(WaterHelmetEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> MODEL_ID = SynchedEntityData.defineId(WaterHelmetEntity.class, EntityDataSerializers.INT);

    public boolean isOwnerBiped = false, suffocate = false;

    public WaterHelmetEntity(EntityType<WaterHelmetEntity> type, Level world) {
        super(type, world, LivingEntity.class);
    }

    public WaterHelmetEntity(Level world, LivingEntity owner) {
        this(world, owner, owner.getX(), owner.getY(), owner.getZ());
    }

    public WaterHelmetEntity(Level world, LivingEntity owner, double x, double y, double z) {
        this(world, owner, x, y, z, false);
    }

    public WaterHelmetEntity(Level world, LivingEntity owner, double x, double y, double z, boolean suffocate) {
        super(WATERHELMET, world, LivingEntity.class);
        setPos(x, owner.getEyeY(), z);
        setOwner(owner);
        this.suffocate = suffocate;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CASTER_ID, 0);
        builder.define(MODEL_ID, 0);
        builder.define(STEALTHY, false);
        builder.define(RANGE, 10);

    }

    @Override
    public void tick() {
        super.tick();

        LivingEntity owner = getOwner();
        if (owner == null || isRemoved() || owner.isRemoved()) {
            return;
        }

        isOwnerBiped = (owner.getBbWidth() / owner.getBbHeight() < 0.4 || (owner instanceof Player && !owner.isSprinting())) && !owner.hasEffect(MobEffects.INVISIBILITY);

        if (!isOwnerBiped && level().isClientSide) {
            summonParticles(owner, this.random, getModelId() == 0 ? ParticleTypes.SPLASH : ParticleTypes.POOF, 0, 10);
        }

        if (owner.isOnFire() ||
                ((maxLifeTime == -1 && !owner.isUnderWater())
                && !suffocate && !this.level().isClientSide)) {
            discard();
            return;
        }


        // if you change any part of this check the renderer because it also modifies the entity pos
        Vec3 eyePos = getOwner().getEyePosition();
        moveEntityTowardsGoal(new Vector3f((float) eyePos.x, (float) (eyePos.y - 0.5), (float) eyePos.z));

        if (!suffocate) {
            if (isStealthy() && owner.isCrouching() && owner.getDeltaMovement().lengthSqr() <= 0.5) {
                owner.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 10, 1, false, false, false));
            }
            owner.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 10, 1, false, false, false));
        } else {
            int range = getRange();
            LivingEntity caster = getCaster();
            if (caster == null) {
                return;
            }
            owner.addEffect(new MobEffectInstance(ElementalsStatusEffects.DROWNING.get(), 10, 1, false, false, false));
            Vec3 direction = caster.position().subtract(owner.position());
            double distance = direction.length();
            if (distance > range) {
                direction = direction.scale(distance - range).scale(0.1f);


                double damping = 0.1f + (0.3f - 0.1f) * (1 - Math.min(1, distance / range));
                direction = direction.scale(damping);

                owner.addDeltaMovement(new Vec3(direction.x, direction.y, direction.z));

                owner.move(MoverType.SELF, owner.getDeltaMovement());
            }
        }
    }

    @Override
    public void onClientRemoval() {
        super.onClientRemoval();
    }

    public LivingEntity getCaster() {
        Entity owner = this.level().getEntity(this.getEntityData().get(CASTER_ID));
        return (owner instanceof LivingEntity) ? (LivingEntity) owner : null;
    }

    public void setCaster(LivingEntity caster) {
        this.getEntityData().set(CASTER_ID, caster.getId());
    }

    public int getModelId() {
        return this.getEntityData().get(MODEL_ID);
    }

    public void setModelId(int id) {
        this.getEntityData().set(MODEL_ID, id);
    }

    public boolean isOwnerBiped() {
        return isOwnerBiped;
    }

    public boolean isStealthy() {
        return this.getEntityData().get(STEALTHY);
    }

    public void setStealthy(boolean val) {
        this.getEntityData().set(STEALTHY, val);
    }

    public int getRange() {
        return this.getEntityData().get(RANGE);
    }

    public void setRange(int val) {
        this.getEntityData().set(RANGE, val);
    }

    @Override
    public boolean pushesEntitiesAway() {
        return false;
    }

    @Override
    public boolean discardsOnNullOwner() {
        return true;
    }

    @Override
    public int getLifeTimeIncrement() {
        if (suffocate) {
            return 0;
        } else if (!getOwner().isUnderWater()) {
            return 50;
        } else {
            return 1;
        }
    }
}

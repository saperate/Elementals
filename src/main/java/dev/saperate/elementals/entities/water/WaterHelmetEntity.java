package dev.saperate.elementals.entities.water;

import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

import static dev.saperate.elementals.effects.DrowningStatusEffect.DROWNING_EFFECT;
import static dev.saperate.elementals.entities.ElementalEntities.WATERHELMET;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

/**
 * <b>IMPORTANT NOTICE</b> this entity also handles air bending's suffocate. To modify the air suffocate model,
 * go to the water helmet renderer
 */
public class WaterHelmetEntity extends AbstractElementalsEntity {
    private static final TrackedData<Integer> RANGE = DataTracker.registerData(WaterHelmetEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> CASTER_ID = DataTracker.registerData(WaterHelmetEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> STEALTHY = DataTracker.registerData(WaterHelmetEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> MODEL_ID = DataTracker.registerData(WaterHelmetEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public boolean isOwnerBiped = false, suffocate = false;

    public WaterHelmetEntity(EntityType<WaterHelmetEntity> type, World world) {
        super(type, world);
    }

    public WaterHelmetEntity(World world, LivingEntity owner) {
        this(world, owner, owner.getX(), owner.getY(), owner.getZ());
    }

    public WaterHelmetEntity(World world, LivingEntity owner, double x, double y, double z) {
        this(world, owner, x, y, z, false);
    }

    public WaterHelmetEntity(World world, LivingEntity owner, double x, double y, double z, boolean drown) {
        super(WATERHELMET, world);
        setPos(x, owner.getEyeY(), z);
        setOwner(owner);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.getDataTracker().startTracking(CASTER_ID, 0);
        this.getDataTracker().startTracking(MODEL_ID, 0);
        this.getDataTracker().startTracking(STEALTHY, false);
        this.getDataTracker().startTracking(RANGE, 10);

    }

    @Override
    public void tick() {
        super.tick();

        LivingEntity owner = getOwner();
        if (owner.isRemoved()) {
            return;
        }
        if (owner.isOnFire() || (maxLifeTime == -1 && !owner.isSubmergedInWater())) {
            discard();
            return;
        }


        isOwnerBiped = (owner.getWidth() / owner.getHeight() < 0.4 || (owner instanceof PlayerEntity && !owner.isSprinting())) && !owner.hasStatusEffect(StatusEffects.INVISIBILITY);

        if (!isOwnerBiped && getWorld().isClient) {
            summonParticles(owner, this.random, getModelId() == 0 ? ParticleTypes.SPLASH : ParticleTypes.POOF, 0, 10);
        }

        // if you change any part of this check the renderer because it also modifies the entity pos
        Vec3d eyePos = owner.getEyePos();
        moveEntityTowardsGoal(new Vector3f((float) eyePos.x, (float) (eyePos.y - 0.5), (float) eyePos.z));

        if (!suffocate) {
            if (isStealthy() && owner.isSneaking() && owner.getVelocity().lengthSquared() <= 0.5) {
                owner.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 10, 1, false, false, false));
            }
            owner.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 10, 1, false, false, false));
        } else {
            int range = getRange();
            LivingEntity caster = getCaster();
            if (caster == null) {
                return;
            }
            owner.addStatusEffect(new StatusEffectInstance(DROWNING_EFFECT, 10, 1, false, false, false));
            Vec3d direction = caster.getPos().subtract(owner.getPos());
            double distance = direction.length();
            if (distance > range) {
                direction = direction.multiply(distance - range).multiply(0.1f);


                double damping = 0.1f + (0.3f - 0.1f) * (1 - Math.min(1, distance / range));
                direction = direction.multiply(damping);

                owner.addVelocity(direction.x, direction.y, direction.z);

                owner.move(MovementType.SELF, owner.getVelocity());
            }
        }
    }


    public LivingEntity getCaster() {
        Entity owner = this.getWorld().getEntityById(this.getDataTracker().get(CASTER_ID));
        return (owner instanceof LivingEntity) ? (LivingEntity) owner : null;
    }

    public void setCaster(LivingEntity caster) {
        this.getDataTracker().set(CASTER_ID, caster.getId());
    }

    public int getModelId() {
        return this.getDataTracker().get(MODEL_ID);
    }

    public void setModelId(int id) {
        this.getDataTracker().set(MODEL_ID, id);
    }

    public boolean isOwnerBiped() {
        return isOwnerBiped;
    }

    public boolean isStealthy() {
        return this.getDataTracker().get(STEALTHY);
    }

    public void setStealthy(boolean val) {
        this.getDataTracker().set(STEALTHY, val);
    }

    public int getRange() {
        return this.getDataTracker().get(RANGE);
    }

    public void setRange(int val) {
        this.getDataTracker().set(RANGE, val);
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
        } else if (!getOwner().isSubmergedInWater()) {
            return 50;
        } else {
            return 1;
        }
    }
}

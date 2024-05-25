package dev.saperate.elementals.entities.water;

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

import static dev.saperate.elementals.effects.DrowningStatusEffect.DROWNING_EFFECT;
import static dev.saperate.elementals.entities.ElementalEntities.WATERHELMET;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

/**
 * <b>IMPORTANT NOTICE</b> this entity also handles air bending's suffocate. To modify the air suffocate model,
 * go to the water helmet renderer
 * @see WaterHelmetEntityRenderer
 */
public class WaterHelmetEntity extends Entity {
    public boolean isOwnerBiped = false;
    private static final TrackedData<Integer> CASTER_ID = DataTracker.registerData(WaterHelmetEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> OWNER_ID = DataTracker.registerData(WaterHelmetEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> DROWN = DataTracker.registerData(WaterHelmetEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> MODEL_ID = DataTracker.registerData(WaterHelmetEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public int aliveTicks = 0, maxLifeTime = 1000;



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
        setDrown(drown);
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(CASTER_ID, 0);
        this.getDataTracker().startTracking(OWNER_ID, 0);
        this.getDataTracker().startTracking(DROWN, false);
        this.getDataTracker().startTracking(MODEL_ID, 0);

    }

    @Override
    public void tick() {
        super.tick();
        boolean drown = getDrown();
        if(!drown){
            aliveTicks++;
        }


        LivingEntity owner = getOwner();
        if (owner == null || aliveTicks > maxLifeTime || owner.isOnFire()) {
            discard();
            return;
        }
        if (!owner.isSubmergedInWater() && !drown) {
            aliveTicks += 50;
        }

        isOwnerBiped = owner.getWidth() / owner.getHeight() < 0.4 || owner instanceof PlayerEntity;

        if(!isOwnerBiped && getWorld().isClient){
            summonParticles(owner,this.random, getModelId() == 0 ? ParticleTypes.SPLASH : ParticleTypes.WHITE_SMOKE,0,10);
        }



        Vec3d eyePos = owner.getEyePos();
        setPos(eyePos.x, eyePos.y - 0.5f, eyePos.z); // if you change any part of this check the renderer because it also modifies to entity pos


        if (!drown) {
            owner.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 10, 1, false, false, false));
        } else {
            LivingEntity caster = getCaster();
            if(caster == null){
                return;
            }
            owner.addStatusEffect(new StatusEffectInstance(DROWNING_EFFECT, 10, 1, false, false, false));
            Vec3d direction = caster.getPos().subtract(owner.getPos());
            double distance = direction.length();
            if (distance > 10) {
                direction = direction.multiply(distance - 10).multiply(0.1f);


                double damping = 0.1f + (0.3f - 0.1f) * (1 - Math.min(1, distance / 10));
                direction = direction.multiply(damping);

                owner.addVelocity(direction.x, direction.y, direction.z);

                owner.move(MovementType.SELF, owner.getVelocity());
            }
        }
    }


    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }


    public LivingEntity getOwner() {
        Entity owner = this.getWorld().getEntityById(this.getDataTracker().get(OWNER_ID));
        return (owner instanceof LivingEntity) ? (LivingEntity) owner : null;
    }

    public void setOwner(LivingEntity owner) {
        this.getDataTracker().set(OWNER_ID, owner.getId());
    }

    public LivingEntity getCaster() {
        Entity owner = this.getWorld().getEntityById(this.getDataTracker().get(CASTER_ID));
        return (owner instanceof LivingEntity) ? (LivingEntity) owner : null;
    }

    public void setCaster(LivingEntity caster) {
        this.getDataTracker().set(CASTER_ID, caster.getId());
    }


    public boolean getDrown() {
        return this.getDataTracker().get(DROWN);
    }

    public void setDrown(boolean drown) {
        this.getDataTracker().set(DROWN, drown);
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
}

package dev.saperate.elementals.entities.air;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import static dev.saperate.elementals.entities.ElementalEntities.AIRSCOOTER;
import static dev.saperate.elementals.misc.ElementalsSounds.WIND_BURST_SOUND_EVENT;
import static dev.saperate.elementals.misc.ElementalsSounds.WIND_SOUND_EVENT;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class AirScooterEntity extends AbstractElementalsEntity<Player> {
    private static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(AirScooterEntity.class, EntityDataSerializers.FLOAT);

    public AirScooterEntity(EntityType<AirScooterEntity> type, Level world) {
        super(type, world, Player.class);
    }

    public AirScooterEntity(Level world, Player owner) {
        this(world, owner, owner.getX(), owner.getY(), owner.getZ());
    }

    public AirScooterEntity(Level world, Player owner, double x, double y, double z) {
        super(AIRSCOOTER.get(), world, Player.class);
        setPos(x, y, z);
        setOwner(owner);
        setNoGravity(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SPEED, 0.5f);
    }

    @Override
    public void tick() {
        super.tick();
        if (random.nextInt(0, 40) == 6) {
            playSound(WIND_SOUND_EVENT, 1, (1.0f + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2f) * 0.7f);
        }
        if (onGround()) {
            playStepSound(getOnPos().below(), level().getBlockState(getOnPos().below()));
        }

        summonParticles(this, random,
                ParticleTypes.POOF,
                0, 1);
        moveEntity();
    }

    private void moveEntity() {
        LivingEntity player = getOwner();

        if(player == null || player.isRemoved()){
            return;
        }

        if (isOnFire() || player.isShiftKeyDown()) {
            player.setShiftKeyDown(false);
            discard();
            return;
        }
        if (!player.equals(getFirstPassenger())) {
            player.startRiding(this);
        }
        player.fallDistance = 0;
        float dx = (float) (-Math.sin(Math.toRadians(player.getYRot())));
        float dz = (float) (Math.cos(Math.toRadians(player.getYRot())));

        float gravity = isInWater() ? 0 : -0.4f;
        this.addDeltaMovement(new Vec3(dx, gravity, dz));
        this.setDeltaMovement(getDeltaMovement().normalize().scale(getSpeed() * (isInWater() ? 0.65f : 1f)));

        this.move(MoverType.SELF, this.getDeltaMovement());
    }


    @Override
    public void onClientRemoval() {
        summonParticles(this, random,
                ParticleTypes.POOF,
                0.25f, 25);
        this.level().playSound(this, getOnPos(), WIND_BURST_SOUND_EVENT, SoundSource.BLOCKS, 1, (1.0f + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2f) * 0.7f);
        if(!level().isClientSide && getOwner() != null){
            Player owner = (Player) getOwner();
            Bender.getBender((ServerPlayer) owner).currAbility.onRemove(Bender.getBender((ServerPlayer) owner));
        }
    }


    public void setSpeed(float speed) {
        this.entityData.set(SPEED, speed);
    }

    public float getSpeed() {
        return this.entityData.get(SPEED);
    }

    @Override
    public boolean discardsOnNullOwner() {
        return true;
    }
    @Override
    public float maxUpStep() {
        return 1.1f;
    }
}

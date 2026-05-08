package dev.saperate.elementals.entities.air;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.data.SynchedEntityData;
import net.minecraft.entity.data.EntityDataAccessor;
import net.minecraft.entity.data.EntityDataSerializers;
import net.minecraft.entity.player.Player;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundSource;
import net.minecraft.world.Level;

import static dev.saperate.elementals.Elementals.WIND_BURST_SOUND_EVENT;
import static dev.saperate.elementals.Elementals.WIND_SOUND_EVENT;
import static dev.saperate.elementals.entities.ElementalEntities.AIRSCOOTER;
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
        super(AIRSCOOTER, world, Player.class);
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
        if (random.nextBetween(0, 40) == 6) {
            playSound(WIND_SOUND_EVENT, 1, (1.0f + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2f) * 0.7f);
        }
        if (isOnGround()) {
            playStepSound(getOnPos().down(), level().getBlockState(getOnPos().down()));
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

        if (isOnFire() || player.isCrouching()) {
            player.setSneaking(false);
            discard();
            return;
        }
        if (!player.equals(getFirstPassenger())) {
            player.startRiding(this);
        }
        player.fallDistance = 0;
        float dx = (float) (-Math.sin(Math.toRadians(player.getYaw())));
        float dz = (float) (Math.cos(Math.toRadians(player.getYaw())));

        float gravity = touchingWater ? 0 : -0.4f;
        this.addDeltaMovement(dx, gravity, dz);
        this.setDeltaMovement(getDeltaMovement().normalize().multiply(getSpeed() * (touchingWater ? 0.65f : 1f)));

        this.move(MoverType.SELF, this.getDeltaMovement());
    }


    @Override
    public void onClientRemoval() {
        summonParticles(this, random,
                ParticleTypes.POOF,
                0.25f, 25);
        this.level().playSound(getX(), getY(), getZ(), WIND_BURST_SOUND_EVENT, SoundSource.BLOCKS, 1, (1.0f + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2f) * 0.7f, true);
        if(!level().isClientSide && getOwner() != null){
            Player owner = (Player) getOwner();
            Bender.getBender((ServerPlayerEntity) owner).currAbility.onRemove(Bender.getBender((ServerPlayerEntity) owner));
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
    public float getStepHeight() {
        return 1.1f;
    }
}

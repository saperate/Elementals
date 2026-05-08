package dev.saperate.elementals.entities.air;

import dev.saperate.elementals.data.ElementalConfig;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.data.SynchedEntityData;
import net.minecraft.entity.data.EntityDataAccessor;
import net.minecraft.entity.data.EntityDataSerializers;
import net.minecraft.entity.player.Player;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundSource;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Level;
import org.joml.Vector3f;

import static dev.saperate.elementals.Elementals.WIND_BURST_SOUND_EVENT;
import static dev.saperate.elementals.Elementals.WIND_SOUND_EVENT;
import static dev.saperate.elementals.entities.ElementalEntities.AIRBULLET;
import static dev.saperate.elementals.entities.ElementalEntities.WATERBULLET;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class AirBulletEntity extends AbstractElementalsEntity<Player> {
    private static final EntityDataAccessor<Integer> ARRAY_ID = SynchedEntityData.defineId(AirBulletEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ARRAY_SIZE = SynchedEntityData.defineId(AirBulletEntity.class, EntityDataSerializers.INT);
    
    public AirBulletEntity(EntityType<AirBulletEntity> type, Level world) {
        super(type, world, Player.class);
    }

    public AirBulletEntity(Level world, Player owner) {
        super(AIRBULLET, world, Player.class);
        setOwner(owner);
        setPos(owner.getX(), owner.getY(), owner.getZ());
    }

    public AirBulletEntity(Level world, Player owner, double x, double y, double z) {
        super(AIRBULLET, world, Player.class);
        setOwner(owner);
        setPos(x, y, z);
        setControlled(true);
        setNoGravity(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ARRAY_ID, 0);
        builder.define(ARRAY_SIZE, 1);
    }

    @Override
    public void tick() {
        super.tick();
        if (random.nextBetween(0, 40) == 6) {
            summonParticles(this, random,
                    ParticleTypes.POOF,
                    0, 1);
            if(getArrayId() == 0){
                playSound(WIND_SOUND_EVENT,0.1f,(1.0f + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2f) * 0.7f);
            }
        }

        LivingEntity owner = getOwner();
        if (owner == null) {
            return;
        }

        moveEntity(owner);
    }


    private void moveEntity(Entity owner) {
        if (getIsControlled()) {
            controlEntity(owner);
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    private void controlEntity(Entity owner) {
        Vector3f direction;
            direction = getEntityLookVector(owner, 3)
                    .subtract(0, 1, 0)
                    .subtract(getPos()).toVector3f();

        double angle = ((2 * Math.PI) / getArraySize()) * getArrayId() + Math.toRadians(tickCount * 2);

        direction = direction.add(
                new Vector3f(0, 1, 0).add(new Vector3f(1, 0, 0).mul((float) Math.sin(angle)).add(new Vector3f(0, 0, 1).mul((float) Math.cos(angle))))
        );

        direction.mul(0.25f);

        if (direction.length() < 0.6f) {
            this.setDeltaMovement(0, 0, 0);
        }


        this.addDeltaMovement(direction.x, direction.y, direction.z);
    }

    @Override
    public void collidesWithGround() {
        discard();
    }

    @Override
    public void onHitEntity(Entity entity) {
        Player owner = (Player) getOwner();
        PlayerData plrData = PlayerData.get(owner);

        float damage = 1;
        if (plrData.canUseUpgrade("airBulletsMastery")) {
            damage = 4;
        } else if (plrData.canUseUpgrade("airBulletsDamageI")) {
            damage = 2;
        }
        entity.hurt(this.damageSources().playerAttack(owner), damage * ElementalConfig.get().BENDING_DAMAGE_MULTIPLIER);
        if (!getIsControlled()) {
            entity.addDeltaMovement(this.getDeltaMovement().multiply(1.2f));
            discard();
        }
    }

    @Override
    public void onClientRemoval() {
        summonParticles(this, random, ParticleTypes.POOF, 0.01f, 10);
        this.level().playSound(getX(), getY(), getZ(), WIND_BURST_SOUND_EVENT, SoundSource.BLOCKS, 0.1f, (1.0f + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2f) * 0.7f, true);

    }

    public void setArrayId(int val) {
        this.getEntityData().set(ARRAY_ID, val);
    }

    public int getArrayId() {
        return this.getEntityData().get(ARRAY_ID);
    }

    /**
     * @param val The amount of entities in the same batch + 1
     */
    public void setArraySize(int val) {
        this.getEntityData().set(ARRAY_SIZE, val);
    }

    public int getArraySize() {
        return this.getEntityData().get(ARRAY_SIZE);
    }

}

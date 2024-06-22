package dev.saperate.elementals.entities.air;

import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.joml.Vector3f;

import static dev.saperate.elementals.Elementals.WIND_BURST_SOUND_EVENT;
import static dev.saperate.elementals.Elementals.WIND_SOUND_EVENT;
import static dev.saperate.elementals.entities.ElementalEntities.AIRBULLET;
import static dev.saperate.elementals.entities.ElementalEntities.WATERBULLET;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

//TODO merge
public class AirBulletEntity extends ProjectileEntity {
    private static final TrackedData<Integer> OWNER_ID = DataTracker.registerData(AirBulletEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> IS_CONTROLLED = DataTracker.registerData(AirBulletEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> ARRAY_ID = DataTracker.registerData(AirBulletEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> ARRAY_SIZE = DataTracker.registerData(AirBulletEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public Vector3f lastCenterPos;


    public AirBulletEntity(EntityType<AirBulletEntity> type, World world) {
        super(type, world);
    }

    public AirBulletEntity(World world, LivingEntity owner) {
        super(AIRBULLET, world);
        setOwner(owner);
        setPos(owner.getX(), owner.getY(), owner.getZ());
    }

    public AirBulletEntity(World world, LivingEntity owner, double x, double y, double z) {
        super(AIRBULLET, world);
        setOwner(owner);
        setPos(x, y, z);
        setControlled(true);
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(OWNER_ID, 0);
        this.getDataTracker().startTracking(IS_CONTROLLED, false);
        this.getDataTracker().startTracking(ARRAY_ID, 0);
        this.getDataTracker().startTracking(ARRAY_SIZE, 1);
    }

    @Override
    public void tick() {
        super.tick();
        if (random.nextBetween(0, 40) == 6) {
            summonParticles(this, random,
                    ParticleTypes.POOF,
                    0, 1);
            if(getArrayId() == 0){
                playSound(WIND_SOUND_EVENT,1,(1.0f + (this.getWorld().random.nextFloat() - this.getWorld().random.nextFloat()) * 0.2f) * 0.7f);
            }
        }
        BlockPos blockHit = SapsUtils.checkBlockCollision(this, 0.1f);

        PlayerEntity owner = getOwner();
        if (owner == null) {
            this.setVelocity(this.getVelocity().add(0.0, -0.02, 0.0));
            this.move(MovementType.SELF, this.getVelocity());
            if (blockHit != null) {
                collidesWithGround();
            }
            return;
        }


        if (blockHit != null && !getIsControlled()) {
            collidesWithGround();
            return;
        }

        if (!getIsControlled() && !getWorld().isClient) {
            HitResult hit = ProjectileUtil.getCollision(this, entity -> entity instanceof LivingEntity);
            if (hit.getType() == HitResult.Type.ENTITY) {
                LivingEntity entity = (LivingEntity) ((EntityHitResult) hit).getEntity();
                PlayerData plrData = PlayerData.get(owner);

                float damage = 0.5f;
                if (plrData.canUseUpgrade("airBulletMastery")) {
                    damage = 8;
                } else if (plrData.canUseUpgrade("airBulletsDamageI")) {
                    damage = 1;
                }
                entity.damage(this.getDamageSources().playerAttack(owner), damage);
                if (!getIsControlled()) {
                    entity.addVelocity(this.getVelocity().multiply(1.2f));
                    discard();
                }
            }
        }


        moveEntity(owner);
    }


    private void moveEntity(Entity owner) {


        //gravity
        //this.setVelocity(this.getVelocity().add(0.0, -0.02, 0.0));

        if (getIsControlled()) {
            controlEntity(owner);
        }


        this.move(MovementType.SELF, this.getVelocity());
    }

    private void controlEntity(Entity owner) {
        Vector3f direction;
        if (!owner.isSneaking() || lastCenterPos == null) {
            direction = getEntityLookVector(owner, 3)
                    .subtract(0, 1, 0)
                    .subtract(getPos()).toVector3f();
            lastCenterPos = direction;
        } else {
            direction = lastCenterPos;
        }

        double angle = ((2 * Math.PI) / getArraySize()) * getArrayId() + Math.toRadians(age * 2);

        direction = direction.add(
                new Vector3f(0, 1, 0).add(new Vector3f(1, 0, 0).mul((float) Math.sin(angle)).add(new Vector3f(0, 0, 1).mul((float) Math.cos(angle))))
        );

        direction.mul(0.25f);

        if (direction.length() < 0.6f) {
            this.setVelocity(0, 0, 0);
        }


        this.addVelocity(direction.x, direction.y, direction.z);
    }

    public void collidesWithGround() {
        discard();
    }

    @Override
    public void onRemoved() {
        summonParticles(this, random, ParticleTypes.POOF, 0.01f, 10);
        this.getWorld().playSound(getX(), getY(), getZ(), WIND_BURST_SOUND_EVENT, SoundCategory.BLOCKS, 1, (1.0f + (this.getWorld().random.nextFloat() - this.getWorld().random.nextFloat()) * 0.2f) * 0.7f, true);

    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        if (getOwner() != null) {
            super.writeCustomDataToNbt(nbt);
            nbt.putInt("OwnerID", this.getOwner().getId());
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        int ownerId = nbt.getInt("OwnerID");
        this.getDataTracker().set(OWNER_ID, ownerId);
    }

    public void setControlled(boolean val) {
        this.getDataTracker().set(IS_CONTROLLED, val);
    }

    public boolean getIsControlled() {
        return this.getDataTracker().get(IS_CONTROLLED);
    }

    public PlayerEntity getOwner() {
        Entity owner = this.getWorld().getEntityById(this.getDataTracker().get(OWNER_ID));
        return (owner instanceof PlayerEntity) ? (PlayerEntity) owner : null;
    }

    public void setOwner(LivingEntity owner) {
        this.getDataTracker().set(OWNER_ID, owner.getId());
    }

    public void setArrayId(int val) {
        this.getDataTracker().set(ARRAY_ID, val);
    }

    public int getArrayId() {
        return this.getDataTracker().get(ARRAY_ID);
    }

    /**
     * @param val The amount of entities in the same batch + 1
     */
    public void setArraySize(int val) {
        this.getDataTracker().set(ARRAY_SIZE, val);
    }

    public int getArraySize() {
        return this.getDataTracker().get(ARRAY_SIZE);
    }

}

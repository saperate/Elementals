package dev.saperate.elementals.entities.air;

import dev.saperate.elementals.data.Bender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

import static dev.saperate.elementals.Elementals.WIND_SOUND_EVENT;
import static dev.saperate.elementals.entities.ElementalEntities.AIRSHIELD;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;


public class AirShieldEntity extends Entity {
    private static final TrackedData<Integer> OWNER_ID = DataTracker.registerData(AirShieldEntity.class, TrackedDataHandlerRegistry.INTEGER);


    public AirShieldEntity(EntityType<AirShieldEntity> type, World world) {
        super(type, world);
    }

    public AirShieldEntity(World world, LivingEntity owner) {
        this(world, owner, owner.getX(), owner.getY(), owner.getZ());
    }

    public AirShieldEntity(World world, LivingEntity owner, double x, double y, double z) {
        super(AIRSHIELD, world);
        setPos(x, y, z);
        setOwner(owner);
    }


    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(OWNER_ID, 0);
    }

    @Override
    public void tick() {
        super.tick();
        if (random.nextBetween(0, 20) == 6) {
            summonParticles(this, random,
                    ParticleTypes.POOF,
                    0, 1);
            playSound(WIND_SOUND_EVENT, 1, (1.0f + (this.getWorld().random.nextFloat() - this.getWorld().random.nextFloat()) * 0.2f) * 0.7f);
        }
        PlayerEntity owner = getOwner();

        if(owner == null){
            discard();
            return;
        }

        setPos(owner.getX(),owner.getY(),owner.getZ());


        List<LivingEntity> hits = getWorld().getEntitiesByClass(LivingEntity.class,
                getWorld().isClient ? getBoundingBox() : getBoundingBox().offset(getPos()),
                LivingEntity::isAlive);

        for (LivingEntity entity : hits) {
           if(entity != owner){
               //Naughty entities can still somehow get inside the shield. This pushes them back out
               Vec3d direction = entity.getPos().add(0,1.5f,0).subtract(getPos()).multiply(0.1f);
               entity.setVelocity(getVelocity().add(direction));
           }
        }

        List<ProjectileEntity> projectiles = getWorld().getEntitiesByClass(ProjectileEntity.class,
                getWorld().isClient ? getBoundingBox().expand(1.75f) : getBoundingBox().expand(1.75f).offset(getPos()),
                ProjectileEntity::isAlive);

        for (ProjectileEntity e : projectiles){
            Vec3d direction = e.getPos().add(0,1.7f,0).subtract(getPos()).multiply(0.2f);
            e.setVelocity(getVelocity().add(direction));
        }
    }


    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public boolean canHit() {
        return true;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }


    public PlayerEntity getOwner() {
        Entity owner = this.getWorld().getEntityById(this.getDataTracker().get(OWNER_ID));
        return (owner instanceof LivingEntity) ? (PlayerEntity) owner : null;
    }

    public void setOwner(LivingEntity owner) {
        this.getDataTracker().set(OWNER_ID, owner.getId());
    }
}

package dev.saperate.elementals.entities.air;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
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


public class AirShieldEntity extends AbstractElementalsEntity<PlayerEntity> {

    public AirShieldEntity(EntityType<AirShieldEntity> type, World world) {
        super(type, world, PlayerEntity.class);
    }

    public AirShieldEntity(World world, PlayerEntity owner) {
        this(world, owner, owner.getX(), owner.getY(), owner.getZ());
    }

    public AirShieldEntity(World world, PlayerEntity owner, double x, double y, double z) {
        super(AIRSHIELD, world, PlayerEntity.class);
        setPos(x, y, z);
        setOwner(owner);
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
        LivingEntity owner = getOwner();

        if (owner == null || isRemoved()) {
            return;
        }

        moveEntityTowardsGoal(owner.getPos().toVector3f());
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
    public boolean teleportsToGoal() {
        return true;
    }

    @Override
    public float projectileDeflectionRange() {
        return 1.75f;
    }

    @Override
    public boolean discardsOnNullOwner() {
        return true;
    }
}

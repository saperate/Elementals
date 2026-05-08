package dev.saperate.elementals.entities.air;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.SynchedEntityData;
import net.minecraft.entity.data.EntityDataAccessor;
import net.minecraft.entity.data.EntityDataSerializers;
import net.minecraft.entity.player.Player;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3;
import net.minecraft.world.Level;

import java.util.List;

import static dev.saperate.elementals.Elementals.WIND_SOUND_EVENT;
import static dev.saperate.elementals.entities.ElementalEntities.AIRSHIELD;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;


public class AirShieldEntity extends AbstractElementalsEntity<Player> {

    public AirShieldEntity(EntityType<AirShieldEntity> type, Level world) {
        super(type, world, Player.class);
    }

    public AirShieldEntity(Level world, Player owner) {
        this(world, owner, owner.getX(), owner.getY(), owner.getZ());
    }

    public AirShieldEntity(Level world, Player owner, double x, double y, double z) {
        super(AIRSHIELD, world, Player.class);
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
            playSound(WIND_SOUND_EVENT, 1, (1.0f + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2f) * 0.7f);
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
    public boolean isPickable() {
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

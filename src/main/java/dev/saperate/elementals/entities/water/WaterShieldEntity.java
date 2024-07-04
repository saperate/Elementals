package dev.saperate.elementals.entities.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

import static dev.saperate.elementals.entities.ElementalEntities.WATERSHIELD;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;


public class WaterShieldEntity extends AbstractElementalsEntity<PlayerEntity> {
    private static final TrackedData<Integer> OWNER_ID = DataTracker.registerData(WaterShieldEntity.class, TrackedDataHandlerRegistry.INTEGER);


    public WaterShieldEntity(EntityType<WaterShieldEntity> type, World world) {
        super(type, world, PlayerEntity.class);
    }

    public WaterShieldEntity(World world, PlayerEntity owner) {
        this(world, owner, owner.getX(), owner.getY(), owner.getZ());
    }

    public WaterShieldEntity(World world, PlayerEntity owner, double x, double y, double z) {
        super(WATERSHIELD, world, PlayerEntity.class);
        setPos(x, y, z);
        setOwner(owner);
    }

    @Override
    public void tick() {
        super.tick();

        if (random.nextBetween(0, 10) == 6) {
            playSound(SoundEvents.ENTITY_PLAYER_SWIM,0.05f,0);
        }

        PlayerEntity owner = (PlayerEntity) getOwner();

        if(owner == null || isRemoved()){
            return;
        }

        moveEntityTowardsGoal(owner.getPos().toVector3f());
    }

    @Override
    public void onRemoved() {
        summonParticles( this,random, ParticleTypes.SPLASH, 10,100);
        if(!this.getWorld().isClient){
            Bender bender = Bender.getBender((PlayerEntity) getOwner());
            if(bender != null && bender.currAbility != null){//Clean up the mess
                bender.abilityData = null;
                bender.currAbility.onRemove(bender);
            }
        }
    }


    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public boolean teleportsToGoal() {
        return true;
    }

    @Override
    public float projectileDeflectionRange() {
        return 1.5f;
    }

    @Override
    public boolean discardsOnNullOwner() {
        return true;
    }


}

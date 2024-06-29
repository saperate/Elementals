package dev.saperate.elementals.entities.water;

import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TypeFilter;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static dev.saperate.elementals.entities.ElementalEntities.WATERTOWER;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class WaterTowerEntity extends AbstractElementalsEntity {
    public static final int heightLimit = 10;
    private static final TrackedData<Float> TOWER_HEIGHT = DataTracker.registerData(WaterTowerEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> MAX_TOWER_HEIGHT = DataTracker.registerData(WaterTowerEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Integer> OWNER_ID = DataTracker.registerData(WaterTowerEntity.class, TrackedDataHandlerRegistry.INTEGER);


    public WaterTowerEntity(EntityType<WaterTowerEntity> type, World world) {
        super(type, world);
    }

    public WaterTowerEntity(World world, LivingEntity owner) {
        super(WATERTOWER, world);
        setOwner(owner);
        setPos(owner.getX(), owner.getY(), owner.getZ());
    }

    public WaterTowerEntity(World world, LivingEntity owner, double x, double y, double z) {
        super(WATERTOWER, world);
        setOwner(owner);
        setPos(x, y, z);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.getDataTracker().startTracking(TOWER_HEIGHT, 1f);
        this.getDataTracker().startTracking(MAX_TOWER_HEIGHT, 1f);
    }

    @Override
    public void tick() {
        super.tick();

        if (random.nextBetween(0, 40) == 6) {
            playSound(SoundEvents.ENTITY_PLAYER_SWIM,0.1f,0);
        }

        PlayerEntity owner = getOwner();
        if(owner == null || isRemoved()){
            return;
        }

        updatePosition(owner);

        summonParticles(owner, random, ParticleTypes.SPLASH, 0, 2, 0);
        summonParticles(owner, random, ParticleTypes.BUBBLE, 0, 2, 0);

        owner.getAbilities().allowFlying = true;
        owner.getAbilities().flying = true;
        owner.getAbilities().setFlySpeed(0.02f);
    }

    public void updatePosition(@Nullable Entity owner){
        if(owner == null){
            return;
        }
        setTowerHeight((float) Math.max(0, owner.getY() - getY()));
        setPosition(owner.getPos().multiply(1,0,1).add(0,getY(),0));
        if(owner.getY() >= getY() + getMaxTowerHeight() - 0.5f){
            owner.setPosition(owner.getPos().multiply(1,0,1).add(0, getY() + getMaxTowerHeight() - 0.5f, 0));
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        resetOwner();
    }

    @Override
    public void onRemoved() {
        super.onRemoved();
        resetOwner();
    }

    public void resetOwner(){
        //TODO add values that store what it was before so that we dont mess things up
        PlayerEntity owner = getOwner();
        if (owner == null) {
            return;
        }
        owner.getAbilities().allowFlying = false;
        owner.getAbilities().flying = false;
        owner.getAbilities().setFlySpeed(0.05f);
    }


    public void setTowerHeight(float val){
        this.getDataTracker().set(TOWER_HEIGHT,val);
    }

    public float getTowerHeight(){
        return this.getDataTracker().get(TOWER_HEIGHT);
    }

    public void setMaxTowerHeight(float val){
        this.getDataTracker().set(MAX_TOWER_HEIGHT,val);
    }

    public float getMaxTowerHeight(){
        return this.getDataTracker().get(MAX_TOWER_HEIGHT);
    }

    @Override
    public boolean discardsOnNullOwner() {
        return true;
    }
}

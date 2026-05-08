package dev.saperate.elementals.entities.water;

import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import static dev.saperate.elementals.entities.ElementalEntities.WATERTOWER;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class WaterTowerEntity extends AbstractElementalsEntity<Player> {
    public static final int heightLimit = 10;
    private static final EntityDataAccessor<Float> TOWER_HEIGHT = SynchedEntityData.defineId(WaterTowerEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> MAX_TOWER_HEIGHT = SynchedEntityData.defineId(WaterTowerEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> OWNER_COULD_FLY = SynchedEntityData.defineId(WaterTowerEntity.class, EntityDataSerializers.BOOLEAN);

    public WaterTowerEntity(EntityType<WaterTowerEntity> type, Level world) {
        super(type, world, Player.class);
    }

    public WaterTowerEntity(Level world, Player owner) {
        super(WATERTOWER, world, Player.class);
        setOwner(owner);
        setPos(owner.getX(), owner.getY(), owner.getZ());
    }

    public WaterTowerEntity(Level world, Player owner, double x, double y, double z) {
        super(WATERTOWER, world, Player.class);
        setOwner(owner);
        setPos(x, y, z);
        setNoGravity(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TOWER_HEIGHT, 1f);
        builder.define(MAX_TOWER_HEIGHT, 1f);
        builder.define(OWNER_COULD_FLY, false);
    }

    @Override
    public void tick() {
        super.tick();

        if (random.nextInt(0, 40) == 6) {
            playSound(SoundEvents.PLAYER_SWIM,0.1f,0);
        }

        Player owner = getOwner();
        if(owner == null || isRemoved()){
            return;
        }

        updatePosition(owner);

        summonParticles(owner, random, ParticleTypes.SPLASH, 0, 2, 0);
        summonParticles(owner, random, ParticleTypes.BUBBLE, 0, 2, 0);

        owner.getAbilities().mayfly = true;
        owner.getAbilities().flying = true;
        owner.getAbilities().setFlyingSpeed(0.02f);
    }

    public void updatePosition(@Nullable Entity owner){
        if(owner == null){
            return;
        }
        setTowerHeight((float) Math.max(0, owner.getY() - getY()));
        setPos(owner.position().multiply(1,0,1).add(0,getY(),0));
        if(getTowerHeight() - 1 > getMaxTowerHeight()){
            owner.setPos(owner.position().multiply(1,0,1).add(0, getY() + getMaxTowerHeight() - 0.25f, 0));
        }
    }
    

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        resetOwner();
    }

    @Override
    public void onClientRemoval() {
        super.onClientRemoval();
        resetOwner();
    }

    public void resetOwner(){
        Player owner = (Player) getOwner();
        if (owner == null) {
            return;
        }

        owner.getAbilities().mayfly = getOwnerCouldFly();
        owner.getAbilities().flying = false;
        owner.getAbilities().setFlyingSpeed(0.05f);
    }


    public void setTowerHeight(float val){
        this.getEntityData().set(TOWER_HEIGHT,val);
    }

    public float getTowerHeight(){
        return this.getEntityData().get(TOWER_HEIGHT);
    }

    public void setMaxTowerHeight(float val){
        this.getEntityData().set(MAX_TOWER_HEIGHT,val);
    }

    public float getMaxTowerHeight(){
        return this.getEntityData().get(MAX_TOWER_HEIGHT);
    }

    public void setOwnerCouldFly(boolean val){
        this.getEntityData().set(OWNER_COULD_FLY,val);
    }

    public boolean getOwnerCouldFly(){
        return this.getEntityData().get(OWNER_COULD_FLY);
    }

    @Override
    public boolean discardsOnNullOwner() {
        return true;
    }

    @Override
    public boolean pushesEntitiesAway() {
        return false;
    }
}

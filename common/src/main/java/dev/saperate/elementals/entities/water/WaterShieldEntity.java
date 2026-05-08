package dev.saperate.elementals.entities.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import static dev.saperate.elementals.entities.ElementalEntities.WATERSHIELD;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;


public class WaterShieldEntity extends AbstractElementalsEntity<Player> {

    public WaterShieldEntity(EntityType<WaterShieldEntity> type, Level world) {
        super(type, world, Player.class);
    }

    public WaterShieldEntity(Level world, Player owner) {
        this(world, owner, owner.getX(), owner.getY(), owner.getZ());
    }

    public WaterShieldEntity(Level world, Player owner, double x, double y, double z) {
        super(WATERSHIELD, world, Player.class);
        setPos(x, y, z);
        setOwner(owner);
    }

    @Override
    public void tick() {
        super.tick();

        if (random.nextInt(0, 10) == 6) {
            playSound(SoundEvents.PLAYER_SWIM,0.05f,0);
        }

        Player owner = (Player) getOwner();

        if(owner == null || isRemoved()){
            return;
        }

        moveEntityTowardsGoal(owner.position().toVector3f());
    }


    @Override
    public void onClientRemoval() {
        summonParticles( this,random, ParticleTypes.SPLASH, 10,100);
        if(!this.level().isClientSide){
            Bender bender = Bender.getBender((ServerPlayer) getOwner());
            if(bender != null && bender.currAbility != null){//Clean up the mess
                bender.abilityData = null;
                bender.currAbility.onRemove(bender);
            }
        }
    }
    
    @Override
    public boolean canBeCollidedWith() {
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

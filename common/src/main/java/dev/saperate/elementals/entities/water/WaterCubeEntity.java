package dev.saperate.elementals.entities.water;

import dev.saperate.elementals.data.ElementalConfig;
import dev.saperate.elementals.elements.water.WaterElement;
import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;


import static dev.saperate.elementals.Elementals.BENDING_GRIEFING;
import static dev.saperate.elementals.entities.ElementalEntities.WATERCUBE;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class WaterCubeEntity extends AbstractElementalsEntity<Player> {

    public WaterCubeEntity(EntityType<WaterCubeEntity> type, Level world) {
        super(type, world, Player.class);
    }

    public WaterCubeEntity(Level world, Player owner) {
        super(WATERCUBE.get(), world, Player.class);
        setOwner(owner);
        setPos(owner.getX(), owner.getY(), owner.getZ());
    }

    public WaterCubeEntity(Level world, Player owner, double x, double y, double z) {
        super(WATERCUBE.get(), world, Player.class);
        setOwner(owner);
        setPos(x, y, z);
        setControlled(true);
    }

    @Override
    public void tick() {
        super.tick();

        if (random.nextInt(0, 40) == 6) {
            summonParticles(this, random,
                    ParticleTypes.SPLASH,
                    0, 1);
            playSound(SoundEvents.PLAYER_SWIM, 0.25f, 0);
        }

        Entity owner = getOwner();
        if (owner == null || isRemoved()) {
            return;
        }


        if (!owner.isCrouching()) {
            moveEntity(owner);
        }

    }

    private void moveEntity(Entity owner) {
        if (getIsControlled()) {
            moveEntityTowardsGoal(getEntityLookVector(owner, 3).toVector3f());
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    @Override
    public void collidesWithGround() {
        if (level().getGameRules().getBoolean(BENDING_GRIEFING)) {
            WaterElement.placeWater(getOnPos(), level());
        }
        discard();
    }

    @Override
    public void onHitEntity(Entity entity) {
        entity.hurt(this.damageSources().playerAttack((Player) getOwner()), 6 * ElementalConfig.get().BENDING_DAMAGE_MULTIPLIER);
        entity.addDeltaMovement(this.getDeltaMovement().scale(1.25));
        discard();
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public void onClientRemoval() {
        summonParticles(this, random, ParticleTypes.SPLASH, 0, 10);
        this.level().playSound(this, getOnPos(), SoundEvents.PLAYER_SPLASH, SoundSource.BLOCKS, 0.25f, (1.0f + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2f) * 0.7f);

    }

    @Override
    public float projectileDeflectionRange() {
        return .5f;
    }

}

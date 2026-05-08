package dev.saperate.elementals.entities.blood;

import dev.saperate.elementals.data.ElementalConfig;
import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static dev.saperate.elementals.entities.ElementalEntities.BLOODSHOT;
import static dev.saperate.elementals.entities.ElementalEntities.WATERHEALING;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class BloodShotEntity extends AbstractElementalsEntity<Player> {
    public List<MobEffectInstance> effects = new ArrayList<>();

    public BloodShotEntity(EntityType<BloodShotEntity> type, Level world) {
        super(type, world, Player.class);
    }


    public BloodShotEntity(Level world, Player owner, double x, double y, double z, Collection<MobEffectInstance> ownerEffects) {
        super(BLOODSHOT, world, Player.class);
        setOwner(owner);
        setPos(x, y, z);
        setControlled(true);

        effects.addAll(ownerEffects);
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

        Player owner = (Player) getOwner();

        if (owner != null && !isRemoved()) {
            moveEntity(owner);
        }
    }

    @Override
    public void onHitEntity(Entity entity) {
        if (entity instanceof LivingEntity living) {
            for (MobEffectInstance instance : effects) {
                living.addEffect(instance);
            }
            living.hurt(this.damageSources().playerAttack(getOwner()), 1 * ElementalConfig.get().BENDING_DAMAGE_MULTIPLIER);
            discard();
        }
    }


    private void moveEntity(Entity owner) {
        if (getIsControlled()) {
            controlEntity(owner);
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    private void controlEntity(Entity owner) {
        float distance = 3;
        if (owner.isCrouching()) {
            distance = 6;
        }
        Vector3f direction = getEntityLookVector(owner, distance)
                .subtract(0, 0.25f, 0)
                .subtract(position()).toVector3f();
        direction.mul(0.25f);

        if (direction.length() < 0.6f) {
            this.setDeltaMovement(0, 0, 0);
        }


        this.addDeltaMovement(new Vec3(direction.x, direction.y, direction.z));
    }

    @Override
    public void collidesWithGround() {
        discard();
    }

    @Override
    public void onClientRemoval() {
        summonParticles(this, random, ParticleTypes.SPLASH, 0, 10);
        this.level().playSound(this, getOnPos(), SoundEvents.PLAYER_SPLASH, SoundSource.BLOCKS, 0.25f, (1.0f + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2f) * 0.7f);

    }

    @Override
    public boolean damagesOnTouch() {
        return true;
    }

    @Override
    public boolean pushesEntitiesAway() {
        return false;
    }
}

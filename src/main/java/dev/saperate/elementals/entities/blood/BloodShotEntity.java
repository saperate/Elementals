package dev.saperate.elementals.entities.blood;

import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dev.saperate.elementals.entities.ElementalEntities.BLOODSHOT;
import static dev.saperate.elementals.entities.ElementalEntities.WATERHEALING;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class BloodShotEntity extends AbstractElementalsEntity<PlayerEntity> {
    public List<StatusEffectInstance> effects = new ArrayList<>();

    public BloodShotEntity(EntityType<BloodShotEntity> type, World world) {
        super(type, world, PlayerEntity.class);
    }


    public BloodShotEntity(World world, PlayerEntity owner, double x, double y, double z, Map<StatusEffect, StatusEffectInstance> ownerEffects) {
        super(BLOODSHOT, world, PlayerEntity.class);
        setOwner(owner);
        setPos(x, y, z);
        setControlled(true);

        for(StatusEffectInstance instance : ownerEffects.values()){
            effects.add(new StatusEffectInstance(instance));
        }
    }


    @Override
    public void tick() {
        super.tick();

        if (random.nextBetween(0, 40) == 6) {
            summonParticles(this, random,
                    ParticleTypes.SPLASH,
                    0, 1);
            playSound(SoundEvents.ENTITY_PLAYER_SWIM, 0.25f, 0);
        }

        PlayerEntity owner = (PlayerEntity) getOwner();

        if (owner != null && !isRemoved()) {
            moveEntity(owner);
        }
    }

    @Override
    public void onHitEntity(Entity entity) {
        if (entity instanceof LivingEntity living) {
            for (StatusEffectInstance instance : effects) {
                living.addStatusEffect(instance);
            }
            living.damage(this.getDamageSources().playerAttack(getOwner()), 1);
            discard();
        }
    }


    private void moveEntity(Entity owner) {
        if (getIsControlled()) {
            controlEntity(owner);
        }

        this.move(MovementType.SELF, this.getVelocity());
    }

    private void controlEntity(Entity owner) {
        float distance = 3;
        if (owner.isSneaking()) {
            distance = 6;
        }
        Vector3f direction = getEntityLookVector(owner, distance)
                .subtract(0, 0.25f, 0)
                .subtract(getPos()).toVector3f();
        direction.mul(0.25f);

        if (direction.length() < 0.6f) {
            this.setVelocity(0, 0, 0);
        }


        this.addVelocity(direction.x, direction.y, direction.z);
    }

    @Override
    public void collidesWithGround() {
        discard();
    }

    @Override
    public void onRemoved() {
        summonParticles(this, random, ParticleTypes.SPLASH, 0, 10);
        this.getWorld().playSound(getX(), getY(), getZ(), SoundEvents.ENTITY_PLAYER_SPLASH, SoundCategory.BLOCKS, 0.25f, (1.0f + (this.getWorld().random.nextFloat() - this.getWorld().random.nextFloat()) * 0.2f) * 0.7f, false);

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

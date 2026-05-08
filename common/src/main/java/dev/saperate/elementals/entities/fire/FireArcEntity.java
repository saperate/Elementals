package dev.saperate.elementals.entities.fire;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.ElementalConfig;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.entities.common.AbstractElementalsEntity;
import dev.saperate.elementals.entities.water.WaterArcEntity;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.data.SynchedEntityData;
import net.minecraft.entity.data.EntityDataAccessor;
import net.minecraft.entity.data.EntityDataSerializers;
import net.minecraft.entity.player.Player;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3;
import net.minecraft.world.Level;
import org.joml.Vector3f;

import static dev.saperate.elementals.entities.ElementalEntities.FIREARC;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class FireArcEntity extends AbstractElementalsEntity<Player> {
    private static final EntityDataAccessor<Integer> PARENT_ID = SynchedEntityData.defineId(FireArcEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CHILD_ID = SynchedEntityData.defineId(FireArcEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_BLUE = SynchedEntityData.defineId(FireArcEntity.class, EntityDataSerializers.BOOLEAN);

    public static final float chainDistance = 0.75f;
    private static final int MAX_CHAIN_LENGTH = 6;
    public int chainLength = 0;


    public FireArcEntity(EntityType<FireArcEntity> type, Level world) {
        super(type, world, Player.class);
    }

    public FireArcEntity(Level world, Player owner) {
        this(world, owner, owner.getX(), owner.getY(), owner.getZ());
    }

    public FireArcEntity(Level world, Player owner, double x, double y, double z) {
        super(FIREARC, world, Player.class);
        setOwner(owner);
        setPos(x, y, z);
        setNoGravity(false);
        setControlled(true);
    }

    public void createChain(Player owner) {
        if (chainLength < MAX_CHAIN_LENGTH) {
            FireArcEntity newArc = new FireArcEntity(level(), owner, getX(), getY(), getZ());
            newArc.setParent(this);
            setChild(newArc);
            newArc.setControlled(false);
            newArc.setIsBlue(isBlue());
            level().addFreshEntity(newArc);
            chainLength++;
            newArc.chainLength = chainLength;
            newArc.createChain(owner);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PARENT_ID, 0);
        builder.define(CHILD_ID, 0);
        builder.define(IS_BLUE, false);
    }

    @Override
    public void tick() {
        super.tick();

        if (touchingWater && getParent() == null && !level().isClientSide) {
            remove();

            Player owner = getOwner();
            if(owner != null){
                Bender bender = Bender.getBender((ServerPlayerEntity) owner);
                if(bender.currAbility != null){
                    bender.currAbility.onRemove(bender);
                }
            }
            return;
        }

        if (random.nextBetween(0, 20) == 6) {
            summonParticles(this, random,
                    isBlue() ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME,
                    0, 1);
            if (getParent() == null) {
                playSound(SoundEvents.BLOCK_FIRE_AMBIENT, 1, 0);
            }
        }

        super.tick();

        Player owner = getOwner();
        if (owner == null && isRemoved()) {
            return;
        }

        moveEntity(owner, getParent());
    }

    @Override
    public void collidesWithGround() {
        if (getParent() == null) {
            remove();
        }
    }

    @Override
    public void onHitEntity(Entity entity) {
        if (entity == getOwner() || getParent() != null) {
            return;
        }
        entity.addDeltaMovement(this.getDeltaMovement().multiply(0.2f));
        PlayerData plrData = PlayerData.get(getOwner());

        float damage = isBlue() ? 3.5f : 2.5f;//TODO BUFF
        if (plrData.canUseUpgrade("fireArcMastery")) {
            damage += 4;
        } else if (plrData.canUseUpgrade("fireArcDamageI")) {
            damage += 2;
        }

        if (SapsUtils.isBeingRainedOn(this)) {
            damage /= 2;
        }

        if (!entity.isFireImmune()) {
            entity.setOnFireFor(8);
        }
        entity.hurt(this.damageSources().playerAttack(getOwner()), damage * ElementalConfig.get().BENDING_DAMAGE_MULTIPLIER);
        remove();
    }

    private void moveEntity(Entity owner, Entity parent) {

        if (getIsControlled()) {
            moveEntityTowardsGoal(getEntityLookVector(owner, 3).add(0, 0.5, 0).toVector3f());
        } else {
            if (parent != null) {
                Vec3 direction = parent.getPos().subtract(getPos());
                double distance = direction.length();

                if (distance > chainDistance) {
                    direction = direction.normalize().multiply(distance - chainDistance).add(getPos());
                    setPos(direction.x, direction.y, direction.z);
                }
            }
        }


        this.move(MoverType.SELF, this.getDeltaMovement());
    }


    @Override
    public void onClientRemoval() {
        summonParticles(this, random,
                isBlue() ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME,
                0.1f, 10);
        this.level().playSound(getX(), getY(), getZ(), SoundEvents.ITEM_FIRECHARGE_USE, SoundSource.BLOCKS, 0.25f, (1.0f + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2f) * 0.7f, false);
    }

    /**
     * Safely despawns the arc along with all of its children
     */
    public void despawn() {
        if (level().isClientSide) {
            return;
        }
        getHead().remove();
    }

    public FireArcEntity getHead() {
        FireArcEntity parent = getParent();
        if (parent == null) {
            return this;
        }
        return parent.getHead();
    }

    /**
     * remove a specific link from the chain. it also kills its children
     */
    public void remove() {
        FireArcEntity child = getChild();
        if (child == null) {
            if (getParent() == null) {
                this.discard();
                return;
            }
            getParent().setChild(null);
            this.discard();
            return;
        }
        child.remove();
        FireArcEntity parent = getParent();
        if (parent != null) {
            getParent().setChild(null);
        }
        this.discard();
    }

    public FireArcEntity getParent() {
        int parentId = this.getEntityData().get(PARENT_ID);
        Entity parent = this.level().getEntity(parentId);
        return parent instanceof FireArcEntity ? (FireArcEntity) this.level().getEntity(parentId) : null;
    }

    public void setParent(FireArcEntity parent) {
        this.getEntityData().set(PARENT_ID, parent != null ? parent.getId() : 0);
    }

    public FireArcEntity getChild() {
        int childId = this.getEntityData().get(CHILD_ID);
        Entity child = this.level().getEntity(childId);
        return child instanceof FireArcEntity ? (FireArcEntity) this.level().getEntity(childId) : null;
    }

    public void setChild(FireArcEntity child) {
        this.getEntityData().set(CHILD_ID, child != null ? child.getId() : 0);
    }

    public boolean isBlue() {
        return this.entityData.get(IS_BLUE);
    }

    public void setIsBlue(boolean val) {
        this.getEntityData().set(IS_BLUE, val);
    }

    @Override
    public boolean isNoGravity() {
        return super.isNoGravity() || getParent() != null;
    }

    @Override
    public boolean emitsLight() {
        return true;
    }
}

package dev.saperate.elementals.entities.fire;

import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.entities.water.WaterArcEntity;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

import static dev.saperate.elementals.entities.ElementalEntities.FIREARC;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class FireArcEntity extends ProjectileEntity {
    private static final TrackedData<Integer> PARENT_ID = DataTracker.registerData(FireArcEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> CHILD_ID = DataTracker.registerData(FireArcEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> OWNER_ID = DataTracker.registerData(FireArcEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> IS_CONTROLLED = DataTracker.registerData(FireArcEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_BLUE = DataTracker.registerData(FireArcEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public static final float chainDistance = 0.75f;
    private static final int MAX_CHAIN_LENGTH = 3;
    public int chainLength = 0;


    public FireArcEntity(EntityType<FireArcEntity> type, World world) {
        super(type, world);
    }

    public FireArcEntity(World world, LivingEntity owner) {
        this(world, owner, owner.getX(), owner.getY(), owner.getZ());
    }

    public FireArcEntity(World world, LivingEntity owner, double x, double y, double z) {
        super(FIREARC, world);
        setOwner(owner);
        setPos(x, y, z);
        setNoGravity(false);
        setControlled(true);
    }

    public void createChain(LivingEntity owner) {
        if (chainLength < MAX_CHAIN_LENGTH) {
            FireArcEntity newArc = new FireArcEntity(getWorld(), owner, getX(), getY(), getZ());
            newArc.setParent(this);
            setChild(newArc);
            newArc.setControlled(false);
            newArc.setIsBlue(isBlue());
            getWorld().spawnEntity(newArc);
            chainLength++;
            newArc.chainLength = chainLength;
            newArc.createChain(owner);
        }
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(PARENT_ID, 0);
        this.getDataTracker().startTracking(CHILD_ID, 0);
        this.getDataTracker().startTracking(OWNER_ID, 0);
        this.getDataTracker().startTracking(IS_CONTROLLED, false);
        this.getDataTracker().startTracking(IS_BLUE, false);
    }

    @Override
    public void tick() {
        if (random.nextBetween(0, 20) == 6) {
            summonParticles(this, random,
                    isBlue() ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME,
                    0, 1);
            if(getParent() == null){
                playSound(SoundEvents.BLOCK_FIRE_AMBIENT,1,0);
            }
        }

        super.tick();
        PlayerEntity owner = getOwner();
        if (owner == null) {
            discard();
            return;
        }
        FireArcEntity parent = getParent();
        if (!getIsControlled() && parent == null && !getWorld().isClient) {
            HitResult hit = ProjectileUtil.getCollision(this, entity -> entity instanceof LivingEntity);
            if (hit.getType() == HitResult.Type.ENTITY) { //TODO make it ignore if on client
                LivingEntity entity = (LivingEntity) ((EntityHitResult) hit).getEntity();
                entity.addVelocity(this.getVelocity().multiply(0.2f));
                PlayerData plrData = PlayerData.get(owner);

                float damage = isBlue() ? 3.5f : 2.5f;
                if (plrData.canUseUpgrade("fireArcMastery")) {
                    damage += 4;
                } else if (plrData.canUseUpgrade("fireArcDamageI")) {
                    damage += 2;
                }


                if (!entity.isFireImmune()) {
                    entity.setOnFireFor(8);
                }
                entity.damage(getDamageSources().inFire(),damage);
                remove();
            }else if (SapsUtils.checkBlockCollision(this, 0.1f, true) != null) {
                remove();
            }
        }


        moveEntity(owner, parent);
    }

    private void moveEntity(Entity owner, Entity parent) {
        this.getWorld().getEntitiesByType(TypeFilter.instanceOf(PlayerEntity.class), this.getBoundingBox(), EntityPredicates.canBePushedBy(this)).forEach(this::pushAway);

        //gravity
        if (!hasNoGravity() && parent == null) {
            this.setVelocity(this.getVelocity().add(0.0, -0.02, 0.0));
        }


        if (!hasNoGravity()) {
            if (getIsControlled()) {
                controlEntity(owner);
            } else {
                if (parent != null) {
                    Vec3d direction = parent.getPos().subtract(getPos());
                    double distance = direction.length();

                    if (distance > chainDistance) {
                        direction = direction.normalize().multiply(distance - chainDistance).add(getPos());
                        setPos(direction.x, direction.y, direction.z);
                    }

                }
            }
        }


        this.move(MovementType.SELF, this.getVelocity());
    }

    private void controlEntity(Entity owner) {
        Vector3f direction = getEntityLookVector(owner, 3)
                .subtract(getPos()).toVector3f();
        direction.mul(0.2f);

        if (direction.length() < 0.4f) {
            this.setVelocity(0, 0, 0);
        }


        this.addVelocity(direction.x, direction.y, direction.z);
    }


    @Override
    public void onRemoved() {
        summonParticles(this, random,
                isBlue() ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME,
                0.1f, 10);
        this.getWorld().playSound(getX(), getY(), getZ(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 0.25f, (1.0f + (this.getWorld().random.nextFloat() - this.getWorld().random.nextFloat()) * 0.2f) * 0.7f, false);
    }

    /**
     * Safely despawns the arc along with all of its children
     */
    public void despawn() {
        if (getWorld().isClient) {
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
            if(getParent() == null){
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

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        if (getOwner() != null) {
            super.writeCustomDataToNbt(nbt);
            nbt.putInt("OwnerID", this.getOwner().getId());
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        int ownerId = nbt.getInt("OwnerID");
        this.getDataTracker().set(OWNER_ID, ownerId);
    }

    public void setControlled(boolean val) {
        this.getDataTracker().set(IS_CONTROLLED, val);
    }

    public boolean getIsControlled() {
        return this.getDataTracker().get(IS_CONTROLLED);
    }

    public PlayerEntity getOwner() {
        Entity owner = this.getWorld().getEntityById(this.getDataTracker().get(OWNER_ID));
        return (owner instanceof PlayerEntity) ? (PlayerEntity) owner : null;
    }

    public void setOwner(LivingEntity owner) {
        this.getDataTracker().set(OWNER_ID, owner.getId());
    }

    protected void pushAway(Entity entity) {
        entity.pushAwayFrom(this);
    }

    public FireArcEntity getParent() {
        int parentId = this.getDataTracker().get(PARENT_ID);
        return parentId != 0 ? (FireArcEntity) this.getWorld().getEntityById(parentId) : null;
    }

    public void setParent(FireArcEntity parent) {
        this.getDataTracker().set(PARENT_ID, parent != null ? parent.getId() : 0);
    }

    public FireArcEntity getChild() {
        int childId = this.getDataTracker().get(CHILD_ID);
        return childId != 0 ? (FireArcEntity) this.getWorld().getEntityById(childId) : null;
    }

    public void setChild(FireArcEntity child) {
        this.getDataTracker().set(CHILD_ID, child != null ? child.getId() : 0);
    }

    public boolean isBlue() {
        return this.dataTracker.get(IS_BLUE);
    }

    public void setIsBlue(boolean val) {
        this.getDataTracker().set(IS_BLUE, val);
    }
}

package dev.saperate.elementals.entities.water;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3d;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;

public class WaterArcEntity extends ProjectileEntity {
    private static final TrackedData<Integer> OWNER_ID = DataTracker.registerData(WaterArcEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> IS_CONTROLLED = DataTracker.registerData(WaterArcEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public static final EntityType<WaterArcEntity> WATERARC = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "water_arc"),
            FabricEntityTypeBuilder.<WaterArcEntity>create(SpawnGroup.MISC, WaterArcEntity::new)
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build());
    private static final int MAX_CHAIN_LENGTH = 5;
    public int chainLength = 0;
    public WaterArcEntity parent;
    public WaterArcEntity child;


    public WaterArcEntity(EntityType<WaterArcEntity> type, World world) {
        super(type, world);
    }

    public WaterArcEntity(World world, LivingEntity owner) {
        this(world, owner, owner.getX(), owner.getY(), owner.getZ());
    }

    public WaterArcEntity(World world, LivingEntity owner, double x, double y, double z) {
        super(WATERARC, world);
        setOwner(owner);
        setPos(x, y, z);
        setNoGravity(true);
    }

    public void createChain(LivingEntity owner) {
        if (chainLength < MAX_CHAIN_LENGTH) {
            WaterArcEntity newArc = new WaterArcEntity(getWorld(), owner, getX(), getY(), getZ());
            newArc.parent = this;
            this.child = newArc;
            newArc.setControlled(false);
            getWorld().spawnEntity(newArc);
            chainLength++;
            newArc.chainLength = chainLength;
            //newArc.createChain(owner);
        }
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(OWNER_ID, 0);
        this.getDataTracker().startTracking(IS_CONTROLLED, true);
    }

    @Override
    public void tick() {
        super.tick();
        Entity owner = getOwner();
        if (owner == null) {
            discard();
            return;
        }
        /*
        if(!getIsControlled()){
            HitResult hit = ProjectileUtil.getCollision(this, entity -> entity instanceof LivingEntity);
            if (hit.getType() == HitResult.Type.ENTITY){
                LivingEntity entity = (LivingEntity) ((EntityHitResult) hit).getEntity();
                entity.damage(this.getDamageSources().playerAttack((PlayerEntity) owner),10);
                discard();
            }
        }*/


        moveEntity(owner);
    }

    private void moveEntity(Entity owner) {
        this.getWorld().getEntitiesByType(TypeFilter.instanceOf(PlayerEntity.class), this.getBoundingBox(), EntityPredicates.canBePushedBy(this)).forEach(this::pushAway);


        //gravity
        if (!hasNoGravity()) {
            this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
        }


        if (getIsControlled()) {
            controlEntity(owner);
        } else {
            if (parent != null) {
                Vec3d direction = parent.getPos().subtract(getPos());

                //Constraint so that it stays roughly 2 blocks away from the parent
                double distance = direction.length();
                if (distance > 2) {
                    direction = direction.multiply(distance - 2).multiply(0.1f);
                } else if (distance < 2 ) {
                    direction = direction.multiply(-0.1f);
                }else{
                    direction = direction.multiply(0);
                }


                double damping = 0.1f + (0.5f - 0.1f) * (1 - Math.min(1, distance / 2));
                direction = direction.multiply(damping);

                this.addVelocity(direction.x, direction.y, direction.z);

                this.addVelocity(getVelocity().multiply(-0.1f));
            }

        }


        this.move(MovementType.SELF, this.getVelocity());
    }

    private void controlEntity(Entity owner) {
        Vector3f direction = getEntityLookVector(owner, 3)
                .sub(0, 0.5f, 0)
                .sub(getPos().toVector3f());
        direction.mul(0.1f);

        if (direction.length() < 0.4f) {
            this.setVelocity(0, 0, 0);
        }


        this.addVelocity(direction.x, direction.y, direction.z);
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

    public LivingEntity getOwner() {
        Entity owner = this.getWorld().getEntityById(this.getDataTracker().get(OWNER_ID));
        return (owner instanceof LivingEntity) ? (LivingEntity) owner : null;
    }

    public void setOwner(LivingEntity owner) {
        this.getDataTracker().set(OWNER_ID, owner.getId());
    }

    protected void pushAway(Entity entity) {
        entity.pushAwayFrom(this);
    }
}

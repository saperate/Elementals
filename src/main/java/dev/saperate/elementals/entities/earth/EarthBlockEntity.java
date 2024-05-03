package dev.saperate.elementals.entities.earth;

import dev.saperate.elementals.utils.SapsUtils;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
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
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.List;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class EarthBlockEntity extends ProjectileEntity {
    private static final TrackedData<Integer> OWNER_ID = DataTracker.registerData(EarthBlockEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> IS_CONTROLLED = DataTracker.registerData(EarthBlockEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<BlockState> BLOCK_STATE = DataTracker.registerData(EarthBlockEntity.class, TrackedDataHandlerRegistry.BLOCK_STATE);
    public static final EntityType<EarthBlockEntity> EARTHBLOCK = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "earth_block"),
            FabricEntityTypeBuilder.<EarthBlockEntity>create(SpawnGroup.MISC, EarthBlockEntity::new)
                    .dimensions(EntityDimensions.fixed(1, 1)).build());


    public EarthBlockEntity(EntityType<EarthBlockEntity> type, World world) {
        super(type, world);
    }

    public EarthBlockEntity(World world, LivingEntity owner) {
        super(EARTHBLOCK, world);
        setOwner(owner);
        setPos(owner.getX(), owner.getY(), owner.getZ());
    }

    public EarthBlockEntity(World world, LivingEntity owner, double x, double y, double z) {
        super(EARTHBLOCK, world);
        setOwner(owner);
        setPos(x, y, z);
        setControlled(true);
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(OWNER_ID, 0);
        this.getDataTracker().startTracking(IS_CONTROLLED, false);
        this.getDataTracker().startTracking(BLOCK_STATE, Blocks.AIR.getDefaultState());
    }

    @Override
    public void tick() {
        super.tick();
        Entity owner = getOwner();
        if (owner == null) {
            this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
            this.move(MovementType.SELF, this.getVelocity());
            if (SapsUtils.checkBlockCollision(this, 0.05f,false) != null) {
                collidesWithGround();
            }

            return;
        }

        List<ProjectileEntity> projectiles = getWorld().getEntitiesByClass(ProjectileEntity.class,
                getWorld().isClient ? getBoundingBox().expand(.25f) : getBoundingBox().offset(getPos()).expand(.25f),
                ProjectileEntity::isAlive);

        for (ProjectileEntity e : projectiles) {
            if (!(e instanceof EarthBlockEntity)) {
                e.discard();
            }
        }


        if (!getIsControlled()) {
            HitResult hit = ProjectileUtil.getCollision(this, entity -> entity instanceof LivingEntity);
            if (hit.getType() == HitResult.Type.ENTITY) {
                LivingEntity entity = (LivingEntity) ((EntityHitResult) hit).getEntity();
                entity.damage(this.getDamageSources().playerAttack((PlayerEntity) owner), 2);
                entity.addVelocity(this.getVelocity().multiply(0.8f));
                discard();
            }
        }

        this.getWorld().getEntitiesByType(TypeFilter.instanceOf(PlayerEntity.class), this.getBoundingBox(), EntityPredicates.canBePushedBy(this)).forEach(this::pushAway);
        if (!owner.isSneaking()) {
            moveEntity(owner);
        }

    }

    private void moveEntity(Entity owner) {


        //gravity
        this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));

        if (getIsControlled()) {
            controlEntity(owner);
        } else if (!getWorld().isClient && SapsUtils.checkBlockCollision(this, 0.05f,false) != null) {
            if(getVelocity().lengthSquared() > 0.3){
                setVelocity(getVelocity().add(getVelocity().multiply(-0.1f)));
            }else {
                collidesWithGround();
            }
        }

        this.move(MovementType.SELF, this.getVelocity());
    }

    private void controlEntity(Entity owner) {
        Vector3f direction = getEntityLookVector(owner, 3)
                .subtract(0, 0.5f, 0)
                .subtract(getPos()).toVector3f();
        direction.mul(0.1f);

        if (direction.length() < 0.4f) {
            this.setVelocity(0, 0, 0);
        }


        this.addVelocity(direction.x, direction.y, direction.z);
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public boolean canHit() {
        return true;
    }

    public void collidesWithGround() {
        getWorld().setBlockState(
                new BlockPos(
                        getBlockX(),
                        (int) Math.round(getY()),
                        getBlockZ()
                ),
                getBlockState());
        discard();
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

    public BlockState getBlockState() {
        return this.getDataTracker().get(BLOCK_STATE);
    }

    public void setBlockState(BlockState state) {
        this.getDataTracker().set(BLOCK_STATE, state);
    }

    protected void pushAway(Entity entity) {
        entity.pushAwayFrom(this);
    }
}

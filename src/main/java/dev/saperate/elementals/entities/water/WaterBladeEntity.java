package dev.saperate.elementals.entities.water;

import dev.saperate.elementals.utils.SapsUtils;
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

public class WaterBladeEntity extends ProjectileEntity {
    private static final TrackedData<Integer> OWNER_ID = DataTracker.registerData(WaterBladeEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> IS_CONTROLLED = DataTracker.registerData(WaterBladeEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final EntityType<WaterBladeEntity> WATERBLADE = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "water_blade"),
            FabricEntityTypeBuilder.<WaterBladeEntity>create(SpawnGroup.MISC, WaterBladeEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 0.125f)).build());


    public WaterBladeEntity(EntityType<WaterBladeEntity> type, World world) {
        super(type, world);
    }

    public WaterBladeEntity(World world, LivingEntity owner) {
        super(WATERBLADE, world);
        setOwner(owner);
        setPos(owner.getX(), owner.getY(), owner.getZ());
    }

    public WaterBladeEntity(World world, LivingEntity owner, double x, double y, double z) {
        super(WATERBLADE, world);
        setOwner(owner);
        setPos(x, y, z);
        setControlled(true);
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(OWNER_ID, 0);
        this.getDataTracker().startTracking(IS_CONTROLLED, false);
    }

    @Override
    public void tick() {
        super.tick();

        BlockPos blockHit = SapsUtils.checkBlockCollision(this);

        Entity owner = getOwner();
        if (owner == null) {
            this.setVelocity(this.getVelocity().add(0.0, -0.02, 0.0));
            this.move(MovementType.SELF, this.getVelocity());
            if (blockHit != null) {
                collidesWithGround();
            }
            return;
        }


        if (blockHit != null) {
            if (getIsControlled()) {
                if (age % 5 == 0) {
                    summonParticles(this, random,
                            ParticleTypes.CLOUD,
                            0, 1, 0);
                    getWorld().setBlockBreakingInfo(getId(), blockHit, 5);
                }
            } else {
                collidesWithGround();
            }
        }


        HitResult hit = ProjectileUtil.getCollision(this, entity -> entity instanceof LivingEntity);
        if (hit.getType() == HitResult.Type.ENTITY) {
            LivingEntity entity = (LivingEntity) ((EntityHitResult) hit).getEntity();
            entity.damage(this.getDamageSources().playerAttack((PlayerEntity) owner), 2);
            if (!getIsControlled()) {
                entity.addVelocity(this.getVelocity().multiply(0.8f));
                discard();
            }
        }

        moveEntity(owner);
    }


    private void moveEntity(Entity owner) {


        //gravity
        this.setVelocity(this.getVelocity().add(0.0, -0.02, 0.0));

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
                .subtract(getPos()).toVector3f();
        direction.mul(0.25f);

        if (direction.length() < 0.6f) {
            this.setVelocity(0, 0, 0);
        }


        this.addVelocity(direction.x, direction.y, direction.z);
    }

    public void collidesWithGround() {
        //getWorld().setBlockState(getBlockPos(), Blocks.WATER.getDefaultState());
        discard();
    }

    @Override
    public void onRemoved() {
        summonParticles(this, random, ParticleTypes.SPLASH, 10, 100);
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

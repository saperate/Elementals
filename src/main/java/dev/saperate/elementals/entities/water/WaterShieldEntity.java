package dev.saperate.elementals.entities.water;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

public class WaterShieldEntity extends Entity {
    private static final TrackedData<Integer> OWNER_ID = DataTracker.registerData(WaterShieldEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public static final EntityType<WaterShieldEntity> WATERSHIELD = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "water_shield"),
            FabricEntityTypeBuilder.<WaterShieldEntity>create(SpawnGroup.MISC, WaterShieldEntity::new)
                    .dimensions(EntityDimensions.changing(2, 2)).build());


    public WaterShieldEntity(EntityType<WaterShieldEntity> type, World world) {
        super(type, world);
    }

    public WaterShieldEntity(World world, LivingEntity owner) {
        this(world, owner, owner.getX(), owner.getY(), owner.getZ());
    }

    public WaterShieldEntity(World world, LivingEntity owner, double x, double y, double z) {
        super(WATERSHIELD, world);
        setPos(x, y, z);
        setOwner(owner);
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(OWNER_ID, 0);
    }

    @Override
    public void tick() {
        super.tick();

        Entity owner = getOwner();

        if(owner == null){
            discard();
            return;
        }

        setPos(owner.getX(),owner.getY(),owner.getZ());



        List<ProjectileEntity> projectiles = getWorld().getEntitiesByClass(ProjectileEntity.class,
                getWorld().isClient ? getBoundingBox().expand(1.5f) : getBoundingBox().expand(1.5f).offset(getPos()),
                ProjectileEntity::isAlive);

        for (ProjectileEntity e : projectiles){
            e.discard();
        }
    }


    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public boolean canHit() {
        return true;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }
    public LivingEntity getOwner() {
        Entity owner = this.getWorld().getEntityById(this.getDataTracker().get(OWNER_ID));
        return (owner instanceof LivingEntity) ? (LivingEntity) owner : null;
    }

    public void setOwner(LivingEntity owner) {
        this.getDataTracker().set(OWNER_ID, owner.getId());
    }
}

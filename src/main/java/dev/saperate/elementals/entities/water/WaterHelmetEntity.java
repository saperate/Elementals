package dev.saperate.elementals.entities.water;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.List;

import static dev.saperate.elementals.effects.StationaryStatusEffect.STATIONARY_EFFECT;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;

public class WaterHelmetEntity extends Entity {

    private static final TrackedData<Integer> OWNER_ID = DataTracker.registerData(WaterHelmetEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public int aliveTicks = 0, maxLifeTime = 1000;

    public static final EntityType<WaterHelmetEntity> WATERHELMET = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "water_helmet"),
            FabricEntityTypeBuilder.<WaterHelmetEntity>create(SpawnGroup.MISC, WaterHelmetEntity::new)
                    .dimensions(EntityDimensions.changing(1, 1)).build());


    public WaterHelmetEntity(EntityType<WaterHelmetEntity> type, World world) {
        super(type, world);
    }

    public WaterHelmetEntity(World world, LivingEntity owner) {
        this(world, owner, owner.getX(), owner.getY(), owner.getZ());
    }

    public WaterHelmetEntity(World world, LivingEntity owner, double x, double y, double z) {
        super(WATERHELMET, world);
        setPos(x, owner.getEyeY(), z);
        setOwner(owner);
    }

    @Override
    protected void initDataTracker() {

        this.getDataTracker().startTracking(OWNER_ID, 0);
    }

    @Override
    public void tick() {
        super.tick();
        aliveTicks++;
        System.out.println(aliveTicks);
        LivingEntity owner = getOwner();
        if(owner == null || aliveTicks > maxLifeTime || !owner.isSubmergedInWater()){
            discard();
            return;
        }


        owner.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING,10,1,false,false,false));
        setPos(owner.getX(),owner.getEyeY() - 0.5f,owner.getZ()); // if you change any part of this check the renderer because it also modifies to entity pos
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

package dev.saperate.elementals.entities.fire;

import dev.saperate.elementals.entities.water.WaterCubeEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class FireBlockEntity extends Entity {
    private static final TrackedData<Float> HEIGHT = DataTracker.registerData(FireBlockEntity.class, TrackedDataHandlerRegistry.FLOAT);
    public Long creationTime;
    public static final EntityType<FireBlockEntity> FIREBLOCK = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "fire_block"),
            FabricEntityTypeBuilder.<FireBlockEntity>create(SpawnGroup.MISC, FireBlockEntity::new)
                    .dimensions(EntityDimensions.changing(1, 1)).build());


    public FireBlockEntity(EntityType<FireBlockEntity> type, World world) {
        super(type, world);
    }

    public FireBlockEntity(World world, LivingEntity owner) {
        this(world, owner, owner.getX(), owner.getY(), owner.getZ());
    }

    public FireBlockEntity(World world, LivingEntity owner, double x, double y, double z) {
        super(FIREBLOCK, world);
        setPos(x, y, z);
        creationTime = System.currentTimeMillis();
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(HEIGHT, 1f);
    }

    @Override
    public void tick() {
        super.tick();
        if (creationTime == null && !getWorld().isClient) {
            discard();
            return;
        }
        setFireHeight(2);
        HitResult hit = ProjectileUtil.getCollision(this, entity -> entity instanceof LivingEntity);
        if (hit.getType() == HitResult.Type.ENTITY) {
            LivingEntity entity = (LivingEntity) ((EntityHitResult) hit).getEntity();
            System.out.println(entity);
            entity.damage(this.getDamageSources().inFire(),1);
            entity.setOnFire(true);
        }
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }

    public float getFireHeight() {
        return this.dataTracker.get(HEIGHT);
    }

    public void setFireHeight(float h) {
        this.getDataTracker().set(HEIGHT, h);
    }

}

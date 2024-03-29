package dev.saperate.elementals.entities.fire;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
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

import static net.minecraft.entity.projectile.ProjectileUtil.getEntityCollision;

public class FireBlockEntity extends Entity {
    public static final int MAX_FLAME_SIZE = 5;
    private static final TrackedData<Float> HEIGHT = DataTracker.registerData(FireBlockEntity.class, TrackedDataHandlerRegistry.FLOAT);
    public Long creationTime;
    public float prevFlameSize = 0;
    public int heightAdjustSpeed = 10;//Smaller is faster
    public float pr =0;
    public static final EntityType<FireBlockEntity> FIREBLOCK = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "fire_block"),
            FabricEntityTypeBuilder.<FireBlockEntity>create(SpawnGroup.MISC, FireBlockEntity::new)
                    .dimensions(EntityDimensions.changing(1, MAX_FLAME_SIZE)).build());


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
        setFireHeight(MAX_FLAME_SIZE);
    }


    @Override
    public boolean damage(DamageSource source, float amount) {
        Entity entity = source.getSource();
        if (entity instanceof ProjectileEntity) {
            entity.discard();
        }
        return super.damage(source, amount);

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
        float diff = ( getFireHeight() - prevFlameSize ) / heightAdjustSpeed;
        prevFlameSize += diff;
        if (prevFlameSize > MAX_FLAME_SIZE - 1) {
            setFireHeight(1.5f); //Upgrade will go : 1.5 -> 2 -> 3 -> 4 -> 5
            heightAdjustSpeed = 5;
        }
        float h = getFireHeight();

        if(getWorld().getBlockState(getBlockPos()).getBlock().equals(Blocks.WATER)){
            this.discard();
        }

        List<LivingEntity> hits = getWorld().getEntitiesByClass(LivingEntity.class, getWorld().isClient ? getBoundingBox() : getBoundingBox().offset(getPos()), LivingEntity::isAlive);

        for (LivingEntity entity : hits) {
            if (!entity.isFireImmune() && entity.getY() - getY() < h) {
                if (!entity.isFireImmune()) {
                    entity.setOnFireFor(8);
                }
                entity.damage(getDamageSources().inFire(), 1.5f);//1.5f for normal, 2.5f for blue
            }
        }
    }

    @Override
    public void onDataTrackerUpdate(List<DataTracker.SerializedEntry<?>> dataEntries) {
        super.onDataTrackerUpdate(dataEntries);
        System.out.println(dataEntries);
    }



    public float getFireHeight() {
        return this.dataTracker.get(HEIGHT);
    }

    public void setFireHeight(float h) {
        this.getDataTracker().set(HEIGHT, h);
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



}

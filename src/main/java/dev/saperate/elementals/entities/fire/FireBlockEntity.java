package dev.saperate.elementals.entities.fire;

import com.mojang.datafixers.types.templates.Tag;
import dev.saperate.elementals.entities.water.WaterCubeEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.TagManagerLoader;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class FireBlockEntity extends Entity{
    public static final int MAX_FLAME_SIZE = 5;
    private static final TrackedData<Float> HEIGHT = DataTracker.registerData(FireBlockEntity.class, TrackedDataHandlerRegistry.FLOAT);
    public Long creationTime;
    public float prevFlameSize = 0;
    public int heightAdjustSpeed = 25;//Smaller is faster
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
        if(entity instanceof ProjectileEntity){
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
        if (prevFlameSize > MAX_FLAME_SIZE - 1) {
            setFireHeight(1.5f); //Upgrade will go : 1.5 -> 2 -> 3 -> 4 -> 5
            heightAdjustSpeed = 40;
        }

        float h = getFireHeight();
        EntityHitResult hit = ProjectileUtil.raycast(this, getPos(), getPos().add(0, h + 0.5f, 0), getBoundingBox(), entity -> entity instanceof Entity, h + 0.5f);
        if (hit != null) {
            if (hit.getType() == HitResult.Type.ENTITY) {
                if (hit.getEntity() instanceof LivingEntity entity) {
                    entity.damage(this.getDamageSources().inFire(), 1);
                    entity.setOnFire(true);
                }
            }
        }
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

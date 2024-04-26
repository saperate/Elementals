package dev.saperate.elementals.entities.water;

import dev.saperate.elementals.data.Bender;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;

import static dev.saperate.elementals.utils.SapsUtils.summonParticles;


public class WaterShieldEntity extends Entity {
    private static final TrackedData<Integer> OWNER_ID = DataTracker.registerData(WaterShieldEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public static final EntityType<WaterShieldEntity> WATERSHIELD = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "water_shield"),
            FabricEntityTypeBuilder.<WaterShieldEntity>create(SpawnGroup.MISC, WaterShieldEntity::new)
                    .dimensions(EntityDimensions.changing(3, 3)).build());

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

        PlayerEntity owner = getOwner();

        if(owner == null){
            discard();
            return;
        }

        setPos(owner.getX(),owner.getY(),owner.getZ());


        List<LivingEntity> hits = getWorld().getEntitiesByClass(LivingEntity.class,
                getWorld().isClient ? getBoundingBox() : getBoundingBox().offset(getPos()),
                LivingEntity::isAlive);

        for (LivingEntity entity : hits) {
           if(entity != owner){
               //Naughty entities can still somehow get inside the shield. This pushes them back out
               Vec3d direction = entity.getPos().add(0,1.5f,0).subtract(getPos()).multiply(0.1f);
               entity.setVelocity(getVelocity().add(direction));
           }
        }

        List<ProjectileEntity> projectiles = getWorld().getEntitiesByClass(ProjectileEntity.class,
                getWorld().isClient ? getBoundingBox().expand(1.5f) : getBoundingBox().expand(1.5f).offset(getPos()),
                ProjectileEntity::isAlive);

        for (ProjectileEntity e : projectiles){
            System.out.println("removbing entity ");
            Vec3d direction = e.getPos().add(0,1.7f,0).subtract(getPos()).multiply(0.1f);
            e.setVelocity(getVelocity().add(direction));
        }
    }

    @Override
    public void onRemoved() {
        summonParticles( this,random, ParticleTypes.SPLASH, 10,100);
        if(!this.getWorld().isClient){
            Bender bender = Bender.getBender(getOwner());
            if(bender != null){//Clean up the mess
                bender.abilityData = null;
                bender.currAbility.onRemove(bender);
            }
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


    public PlayerEntity getOwner() {
        Entity owner = this.getWorld().getEntityById(this.getDataTracker().get(OWNER_ID));
        return (owner instanceof LivingEntity) ? (PlayerEntity) owner : null;
    }

    public void setOwner(LivingEntity owner) {
        this.getDataTracker().set(OWNER_ID, owner.getId());
    }
}

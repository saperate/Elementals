package dev.saperate.elementals.entities.water;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;

public class WaterCubeEntity extends ProjectileEntity {
    private static final TrackedData<Integer> OWNER_ID = DataTracker.registerData(WaterCubeEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final EntityType<WaterCubeEntity> WATERCUBE = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "water_cube"),
            FabricEntityTypeBuilder.<WaterCubeEntity>create(SpawnGroup.MISC, WaterCubeEntity::new)
                    .dimensions(EntityDimensions.fixed(1, 1)).build());

    public WaterCubeEntity(EntityType<WaterCubeEntity> type, World world) {
        super(type, world);
    }

    public WaterCubeEntity(World world, LivingEntity owner) {
        super(WATERCUBE, world);
        setOwner(owner);
        setPos(owner.getX(), owner.getY(), owner.getZ());
    }

    public WaterCubeEntity(World world, LivingEntity owner, double x, double y, double z) {
        super(WATERCUBE, world);
        setOwner(owner);
        setPos(x, y, z);
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(OWNER_ID, 0);
    }

    @Override
    public void tick() {
        super.tick();
        Entity owner = getOwner();
        if (owner == null) {
            discard();
            return;
        }
        this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));



        Vector3f target = getEntityLookVector(owner, 3);

        this.addVelocity(target.x - getX(), target.y - getY(), target.z - getZ());
        this.move(MovementType.SELF, this.getVelocity());
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


    public LivingEntity getOwner() {
        Entity owner = this.getWorld().getEntityById(this.getDataTracker().get(OWNER_ID));
        return (owner instanceof LivingEntity) ? (LivingEntity) owner : null;
    }

    public void setOwner(LivingEntity owner) {
        this.getDataTracker().set(OWNER_ID, owner.getId());
    }
}

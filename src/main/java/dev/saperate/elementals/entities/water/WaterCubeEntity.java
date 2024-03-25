package dev.saperate.elementals.entities.water;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
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
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.summonParticles;

public class WaterCubeEntity extends ProjectileEntity {
    private static final TrackedData<Integer> OWNER_ID = DataTracker.registerData(WaterCubeEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> IS_CONTROLLED = DataTracker.registerData(WaterCubeEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
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
        if(!getIsControlled()){
            HitResult hit = ProjectileUtil.getCollision(this, entity -> entity instanceof LivingEntity);
            if (hit.getType() == HitResult.Type.ENTITY){
                LivingEntity entity = (LivingEntity) ((EntityHitResult) hit).getEntity();
                entity.damage(this.getDamageSources().playerAttack((PlayerEntity) owner),10);
                discard();
            }
        }


        moveEntity(owner);
    }

    private void moveEntity(Entity owner){
        this.getWorld().getEntitiesByType(TypeFilter.instanceOf(PlayerEntity.class), this.getBoundingBox(), EntityPredicates.canBePushedBy(this)).forEach(this::pushAway);


        //gravity
        this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));

        if(getIsControlled()){

            controlEntity(owner);
        }else if(!getWorld().isClient){
            BlockPos blockDown = getBlockPos().down();
            BlockState blockState = getWorld().getBlockState(blockDown);

            if(!blockState.isAir() && getY() - getBlockPos().getY() == 0){
                System.out.println(blockState);
                getWorld().setBlockState(getBlockPos(), Blocks.WATER.getDefaultState());
                discard();
            }
        }

        this.move(MovementType.SELF, this.getVelocity());
    }

    private void controlEntity(Entity owner){
        Vector3f direction = getEntityLookVector(owner, 3)
                .sub(0,0.5f,0)
                .sub(getPos().toVector3f());
        direction.mul(0.1f);

        if(direction.length() < 0.4f){
            this.setVelocity(0,0,0);
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

    public void setControlled(boolean val){
        this.getDataTracker().set(IS_CONTROLLED,val);
    }

    public boolean getIsControlled(){
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

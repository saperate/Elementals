package dev.saperate.elementals.entities.common;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import static dev.saperate.elementals.entities.ElementalEntities.DECOYPLAYER;

public class DecoyPlayerEntity extends PathfinderMob {
    public double prevCapeX, prevCapeY, prevCapeZ;
    public double capeX, capeY, capeZ;
    public static final EntityDataAccessor<Optional<UUID>> OWNER_ID = SynchedEntityData.defineId(DecoyPlayerEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    public static final EntityDataAccessor<String> OWNER_NAME = SynchedEntityData.defineId(DecoyPlayerEntity.class, EntityDataSerializers.STRING);
    private final NonNullList<ItemStack> items = NonNullList.withSize(6,ItemStack.EMPTY);;
    public static final EntityDataAccessor<Integer> RANGE = SynchedEntityData.defineId(DecoyPlayerEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> FOCUS_CAMERA = SynchedEntityData.defineId(DecoyPlayerEntity.class, EntityDataSerializers.BOOLEAN);
    public DecoyPlayerEntity(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
    }

    public DecoyPlayerEntity(Level world, Player owner) {
        super(DECOYPLAYER.get(), world);
        setOwner(owner);
        setPos(owner.getX(), owner.getY(), owner.getZ());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(OWNER_ID, Optional.empty());
        builder.define(OWNER_NAME, "");
        builder.define(RANGE,5);
        builder.define(FOCUS_CAMERA,false);
    }


    @Override
    public void tick() {
        super.tick();
        this.updateCapeAngles();
        if (getOwner() == null) {
            if (!level().isClientSide) {
                discard();
            }
            return;
        }

        if(SapsUtils.checkBlockCollision(this,-0.1f,false) != null){//checks if we are INSIDE a block
            setDeltaMovement(0,0,0);
        }

        //I have no idea why, but this prevents the entity from floating and being stuck, so it is staying
        this.xOld = this.getX();
        this.yOld = this.getY();
        this.zOld = this.getZ();
        Vec3 vec3d = this.getDeltaMovement();
        float f = this.getEyeHeight() - 0.11111111f;
        if (this.isInWater() && this.getFluidHeight(FluidTags.WATER) > (double) f) {
            this.applyWaterBuoyancy();
        } else if (this.isInLava() && this.getFluidHeight(FluidTags.LAVA) > (double) f) {
            this.applyLavaBuoyancy();
        } else if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.04, 0.0));
        }
        noPhysics  =  !this.level().noCollision(this, this.getBoundingBox().contract(1.0E-7, 1.0E-7, 1.0E-7));
        if (noPhysics) {
            setDeltaMovement(Vec3.ZERO);
        }
        if (!this.onGround() || this.getDeltaMovement().horizontalDistanceSqr() > (double) 1.0E-5f || (this.tickCount + this.getId()) % 4 == 0) {
            this.move(MoverType.SELF, this.getDeltaMovement());
            float g = 0.98f;
            if (this.onGround()) {
                g = this.level().getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getBlock().getFriction() * 0.98f;
            }
            this.setDeltaMovement(this.getDeltaMovement().multiply(g, 0.98, g));
            if (this.onGround()) {
                Vec3 vec3d2 = this.getDeltaMovement();
                if (vec3d2.y < 0.0) {
                    this.setDeltaMovement(vec3d2.multiply(1.0, -0.5, 1.0));
                }
            }
        }
        this.hurtMarked |= this.updateInWaterStateAndDoFluidPushing();
        if (!this.level().isClientSide && this.getDeltaMovement().subtract(vec3d).lengthSqr() > 0.01) {
            this.hurtMarked = true;
        }
    }
    
    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    @Override
    public boolean requiresCustomPersistence() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!level().isClientSide && getHealth() - amount <= 0) {
            Player owner = getOwner();
            if(owner == null){
                discard();
                return false;
            }
            Bender.getBender((ServerPlayer) owner).currAbility.onRemove(Bender.getBender((ServerPlayer) owner));
        }
        return super.hurt(source, amount);
    }
    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return items;
    }
    
    
    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        return items.get(slot.getIndex());
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
        items.set(slot.getIndex(), stack);
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    /**
     * <b>IMPORTANT NOTICE</b>
     * <br>this method will probably return null
     * <br>the only times it will not is if the owner is not in spectator, the owner is the client,
     * or we are on the server. Do NOT rely on this too heavily...
     */
    public Player getOwner() {
        UUID uuid = getOwnerUUID();
        if (uuid == null) {
            return null;
        }
        return level().getPlayerByUUID(uuid);
    }

    public UUID getOwnerUUID() {
        return getEntityData().get(OWNER_ID).orElse(null);
    }

    public void setOwner(Player owner) {
        this.getEntityData().set(OWNER_ID, Optional.of(owner.getUUID()));
        setOwnerName(owner);
    }

    public String getOwnerName() {
        return getEntityData().get(OWNER_NAME);
    }

    private void setOwnerName(Player owner) {
        this.getEntityData().set(OWNER_NAME, owner.getScoreboardName());
    }

    public void equipItemStack() {
        this.setItemSlot(EquipmentSlot.HEAD, items.get(0));
        this.setItemSlot(EquipmentSlot.CHEST, items.get(1));
        this.setItemSlot(EquipmentSlot.LEGS, items.get(2));
        this.setItemSlot(EquipmentSlot.FEET, items.get(3));

        this.setItemSlot(EquipmentSlot.MAINHAND, items.get(4));
        this.setItemSlot(EquipmentSlot.OFFHAND, items.get(5));
    }


    private void updateCapeAngles() {
        this.prevCapeX = this.capeX;
        this.prevCapeY = this.capeY;
        this.prevCapeZ = this.capeZ;
        double d = this.getX() - this.capeX;
        double e = this.getY() - this.capeY;
        double f = this.getZ() - this.capeZ;
        double g = 10.0;
        if (d > 10.0) {
            this.prevCapeX = this.capeX = this.getX();
        }
        if (f > 10.0) {
            this.prevCapeZ = this.capeZ = this.getZ();
        }
        if (e > 10.0) {
            this.prevCapeY = this.capeY = this.getY();
        }
        if (d < -10.0) {
            this.prevCapeX = this.capeX = this.getX();
        }
        if (f < -10.0) {
            this.prevCapeZ = this.capeZ = this.getZ();
        }
        if (e < -10.0) {
            this.prevCapeY = this.capeY = this.getY();
        }
        this.capeX += d * 0.25;
        this.capeZ += f * 0.25;
        this.capeY += e * 0.25;
    }

    private void applyWaterBuoyancy() {
        Vec3 vec3d = this.getDeltaMovement();
        this.setDeltaMovement(vec3d.x * (double) 0.99f, vec3d.y + (double) (vec3d.y < (double) 0.06f ? 5.0E-4f : 0.0f), vec3d.z * (double) 0.99f);
    }

    private void applyLavaBuoyancy() {
        Vec3 vec3d = this.getDeltaMovement();
        this.setDeltaMovement(vec3d.x * (double) 0.95f, vec3d.y + (double) (vec3d.y < (double) 0.06f ? 5.0E-4f : 0.0f), vec3d.z * (double) 0.95f);
    }

    public void setRange(int range) {
        this.entityData.set(RANGE, range);
    }

    public int getRange() {
        return this.entityData.get(RANGE);
    }

    public void setFocusCamera(boolean val) {
        this.entityData.set(FOCUS_CAMERA, val);
    }

    public boolean getFocusCamera() {
        return this.entityData.get(FOCUS_CAMERA);
    }

    @Override
    public float maxUpStep() {
        return 1.1f;
    }

}

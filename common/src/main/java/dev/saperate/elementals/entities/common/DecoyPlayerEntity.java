package dev.saperate.elementals.entities.common;

import com.google.common.collect.Lists;
import dev.saperate.elementals.commands.BendingCommand;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.entities.air.AirBallEntity;
import dev.saperate.elementals.utils.MathHelper;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.SynchedEntityData;
import net.minecraft.entity.data.EntityDataAccessor;
import net.minecraft.entity.data.EntityDataSerializers;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.Level;

import java.util.*;
import java.util.function.Predicate;

import static dev.saperate.elementals.entities.ElementalEntities.AIRBALL;
import static dev.saperate.elementals.entities.ElementalEntities.DECOYPLAYER;

public class DecoyPlayerEntity extends PathAwareEntity {
    public double prevCapeX, prevCapeY, prevCapeZ;
    public double capeX, capeY, capeZ;
    public static final EntityDataAccessor<Optional<UUID>> OWNER_ID = SynchedEntityData.defineId(DecoyPlayerEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    public static final EntityDataAccessor<String> OWNER_NAME = SynchedEntityData.defineId(DecoyPlayerEntity.class, EntityDataSerializers.STRING);
    private DefaultedList<ItemStack> items = DefaultedList.ofSize(7, ItemStack.EMPTY);
    public static final EntityDataAccessor<Integer> RANGE = SynchedEntityData.defineId(DecoyPlayerEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> FOCUS_CAMERA = SynchedEntityData.defineId(DecoyPlayerEntity.class, EntityDataSerializers.BOOLEAN);
    public DecoyPlayerEntity(EntityType<? extends PathAwareEntity> entityType, Level world) {
        super(entityType, world);
    }

    public DecoyPlayerEntity(Level world, Player owner) {
        super(DECOYPLAYER, world);
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
        if (tickCount <= 10 || true) {
            this.prevX = this.getX();
            this.prevY = this.getY();
            this.prevZ = this.getZ();
            Vec3 vec3d = this.getDeltaMovement();
            float f = this.getStandingEyeHeight() - 0.11111111f;
            if (this.isTouchingWater() && this.getFluidHeight(FluidTags.WATER) > (double) f) {
                this.applyWaterBuoyancy();
            } else if (this.isInLava() && this.getFluidHeight(FluidTags.LAVA) > (double) f) {
                this.applyLavaBuoyancy();
            } else if (!this.isNoGravity()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.04, 0.0));
            }
            if (this.level().isClientSide) {
                this.noClip = false;
            } else {
                boolean bl = this.noClip = !this.level().isSpaceEmpty(this, this.getBoundingBox().contract(1.0E-7));
                if (this.noClip) {
                    this.pushOutOfBlocks(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0, this.getZ());
                }
            }
            if (!this.isOnGround() || this.getDeltaMovement().horizontalLengthSquared() > (double) 1.0E-5f || (this.tickCount + this.getId()) % 4 == 0) {
                this.move(MoverType.SELF, this.getDeltaMovement());
                float g = 0.98f;
                if (this.isOnGround()) {
                    g = this.level().getBlockState(this.getVelocityAffectingPos()).getBlock().getSlipperiness() * 0.98f;
                }
                this.setDeltaMovement(this.getDeltaMovement().multiply(g, 0.98, g));
                if (this.isOnGround()) {
                    Vec3 vec3d2 = this.getDeltaMovement();
                    if (vec3d2.y < 0.0) {
                        this.setDeltaMovement(vec3d2.multiply(1.0, -0.5, 1.0));
                    }
                }
            }
            this.velocityDirty |= this.updateWaterState();
            if (!this.level().isClientSide && this.getDeltaMovement().subtract(vec3d).lengthSquared() > 0.01) {
                this.velocityDirty = true;
            }
        }
    }
    
    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    public boolean cannotDespawn() {
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
            Bender.getBender((ServerPlayerEntity) owner).currAbility.onRemove(Bender.getBender((ServerPlayerEntity) owner));
        }
        return super.hurt(source, amount);
    }


    @Override
    public Iterable<ItemStack> getArmorItems() {
        return items;
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        return items.get(slot.getArmorStandSlotId());
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {
        items.set(slot.getArmorStandSlotId(), stack);
    }

    @Override
    public Arm getMainArm() {
        return Arm.RIGHT;
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
        return level().getPlayerByUuid(uuid);
    }

    public UUID getOwnerUUID() {
        return getEntityData().get(OWNER_ID).orElse(null);
    }

    public void setOwner(Player owner) {
        this.getEntityData().set(OWNER_ID, Optional.of(owner.getUuid()));
        setOwnerName(owner);
    }

    public String getOwnerName() {
        return getEntityData().get(OWNER_NAME);
    }

    private void setOwnerName(Player owner) {
        this.getEntityData().set(OWNER_NAME, owner.getNameForScoreboard());
    }

    public void equipItemStack() {
        this.equipStack(EquipmentSlot.HEAD, items.get(0));
        this.equipStack(EquipmentSlot.CHEST, items.get(1));
        this.equipStack(EquipmentSlot.LEGS, items.get(2));
        this.equipStack(EquipmentSlot.FEET, items.get(3));

        this.equipStack(EquipmentSlot.MAINHAND, items.get(4));
        this.equipStack(EquipmentSlot.OFFHAND, items.get(5));
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
    public float getStepHeight() {
        return 1.1f;
    }
}

package dev.saperate.elementals.entities.common;

import com.google.common.collect.Lists;
import dev.saperate.elementals.commands.BendingCommand;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.entities.air.AirBallEntity;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.Arm;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.*;

import static dev.saperate.elementals.entities.ElementalEntities.AIRBALL;
import static dev.saperate.elementals.entities.ElementalEntities.DECOYPLAYER;

public class DecoyPlayerEntity extends PathAwareEntity {
    public double prevCapeX, prevCapeY, prevCapeZ;
    public double capeX, capeY, capeZ;
    public static final TrackedData<Optional<UUID>> OWNER_ID = DataTracker.registerData(DecoyPlayerEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    public static final TrackedData<String> OWNER_NAME = DataTracker.registerData(DecoyPlayerEntity.class, TrackedDataHandlerRegistry.STRING);
    private DefaultedList<ItemStack> items = DefaultedList.ofSize(6, ItemStack.EMPTY);
    public static final TrackedData<Integer> RANGE = DataTracker.registerData(DecoyPlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public DecoyPlayerEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    public DecoyPlayerEntity(World world, PlayerEntity owner) {
        super(DECOYPLAYER, world);
        setOwner(owner);
        setPos(owner.getX(), owner.getY(), owner.getZ());
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.getDataTracker().startTracking(OWNER_ID, Optional.empty());
        this.getDataTracker().startTracking(OWNER_NAME, "");
        this.getDataTracker().startTracking(RANGE,5);
    }


    @Override
    public void tick() {
        super.tick();
        this.updateCapeAngles();
        if (getOwner() == null) {
            if (!getWorld().isClient) {
                discard();
            }
            return;
        }

        preventOwnerFromGoingFar(getRange());

        if(SapsUtils.checkBlockCollision(this,-0.1f,false) != null){//checks if we are INSIDE a block
            setVelocity(0,0,0);
        }

        //I have no idea why, but this prevents the entity from floating and being stuck so it is staying
        if (age <= 10) {
            this.prevX = this.getX();
            this.prevY = this.getY();
            this.prevZ = this.getZ();
            Vec3d vec3d = this.getVelocity();
            float f = this.getStandingEyeHeight() - 0.11111111f;
            if (this.isTouchingWater() && this.getFluidHeight(FluidTags.WATER) > (double) f) {
                this.applyWaterBuoyancy();
            } else if (this.isInLava() && this.getFluidHeight(FluidTags.LAVA) > (double) f) {
                this.applyLavaBuoyancy();
            } else if (!this.hasNoGravity()) {
                this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
            }
            if (this.getWorld().isClient) {
                this.noClip = false;
            } else {
                boolean bl = this.noClip = !this.getWorld().isSpaceEmpty(this, this.getBoundingBox().contract(1.0E-7));
                if (this.noClip) {
                    this.pushOutOfBlocks(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0, this.getZ());
                }
            }
            if (!this.isOnGround() || this.getVelocity().horizontalLengthSquared() > (double) 1.0E-5f || (this.age + this.getId()) % 4 == 0) {
                this.move(MovementType.SELF, this.getVelocity());
                float g = 0.98f;
                if (this.isOnGround()) {
                    g = this.getWorld().getBlockState(this.getVelocityAffectingPos()).getBlock().getSlipperiness() * 0.98f;
                }
                this.setVelocity(this.getVelocity().multiply(g, 0.98, g));
                if (this.isOnGround()) {
                    Vec3d vec3d2 = this.getVelocity();
                    if (vec3d2.y < 0.0) {
                        this.setVelocity(vec3d2.multiply(1.0, -0.5, 1.0));
                    }
                }
            }
            this.velocityDirty |= this.updateWaterState();
            if (!this.getWorld().isClient && this.getVelocity().subtract(vec3d).lengthSquared() > 0.01) {
                this.velocityDirty = true;
            }
        }
    }


    public void preventOwnerFromGoingFar(int max) {
        Vec3d direction = getPos().subtract(getOwner().getPos());
        double distance = direction.length();
        if (distance > max) {
            if (distance > max * 10 && !getWorld().isClient) {
                getOwner().teleport(getX(), getY(), getZ());
            }


            direction = direction.multiply(distance - max).multiply(0.1f);


            double damping = 0.1f + (0.3f - 0.1f) * (1 - Math.min(1, distance / max));
            direction = direction.multiply(damping);

            getOwner().addVelocity(direction.x, direction.y, direction.z);

            getOwner().move(MovementType.SELF, getOwner().getVelocity());
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
    public boolean damage(DamageSource source, float amount) {
        if (!getWorld().isClient && getHealth() - amount <= 0) {
            PlayerEntity owner = getOwner();
            if(owner == null){
                discard();
                return false;
            }
            Bender.getBender(owner).currAbility.onRemove(Bender.getBender(owner));
            owner.damage(source,amount);
        }
        return super.damage(source, amount);
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
    public PlayerEntity getOwner() {
        UUID uuid = getOwnerUUID();
        if (uuid == null) {
            return null;
        }
        return getWorld().getPlayerByUuid(uuid);
    }

    public UUID getOwnerUUID() {
        return getDataTracker().get(OWNER_ID).orElse(null);
    }

    public void setOwner(PlayerEntity owner) {
        this.getDataTracker().set(OWNER_ID, Optional.of(owner.getUuid()));
        setOwnerName(owner);
    }

    public String getOwnerName() {
        return getDataTracker().get(OWNER_NAME);
    }

    private void setOwnerName(PlayerEntity owner) {
        this.getDataTracker().set(OWNER_NAME, owner.getEntityName());
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
        Vec3d vec3d = this.getVelocity();
        this.setVelocity(vec3d.x * (double) 0.99f, vec3d.y + (double) (vec3d.y < (double) 0.06f ? 5.0E-4f : 0.0f), vec3d.z * (double) 0.99f);
    }

    private void applyLavaBuoyancy() {
        Vec3d vec3d = this.getVelocity();
        this.setVelocity(vec3d.x * (double) 0.95f, vec3d.y + (double) (vec3d.y < (double) 0.06f ? 5.0E-4f : 0.0f), vec3d.z * (double) 0.95f);
    }

    public void setRange(int range) {
        this.dataTracker.set(RANGE, range);
    }

    public int getRange() {
        return this.dataTracker.get(RANGE);
    }
}

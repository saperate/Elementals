package dev.saperate.elementals.entities.common.sky_bison;

import dev.saperate.elementals.mixin.ElementalsLivingEntityAccessor;
import dev.saperate.elementals.utils.SapsUtils;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.gen.trunk.BendingTrunkPlacer;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SkyBisonEntity extends AnimalEntity implements GeoEntity {
    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
    public static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenPlay("idle");
    public static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    public static final RawAnimation FLY_ANIM = RawAnimation.begin().thenLoop("flying");
    public static final TrackedData<Boolean> FLYING = DataTracker.registerData(SkyBisonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<ItemStack> SADDLE = DataTracker.registerData(SkyBisonEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    
    public SkyBisonEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new SkyBisonMoveControl(this);
        setFlying(hasNoGravity());
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem().equals(Items.APPLE);
    }

    protected void initGoals() {
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.2));
        this.goalSelector.add(2, new SwimGoal(this));
        this.goalSelector.add(4, new FollowParentGoal(this, 1.0));
        this.goalSelector.add(6, new BisonWanderAroundGoal(this));
        this.goalSelector.add(7, new WanderAroundGoal(this,1f));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.goalSelector.add(3, new TemptGoal(this, 1.25, Ingredient.ofItems(new ItemConvertible[]{Items.APPLE}), false));
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(FLYING, false);
        builder.add(SADDLE, ItemStack.EMPTY);
    }



//TODO stamina system for flying?
    @Override
    public void tick() {
        if (hasNoGravity() && !isFlying()) {
            setNoGravity(false);
        } else if (!hasNoGravity() && isFlying()) {
            setNoGravity(true);
        }
        
        if(isOnGround()){
            setFlying(false);
        } else if (hasControllingPassenger()) {
            setFlying(true);
        }


        if(getRandom().nextInt(480) == 0 && !getWorld().isClient && !hasControllingPassenger()){
            setFlying(!isFlying());
            addVelocity(0,.1f,0);
            move(MovementType.SELF,getVelocity());
        }

        
        super.tick();
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack handStack = player.getStackInHand(hand);
        if(player.isSneaking()) {
            if (handStack.getItem() == Items.SADDLE) {
                setSaddle(handStack);
                player.setStackInHand(hand, ItemStack.EMPTY);
                return ActionResult.PASS;
            } else if (handStack.isEmpty() && getSaddle() != null) {
                player.setStackInHand(hand, getSaddle());
                setSaddle(ItemStack.EMPTY);
                return ActionResult.PASS;
            }
        }
        return tryRideMob(player);
    }

    public ActionResult tryRideMob(PlayerEntity player){
        if((getPassengerList().size() < 8 && hasSaddle()) || getPassengerList().isEmpty()){
            player.startRiding(this,true);
            return ActionResult.PASS;
        }
        return ActionResult.FAIL;
    }
    
    @Override
    public boolean damage(DamageSource source, float amount) {
        if(!source.isOf(DamageTypes.FALL)){
            return super.damage(source,amount);
        }
        return false;
    }
    
    public double getMountedHeightOffset() {
        return (double)getDimensions(getPose()).height() * 1;
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        if(getFirstPassenger() instanceof LivingEntity living){
            return living;
        }
        return null;
    }

    @Override
    protected Vec3d getControlledMovementInput(PlayerEntity controllingPlayer, Vec3d movementInput) {
        setBodyYaw(controllingPlayer.bodyYaw);
        
        Vec3d forward = SapsUtils.getEntityLookVectorIgnorePitch(controllingPlayer,1)
                .subtract(controllingPlayer.getEyePos());
        Vec3d sideways = forward.crossProduct(new Vec3d(0,1,0)).multiply(-controllingPlayer.sidewaysSpeed);
        
        //Can't inline it cause it's used to cross product sideways vector
        forward = forward.multiply(controllingPlayer.forwardSpeed);
        
        float h = 0;

        if(forward.length() != 0){
            float i = MathHelper.sin(controllingPlayer.getPitch() * 0.017453292F);
            if (controllingPlayer.forwardSpeed > 0.0F) {
                i *= -0.5F;
            }
            h = i * 3;
        }
        
        if (((ElementalsLivingEntityAccessor) controllingPlayer).isJumping()) {
            h += 0.5F;
            setFlying(true);
        }

        Vec3d movement = forward.add(sideways).add(0,h,0).normalize().multiply(3.9000000953674316 * 0.15f);
        setVelocity(movement);
        return movement;
    }

    //TODO require saddle only for multiple people 
    @Override
    protected void updatePassengerPosition(Entity passenger, PositionUpdater positionUpdater) {
        //If this broke, check CamelEntity for how to fix it
        if (this.hasPassenger(passenger)) {
            int passengerIndex = getPassengerIndex(passenger);
            Vec3d forward = SapsUtils.getEntityLookVectorIgnorePitch(this,1)
                    .subtract(getEyePos());
            Vec3d sideways = forward.crossProduct(new Vec3d(0,1,0))
                    .multiply(0.75f); // less annoying than doing
            double heightOffset = this.getY() + this.getMountedHeightOffset(); //+ passenger.getHeightOffset();

            Vec3d offset = switch (passengerIndex) {
                case 0 -> forward.multiply(2f);
                case 1 -> forward.add(sideways);
                case 2 -> forward.add(sideways.multiply(-1));
                case 3 -> sideways;
                case 4 -> sideways.multiply(-1);
                case 5 -> forward.multiply(-1.25f).add(sideways);
                case 6 -> forward.multiply(1.25f).add(sideways).multiply(-1);
                default -> Vec3d.ZERO;
            };

            positionUpdater.accept(passenger, this.getX() + offset.x, heightOffset, this.getZ() + offset.z);
        }
    }
    
    @Override
    protected Vec3d getPassengerAttachmentPos(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
        return super.getPassengerAttachmentPos(passenger, dimensions, scaleFactor);
    }
    
    
    public int getPassengerIndex(Entity passenger){
        List<Entity> passengers = getPassengerList();
        if(passengers.contains(passenger)){
            return passengers.indexOf(passenger);
        }
        return -1;
    }

    protected Vec2f getRotation(LivingEntity controllingEntity) {
        return new Vec2f(controllingEntity.getPitch() * 0.5F, controllingEntity.getYaw());
    }

    protected void tickControlled(PlayerEntity controllingPlayer, Vec3d movementInput) {
        super.tickControlled(controllingPlayer, movementInput);
        Vec2f vec2f = getRotation(controllingPlayer);
        float f = this.getYaw();
        float g = MathHelper.wrapDegrees(vec2f.y - f);
        float h = 0.08F;
        f += g * 0.08F;
        this.setRotation(f, vec2f.x);
        this.prevYaw = this.bodyYaw = this.headYaw = f;
    }
    
    @Override
    public void limitFallDistance() {
        this.fallDistance = 0;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Flying", 5, this::animationPredicate));
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2);
    }


    //Chooses which animation to use
    private PlayState animationPredicate(AnimationState<SkyBisonEntity> animationState) {

        if (animationState.isMoving()) {
            if(!animationState.getAnimatable().isOnGround() && false){
                animationState.getController().setAnimation(FLY_ANIM);
            }else{
                animationState.getController().setAnimation(WALK_ANIM);
            }
           
        } else {
            animationState.getController().setAnimation(IDLE_ANIM);
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableInstanceCache;
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    public void setFlying(boolean val) {
        this.dataTracker.set(FLYING, val);
        setNoGravity(val);
    }

    public boolean isFlying() {
        return this.dataTracker.get(FLYING);
    }

    public void setSaddle(ItemStack saddle) {
        this.dataTracker.set(SADDLE, saddle);
    }

    public ItemStack getSaddle() {
        return this.dataTracker.get(SADDLE);
    }
    
    public boolean hasSaddle(){
        return !getSaddle().isEmpty();
    }


    /**
     * This handles the bison's movement when wandering. We need our own custom one
     * since it can both fly and walk around
     */
    private static class BisonWanderAroundGoal extends Goal {

        private final SkyBisonEntity entity;
        private double targetX,targetY, targetZ;

        BisonWanderAroundGoal(SkyBisonEntity entity) {
            this.entity = entity;
        }

        public boolean canStart() {
            MoveControl moveControl = entity.getMoveControl();
            if(!entity.isFlying() || entity.hasPassengers()){
                return false;
            }else {
                if (!moveControl.isMoving()) {
                    return entity.getRandom().nextInt(toGoalTicks(120)) == 0;
                } else {
                    double x = moveControl.getTargetX() - entity.getX();
                    double y = moveControl.getTargetY() - entity.getY();
                    double z = moveControl.getTargetZ() - entity.getZ();
                    double d = x * x + y * y + z * z;
                    return d < 1.0 || d > 3600.0 // Thing below gives it a chance to abandon goal
                            || entity.getRandom().nextInt(toGoalTicks(240)) == 0;
                }
            }
        }

        private boolean wanderAroundGoalCanStart(){
            if (entity.hasPassengers()) {
                return false;
            } else {
                    if (entity.getRandom().nextInt(toGoalTicks(120)) != 0) {
                        return false;
                    }

                Vec3d vec3d = this.getWanderTarget();
                if (vec3d == null) {
                    return false;
                } else {
                    this.targetX = vec3d.x;
                    this.targetY = vec3d.y;
                    this.targetZ = vec3d.z;
                    return true;
                }
            }
        }
        public void start() {
            if(entity.isFlying()) {
                Vec3d target = getWanderTarget();
                if (target != null) {
                    entity.getMoveControl().moveTo(target.x, target.y, target.z, 0.8f);
                }
            }else{
                entity.getMoveControl().moveTo(targetX,targetY,targetZ,1.2f);
            }
        }

        public void stop() {
            entity.getNavigation().stop();
            super.stop();
        }

        @Nullable
        protected Vec3d getWanderTarget() {
            if (entity.isFlying()) {
                Random random = entity.getRandom();
                double x = entity.getX() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
                double y = entity.getY() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
                double z = entity.getZ() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
                return new Vec3d(x, y, z);
            }
            return NoPenaltyTargeting.find(entity, 10, 7);
        }
    }

    //Flying code taken from vanilla
    private static class SkyBisonMoveControl extends MoveControl {
        private final SkyBisonEntity entity;
        private int collisionCheckCooldown;

        public SkyBisonMoveControl(SkyBisonEntity entity) {
            super(entity);
            this.entity = entity;
            state = State.MOVE_TO;
        }


        public void tick() {
            if(entity.hasControllingPassenger()){
                return;
            }
            if (state == State.MOVE_TO) {
                if (entity.isFlying()) {
                    if (this.collisionCheckCooldown-- <= 0) {
                        this.collisionCheckCooldown += entity.getRandom().nextInt(5) + 2;
                        Vec3d vec3d = getDistanceToTarget();
                        double d = vec3d.length();
                        vec3d = vec3d.normalize();
                        if (this.willCollide(vec3d, MathHelper.ceil(d))) {
                            entity.setVelocity(entity.getVelocity().add(vec3d.multiply(0.1)));
                            entity.move(MovementType.SELF, entity.getVelocity());
                            
                        } else {
                            state = State.WAIT;
                        }
                    }
                }
            }
            
            entity.setYaw(SapsUtils.calculateYaw(entity.getVelocity()));
            if(!entity.isFlying()){
                super.tick();   
            }
        }

        private boolean willCollide(Vec3d direction, int steps) {
            Box box = entity.getBoundingBox();

            for (int i = 1; i < steps; ++i) {
                box = box.offset(direction);

                if (!entity.getWorld().isSpaceEmpty(entity, box)) {
                    return false;
                }
            }

            return true;
        }

        public Vec3d getDistanceToTarget() {
            return new Vec3d(this.targetX - this.entity.getX(), this.targetY - this.entity.getY(), this.targetZ - this.entity.getZ());
        }
    }
}

package dev.saperate.elementals.entities.common.sky_bison;

import dev.saperate.elementals.mixin.ElementalsLivingEntityAccessor;
import dev.saperate.elementals.utils.MathHelper;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class SkyBisonEntity extends Animal implements GeoEntity {
    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
    public static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenPlay("idle");
    public static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    public static final RawAnimation FLY_ANIM = RawAnimation.begin().thenLoop("flying");
    public static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(SkyBisonEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<ItemStack> SADDLE = SynchedEntityData.defineId(SkyBisonEntity.class, EntityDataSerializers.ITEM_STACK);
    
    public SkyBisonEntity(EntityType<? extends Animal> entityType, Level world) {
        super(entityType, world);
        this.moveControl = new SkyBisonMoveControl(this);
        setFlying(isNoGravity());
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.getItem().equals(Items.APPLE);
    }

    protected void initGoals() {
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.2));
        this.goalSelector.addGoal(2, new FloatGoal(this));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.0));
        this.goalSelector.addGoal(6, new BisonWanderAroundGoal(this));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this,1f));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.25, Ingredient.of(Items.APPLE), false));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(FLYING, false);
        builder.define(SADDLE, ItemStack.EMPTY);
    }



//TODO stamina system for flying?
    @Override
    public void tick() {
        if (isNoGravity() && !isFlying()) {
            setNoGravity(false);
        } else if (!isNoGravity() && isFlying()) {
            setNoGravity(true);
        }
        
        if(onGround()){
            setFlying(false);
        } else if (hasControllingPassenger()) {
            setFlying(true);
        }


        if(getRandom().nextInt(480) == 0 && !level().isClientSide && !hasControllingPassenger()){
            setFlying(!isFlying());
            addDeltaMovement(new Vec3(0,.1f,0));
            move(MoverType.SELF,getDeltaMovement());
        }

        
        super.tick();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack handStack = player.getItemInHand(hand);
        if(player.isShiftKeyDown()) {
            if (handStack.getItem() == Items.SADDLE) {
                setSaddle(handStack);
                player.setItemInHand(hand, ItemStack.EMPTY);
                return InteractionResult.PASS;
            } else if (handStack.isEmpty() && getSaddle() != null) {
                player.setItemInHand(hand, getSaddle());
                setSaddle(ItemStack.EMPTY);
                return InteractionResult.PASS;
            }
        }
        return tryRideMob(player);
    }

    public InteractionResult tryRideMob(Player player){
        if((getPassengers().size() < 8 && hasSaddle()) || getPassengers().isEmpty()){
            player.startRiding(this,true);
            return InteractionResult.PASS;
        }
        return InteractionResult.FAIL;
    }
    
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if(!source.is(DamageTypes.FALL)){
            return super.hurt(source,amount);
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
    protected Vec3 getRiddenInput(Player controllingPlayer, Vec3 movementInput) {
        setYBodyRot(controllingPlayer.yBodyRot);
        
        Vec3 forward = SapsUtils.getEntityLookVectorIgnorePitch(controllingPlayer,1)
                .subtract(controllingPlayer.getEyePosition());
        Vec3 sideways = forward.cross(new Vec3(0,1,0)).scale(-controllingPlayer.xxa);
        
        //Can't inline it cause it's used to cross product sideways vector
        forward = forward.scale(controllingPlayer.zza);
        
        float h = 0;

        if(forward.length() != 0){
            float i = Mth.sin(controllingPlayer.getXRot() * 0.017453292F);
            if (controllingPlayer.zza > 0.0F) {
                i *= -0.5F;
            }
            h = i * 3;
        }
        
        if (((ElementalsLivingEntityAccessor) controllingPlayer).isJumping()) {
            h += 0.5F;
            setFlying(true);
        }

        Vec3 movement = forward.add(sideways).add(0,h,0).normalize().scale(3.9000000953674316 * 0.15f);
        setDeltaMovement(movement);
        return movement;
    }

    //TODO require saddle only for multiple people 
    @Override
    protected void positionRider(Entity passenger, Entity.MoveFunction positionUpdater) {
        //If this broke, check CamelEntity for how to fix it
        if (this.hasPassenger(passenger)) {
            int passengerIndex = getPassengerIndex(passenger);
            Vec3 forward = SapsUtils.getEntityLookVectorIgnorePitch(this,1)
                    .subtract(getEyePosition());
            Vec3 sideways = forward.cross(new Vec3(0,1,0))
                    .scale(0.75f); // less annoying than doing
            double heightOffset = this.getY() + this.getMountedHeightOffset(); //+ passenger.getHeightOffset();

            Vec3 offset = switch (passengerIndex) {
                case 0 -> forward.scale(2f);
                case 1 -> forward.add(sideways);
                case 2 -> forward.add(sideways.scale(-1));
                case 3 -> sideways;
                case 4 -> sideways.scale(-1);
                case 5 -> forward.scale(-1.25f).add(sideways);
                case 6 -> forward.scale(1.25f).add(sideways).scale(-1);
                default -> Vec3.ZERO;
            };

            positionUpdater.accept(passenger, this.getX() + offset.x, heightOffset, this.getZ() + offset.z);
        }
    }
    
    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
        return super.getPassengerAttachmentPoint(passenger, dimensions, scaleFactor);
    }
    
    
    public int getPassengerIndex(Entity passenger){
        List<Entity> passengers = getPassengers();
        if(passengers.contains(passenger)){
            return passengers.indexOf(passenger);
        }
        return -1;
    }

    protected Vec2 getRotation(LivingEntity controllingEntity) {
        return new Vec2(controllingEntity.getXRot() * 0.5F, controllingEntity.getYRot());
    }

    @Override
    protected void tickRidden(Player controllingPlayer, Vec3 movementInput) {
        super.tickRidden(controllingPlayer, movementInput);
        Vec2 vec2f = getRotation(controllingPlayer);
        float f = this.getYRot();
        float g = Mth.wrapDegrees(vec2f.y - f);
        float h = 0.08F;
        f += g * 0.08F;
        this.setRot(f, vec2f.x);
        this.yOld = this.yBodyRot = this.yHeadRot = f;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
        this.fallDistance = 0;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Flying", 5, this::animationPredicate));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40)
                .add(Attributes.MOVEMENT_SPEED, 0.2);
    }


    //Chooses which animation to use
    private PlayState animationPredicate(AnimationState<SkyBisonEntity> animationState) {

        if (animationState.isMoving()) {
            if(!animationState.getAnimatable().onGround()){
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

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    public void setFlying(boolean val) {
        this.entityData.set(FLYING, val);
        setNoGravity(val);
    }

    public boolean isFlying() {
        return this.entityData.get(FLYING);
    }

    public void setSaddle(ItemStack saddle) {
        this.entityData.set(SADDLE, saddle);
    }

    public ItemStack getSaddle() {
        return this.entityData.get(SADDLE);
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

        @Override
        public boolean canUse() {
            MoveControl moveControl = entity.getMoveControl();
            if(!entity.isFlying() || entity.isVehicle()){
                return false;
            }else {
                if (!moveControl.hasWanted()) {
                    return entity.getRandom().nextInt(reducedTickDelay(120)) == 0;
                } else {
                    double x = moveControl.getWantedX() - entity.getX();
                    double y = moveControl.getWantedY() - entity.getY();
                    double z = moveControl.getWantedZ() - entity.getZ();
                    double d = x * x + y * y + z * z;
                    return d < 1.0 || d > 3600.0 // Thing below gives it a chance to abandon goal
                            || entity.getRandom().nextInt(reducedTickDelay(240)) == 0;
                }
            }
        }
        private boolean wanderAroundGoalCanStart(){
            if (entity.isVehicle()) {
                return false;
            } else {
                    if (entity.getRandom().nextInt(reducedTickDelay(120)) != 0) {
                        return false;
                    }

                Vec3 vec3d = this.getWanderTarget();
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
                Vec3 target = getWanderTarget();
                if (target != null) {
                    entity.getMoveControl().setWantedPosition(target.x, target.y, target.z, 0.8f);
                }
            }else{
                entity.getMoveControl().setWantedPosition(targetX,targetY,targetZ,1.2f);
            }
        }

        public void stop() {
            entity.getNavigation().stop();
            super.stop();
        }

        @Nullable
        protected Vec3 getWanderTarget() {
            if (entity.isFlying()) {
                RandomSource random = entity.getRandom();
                double x = entity.getX() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
                double y = entity.getY() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
                double z = entity.getZ() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
                return new Vec3(x, y, z);
            }
            return DefaultRandomPos.getPos(entity, 10, 7);
        }
    }

    //Flying code taken from vanilla
    private static class SkyBisonMoveControl extends MoveControl {
        private final SkyBisonEntity entity;
        private int collisionCheckCooldown;

        public SkyBisonMoveControl(SkyBisonEntity entity) {
            super(entity);
            this.entity = entity;
            operation = Operation.MOVE_TO;
        }


        public void tick() {
            if(entity.hasControllingPassenger()){
                return;
            }
            if (operation == Operation.MOVE_TO) {
                if (entity.isFlying()) {
                    if (this.collisionCheckCooldown-- <= 0) {
                        this.collisionCheckCooldown += entity.getRandom().nextInt(5) + 2;
                        Vec3 vec3d = getDistanceToTarget();
                        double d = vec3d.length();
                        vec3d = vec3d.normalize();
                        if (this.willCollide(vec3d, Mth.ceil(d))) {
                            entity.setDeltaMovement(entity.getDeltaMovement().add(vec3d.scale(0.1)));
                            entity.move(MoverType.SELF, entity.getDeltaMovement());
                            
                        } else {
                            operation = Operation.WAIT;
                        }
                    }
                }
            }
            
            entity.setYRot(SapsUtils.calculateYaw(entity.getDeltaMovement()));
            if(!entity.isFlying()){
                super.tick();   
            }
        }

        private boolean willCollide(Vec3 direction, int steps) {
            AABB box = entity.getBoundingBox();

            for (int i = 1; i < steps; ++i) {
                box = box.move(direction);

                if (!entity.level().noCollision(entity, box)) {
                    return false;
                }
            }

            return true;
        }

        public Vec3 getDistanceToTarget() {
            return new Vec3(this.getWantedX() - this.entity.getX(), 
                    this.getWantedY() - this.entity.getY(), 
                    this.getWantedZ() - this.entity.getZ());
        }
    }
}

package dev.saperate.elementals.items;

import dev.saperate.elementals.Elementals;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.*;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

//TODO make colorable!!
public class GliderItem extends Item implements GeoItem {
    private final RawAnimation OPENED_ANIM = RawAnimation.begin().thenPlayAndHold("opened.glider");
    private final RawAnimation CLOSED_ANIM = RawAnimation.begin().thenPlayAndHold("closed.glider");
    private final RawAnimation OPEN_ANIM = RawAnimation.begin().thenPlay("open.glider");
    private final RawAnimation CLOSE_ANIM = RawAnimation.begin().thenPlay("close.glider");
    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);

    public GliderItem(Settings settings) {
        super(settings);

        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        user.getItemCooldownManager().set(this, 5);
        if (!world.isClient) {
            switch (getState(stack)) { //Handles the switch between open and closed.
                case OPEN -> {
                    setState(stack, GliderStates.CLOSED);
                    
                    triggerAnim(user,GeoItem.getOrAssignId(stack, (ServerWorld) world),
                            "controller","close"
                    );
                }
                case CLOSED -> {
                    setState(stack, GliderStates.OPEN);

                    triggerAnim(user,GeoItem.getOrAssignId(stack, (ServerWorld) world),
                            "controller","open"
                    );
                }
                default -> {
                    return TypedActionResult.fail(stack);
                }
            }
        }
        return TypedActionResult.success(stack);
    }


    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(Elementals.GLIDER_ITEM_RENDER_PROVIDER.Create());
    }


    //Chooses which animation to use
    private PlayState animationPredicate(AnimationState<GliderItem> animationState) {
        ItemStack stack = animationState.getData(DataTickets.ITEMSTACK);
        GliderStates gliderState = getState(stack);

        switch (gliderState) {
            case OPEN -> animationState.setAnimation(OPENED_ANIM);
            case CLOSED -> animationState.setAnimation(CLOSED_ANIM);
        }


        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(
                this,
                "controller",
                0, this::animationPredicate)
                .triggerableAnim("open", OPEN_ANIM)
                .triggerableAnim("close", CLOSE_ANIM)
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableInstanceCache;
    }

    public GliderStates getState(ItemStack stack) {
        NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (customData == null || !GliderStates.isValid(customData.copyNbt().getString("state"))) {
            return GliderStates.CLOSED;
        }
        return GliderStates.valueOf(customData.copyNbt().getString("state").toUpperCase(Locale.ROOT));
    }

    public void setState(ItemStack stack, GliderStates state) {
        NbtComponent component = stack.get(DataComponentTypes.CUSTOM_DATA);
        if(stack.getHolder() == null){
            return;
        }
        NbtCompound data;
        if(component != null){
            data = component.copyNbt();
        }else{
            data = new NbtCompound();
        }
        data.putString("state", state.id);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(data));
        updateStateChangeTick(stack);
    }
    

    private void updateStateChangeTick(ItemStack stack) {
        NbtComponent component = stack.get(DataComponentTypes.CUSTOM_DATA);
        if(stack.getHolder() == null){
            return;
        }
        NbtCompound data;
        if(component != null){
            data = component.copyNbt();
        }else{
            data = new NbtCompound();
        }
        data.putDouble("tickAtStateChange", stack.getHolder().age);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(data));
    }

    public double timeSinceStateChange(ItemStack stack) {
        NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (customData == null || stack.getHolder() == null) {
            return Double.MAX_VALUE;
        }
        return Math.max(stack.getHolder().age - customData.copyNbt().getDouble("tickAtStateChange"), 0);
    }
    

    public enum GliderStates {
        OPEN("open"),
        CLOSED("closed");

        public final String id;

        GliderStates(String id) {
            this.id = id;
        }

        public static boolean isValid(String id) {
            for (GliderStates state : values()) {
                if (state.id.equalsIgnoreCase(id)) {
                    return true;
                }
            }
            return false;
        }
    }
}
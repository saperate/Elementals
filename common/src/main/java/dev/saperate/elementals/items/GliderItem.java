package dev.saperate.elementals.items;

import dev.saperate.elementals.Elementals;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
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

    public GliderItem(Properties settings) {
        super(settings);

        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);

        user.getCooldowns().addCooldown(this, 5);
        if (!level.isClientSide) {
            switch (getState(stack)) { //Handles the switch between open and closed.
                case OPEN -> {
                    setState(stack, GliderStates.CLOSED, (ServerLevel) level);
                    
                    triggerAnim(user,GeoItem.getOrAssignId(stack, (ServerLevel) level),
                            "controller","close"
                    );
                }
                case CLOSED -> {
                    setState(stack, GliderStates.OPEN, (ServerLevel) level);

                    triggerAnim(user,GeoItem.getOrAssignId(stack, (ServerLevel) level),
                            "controller","open"
                    );
                }
                default -> {
                    return InteractionResultHolder.fail(stack);
                }
            }
        }
        return InteractionResultHolder.success(stack);
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
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null || !GliderStates.isValid(customData.copyTag().getString("state"))) {
            return GliderStates.CLOSED;
        }
        return GliderStates.valueOf(customData.copyTag().getString("state").toUpperCase(Locale.ROOT));
    }

    public void setState(ItemStack stack, GliderStates state, ServerLevel world) {
        CustomData component = stack.get(DataComponents.CUSTOM_DATA);
        CompoundTag data;
        if(component != null){
            data = component.copyTag();
        }else{
            data = new CompoundTag();
        }
        data.putString("state", state.id);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(data));
        updateStateChangeTick(stack, world);
    }
    

    private void updateStateChangeTick(ItemStack stack, Level level) {
        CustomData component = stack.get(DataComponents.CUSTOM_DATA);
        CompoundTag data;
        if(component != null){
            data = component.copyTag();
        }else{
            data = new CompoundTag();
        }
        data.putDouble("tickAtStateChange", level.getGameTime());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(data));
    }

    public double timeSinceStateChange(ItemStack stack, Level world) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return Double.MAX_VALUE;
        }
        return Math.max(world.getGameTime() - customData.copyTag().getDouble("tickAtStateChange"), 0);
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
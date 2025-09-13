package dev.saperate.elementals.items.glider;

import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Vanishable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.DataTicket;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtils;
import net.minecraft.item.DyeableItem;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

//TODO make colorable!!
public class GliderItem extends Item implements Vanishable, GeoItem {
    private final RawAnimation OPENED_ANIM = RawAnimation.begin().thenPlayAndHold("open.glider");
    private final RawAnimation CLOSED_ANIM = RawAnimation.begin().thenPlayAndHold("close.glider");
    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
    private final  Supplier<Object> renderer = GeoItem.makeRenderer(this);
    
    public GliderItem(Settings settings) {
        super(settings);

        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override 
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand); //TODO shift to change state, otherwise wear/unwear (to glide)
        
        user.getItemCooldownManager().set(this, 5);
        switch (getState(stack)){ //Handles the switch between open and closed.
            case OPEN -> setState(stack,GliderStates.CLOSED);
            case CLOSED -> setState(stack,GliderStates.OPEN);
            default -> {
                return TypedActionResult.fail(stack);
            }
        }
        return TypedActionResult.success(stack);
    }



    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(Elementals.GLIDER_ITEM_RENDER_PROVIDER.Create());
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        return renderer;
    }

    @Override
    public double getTick(Object itemStack) {
        return RenderUtils.getCurrentTick();
    }

    //Chooses which animation to use
    private PlayState animationPredicate(AnimationState<GliderItem> animationState){
        ItemStack stack = animationState.getData(DataTickets.ITEMSTACK);
        GliderStates gliderState = getState(stack);

        switch (gliderState) {
            case OPEN -> animationState.getController().setAnimation(OPENED_ANIM);
            case CLOSED -> animationState.getController().setAnimation(CLOSED_ANIM);
        }
        
        return PlayState.CONTINUE;
    }
    
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(
                this,
                "controller",
                0, this::animationPredicate)
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableInstanceCache;
    }

    public GliderStates getState(ItemStack stack){
        if(stack.getNbt() == null || !GliderStates.isValid(stack.getNbt().getString("state"))){
            NbtCompound nbt = new NbtCompound();
            nbt.putString("state", GliderStates.CLOSED.id);
            stack.setNbt(nbt);
        }
        return GliderStates.valueOf(stack.getNbt().getString("state").toUpperCase(Locale.getDefault()));
    }

    public void setState(ItemStack stack, GliderStates state){
        if(stack.getNbt() == null){
            NbtCompound nbt = new NbtCompound();
            stack.setNbt(nbt);
        }
        stack.getNbt().putString("state", state.id);
        updateStateChangeTick(stack);
    }

    private void updateStateChangeTick(ItemStack stack){
        if(stack.getNbt() == null ){
            NbtCompound nbt = new NbtCompound();
            stack.setNbt(nbt);
        }
        stack.getNbt().putDouble("tickAtStateChange",getTick(stack));
    }
    
    public double timeSinceStateChange(ItemStack stack){
        if(stack.getNbt() == null ){
            NbtCompound nbt = new NbtCompound();
            nbt.putDouble("tickAtStateChange", 0);
            stack.setNbt(nbt);
        }
        return getTick(stack) - stack.getNbt().getDouble("tickAtStateChange");
    }

    
    public enum GliderStates {
        OPEN("open"),
        CLOSED("closed");
        
        public final String id;
        
        GliderStates(String id){
            this.id = id;
        }
        
        public static boolean isValid(String id){
            for (GliderStates state : values()) {
                if(state.id.equalsIgnoreCase(id)){
                    return true;
                }
            }
            return false;
        }
    }
}
package dev.saperate.elementals.items;

import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.effects.ElementalsStatusEffects;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

//FIXME port the geckolib impl
public class MetalArmorItem extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);
    private static final String ITEMS_KEY = "Items";
    public static final int MAX_STORAGE = 1;


    public MetalArmorItem(RegistryEntry<ArmorMaterial> armorMaterial, Type type, Settings settings) {
        super(armorMaterial, type, settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if(slot != 0){
            return;
        }
        if(entity instanceof LivingEntity living){//Not inlining since i might need that later
            if(entity instanceof PlayerEntity player){
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS,60, 3, false, false, false));
                player.addStatusEffect(new StatusEffectInstance(ElementalsStatusEffects.DENSE,120,10, false, false, false));
            }
            //TODO figure out how to add armor points
        }
    }
    
    /**
     * Stores the previous armor and colors the new one then returns the new armor
     * @param prevArmor The previous armor the player was wearing
     * @param standingBlock The block the player is standing on
     * @return the new armor as an item stack
     */
    public ItemStack getItemStack(ItemStack prevArmor, Block standingBlock) {
        
    }


    @Override
    public boolean hasGlint(ItemStack stack) {
        return false;
    }


    static int darkenColor(int col, int amt) {
        int r = Math.max((col >> 16), amt);
        int b = Math.max(((col >> 8) & 0x00FF), amt);
        int g = Math.max((col & 0x0000FF), amt);
        return g | (b << 8) | (r << 16);
    }

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(Elementals.METAL_ARMOR_RENDER_PROVIDER.Create());
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        return renderProvider;
    }
    

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableInstanceCache;
    }


}

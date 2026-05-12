package dev.saperate.elementals;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static dev.saperate.elementals.Constants.MODID;

@Mod(Constants.MODID)
public class ElementalsForge {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID);
    public ElementalsForge(FMLJavaModLoadingContext context) {
        Elementals.init();
        
        MOB_EFFECTS.register(context.getModEventBus());
    }
    
}
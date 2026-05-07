package dev.saperate.elementals.commands;

import dev.saperate.elementals.Constants;
import dev.saperate.elementals.platform.Services;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class ElementalsCommands {
    private static void registerCommands() {
        Services.REGISTRY.registerCommands();
        
        Registry.register(
                BuiltInRegistries.COMMAND_ARGUMENT_TYPE,
                ResourceLocation.fromNamespaceAndPath(Constants.MODID, "bending"),
                SingletonArgumentInfo.contextFree(ElementArgumentType::element)
                );
    }

}

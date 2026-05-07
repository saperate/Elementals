package dev.saperate.elementals.mixin;

import com.google.gson.JsonElement;
import dev.saperate.elementals.items.ElementalsDynamicRecipes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.util.perf.Profiler;

import java.util.Map;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {

    @Inject(method = "apply", at = @At("HEAD"))
    public void interceptApply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, Profiler profiler, CallbackInfo info) {
        for (ElementalsDynamicRecipes.RecipeEntry entry : ElementalsDynamicRecipes.entries ){
            map.put(entry.ID, entry.object);
        }
    }

}

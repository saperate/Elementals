package dev.saperate.elementals.mixin.client;

import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.sugar.Local;
import dev.saperate.elementals.client.ElementalsClient;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

/**
 * From Fabric-API {5/8/2026}
 * Use their license for this file
 */
@Mixin(LayerDefinitions.class)
abstract class EntityModelsMixin {
    @Inject(method = "createRoots", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap$Builder;build()Lcom/google/common/collect/ImmutableMap;", remap = false))
    private static void registerExtraModelData(CallbackInfoReturnable<Map<ModelLayerLocation, LayerDefinition>> info, @Local(name = "builder") ImmutableMap.Builder<ModelLayerLocation, LayerDefinition> builder) {
        for (Map.Entry<ModelLayerLocation, LayerDefinition> entry : ElementalsClient.getModels().entrySet()) {
            builder.put(entry.getKey(), entry.getValue());
        }
    }
}

package dev.saperate.elementals.mixin.client;

import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Set;

@Mixin(ModelLayers.class)
public interface ModelLayersAccessor {
    @Accessor("ALL_MODELS")
    Set<ModelLayerLocation> getAllModels();
}

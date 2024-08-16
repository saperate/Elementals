package dev.saperate.elementals.mixin.client;

import dev.saperate.elementals.entities.earth.EarthBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.saperate.elementals.Elementals.MODID;
import static dev.saperate.elementals.effects.SeismicSenseStatusEffect.SEISMIC_SENSE_EFFECT;
import static dev.saperate.elementals.utils.ClientUtils.safeHasStatusEffect;


@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Shadow abstract void loadPostProcessor(Identifier id);

    @Inject(at = @At("TAIL"), method = "onCameraEntitySet")
    private void onCamEntitySet(Entity entity, CallbackInfo ci) {
        if(entity instanceof EarthBlockEntity){
            loadPostProcessor(new Identifier(MODID,"shaders/post/seismicsense.json"));
        }
    }


    @Inject(at = @At("TAIL"), method = "render")
    private void render(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        PlayerEntity plr = MinecraftClient.getInstance().player;
        GameRenderer renderer = MinecraftClient.getInstance().gameRenderer;



        boolean hasStatusEffect = safeHasStatusEffect(SEISMIC_SENSE_EFFECT,plr);
        boolean customShaderEnabled = customPostProcessorEnabled(renderer,MODID + ":shaders/post/seismicsense.json");

        if(hasStatusEffect && !customShaderEnabled){
            renderer.onCameraEntitySet(new EarthBlockEntity(plr.getWorld(),plr));
        }else if(!hasStatusEffect && customShaderEnabled){
            renderer.onCameraEntitySet(null);
        }

    }

    @Unique
    private static boolean customPostProcessorEnabled(GameRenderer renderer, String name){
        return renderer.getPostProcessor() != null
                && renderer.getPostProcessor().getName().equals(name);
    }

    @Inject(at = @At("HEAD"), method = "togglePostProcessorEnabled", cancellable = true)
    private void render(CallbackInfo ci) {
        //naughty method, trying to remove the vision debuff (f4)
        if(customPostProcessorEnabled(MinecraftClient.getInstance().gameRenderer,MODID + ":shaders/post/seismicsense.json")){
            ci.cancel();
        }

    }
}

package dev.saperate.elementals.mixin.client;

import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.entities.earth.EarthBlockEntity;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.saperate.elementals.Constants.MODID;
import static dev.saperate.elementals.utils.SapsUtils.safeHasStatusEffect;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Shadow
    protected abstract void loadEffect(ResourceLocation p_109129_);

    @Inject(at = @At("TAIL"), method = "checkEntityPostEffect")
    private void onCamEntitySet(Entity entity, CallbackInfo ci) {
        if(entity instanceof EarthBlockEntity){
            loadEffect(ResourceLocation.fromNamespaceAndPath(MODID,"shaders/post/seismicsense.json"));
        }
    }


    @Inject(at = @At("TAIL"), method = "render")
    private void render(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo ci) {
        Player plr = Minecraft.getInstance().player;
        GameRenderer renderer = Minecraft.getInstance().gameRenderer;



        boolean hasStatusEffect = safeHasStatusEffect(ElementalsStatusEffects.SEISMIC_SENSE.get(),plr);
        boolean customShaderEnabled = elementals$customPostProcessorEnabled(renderer,MODID + ":shaders/post/seismicsense.json");

        if(hasStatusEffect && !customShaderEnabled){
            renderer.checkEntityPostEffect(new EarthBlockEntity(plr.level(),plr));
        }else if(!hasStatusEffect && customShaderEnabled){
            renderer.checkEntityPostEffect(null);
        }

    }

    @Unique
    private static boolean elementals$customPostProcessorEnabled(GameRenderer renderer, String name){
        return renderer.currentEffect() != null
                && renderer.currentEffect().getName().equals(name);
    }

    @Inject(at = @At("HEAD"), method = "togglePostEffect", cancellable = true)
    private void render(CallbackInfo ci) {
        //naughty method, trying to remove the vision debuff (f4)
        if(elementals$customPostProcessorEnabled(Minecraft.getInstance().gameRenderer,MODID + ":shaders/post/seismicsense.json")){
            ci.cancel();
        }

    }
}

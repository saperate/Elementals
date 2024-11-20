package dev.saperate.elementals.mixin.client;

import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.entities.earth.EarthBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.saperate.elementals.utils.ClientUtils.safeHasStatusEffect;

@Mixin(Camera.class)
public abstract class CameraMixin {

    @Shadow private boolean thirdPerson;

    @Inject(at = @At("TAIL"), method = "update")
    private void render(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if(safeHasStatusEffect(ElementalsStatusEffects.SPIRIT_PROJECTION,MinecraftClient.getInstance().player)){
            this.thirdPerson = false;
        }
    }
}

package dev.saperate.elementals.mixin.client;

import dev.saperate.elementals.data.ClientBender;
import dev.saperate.elementals.elements.metal.AbilityMetalDecoy;
import dev.saperate.elementals.entities.common.DecoyPlayerEntity;
import dev.saperate.elementals.entities.earth.EarthBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
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

import static dev.saperate.elementals.Elementals.MODID;
import static dev.saperate.elementals.effects.SeismicSenseStatusEffect.SEISMIC_SENSE_EFFECT;
import static dev.saperate.elementals.effects.SpiritProjectionStatusEffect.SPIRIT_PROJECTION_EFFECT;
import static dev.saperate.elementals.utils.ClientUtils.safeHasStatusEffect;

@Mixin(Camera.class)
public abstract class CameraMixin {

    @Shadow
    private boolean thirdPerson;

    @Shadow
    protected abstract void setPos(double x, double y, double z);


    @Shadow protected abstract void moveBy(double x, double y, double z);

    @Shadow protected abstract double clipToSpace(double desiredCameraDistance);

    @Inject(at = @At("TAIL"), method = "update")
    private void render(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (safeHasStatusEffect(SPIRIT_PROJECTION_EFFECT, MinecraftClient.getInstance().player)) {
            this.thirdPerson = false;
        }
        ClientBender bender = ClientBender.get();
        if (bender.currAbility instanceof AbilityMetalDecoy) {
            DecoyPlayerEntity decoy = ((DecoyPlayerEntity) bender.ClientAbilityData);
            MinecraftClient.getInstance().options.setPerspective(Perspective.THIRD_PERSON_BACK);
            if (decoy != null) {
                setPos(decoy.getX(), decoy.getEyeY(), decoy.getZ());
                moveBy(-clipToSpace(2.0), -0, 0.0);
            }
        }
    }

}

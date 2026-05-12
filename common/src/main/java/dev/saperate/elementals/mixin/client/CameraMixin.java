package dev.saperate.elementals.mixin.client;

import dev.saperate.elementals.client.data.ClientBender;
import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.elements.metal.AbilityMetalDecoy;
import dev.saperate.elementals.entities.common.DecoyPlayerEntity;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.saperate.elementals.utils.SapsUtils.safeHasStatusEffect;

@Mixin(Camera.class)
public abstract class CameraMixin {


    @Shadow
    private boolean detached;

    @Shadow
    protected abstract void setPosition(Vec3 p_90582_);

    @Shadow
    protected abstract void move(float p_343871_, float p_343008_, float p_343953_);

    @Shadow
    protected abstract float getMaxZoom(float p_345111_);

    @Inject(at = @At("TAIL"), method = "setup")
    private void render(BlockGetter level, Entity entity, boolean detached, boolean thirdPersonReverse, float partialTick, CallbackInfo ci) {
        Minecraft minecraftClient = Minecraft.getInstance();
        if (safeHasStatusEffect(ElementalsStatusEffects.SPIRIT_PROJECTION.get(), minecraftClient.player)) {
            this.detached = false;
        }
        ClientBender bender = ClientBender.get();
        if (bender.currAbility instanceof AbilityMetalDecoy) {
            DecoyPlayerEntity decoy = ((DecoyPlayerEntity) bender.ClientAbilityData);
            minecraftClient.options.setCameraType(CameraType.THIRD_PERSON_BACK);
            if (decoy != null && !decoy.isRemoved()) {
                setPosition(new Vec3(decoy.getX(), decoy.getEyeY(), decoy.getZ()));
                move(-getMaxZoom(3.0f), -0, 0.0f);
            }else{
                minecraftClient.setCameraEntity(minecraftClient.player);
            }
        }
    }

}

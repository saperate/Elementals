package dev.saperate.elementals.mixin.client;

import dev.saperate.elementals.data.ClientBender;
import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.elements.metal.AbilityMetalDecoy;
import dev.saperate.elementals.entities.common.DecoyPlayerEntity;
import dev.saperate.elementals.entities.earth.EarthBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.Player;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.saperate.elementals.utils.ClientUtils.safeHasStatusEffect;

@Mixin(Camera.class)
public abstract class CameraMixin {

    @Shadow
    private boolean thirdPerson;

    @Shadow
    protected abstract void setPos(double x, double y, double z);

    @Shadow protected abstract void moveBy(float f, float g, float h);

    @Shadow protected abstract float clipToSpace(float f);

    @Inject(at = @At("TAIL"), method = "update")
    private void render(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (safeHasStatusEffect(ElementalsStatusEffects.SPIRIT_PROJECTION, minecraftClient.player)) {
            this.thirdPerson = false;
        }
        ClientBender bender = ClientBender.get();
        if (bender.currAbility instanceof AbilityMetalDecoy) {
            DecoyPlayerEntity decoy = ((DecoyPlayerEntity) bender.ClientAbilityData);
            minecraftClient.options.setPerspective(Perspective.THIRD_PERSON_BACK);
            if (decoy != null && !decoy.isRemoved()) {
                setPos(decoy.getX(), decoy.getEyeY(), decoy.getZ());
                moveBy(-clipToSpace(3.0f), -0, 0.0f);
            }else{
                minecraftClient.setCameraEntity(minecraftClient.player);
            }
        }
    }

}

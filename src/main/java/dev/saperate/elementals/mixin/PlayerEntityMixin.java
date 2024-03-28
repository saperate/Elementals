package dev.saperate.elementals.mixin;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.data.StateDataSaverAndLoader;
import dev.saperate.elementals.elements.Element;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {


    @Inject(at = @At("TAIL"), method = "<init>")
    private void init(CallbackInfo info) {
        PlayerEntity player = ((PlayerEntity) (Object) this);
        World world = player.getWorld();
        if(!world.isClient){
            PlayerData playerState = StateDataSaverAndLoader.getPlayerState(player);
            Bender bender = new Bender(player, playerState.element);
            bender.boundAbilities = playerState.boundAbilities;
        }
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void tick(CallbackInfo ci) {
        PlayerEntity player = ((PlayerEntity) (Object) this);
        Bender bender = Bender.getBender(player);

        if (bender != null && bender.currAbility != null) {
            bender.currAbility.onTick(bender);
        }
    }

}
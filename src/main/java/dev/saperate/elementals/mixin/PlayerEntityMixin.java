package dev.saperate.elementals.mixin;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Element;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
	@Inject(at = @At("TAIL"), method = "<init>")
	private void init(CallbackInfo info) {
		PlayerEntity player = ((PlayerEntity) (Object) this);
		if(!player.getWorld().isClient){
			new Bender(player, Element.elementList.get(0));
		}
	}
}
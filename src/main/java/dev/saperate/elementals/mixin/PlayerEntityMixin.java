package dev.saperate.elementals.mixin;

import com.mojang.brigadier.ParseResults;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.data.StateDataSaverAndLoader;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.saperate.elementals.effects.SpiritProjectionStatusEffect.SPIRIT_PROJECTION_EFFECT;
import static dev.saperate.elementals.utils.SapsUtils.safeHasStatusEffect;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {


    @Shadow public abstract void remove(Entity.RemovalReason reason);

    @Inject(at = @At("TAIL"), method = "<init>")
    private void init(CallbackInfo info) {
        PlayerEntity player = ((PlayerEntity) (Object) this);
        if(!player.getWorld().isClient){
            if(Bender.getBender(player) == null){
                new Bender(player, null);
            }else{
                Bender.getBender(player).player = player;
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void tick(CallbackInfo ci) {
        PlayerEntity player = ((PlayerEntity) (Object) this);
        if(safeHasStatusEffect(SPIRIT_PROJECTION_EFFECT,player)){
            player.noClip = false;
        }
        Bender bender = Bender.getBender(player);
        if(bender == null){
            return;
        }
        if(bender.castTime != null){
            return;
        }

        if (bender.currAbility != null && !player.getWorld().isClient) {
            bender.currAbility.onTick(bender);
        }
    }

}
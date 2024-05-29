package dev.saperate.elementals.mixin;

import com.mojang.brigadier.ParseResults;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.data.StateDataSaverAndLoader;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShapes;
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
            //checks if we are inside a wall
            float f = player.getDimensions(player.getPose()).width * 0.8f;
            Box box = Box.of(player.getEyePos(), f, 1.0E-6, f);
            boolean e = BlockPos.stream(box).anyMatch(pos -> {
                BlockState blockState = player.getWorld().getBlockState((BlockPos)pos);
                return !blockState.isAir() && blockState.shouldSuffocate(player.getWorld(), (BlockPos)pos) && VoxelShapes.matchesAnywhere(blockState.getCollisionShape(player.getWorld(), (BlockPos)pos).offset(pos.getX(), pos.getY(), pos.getZ()), VoxelShapes.cuboid(box), BooleanBiFunction.AND);
            });

            if(e){
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 21, 0, false, false, false));
            }
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
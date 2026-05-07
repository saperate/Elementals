package dev.saperate.elementals.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.BiConsumer;

/**
 * Taken directly from fabric api
 */
@Mixin(GameRules.BooleanValue.class)
public interface GameRulesBooleanRuleAccessor {
    @Invoker
    static GameRules.Type<GameRules.BooleanValue> invokeCreate(boolean initialValue, BiConsumer<MinecraftServer, GameRules.BooleanValue> changeCallback) {
        throw new AssertionError("This shouldn't happen!");
    }
}

package dev.saperate.elementals.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.BiConsumer;

/**
 * From Fabric-API {5/8/2026}
 * Use their license for this file
 */
@Mixin(GameRules.BooleanValue.class)
public interface GameRulesBooleanRuleAccessor {
    @Invoker
    static GameRules.Type<GameRules.BooleanValue> invokeCreate(boolean initialValue, BiConsumer<MinecraftServer, GameRules.BooleanValue> changeCallback) {
        throw new AssertionError();
    }
}

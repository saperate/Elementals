package dev.saperate.elementals.mixin;

import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

/**
 * Taken directly from fabric api
 */
@Mixin(GameRules.class)
public interface GameRulesAccessor {
    @Invoker("register")
    static <T extends GameRules.Value<T>> GameRules.Key<T> callRegister(String name, GameRules.Category category, GameRules.Type<T> type) {
        throw new AssertionError("This shouldn't happen!");
    }

    @Accessor("RULE_TYPES")
    static Map<GameRules.Key<?>, GameRules.Type<?>> getRuleTypes() {
        throw new AssertionError("This shouldn't happen!");
    }
}

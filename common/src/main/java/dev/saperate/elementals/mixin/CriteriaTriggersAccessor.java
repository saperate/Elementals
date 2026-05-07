package dev.saperate.elementals.mixin;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Modified from Fabric-API
 */
@Mixin(CriteriaTriggers.class)
public interface CriteriaTriggersAccessor {
    @Invoker("register")
    static<T extends CriterionTrigger<?>> T callRegister(String name, T trigger) {
        throw new AssertionError("This shouldn't happen!");
    }
}

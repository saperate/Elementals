package dev.saperate.elementals.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface ElementalsLivingEntityAccessor {

    @Accessor("jumping")
    boolean isJumping();


}
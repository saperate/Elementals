package dev.saperate.elementals.mixin;

import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractFurnaceBlockEntity.class)
public interface FurnaceBlockEntityAccessor {

    @Accessor("litTime")
    void setFuelTime(int fuel);

    @Accessor("litDuration")
    void setBurnTime(int fuel);
    
}
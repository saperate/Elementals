package dev.saperate.elementals.fluids;

import com.mojang.datafixers.util.Pair;
import dev.saperate.elementals.items.ElementalsItems;
import dev.saperate.elementals.platform.Services;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Function;
import java.util.function.Supplier;

public class ElementalsFluids {
    
    public static final Supplier<Fluid> GENERIC_TEA = Services.REGISTRY.registerFluid(
            "generic_tea",
            GenericTeaFluid.Still::new
    );

    public static final Supplier<Fluid> GENERIC_TEA_FLOWING = Services.REGISTRY.registerFluid(
            "generic_tea_flowing",
            GenericTeaFluid.Flowing::new
    );
    
    
    public static void register(){
        
    }
}

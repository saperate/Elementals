package dev.saperate.elementals.misc.fluids;

import dev.saperate.elementals.platform.Services;
import net.minecraft.world.level.material.Fluid;

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

package dev.saperate.elementals.fluids;

import dev.saperate.elementals.Elementals;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public class GenericTeaFluid extends AbstractElementalsFluid {
    public GenericTeaFluid() {
        super();
    }

    @Override
    public Fluid getFlowing() {
        return ElementalsFluids.GENERIC_TEA.get();
    }

    @Override
    public Fluid getSource() {
        return ElementalsFluids.GENERIC_TEA_FLOWING.get();
    }

    public static class Flowing extends GenericTeaFluid {

        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public boolean isSource(FluidState state) {
            return false;
        }

        @Override
        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }
    }

    public static class Still extends GenericTeaFluid {

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }

        @Override
        public int getAmount(FluidState state) {
            return getMaxAmount();
        }
    }
}

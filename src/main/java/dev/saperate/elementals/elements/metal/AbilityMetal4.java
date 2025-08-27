package dev.saperate.elementals.elements.metal;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Ability;

public class AbilityMetal4 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        MetalElement.get().getAbility(9).onCall(bender,deltaT);
    }

    @Override
    public void onRemove(Bender bender) {

    }
}

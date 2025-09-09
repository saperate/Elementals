package dev.saperate.elementals.elements.metal;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Ability;

public class AbilityMetal1 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        MetalElement.get().getAbility(1).onCall(bender,deltaT);//fixme 1 also goes here
    }

    @Override
    public void onRemove(Bender bender) {

    }
}

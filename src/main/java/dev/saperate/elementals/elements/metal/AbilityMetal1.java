package dev.saperate.elementals.elements.metal;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Ability;

public class AbilityMetal1 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        if(deltaT >= 4000){
            MetalElement.get().getAbility(2).onCall(bender,deltaT);
            return;
        }
        MetalElement.get().getAbility(1).onCall(bender,deltaT);
    }

    @Override
    public void onRemove(Bender bender) {}
}

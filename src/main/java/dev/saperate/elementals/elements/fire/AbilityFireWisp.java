package dev.saperate.elementals.elements.fire;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Ability;

public class AbilityFireWisp implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {

    }

    @Override
    public void onLeftClick(Bender bender, boolean started) {

    }

    @Override
    public void onMiddleClick(Bender bender, boolean started) {

    }

    @Override
    public void onRightClick(Bender bender, boolean started) {

    }

    @Override
    public void onTick(Bender bender) {

    }

    @Override
    public void onBackgroundTick(Bender bender, Object data) {
        Ability.super.onBackgroundTick(bender, data);
    }

    @Override
    public void onRemove(Bender bender) {

    }
}

package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.elements.Element;

public class WaterElement extends Element {
    public WaterElement() {
        super("Water");
        addAbility(new AbilityWaterCube());
    }
}

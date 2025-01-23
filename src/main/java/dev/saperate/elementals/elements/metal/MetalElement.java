package dev.saperate.elementals.elements.metal;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.Upgrade;

public class MetalElement extends Element {
    public MetalElement() {
        super("Metal", new Upgrade("Metal", new  Upgrade[]{

        },0));
        addAbility(new AbilityMetal1(), true);
        addAbility(new AbilityMetal2(), true);
        addAbility(new AbilityMetalBind());//TODO move stuff once you add abilities above it
        addAbility(new AbilityMetal3(),true);
        addAbility(new AbilityMetalCable());
    }

    public static Element get() {
        return getElement("Metal");
    }

    @Override
    public int getColor() {
        return 0xFFDADDE1;
    }

    @Override
    public int getAccentColor() {
        return 0xFF919191;
    }

    @Override
    public boolean isSkillTreeComplete(Bender bender) {
        return bender.hasElement(this);
    }//TODO this
}

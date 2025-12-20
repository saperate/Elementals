package dev.saperate.elementals.elements.metal;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.Upgrade;

public class MetalElement extends Element {
    public MetalElement() {
        super("Metal", new Upgrade("Metal", new  Upgrade[]{

        },0));
        addAbility(new AbilityMetal1(), true);
        addAbility(new AbilityMetalBullets());
        addAbility(new AbilityMetalLance());
        addAbility(new AbilityMetal2(), true);
        addAbility(new AbilityMetalBind());
        addAbility(new AbilityMetal3(),true);
        addAbility(new AbilityMetalCable());
        addAbility(new AbilityMetal4(),true);
        addAbility(new AbilityMetalArmor());
        addAbility(new AbilityMetalDecoy());
    }

    public static Element get() {
        return getElement("Metal");
    }

    @Override
    public int getColor() {
        return 0xFFDADDE1;
    }

    @Override
    public int getSecondaryColor() {
        return 0xFF919191;
    }

    @Override
    public boolean isSkillTreeComplete(Bender bender) {
        return bender.hasElement(this);
    }//TODO make an algorithm for this it's annoying to do by hand
}

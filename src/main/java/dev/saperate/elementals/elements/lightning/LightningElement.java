package dev.saperate.elementals.elements.lightning;

import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.Upgrade;

public class LightningElement extends Element {
    public LightningElement() {
        super("Lightning", new Upgrade("Lightning", new Upgrade[]{
                new Upgrade("lightningRedirection", new Upgrade[]{
                        new Upgrade("lightningBolt", new Upgrade[]{

                        }, 2)
                }, 2)
        }, 0));

        addAbility(new AbilityLightning1(), true);
        addAbility(new AbilityLightningBolt());
        addAbility(new AbilityLightningRedirect());
        addAbility(new AbilityLightning2(), true);
        addAbility(new AbilityLightningVoltArc());
    }

    public static Element get() {
        return elementList.get(5);
    }
}

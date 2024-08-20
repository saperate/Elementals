package dev.saperate.elementals.elements.lightning;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.Upgrade;

public class LightningElement extends Element {
    public LightningElement() {
        super("Lightning", new Upgrade("Lightning", new Upgrade[]{
                new Upgrade("lightningRedirection", new Upgrade[]{
                        new Upgrade("lightningRedirectionEfficiencyI", new Upgrade[]{
                                new Upgrade("lightningRedirectionEfficiencyII", 2),
                                new Upgrade("lightningBolt", new Upgrade[]{
                                        new Upgrade("lightningBoltEfficiencyI", new Upgrade[]{
                                                new Upgrade("lightningBoltEfficiencyII", 2)
                                        }, 2)
                                }, 4)
                        }, 2)
                }, 4),
                new Upgrade("lightningVoltArc", new Upgrade[]{
                        new Upgrade("lightningVoltArcStrengthI", new Upgrade[]{
                                new Upgrade("lightningEMP", new Upgrade[]{
                                        new Upgrade("lightningEMPSizeI", 2),
                                }, 4),
                                new Upgrade("lightningVoltArcStrengthII", 2),
                                new Upgrade("lightningStaticAura", new Upgrade[]{
                                        new Upgrade("lightningStaticAuraStrengthI", new Upgrade[]{
                                                new Upgrade("lightningStaticAuraStrengthII", 2)
                                        }, 2)
                                }, 4)
                        }, 2)
                }, 4),
                new Upgrade("lightningOvercharge", new Upgrade[]{
                        new Upgrade("lightningOverchargeStrengthI", new Upgrade[]{
                                new Upgrade("lightningOverchargeStrengthII", 2)
                        }, 2)
                }, 4),
                new Upgrade("lightningStorm", new Upgrade[]{
                        new Upgrade("lightningStormDurationI", 2)
                },4)
        }, 0));

        addAbility(new AbilityLightning1(), true);
        addAbility(new AbilityLightningBolt());
        addAbility(new AbilityLightningRedirect());
        addAbility(new AbilityLightning2(), true);
        addAbility(new AbilityLightningVoltArc());
        addAbility(new AbilityLightningEMP());
        addAbility(new AbilityLightning3(), true);
        addAbility(new AbilityLightning4(), true);
        addAbility(new AbilityLightningStorm());
    }

    public static Element get() {
        return elementList.get(5);
    }

    @Override
    public boolean isSkillTreeComplete(Bender bender) {
        PlayerData plrData = bender.plrData;
        return bender.hasElement(this)
                && plrData.canUseUpgrade("lightningRedirectionEfficiencyII")
                && plrData.canUseUpgrade("lightningBoltEfficiencyII")
                && plrData.canUseUpgrade("lightningEMPSizeI")
                && plrData.canUseUpgrade("lightningVoltArcStrengthII")
                && plrData.canUseUpgrade("lightningStaticAuraStrengthII")
                && plrData.canUseUpgrade("lightningOverchargeStrengthII")
                && plrData.canUseUpgrade("lightningStormDurationI")
                ;
    }
}

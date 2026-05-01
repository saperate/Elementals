package dev.saperate.elementals.elements;

import dev.saperate.elementals.data.Bender;

public class NoneElement extends Element{
    public NoneElement() {
        super("None", new Upgrade("None",new Upgrade[]{
                new Upgrade("bendingAir",0),
                new Upgrade("bendingWater",0),
                new Upgrade("bendingEarth",0),
                new Upgrade("bendingFire",0)
        },0));
    }

    public static Element get(){
        return getElement("None");
    }

    @Override
    public int getColor() {
        return 0xFFcecece;
    }

    @Override
    public int getSecondaryColor() {
        return 0xFFafaeae;
    }

    @Override
    public int getTertiaryColor() {
        return 0xFF777777;
    }

    @Override
    public boolean isSkillTreeComplete(Bender bender) {
        return false;
    }
}

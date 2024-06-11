package dev.saperate.elementals.elements;

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
        return elementList.get(0);
    }
}

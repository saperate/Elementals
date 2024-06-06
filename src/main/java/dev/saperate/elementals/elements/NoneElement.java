package dev.saperate.elementals.elements;

public class NoneElement extends Element{
    public NoneElement() {
        super("None", new Upgrade("None",new Upgrade[]{
                new Upgrade("bendingAir"),
                new Upgrade("bendingWater"),
                new Upgrade("bendingEarth"),
                new Upgrade("bendingFire")
        }));
    }

    public static Element get(){
        return elementList.get(0);
    }
}

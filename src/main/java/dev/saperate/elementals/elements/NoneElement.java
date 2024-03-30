package dev.saperate.elementals.elements;

public class NoneElement extends Element{
    public NoneElement() {
        super("None");
    }

    public static Element get(){
        return elementList.get(0);
    }
}

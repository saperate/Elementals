package dev.saperate.elementals.items.scrolls;



public class EarthScrollItem extends AbstractScrollItem {

    public EarthScrollItem(Properties settings) {
        super(settings);
    }

    @Override
    String getTranslatable() {
        return "item.elementals.earth_scroll.tooltip";
    }

    @Override
    Element getElement() {
        return EarthElement.get();
    }
}
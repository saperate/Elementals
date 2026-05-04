package dev.saperate.elementals.items.scrolls;

public class AirScrollItem extends AbstractScrollItem {

    public AirScrollItem(Properties settings) {
        super(settings);
    }

    @Override
    String getTranslatable() {
        return "item.elementals.air_scroll.tooltip";
    }

    @Override
    Element getElement() {
        return AirElement.get();
    }

}
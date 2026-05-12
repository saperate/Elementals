package dev.saperate.elementals.items.scrolls;

import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.air.AirElement;

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
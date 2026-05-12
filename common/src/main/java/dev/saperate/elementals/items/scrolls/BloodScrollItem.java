package dev.saperate.elementals.items.scrolls;

import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.blood.BloodElement;
import dev.saperate.elementals.elements.water.WaterElement;

public class BloodScrollItem extends AbstractScrollItem {

    public BloodScrollItem(Properties settings) {
        super(settings);
    }

    @Override
    String getTranslatable() {
        return "item.elementals.blood_scroll.tooltip";
    }

    @Override
    Element getElement() {
        return BloodElement.get();
    }

    @Override
    Element getParentElement() {
        return WaterElement.get();
    }
}
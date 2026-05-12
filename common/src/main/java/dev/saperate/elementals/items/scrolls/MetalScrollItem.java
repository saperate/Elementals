package dev.saperate.elementals.items.scrolls;

import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.earth.EarthElement;
import dev.saperate.elementals.elements.metal.MetalElement;

public class MetalScrollItem extends AbstractScrollItem {

    public MetalScrollItem(Properties settings) {
        super(settings);
    }

    @Override
    String getTranslatable() {
        return "item.elementals.metal_scroll.tooltip";
    }

    @Override
    Element getElement() {
        return MetalElement.get();
    }
    @Override
    Element getParentElement() {
        return EarthElement.get();
    }
}
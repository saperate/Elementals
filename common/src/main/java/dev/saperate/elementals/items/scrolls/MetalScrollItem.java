package dev.saperate.elementals.items.scrolls;

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
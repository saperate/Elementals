package dev.saperate.elementals.items.scrolls;

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
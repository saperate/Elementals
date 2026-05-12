package dev.saperate.elementals.items.scrolls;


import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.fire.FireElement;
import dev.saperate.elementals.elements.lightning.LightningElement;

public class LightningScrollItem extends AbstractScrollItem {

    public LightningScrollItem(Properties settings) {
        super(settings);
    }

    @Override
    String getTranslatable() {
        return "item.elementals.lightning_scroll.tooltip";
    }

    @Override
    Element getElement() {
        return LightningElement.get();
    }

    @Override
    Element getParentElement() {
        return FireElement.get();
    }
}
package dev.saperate.elementals.items.scrolls;


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
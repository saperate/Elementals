package dev.saperate.elementals;

import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MODID)
public class ElementalsForge {

    public ElementalsForge() {
        Elementals.init();
    }
}
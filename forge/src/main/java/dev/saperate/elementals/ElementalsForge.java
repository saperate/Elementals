package dev.saperate.elementals;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MODID)
public class ElementalsForge {

    public ElementalsForge() {
        Elementals.init();
    }
    
}
package dev.saperate.elementals.misc;

import net.minecraft.util.Identifier;

import static dev.saperate.elementals.Elementals.MODID;

public class AirBannerPattern extends net.minecraft.block.entity.BannerPattern {
    public static final AirBannerPattern AIR_PATTERN = new AirBannerPattern();
    public AirBannerPattern() {
        super("air");
    }

}

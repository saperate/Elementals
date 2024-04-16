package dev.saperate.elementals.elements;

import dev.saperate.elementals.data.Bender;
import net.minecraft.entity.player.PlayerEntity;

public interface Ability {
    void onCall(Bender bender, long deltaT);

    void onLeftClick(Bender bender, boolean started);

    void onMiddleClick(Bender bender, boolean started);

    void onRightClick(Bender bender, boolean started);

    void onTick(Bender bender);

    void onRemove(Bender bender);

}

package dev.saperate.elementals.elements;

import dev.saperate.elementals.data.Bender;
import net.minecraft.entity.player.PlayerEntity;

public interface Ability {
    void onCall(Bender bender, long deltaT);

    void onLeftClick(Bender bender);

    void onMiddleClick(Bender bender);

    void onRightClick(Bender bender);

    void onTick(Bender bender);


}

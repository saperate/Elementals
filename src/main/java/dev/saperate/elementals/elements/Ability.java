package dev.saperate.elementals.elements;

import dev.saperate.elementals.data.Bender;
import net.minecraft.entity.player.PlayerEntity;

public abstract class Ability {
    public abstract void onCall(Bender bender);

    public abstract void onLeftClick(Bender bender);

    public abstract void onMiddleClick(Bender bender);

    public abstract void onRightClick(Bender bender);

    public abstract void onTick(Bender bender);

}

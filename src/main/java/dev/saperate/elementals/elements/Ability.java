package dev.saperate.elementals.elements;

import net.minecraft.entity.player.PlayerEntity;

public abstract class Ability {
    public abstract void onCall(PlayerEntity player);

    public abstract void onLeftClick();

    public abstract void onMiddleClick();

    public abstract void onRightClick();

}

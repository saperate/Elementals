package dev.saperate.elementals.elements;

import dev.saperate.elementals.data.Bender;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public interface Ability {

    /**
     * The total number of ability keybind slots available (KeyAbility1..N / bindable slots 0..N-1).
     * Bump this (and add the matching KeyAbilityN classes + register them in ElementalsClient.init())
     * whenever more simultaneous keybinds are needed.
     */
    int MAX_KEYBINDS = 10;

    void onCall(Bender bender, long deltaT);

    default void onLeftClick(Bender bender, boolean started) {}

    default void onMiddleClick(Bender bender, boolean started) {}

    default void onRightClick(Bender bender, boolean started) {}

    default void onTick(Bender bender) {}

    default void onBackgroundTick(Bender bender, Object data) {}

    default void onAbilityPress(Bender bender, int keyIndex){}

    void onRemove(Bender bender);

    default @Nullable Vec3 pointArmsTowards(){return null;}//TODO implement so that we can make bending look better

    default boolean shouldImmobilizePlayer(Player player) {return false;}

    default boolean activatesOnPress() {
        return false;
    }

    /**
     * Takes in an ability as a parameter and returns the class name with the word "Ability" removed
     * for better readability. If the ability passes is null, this method will return "null"
     * @param ability The ability of which we want the name
     * @return A string containing a stylised version of ability name
     */
    static String getName(Ability ability) {
        if (ability == null) {
            return "null";
        }
        return ability.getClass().getSimpleName().replace("Ability", "");
    }

}
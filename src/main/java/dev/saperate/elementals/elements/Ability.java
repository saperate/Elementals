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


    /**
     * Takes in an ability as a parameter and returns the class name with the word "Ability" removed
     * for better readability. If the ability passes is null, this method will return "null"
     * @param ability The ability of which we want the name
     * @return A string containing a stylised version of ability name
     */
    static String getName(Ability ability){
        if(ability == null){
            return "null";
        }
        return ability.getClass().getSimpleName().replace("Ability","");
    }

}

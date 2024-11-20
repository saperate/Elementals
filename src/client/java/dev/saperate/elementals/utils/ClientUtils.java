package dev.saperate.elementals.utils;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;

public final class ClientUtils {

    /**
     * This method checks safely if an entity has a status effect.
     * It will return true if the entity has the effect <b>AND</b>
     * if the entity is not null
     * @param effect The effect we want to check
     * @param entity The entity that we check
     * @return true if the entity has the status effect
     */
    public static boolean safeHasStatusEffect(RegistryEntry<StatusEffect> effect, LivingEntity entity){
        boolean hasEffect = false;
        try{
            if(entity != null){
                hasEffect = entity.hasStatusEffect(effect);
            }else {
                return false;
            }
        }catch (Exception ignored){}
        return hasEffect;
    }

    private ClientUtils(){

    }
}

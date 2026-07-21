package dev.saperate.elementals.elements.air;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.air.AirBladeEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import static dev.saperate.elementals.misc.ElementalsSounds.WIND_SOUND_EVENT;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.playSoundAtEntity;

/**
 * A thin, fast blade of compressed air launched in a straight line as a real projectile
 * (a horizontal blade model flying forward). Unlike AirGust (a wide cone that pushes
 * everything back), this pierces through every entity in its path, dealing precise
 * slashing damage with only a light shove instead of a big knockback.
 */
public class AbilityAirBlade implements Ability {

    @Override
    public void onCall(Bender bender, long deltaT) {
        if (!PlayerData.get(bender.player).canUseUpgrade("airBlade")) {
            bender.setCurrAbility(null);
            return;
        }

        if (!bender.reduceChi(8)) {
            bender.setCurrAbility(null);
            return;
        }

        Player player = bender.player;
        PlayerData plrData = PlayerData.get(player);

        int range = plrData.canUseUpgrade("airBladeRangeI") ? 16 : 10;
        float damage = plrData.canUseUpgrade("airBladeDamageI") ? 5f : 3f;
        float speed = 1.6f;

        Vec3 spawnPos = getEntityLookVector(player, 1.5f);
        Vec3 direction = getEntityLookVector(player, 1).subtract(player.getEyePosition());

        AirBladeEntity blade = new AirBladeEntity(player.level(), player, spawnPos.x, spawnPos.y, spawnPos.z);
        blade.setDamage(damage);
        blade.launch(direction, speed);
        //max lifetime scales with range so the "range" upgrade actually changes how far it travels
        blade.maxLifeTime = Math.max(20, (int) (range * 2.2f));

        player.level().addFreshEntity(blade);

        playSoundAtEntity(player, WIND_SOUND_EVENT, 5);

        //instant-cast: the ability itself is done firing, the entity now lives on its own
        bender.setCurrAbility(null);
    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
    }
}
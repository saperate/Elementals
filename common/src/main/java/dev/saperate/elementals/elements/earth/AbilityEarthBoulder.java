package dev.saperate.elementals.elements.earth;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.earth.EarthBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * A heavier, single-cast sibling of Earth Block Pickup. Instead of the two-step
 * "pick up, then click to throw" flow, Boulder Throw instantly conjures a much
 * harder-hitting chunk of terrain and launches it straight down the caster's look
 * vector on cast - a dedicated ranged nuke rather than a controllable floating block.
 */
public class AbilityEarthBoulder implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        if (!PlayerData.get(bender.player).canUseUpgrade("earthBoulder")) {
            bender.setCurrAbility(null);
            return;
        }
        if (!bender.reduceChi(20)) {
            bender.setCurrAbility(null);
            return;
        }

        Player player = bender.player;
        Object[] vars = EarthElement.canBend(player, true);
        if (vars == null) {
            bender.setCurrAbility(null);
            return;
        }

        Vec3 pos = (Vec3) vars[0];
        BlockState state = (BlockState) vars[1];
        PlayerData plrData = PlayerData.get(player);

        EarthBlockEntity boulder = new EarthBlockEntity(player.level(), player, pos.x, pos.y, pos.z);
        boulder.setBlockState(state);
        boulder.setControlled(false);

        //range upgrade scales how long the boulder can travel before dissipating,
        //not just a flat number - mirrors how Air Blade's "range" upgrade works
        float range = plrData.canUseUpgrade("earthBoulderRangeI") ? 25 : 16;
        boulder.setMaxLifeTime((int) (range * 3));

        boulder.setDeltaMovement(player, player.getXRot(), player.getYRot(), 0, 1.1f, 0);
        boulder.setDamage(plrData.canUseUpgrade("earthBoulderDamageI") ? 14 : 9);

        player.level().addFreshEntity(boulder);

        //instant-cast: the boulder is already flying and nothing else needs to be
        //controlled, so the ability is done as soon as it's thrown
        bender.setCurrAbility(null);
    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
    }
}
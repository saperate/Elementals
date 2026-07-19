package dev.saperate.elementals.elements.earth;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import static dev.saperate.elementals.utils.SapsUtils.launchEntity;
import static dev.saperate.elementals.utils.SapsUtils.raycastBlockCustomRotation;

public class AbilityEarthJump implements Ability {

    @Override
    public void onCall(Bender bender, long deltaT) {
        bender.setCurrAbility(null);
        Player player = bender.player;

        BlockHitResult hit = raycastBlockCustomRotation(player, 12, true, new Vec3(0, -1, 0));

        if(!EarthElement.isBlockBendable(player.level().getBlockState(hit.getBlockPos()), bender) || !player.getRootVehicle().onGround()){
            return;
        }

        if (!bender.reduceChi(10)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }

        PlayerData plrData = PlayerData.get(player);
        float power = 2;

        if (plrData.canUseUpgrade("earthJumpRangeII")) {
            power = 6;
        } else if (plrData.canUseUpgrade("earthJumpRangeI")) {
            power = 4;
        }
        bender.ignoreNextFallDamage = true;
        launchEntity(player,power);

    }

    @Override
    public void onRemove(Bender bender) {

    }

}
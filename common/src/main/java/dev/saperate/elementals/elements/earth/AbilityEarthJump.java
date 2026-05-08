package dev.saperate.elementals.elements.earth;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.water.WaterTowerEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.Player;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.*;

public class AbilityEarthJump implements Ability {

    @Override
    public void onCall(Bender bender, long deltaT) {
        bender.setCurrAbility(null);
        Player player = bender.player;

        BlockHitResult hit = raycastBlockCustomRotation(player, 12, true, new Vec3(0, -1, 0));

        if(!EarthElement.isBlockBendable(player.level().getBlockState(hit.getOnPos()), bender) || !player.getRootVehicle().isOnGround()){
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
        launchEntity(player,power);

    }

    @Override
    public void onLeftClick(Bender bender, boolean started) {

    }

    @Override
    public void onMiddleClick(Bender bender, boolean started) {

    }

    @Override
    public void onRightClick(Bender bender, boolean started) {

    }

    @Override
    public void onTick(Bender bender) {

    }

    @Override
    public void onRemove(Bender bender) {

    }

}

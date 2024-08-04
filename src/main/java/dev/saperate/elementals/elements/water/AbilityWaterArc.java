package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.water.WaterArcEntity;
import dev.saperate.elementals.entities.water.WaterCubeEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;

public class AbilityWaterArc implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        PlayerEntity player = bender.player;
        int chi = PlayerData.get(player).canUseUpgrade("waterArcEfficiencyI") ? 5 : 15;
        if (!bender.reduceChi(chi)) {
            if (bender.abilityData == null) {
                bender.setCurrAbility(null);
            } else {
                onRemove(bender);
            }
            return;
        }

        Vector3f pos = WaterElement.canBend(player, true);

        if (pos != null) {
            WaterArcEntity entity = new WaterArcEntity(player.getWorld(), player, pos.x, pos.y, pos.z);
            bender.abilityData = entity;
            entity.createChain(player);
            player.getWorld().spawnEntity(entity);

            bender.setCurrAbility(this);
        } else {
            bender.setCurrAbility(null);
        }
    }

    @Override
    public void onLeftClick(Bender bender, boolean started) {
        onRemove(bender);
        WaterArcEntity entity = (WaterArcEntity) bender.abilityData;
        if (entity == null) {
            return;
        }
        PlayerData plrData = PlayerData.get(bender.player);

        float speed = 1;
        if (plrData.canUseUpgrade("waterArcSpeedII")) {
            speed = 2;
        } else if (plrData.canUseUpgrade("waterArcSpeedI")) {
            speed = 1.5f;
        }
        entity.setVelocity(bender.player, bender.player.getPitch(), bender.player.getYaw(), 0, speed, 0);
    }

    @Override
    public void onMiddleClick(Bender bender, boolean started) {

    }

    @Override
    public void onRightClick(Bender bender, boolean started) {
        PlayerEntity player = bender.player;
        boolean storedWater = player.getInventory().containsAny((stack) -> {
            if(stack.getItem().equals(Items.GLASS_BOTTLE)){
                stack.decrement(1);
                player.getInventory().insertStack(PotionUtil.setPotion(Items.POTION.getDefaultStack(), Potions.WATER));
                return true;
            }
            return false;
        });

        if (storedWater) {
            WaterArcEntity entity = (WaterArcEntity) bender.abilityData;
            if (entity == null) {
                return;
            }
            entity.remove();
            bender.setCurrAbility(null);
            return;
        }
        onRemove(bender);
    }

    @Override
    public void onTick(Bender bender) {

    }

    @Override
    public void onRemove(Bender bender) {
        bender.setCurrAbility(null);
        WaterArcEntity entity = (WaterArcEntity) bender.abilityData;
        if (entity == null) {
            return;
        }
        entity.setControlled(false);
    }

}

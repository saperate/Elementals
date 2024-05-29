package dev.saperate.elementals.elements.air;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.entities.common.DecoyPlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

import static dev.saperate.elementals.effects.SpiritProjectionStatusEffect.SPIRIT_PROJECTION_EFFECT;

public class AbilityAir4 implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        ServerPlayerEntity plr = (ServerPlayerEntity) bender.player;

        DecoyPlayerEntity decoy = new DecoyPlayerEntity(plr.getWorld(), plr);
        decoy.setCustomName(plr.getName());

        decoy.equipStack(EquipmentSlot.HEAD, plr.getEquippedStack(EquipmentSlot.HEAD));
        decoy.equipStack(EquipmentSlot.CHEST, plr.getEquippedStack(EquipmentSlot.CHEST));
        decoy.equipStack(EquipmentSlot.LEGS, plr.getEquippedStack(EquipmentSlot.LEGS));
        decoy.equipStack(EquipmentSlot.FEET, plr.getEquippedStack(EquipmentSlot.FEET));
        decoy.equipStack(EquipmentSlot.MAINHAND, plr.getEquippedStack(EquipmentSlot.MAINHAND));
        decoy.equipStack(EquipmentSlot.OFFHAND, plr.getEquippedStack(EquipmentSlot.OFFHAND));

        decoy.setYaw(plr.getYaw());
        decoy.setHeadYaw(plr.getHeadYaw());
        decoy.setPitch(plr.getPitch());

        decoy.setVelocity(plr.getVelocity());
        decoy.fallDistance = plr.fallDistance;

        plr.getWorld().spawnEntity(decoy);

        decoy.setOwner(plr);

        bender.abilityData = new Object[]{plr.interactionManager.getGameMode(), decoy};
        plr.changeGameMode(GameMode.SPECTATOR);

        bender.player.addStatusEffect(new StatusEffectInstance(SPIRIT_PROJECTION_EFFECT, -1, 0, false, false, false));
        bender.setCurrAbility(this);
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
        if (bender.player.isSneaking()) {
            onRemove(bender);
        }
    }

    @Override
    public void onRemove(Bender bender) {
        //TODO add upgrade to uncast before the timer, otherwise they have to punch their bodies or smth

        Object[] data = (Object[]) bender.abilityData;

        GameMode gm = (GameMode) data[0];
        if (gm != null) {
            ((ServerPlayerEntity) bender.player).changeGameMode(gm);
        }

        DecoyPlayerEntity decoy = (DecoyPlayerEntity) data[1];
        if (decoy != null) {
            bender.player.teleport(decoy.getX(),decoy.getY(),decoy.getZ());
            decoy.discard();
        }

        bender.player.removeStatusEffect(SPIRIT_PROJECTION_EFFECT);
        bender.setCurrAbility(null);
        bender.abilityData = null;
    }
}

package dev.saperate.elementals.elements.lightning;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.elements.Ability;
import dev.saperate.elementals.misc.StunExplosion;
import dev.saperate.elementals.utils.SapsUtils;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

import java.util.List;


public class AbilityLightningStorm implements Ability {
    @Override
    public void onCall(Bender bender, long deltaT) {
        bender.setCurrAbility(null);
        if (bender.isAbilityInBackground(this) || !bender.player.getWorld().isSkyVisible(bender.player.getBlockPos())) {
            return;
        }
        if (!bender.reduceChi(100)) {
            return;
        }
        bender.addBackgroundAbility(this, new Object[]{0, bender.player.getPos()});
        bender.player.addStatusEffect(new StatusEffectInstance(ElementalsStatusEffects.BURNOUT,200,0,false,false,true));
    }


    @Override
    public void onBackgroundTick(Bender bender, Object data) {
        PlayerEntity player = bender.player;
        PlayerData plrData = bender.getData();
        World world = player.getWorld();

        int aliveTicks = (int) ((Object[]) data)[0];
        Vec3d origin = (Vec3d) ((Object[]) data)[1];



        if (aliveTicks >= (plrData.canUseUpgrade("lightningStormDurationI") ? 600 : 300)) {
            bender.removeAbilityFromBackground(this);
            return;
        }

        bender.setBackgroundAbilityData(this, new Object[]{aliveTicks + 1, origin});

        if (player.getRandom().nextBetween(1, 10) != 1) {
            return;
        }

        Vec3d pos = origin;
        int range = 25;

        if (player.getRandom().nextBetween(1, 4) == 1) {
            List<Entity> entities = player.getWorld().getOtherEntities(player, new Box(origin.subtract(range, range, range), origin.add(range, range, range)),
                    (Entity e) -> {
                        if (e instanceof LivingEntity && world.isSkyVisible(e.getBlockPos())){
                            return player.getRandom().nextBoolean();
                        }
                        return false;
                    }
            );

            if(!entities.isEmpty()){
                Entity victim = entities.get(player.getRandom().nextBetween(0,entities.size() - 1));
                range = 0;
                pos = victim.getPos();
            }

        }


        LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
        lightning.setPos(
                pos.x + player.getRandom().nextBetween(-range, range),
                pos.y,
                pos.z + player.getRandom().nextBetween(-range, range)
        );
        world.spawnEntity(lightning);

    }

    @Override
    public void onRemove(Bender bender) {
    }
}

package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.Upgrade;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.joml.Vector3f;

import static dev.saperate.elementals.Elementals.BENDING_GRIEFING;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;

public class WaterElement extends Element {


    public WaterElement() {
        super("Water",
                new Upgrade[]{
                        new Upgrade("waterBubble", new Upgrade[]{
                                new Upgrade("waterHelmet", new Upgrade[]{
                                        new Upgrade("waterShieldHelmetPath", 2),
                                        new Upgrade("waterHelmetDurationI", new Upgrade[]{
                                                new Upgrade("waterHelmetDurationII", new Upgrade[]{
                                                        new Upgrade("waterHelmetDurationIII", new Upgrade[]{
                                                                new Upgrade("waterHelmetDurationIV", new Upgrade[]{
                                                                        new Upgrade("waterHelmetMastery", 2)
                                                                }, 1)
                                                        }, 1),
                                                        new Upgrade("waterHelmetStealth", 2)
                                                }, true, 1)
                                        }, 1),
                                }, false, -1, 2),
                                new Upgrade("waterSuffocate", new Upgrade[]{
                                        new Upgrade("waterShieldSuffocatePath", 1),
                                        new Upgrade("waterSuffocateRange", 1)
                                }, false, 1, 2)
                        }, true, 2),

                        new Upgrade("waterArc", new Upgrade[]{
                                new Upgrade("waterJet", new Upgrade[]{
                                        new Upgrade("waterJetRangeI", new Upgrade[]{
                                                new Upgrade("waterJetDamageI", 1)
                                        }, 1)
                                }, false, -2, 2),
                                new Upgrade("waterArcDamageI", new Upgrade[]{
                                        new Upgrade("waterArcEfficiencyI", new Upgrade[]{
                                                new Upgrade("waterBlade", new Upgrade[]{
                                                        new Upgrade("waterBladeDamageI", new Upgrade[]{
                                                                new Upgrade("waterBladeMiningI", new Upgrade[]{
                                                                        new Upgrade("waterBladeMiningII", 1)
                                                                }, 1),
                                                                new Upgrade("waterBladeSpeedI", new Upgrade[]{
                                                                        new Upgrade("waterBladeSpeedII", 1)
                                                                }, 1)
                                                        }, 1)
                                                }, false, -1, 2),
                                                new Upgrade("waterCannon", new Upgrade[]{
                                                        new Upgrade("waterCannonRangeI", new Upgrade[]{
                                                                new Upgrade("waterCannonDamageI", 1)
                                                        }, 1)
                                                }, 2)
                                        }, true, 1),
                                        new Upgrade("waterArcSpeedI", new Upgrade[]{
                                                new Upgrade("waterArcSpeedII", new Upgrade[]{
                                                        new Upgrade("waterArcMastery", 2)
                                                }, 1)
                                        }, false, 1, 1)
                                }, 1)

                        }, 2),
                        new Upgrade("waterJump", new Upgrade[]{
                                new Upgrade("waterJumpRangeI", new Upgrade[]{
                                        new Upgrade("waterJumpRangeII", 1),
                                        new Upgrade("waterSurf", new Upgrade[]{
                                                new Upgrade("waterSurfSpeedI", new Upgrade[]{
                                                        new Upgrade("waterSurfSpeedII", 1),
                                                        new Upgrade("waterTower", new Upgrade[]{
                                                                new Upgrade("waterTowerRangeI", 1)
                                                        }, 2),
                                                }, 1)
                                        }, 2)
                                }, 1)
                        }, 2),
                        new Upgrade("waterPickupRangeI", new Upgrade[]{
                                new Upgrade("waterPickupRangeII", new Upgrade[]{
                                        new Upgrade("waterPickupEfficiencyI", new Upgrade[]{
                                                new Upgrade("waterHealing", new Upgrade[]{
                                                        new Upgrade("waterHealingEfficiencyI", new Upgrade[]{
                                                                new Upgrade("waterHealingEfficiencyII", 1)
                                                        }, 1)
                                                }, 2)
                                        }, 1)
                                }, 1)
                        }, 3)//This is priced higher than 2 since we don't want players to soft-lock themselves

                });
        addAbility(new AbilityWater1(), true);
        addAbility(new AbilityWaterCube());
        addAbility(new AbilityWater2(), true);
        addAbility(new AbilityWaterArc());
        addAbility(new AbilityWaterHelmet());
        addAbility(new AbilityWaterShield());
        addAbility(new AbilityWaterSuffocate());
        addAbility(new AbilityWaterJet());
        addAbility(new AbilityWaterArms());
        addAbility(new AbilityWaterBlade());
        addAbility(new AbilityWaterBullet());
        addAbility(new AbilityWaterCannon());
        addAbility(new AbilityWater3(), true);
        addAbility(new AbilityWaterSurf());
        addAbility(new AbilityWater4(), true);
        addAbility(new AbilityWaterHealing());
        addAbility(new AbilityWaterTower());
        addAbility(new AbilityWaterJump());
    }

    public static Vector3f canBend(PlayerEntity player, boolean consumeWater) {//todo make it so some abilities still consume water, even though you might have efficiency unlocked
        if (isBeingRainedOn(player)) {
            return getEntityLookVector(player, 2).toVector3f();
        }
        PlayerData plrData = PlayerData.get(player);

        int range = 5;
        if (plrData.canUseUpgrade("waterPickupRangeII")) {
            range = 15;
        } else if (plrData.canUseUpgrade("waterPickupRangeI")) {
            range = 10;
        }

        BlockHitResult hit = (BlockHitResult) player.raycast(range, 1, true);

        BlockState blockState = player.getEntityWorld().getBlockState(hit.getBlockPos());

        boolean hasEfficiency = plrData.canUseUpgrade("waterPickupEfficiencyI");

        if (hit.getType() == HitResult.Type.BLOCK && isBlockBendable(hit.getBlockPos(), player.getWorld(), !hasEfficiency, hasEfficiency)) {
            if (consumeWater && player.getWorld().getGameRules().getBoolean(BENDING_GRIEFING)) {
                if (blockState.contains(Properties.WATERLOGGED) && blockState.get(Properties.WATERLOGGED)) {
                    player.getWorld().setBlockState(hit.getBlockPos(), blockState.with(Properties.WATERLOGGED, false), 3);
                } else if (blockState.getBlock().equals(Blocks.WATER_CAULDRON)) {
                    player.getWorld().setBlockState(hit.getBlockPos(),Blocks.CAULDRON.getDefaultState());
                } else {
                    player.getWorld().setBlockState(hit.getBlockPos(), Blocks.AIR.getDefaultState());
                }
            }
            return hit.getPos().toVector3f();
        }

        //checks if we have any water bottles in our inventory, if we do, consume it do cast our stuff
        if (player.getInventory().containsAny((stack) -> {
            if (PotionUtil.getPotion(stack).equals(Potions.WATER)) {
                player.getInventory().removeOne(stack);
                player.getInventory().insertStack(Items.GLASS_BOTTLE.getDefaultStack());
                return true;
            }
            return false;
        })) {
            return getEntityLookVector(player, 2.5f).toVector3f();
        }

        return null;
    }

    public static boolean isBlockBendable(BlockPos pos, World world, boolean requireFullBlock, boolean canUseDiverseBlocks) {
        BlockState bState = world.getBlockState(pos);
        Block block = bState.getBlock();

        if (block.equals(Blocks.WATER) || block.equals(Blocks.KELP) ||
                canUseDiverseBlocks && (
                        block.equals(Blocks.ICE)
                                || block.equals(Blocks.PACKED_ICE)
                                || block.equals(Blocks.BLUE_ICE)
                                || block.equals(Blocks.POWDER_SNOW)
                                || block.equals(Blocks.WATER_CAULDRON)
                                || block.equals(Blocks.POWDER_SNOW_CAULDRON)
                                || block.equals(Blocks.SNOW)
                                || block.equals(Blocks.SNOW_BLOCK)
                                || block.equals(Blocks.GRASS)
                                || block.equals(Blocks.CACTUS)
                                || block instanceof LeavesBlock
                                || block instanceof PlantBlock
                                || block instanceof AbstractPlantBlock
                ) && !block.equals(Blocks.DEAD_BUSH)
        ) {
            return true;
        }

        return (bState.contains(Properties.WATERLOGGED) && bState.get(Properties.WATERLOGGED) && !requireFullBlock);
    }

    public static boolean isBeingRainedOn(Entity entity) {
        BlockPos blockPos = entity.getBlockPos();
        return entity.getWorld().hasRain(blockPos) || entity.getWorld().hasRain(BlockPos.ofFloored((double) blockPos.getX(), entity.getBoundingBox().maxY, (double) blockPos.getZ()));
    }
    public static Element get() {
        return elementList.get(1);
    }

    @Override
    public int getColor() {
        return 0xFF40BEFF;
    }

    @Override
    public int getAccentColor() {
        return 0xFF0053F3;
    }
}

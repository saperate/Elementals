package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.Upgrade;
import dev.saperate.elementals.items.ElementalsItems;
import dev.saperate.elementals.items.WaterPouchItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.joml.Vector3f;

import static dev.saperate.elementals.Elementals.BENDING_GRIEFING;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;
import static dev.saperate.elementals.utils.SapsUtils.isBeingRainedOn;

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
        addAbility(new AbilityWater1(), 0);
        addAbility(new AbilityWaterCube());
        addAbility(new AbilityWater2(), 1);
        addAbility(new AbilityWaterArc());
        addAbility(new AbilityWaterHelmet(), 4);
        addAbility(new AbilityWaterShield());
        addAbility(new AbilityWaterSuffocate(), 5);
        addAbility(new AbilityWaterJet());
        addAbility(new AbilityWaterArms());
        addAbility(new AbilityWaterBlade(), 6);
        addAbility(new AbilityWaterBullet());
        addAbility(new AbilityWaterCannon(), 7);
        addAbility(new AbilityWater3(), 2);
        addAbility(new AbilityWaterSurf());
        addAbility(new AbilityWater4(), 3);
        addAbility(new AbilityWaterHealing());
        addAbility(new AbilityWaterTower());
        addAbility(new AbilityWaterJump());

        registerUpgradeKeybind("waterHelmet", 4);
        registerUpgradeKeybind("waterSuffocate", 5);
        registerUpgradeKeybind("waterBlade", 6);
        registerUpgradeKeybind("waterCannon", 7);
    }

    public static Vector3f canBend(Player player, boolean consumeWater) {//todo make it so some abilities still consume water, even though you might have efficiency unlocked
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

        BlockHitResult hit = (BlockHitResult) player.pick(range, 1, true);

        BlockState blockState = player.level().getBlockState(hit.getBlockPos());

        boolean hasEfficiency = plrData.canUseUpgrade("waterPickupEfficiencyI");

        if (hit.getType() == HitResult.Type.BLOCK && isBlockBendable(hit.getBlockPos(), player.level(), !hasEfficiency, hasEfficiency)) {
            if (consumeWater && player.level().getGameRules().getBoolean(BENDING_GRIEFING)) {
                if (blockState.hasProperty(BlockStateProperties.WATERLOGGED) && blockState.getValue(BlockStateProperties.WATERLOGGED)) {
                    player.level().setBlockAndUpdate(hit.getBlockPos(), blockState.setValue(BlockStateProperties.WATERLOGGED, false));
                } else if (blockState.getBlock().equals(Blocks.WATER_CAULDRON)) {
                    player.level().setBlockAndUpdate(hit.getBlockPos(),Blocks.CAULDRON.defaultBlockState());
                } else {
                    player.level().setBlockAndUpdate(hit.getBlockPos(), Blocks.AIR.defaultBlockState());
                }
            }
            return hit.getLocation().toVector3f();
        }

        if (tryRetrieveWater(player)) {
            return getEntityLookVector(player, 2.5f).toVector3f();
        }

        if(plrData.canUseUpgrade("bloodBag")){
            player.hurt(player.damageSources().dryOut(),2);
            return getEntityLookVector(player, 2.5f).toVector3f();
        }
        return null;
    }

    public static boolean isBlockBendable(BlockPos pos, Level world, boolean requireFullBlock, boolean canUseDiverseBlocks) {
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
                                || block.equals(Blocks.SHORT_GRASS)
                                || block.equals(Blocks.TALL_GRASS)
                                || block.equals(Blocks.CACTUS)
                                || block instanceof LeavesBlock
                                || block instanceof GrowingPlantBlock
                ) && !block.equals(Blocks.DEAD_BUSH)
        ) {
            return true;
        }

        return (bState.hasProperty(BlockStateProperties.WATERLOGGED) && bState.getValue(BlockStateProperties.WATERLOGGED) && !requireFullBlock);
    }


    /**
     * Places a single unit of water in the player's inventory.
     * 1 unit of water can produce 1 ability
     * @param player The player where the water will be placed
     * @return true if water was placed in the player's inventory
     */
    public static boolean tryStoreWater(Player player){
        return player.getInventory().hasAnyMatching((stack) -> {
            if(stack.getItem().equals(Items.GLASS_BOTTLE)){
                stack.shrink(1);
                player.getInventory().add(Items.POTION.getDefaultInstance());
                return true;
            } else if (stack.getItem().equals(ElementalsItems.WATER_POUCH_ITEM)) {
                WaterPouchItem item = (WaterPouchItem) stack.getItem();
                return item.fillPouch(stack,1);
            }
            return false;
        });
    }

    /**
     * Takes a single unit of water in the player's inventory.
     * 1 unit of water can produce 1 ability
     * @param player The player where the water will be taken
     * @return true if water was taken from the player's inventory
     */
    public static boolean tryRetrieveWater(Player player){
        return player.getInventory().hasAnyMatching((stack) -> {
            PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);

            if (contents != null && !contents.hasEffects()) {
                player.getInventory().removeItem(stack);
                player.getInventory().add(Items.GLASS_BOTTLE.getDefaultInstance());
                return true;
            } else if (stack.is(ElementalsItems.WATER_POUCH_ITEM.get())) {
                WaterPouchItem item = (WaterPouchItem) stack.getItem();
                return item.emptyPouch(stack,1);
            }
            return false;
        });
    }

    /**
     * Tries to place water at the given position.
     * <br>1) If the position can be bucket placed, it will break the block and place water
     * <br>2) If the position is filled with a water-loggable block, it will simply water-log it
     * <br>Otherwise, it will refuse to place water and return false
     *
     * @param pos the position where water will try to be placed
     * @return whether water was placed
     */
    public static boolean placeWater(BlockPos pos, Level world){
        if(world.dimension().equals(Level.NETHER)){
            //TODO add smoke particles or smth
            return true;
        }
        BlockState bState = world.getBlockState(pos);

        if(bState.getBlock() instanceof LiquidBlockContainer fillable){
            boolean success = fillable.placeLiquid(world,pos,bState, Fluids.WATER.defaultFluidState());
            if(success){
                return true;
            }
        }

        if(bState.canBeReplaced(Fluids.WATER)){
            return world.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
        }
        return false;
    }

    public static Element get() {
        return getElement("Water");
    }

    @Override
    public int getColor() {
        return 0xFF40BEFF;
    }

    @Override
    public int getSecondaryColor() {
        return 0xFF006fa4;
    }

    @Override
    public int getTertiaryColor() {
        return 0xFF000b49;
    }


    @Override
    public String[] getBackgroundTextures() {
        return new String[]{"bottom.png"};
    }


    @Override
    public boolean isSkillTreeComplete(Bender bender) {
        PlayerData plrData = bender.plrData;
        return bender.hasElement(this)
                && (plrData.canUseUpgrade("waterShieldSuffocatePath") || plrData.canUseUpgrade("waterShieldHelmetPath"))
                && (plrData.canUseUpgrade("waterHelmetMastery") || plrData.canUseUpgrade("waterHelmetStealth") || plrData.canUseUpgrade("waterSuffocateRange"))
                && plrData.canUseUpgrade("waterJetDamageI")
                && ((plrData.canUseUpgrade("waterBladeMiningII") && plrData.canUseUpgrade("waterBladeSpeedII")) || plrData.canUseUpgrade("waterCannonDamageI"))
                && plrData.canUseUpgrade("waterArcMastery")
                && plrData.canUseUpgrade("waterJumpRangeII")
                && plrData.canUseUpgrade("waterTowerRangeI")
                && plrData.canUseUpgrade("waterSurfSpeedII")
                && plrData.canUseUpgrade("waterHealingEfficiencyII");
    }
}
package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.Upgrade;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.joml.Vector3f;

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
                                                }, true,1)
                                        },1),
                                }, false, -1,2),
                                new Upgrade("waterSuffocate", new Upgrade[]{
                                        new Upgrade("waterShieldSuffocatePath", 1),
                                        new Upgrade("waterSuffocateRange", 1)
                                }, false, 1, 2)
                        }, true, 2),

                        new Upgrade("waterArc", new Upgrade[]{
                                new Upgrade("waterJet", new Upgrade[]{
                                        new Upgrade("waterJetRangeI", new Upgrade[]{
                                                new Upgrade("waterJetDamageI",1)
                                        }, 1)
                                }, false, -2, 2),
                                new Upgrade("waterArcDamageI", new Upgrade[]{
                                        new Upgrade("waterArcEfficiencyI", new Upgrade[]{
                                                new Upgrade("waterBlade", new Upgrade[]{
                                                        new Upgrade("waterBladeDamageI", new Upgrade[]{
                                                                new Upgrade("waterBladeMiningI", new Upgrade[]{
                                                                        new Upgrade("waterBladeMiningII", 1)
                                                                },1),
                                                                new Upgrade("waterBladeSpeedI", new Upgrade[]{
                                                                        new Upgrade("waterBladeSpeedII", 1)
                                                                },1)
                                                        }, 1)
                                                }, false, -1, 2),
                                                new Upgrade("waterCannon", new Upgrade[]{
                                                        new Upgrade("waterCannonRangeI", new Upgrade[]{
                                                                new Upgrade("waterCannonDamageI",1)
                                                        }, 1)
                                                },2)
                                        }, true,1),
                                        new Upgrade("waterArcSpeedI", new Upgrade[]{
                                                new Upgrade("waterArcSpeedII", new Upgrade[]{
                                                        new Upgrade("waterArcMastery",2)
                                                },1)
                                        }, false, 1,1)
                                },1)

                        },2),
                        new Upgrade("waterJump", new Upgrade[]{
                                new Upgrade("waterJumpRangeI", new Upgrade[]{
                                        new Upgrade("waterJumpRangeII",1),
                                        new Upgrade("waterSurf", new Upgrade[]{
                                                new Upgrade("waterSurfSpeedI", new Upgrade[]{
                                                        new Upgrade("waterSurfSpeedII",1),
                                                        new Upgrade("waterTower", new Upgrade[]{
                                                                new Upgrade("waterTowerRangeI",1)
                                                        },2),
                                                },1)
                                        },2)
                                },1)
                        },2),
                        new Upgrade("waterPickupRangeI", new Upgrade[]{
                                new Upgrade("waterPickupRangeII", new Upgrade[]{
                                        new Upgrade("waterPickupEfficiencyI", new Upgrade[]{
                                                new Upgrade("waterHealing", new Upgrade[]{
                                                        new Upgrade("waterHealingEfficiencyI", new Upgrade[]{
                                                                new Upgrade("waterHealingEfficiencyII", 1)
                                                        },1)
                                                },2)
                                        },1)
                                },1)
                        },1)

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

    }

    public static Vector3f canBend(PlayerEntity player, boolean consumeWater) {
        PlayerData plrData = PlayerData.get(player);

        int range = 5;
        if (plrData.canUseUpgrade("waterPickupRangeII")) {
            range = 15;
        } else if (plrData.canUseUpgrade("waterPickupRangeI")) {
            range = 10;
        }

        BlockHitResult hit = (BlockHitResult) player.raycast(range, 1, true);

        BlockState blockState = player.getEntityWorld().getBlockState(hit.getBlockPos());


        if (hit.getType() == HitResult.Type.BLOCK && isBlockBendable(hit.getBlockPos(), player.getWorld(), true, plrData.canUseUpgrade("waterPickupEfficiencyI"))) {
            if (blockState.get(IntProperty.of("level", 0, 15)).equals(0)) {
                if (consumeWater) {
                    if (blockState.contains(Properties.WATERLOGGED)) {
                        ((Waterloggable) blockState.getBlock()).tryFillWithFluid(player.getWorld(), hit.getBlockPos(), blockState, Blocks.AIR.getDefaultState().getFluidState());
                    } else {
                        player.getWorld().setBlockState(hit.getBlockPos(), Blocks.AIR.getDefaultState());
                    }
                }
                return hit.getPos().toVector3f();
            }
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


        if (block.equals(Blocks.WATER) ||
                canUseDiverseBlocks && (
                        block.equals(Blocks.ICE)
                                || block.equals(Blocks.PACKED_ICE)
                                || block.equals(Blocks.BLUE_ICE)
                                || block.equals(Blocks.POWDER_SNOW)
                                || block.equals(Blocks.WATER_CAULDRON)
                                || block.equals(Blocks.POWDER_SNOW_CAULDRON)
                                || block.equals(Blocks.SNOW)
                                || block.equals(Blocks.SNOW_BLOCK)
                )
        ) {
            return true;
        }

        return (bState.contains(Properties.WATERLOGGED) && bState.get(Properties.WATERLOGGED) && !requireFullBlock);
    }

    public static Element get() {
        return elementList.get(1);
    }
}

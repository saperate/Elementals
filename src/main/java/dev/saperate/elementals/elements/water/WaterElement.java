package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.Upgrade;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.joml.Vector3f;

public class WaterElement extends Element {


    public WaterElement() {
        super("Water",
                new Upgrade[]{
                        new Upgrade("fartherPickup", new Upgrade[]{
                                new Upgrade("waterHelmet", new Upgrade[]{
                                        new Upgrade("waterShield")
                                }),
                                new Upgrade("suffocate", new Upgrade[]{
                                        new Upgrade("waterShield")
                                })
                        }, true),

                        new Upgrade("waterArc", new Upgrade[]{
                                new Upgrade("waterJet"),
                                new Upgrade("buythis", new Upgrade[]{
                                        new Upgrade("waterBlade")
                                }, true)
                        }),
                        new Upgrade("waterAgility", new Upgrade[]{
                                new Upgrade("waterTower"),
                                new Upgrade("surf")
                        })
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
        BlockHitResult hit = (BlockHitResult) player.raycast(5, 1, true);

        BlockState blockState = player.getEntityWorld().getBlockState(hit.getBlockPos());


        if (hit.getType() == HitResult.Type.BLOCK && isBlockBendable(hit.getBlockPos(),player.getWorld(),true)) {
            if (blockState.get(IntProperty.of("level", 0, 15)).equals(0)) {//TODO maybe make this less strict
                if(consumeWater){
                    player.getWorld().setBlockState(hit.getBlockPos(), Blocks.AIR.getDefaultState());
                }
               return hit.getPos().toVector3f();
            }
        }
        return null;
    }

    public static boolean isBlockBendable(BlockPos pos, World world, boolean requireFullBlock){
        BlockState bState = world.getBlockState(pos);
        Block block = bState.getBlock();


        if(block.equals(Blocks.WATER)
                || block.equals(Blocks.ICE)
                || block.equals(Blocks.PACKED_ICE)
                || block.equals(Blocks.BLUE_ICE)
                || block.equals(Blocks.POWDER_SNOW)
                || block.equals(Blocks.WATER_CAULDRON)
                || block.equals(Blocks.POWDER_SNOW_CAULDRON)
                || block.equals(Blocks.SNOW)
                || block.equals(Blocks.SNOW_BLOCK)){
            return true;
        }

        return  (bState.contains(Properties.WATERLOGGED) && bState.get(Properties.WATERLOGGED) && !requireFullBlock);
    }

    public static Element get() {
        return elementList.get(1);
    }
}

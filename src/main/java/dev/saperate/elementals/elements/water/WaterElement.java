package dev.saperate.elementals.elements.water;

import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.Upgrade;
import dev.saperate.elementals.elements.fire.AbilityFire1;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3f;

import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;

public class WaterElement extends Element {


    public WaterElement() {
        super("Water",
                new Upgrade[]{
                        new Upgrade("waterCube", new Upgrade[]{
                                new Upgrade("waterHelmet")
                        }),
                        new Upgrade("waterArc")
                });
        addAbility(new AbilityWater1(),true);
        addAbility(new AbilityWaterCube(), false);
        addAbility(new AbilityWater2(),true);
        addAbility(new AbilityWaterArc(), false);
        addAbility(new AbilityWaterHelmet(),false);
    }

    public static Vector3f canBend(PlayerEntity player) {
        BlockHitResult hit = (BlockHitResult) player.raycast(5, 0, true);
        BlockState blockState = player.getEntityWorld().getBlockState(hit.getBlockPos());

        if (hit.getType() == HitResult.Type.BLOCK && blockState.getBlock().equals(Blocks.WATER)) {
            if (blockState.get(IntProperty.of("level", 0, 15)).equals(0)) {

                Vector3f pos = getEntityLookVector(player, 3);
                player.getWorld().setBlockState(hit.getBlockPos(), Blocks.AIR.getDefaultState());
                return pos;
            }
        }
        return null;
    }

    public static Element get(){
        return elementList.get(1);
    }
}

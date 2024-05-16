package dev.saperate.elementals.elements.earth;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.Upgrade;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

import static dev.saperate.elementals.entities.earth.EarthBlockEntity.EARTHBLOCK;
import static dev.saperate.elementals.misc.ElementalsCustomTags.EARTH_BENDABLE_BLOCKS;


public class EarthElement extends Element {
    public EarthElement() {
        super("Earth", new Upgrade[]{
                new Upgrade("shrapnel", new Upgrade[]{
                        new Upgrade("earthWall"),
                        new Upgrade("chunkPickup")
                },true),
                new Upgrade("mine", new Upgrade[]{
                        new Upgrade("earthTrap", new Upgrade[]{
                                new Upgrade("earthRavine"),
                                new Upgrade("earthSpikes")
                        },true)
                })
        });
        addAbility(new AbilityEarth1(), true);
        addAbility(new AbilityEarthBlockPickup());
        addAbility(new AbilityEarthWall());
        addAbility(new AbilityEarthChunkPickup());
        addAbility(new AbilityEarth2(), true);
        addAbility(new AbilityEarthMine());
        addAbility(new AbilityEarthTrap());
        addAbility(new AbilityEarthRavine());
        addAbility(new AbilityEarthSpikes());
        addAbility(new AbilityEarth3(), true);
        addAbility(new AbilityEarthPillar());
        addAbility(new AbilityEarthJump());
        addAbility(new AbilityEarth4(), true);
        addAbility(new AbilityEarthArmor());
    }

    public static Element get() {
        return elementList.get(3);
    }

    /**
     * Calculates if the block can be bent, if it can, we return an array of Object which contains in this order:
     * <br>0 - Vec3d -> the position where the hit intercepted
     * <br>1 - BlockState -> the block state before we consumed it
     * <br>2 - BlockPos -> the block pos where the raycast hit
     * <br>3 - Direction -> the direction of the face where the raycast hit
     * @return Array of calculated items
     */
    public static Object[] canBend(PlayerEntity player, boolean consumeBlock) {
        BlockHitResult hit = (BlockHitResult) player.raycast(5, 1, false);

        BlockState blockState = player.getEntityWorld().getBlockState(hit.getBlockPos());

        if (hit.getType() == HitResult.Type.BLOCK && isBlockBendable(hit.getBlockPos(), player.getWorld())) {
            if (consumeBlock) {
                player.getWorld().setBlockState(hit.getBlockPos(), Blocks.AIR.getDefaultState());
            }
            return new Object[]{hit.getPos(),blockState, hit.getBlockPos(), hit.getSide()};
        }
        return null;
    }

    public static boolean isBlockBendable(BlockPos pos, World world) {
        BlockState bState = world.getBlockState(pos);
        return bState.isIn(EARTH_BENDABLE_BLOCKS);
    }

    public static boolean isBlockBendable(BlockState bState) {
        return bState.isIn(EARTH_BENDABLE_BLOCKS);
    }

    /**
     * Makes a hole given a starting position and depth. It will not mine blocks that are not bendable nor does it drop them
     * Can optionally damage entities standing on top of the block being broken
     * @param pos  the starting y cord along with the x and z
     * @param depth  How far down will the hole go
     * @param bender  The bender that cast the ability
     * @param damagedEntities  Entities that were already damaged, set to null to deal no damage
     */
    public static void makeHole(BlockPos pos, int depth, Bender bender, ArrayList<LivingEntity> damagedEntities) {
        for (int y = 0; y < depth; y++) {
            BlockPos bPos = pos.down(y);
            if(EarthElement.isBlockBendable(bPos, bender.player.getWorld())){
                bender.player.getWorld().breakBlock(bPos,false);
            }
        }

        if(damagedEntities != null){
            damageEntityAboveBlock(bender.player,pos,damagedEntities,1);
        }
    }
    /**
     * Damages entities above a certain block. This is made for full blocks, stairs and slabs might not work
     *
     * @param player The caster of the move as to not damage them
     * @param pos The position of the block where we want to damage entities above
     * @param amount The amount of damage dealt
     */
    public static void damageEntityAboveBlock(PlayerEntity player, BlockPos pos, float amount){
        damageEntityAboveBlock(player,pos,new ArrayList<>(), amount);
    }

    /**
     * Damages entities above a certain block. This is made for full blocks, stairs and slabs might not work
     *
     * @param player The caster of the move as to not damage them
     * @param pos The position of the block where we want to damage entities above
     * @param damagedEntities (Optional) List of already damaged entities to prevent double hits
     * @param amount The amount of damage dealt
     */
    public static void damageEntityAboveBlock(PlayerEntity player, BlockPos pos, ArrayList<LivingEntity> damagedEntities, float amount){
        List<LivingEntity> hits = player.getWorld().getEntitiesByClass(LivingEntity.class,
                EARTHBLOCK.createSimpleBoundingBox(pos.getX() + 0.5f,pos.getY() + 1,pos.getZ() + 0.5f), LivingEntity::isOnGround);
        for (LivingEntity entity : hits){
            if(entity == player){
                continue;
            }
            damagedEntities.add(entity);
            entity.damage(player.getDamageSources().playerAttack(player), 1);
        }
    }
}

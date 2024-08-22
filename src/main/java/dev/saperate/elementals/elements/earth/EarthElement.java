package dev.saperate.elementals.elements.earth;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.Upgrade;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

import static dev.saperate.elementals.Elementals.BENDING_GRIEFING;
import static dev.saperate.elementals.effects.StunnedStatusEffect.STUNNED_EFFECT;
import static dev.saperate.elementals.entities.ElementalEntities.EARTHBLOCK;
import static dev.saperate.elementals.misc.ElementalsCustomTags.EARTH_BENDABLE_BLOCKS;
import static dev.saperate.elementals.utils.SapsUtils.getEntityLookVector;


public class EarthElement extends Element {
    public EarthElement() {
        super("Earth", new Upgrade[]{
                new Upgrade("earthBlock", new Upgrade[]{
                        new Upgrade("earthBlockShrapnel", new Upgrade[]{
                                new Upgrade("earthWall", new Upgrade[]{
                                        new Upgrade("earthWallDurationI", new Upgrade[]{
                                                new Upgrade("earthWallDurationII", new Upgrade[]{
                                                        new Upgrade("earthWallDurationIII", new Upgrade[]{
                                                                new Upgrade("earthWallDurationIV", new Upgrade[]{
                                                                        new Upgrade("earthWallAutoTimer", 1)
                                                                }, 1)
                                                        }, 1)
                                                }, 1)
                                        }, 1)
                                }, 2),
                                new Upgrade("earthChunk", new Upgrade[]{
                                        new Upgrade("earthChunkSizeI", 1)
                                }, 2)
                        }, true, 2),
                        new Upgrade("earthBlockSpeedI", new Upgrade[]{
                                new Upgrade("earthBlockDamageI", new Upgrade[]{
                                        new Upgrade("earthBlockSpeedII", 1)
                                }, 1)
                        }, false, 1, 1)
                }, 2),

                new Upgrade("earthMine", new Upgrade[]{
                        new Upgrade("earthTrap", new Upgrade[]{
                                new Upgrade("earthRavine", new Upgrade[]{
                                        new Upgrade("earthRavineRangeI", 1),
                                        new Upgrade("earthRavineSpreadI", 1)
                                }, true, -1, 2),
                                new Upgrade("earthSpikes", new Upgrade[]{
                                        new Upgrade("earthSpikesRangeI", 1),
                                        new Upgrade("earthSpikesSpreadI", 1)
                                }, true, 1, 2)
                        }, true, 2)
                }, 2),
                new Upgrade("earthPillar", new Upgrade[]{
                        new Upgrade("earthJump", new Upgrade[]{
                                new Upgrade("earthJumpRangeI", new Upgrade[]{
                                        new Upgrade("earthJumpRangeII", 1),
                                }, 1)
                        }, 2),
                        new Upgrade("earthPillarTallI", 1)
                }, 2),
                new Upgrade("earthPickupRangeI", new Upgrade[]{
                        new Upgrade("earthPickupRangeII", new Upgrade[]{
                                new Upgrade("earthSeismicSense", new Upgrade[]{
                                        new Upgrade("earthArmor", 2)
                                }, 2)
                        }, 1)
                }, 3)
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
        PlayerData plrData = PlayerData.get(player);
        int range = 5;
        if (plrData.canUseUpgrade("earthPickupRangeII")) {
            range = 15;
        } else if (plrData.canUseUpgrade("earthPickupRangeI")) {
            range = 10;
        }
        BlockHitResult hit = raycastCollidableBlocks(player.getCameraPosVec(1), getEntityLookVector(player,range), player, 0);
        if(hit == null){
            return null;
        }

        if (isBlockBendable(hit.getBlockPos(), player.getWorld())) {
            BlockState blockState = player.getEntityWorld().getBlockState(hit.getBlockPos());
            if (consumeBlock && player.getWorld().getGameRules().getBoolean(BENDING_GRIEFING)) {
                player.getWorld().setBlockState(hit.getBlockPos(), Blocks.AIR.getDefaultState());
            }
            return new Object[]{hit.getPos(), blockState, hit.getBlockPos(), hit.getSide()};
        }
        return null;
    }

    public static BlockHitResult raycastCollidableBlocks(Vec3d start, Vec3d end, Entity origin, int depth){
        if(depth >= 20){
            return null;
        }
        BlockHitResult bHit = origin.getWorld().raycast(
                new RaycastContext(
                        start,end, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, origin
                ));
        if(bHit.getType().equals(HitResult.Type.MISS)){
            return null;
        }

        BlockState state = origin.getWorld().getBlockState(bHit.getBlockPos());
        if(state.isSolid()){
           return bHit;
        }
        return raycastCollidableBlocks(bHit.getPos(),end,origin, depth + 1);
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
        if(!bender.player.getWorld().getGameRules().getBoolean(BENDING_GRIEFING)){
            return;
        }
        for (int y = 0; y < depth; y++) {
            BlockPos bPos = pos.down(y);
            if (EarthElement.isBlockBendable(bPos, bender.player.getWorld())) {
                bender.player.getWorld().breakBlock(bPos, false);
            }
        }

        if (damagedEntities != null) {
            damageEntityAboveBlock(bender.player, pos, damagedEntities, 1);
        }
    }

    /**
     * Damages entities above a certain block. This is made for full blocks, stairs and slabs might not work
     *
     * @param player The caster of the move as to not damage them
     * @param pos The position of the block where we want to damage entities above
     * @param amount The amount of damage dealt
     */
    public static void damageEntityAboveBlock(PlayerEntity player, BlockPos pos, float amount) {
        damageEntityAboveBlock(player, pos, new ArrayList<>(), amount);
    }

    /**
     * Damages entities above a certain block. This is made for full blocks, stairs and slabs might not work
     *
     * @param player The caster of the move as to not damage them
     * @param pos The position of the block where we want to damage entities above
     * @param damagedEntities (Optional) List of already damaged entities to prevent double hits
     * @param amount The amount of damage dealt
     */
    public static void damageEntityAboveBlock(PlayerEntity player, BlockPos pos, ArrayList<LivingEntity> damagedEntities, float amount) {
        List<LivingEntity> hits = player.getWorld().getEntitiesByClass(LivingEntity.class,
                EARTHBLOCK.createSimpleBoundingBox(pos.getX() + 0.5f, pos.getY() + 1, pos.getZ() + 0.5f), LivingEntity::isOnGround);
        for (LivingEntity entity : hits) {
            if (entity == player) {
                continue;
            }
            damagedEntities.add(entity);
            entity.damage(player.getDamageSources().playerAttack(player), 2.5f);
            entity.addStatusEffect(new StatusEffectInstance(STUNNED_EFFECT, 200, 1, false,false,true));
        }
    }

    @Override
    public int getColor() {
        return 0xFF77F963;
    }

    @Override
    public int getAccentColor() {
        return 0xFF17711B;
    }

    @Override
    public boolean isSkillTreeComplete(Bender bender) {
        PlayerData plrData = bender.plrData;
        return bender.hasElement(this)
                && (plrData.canUseUpgrade("earthWallAutoTimer") || plrData.canUseUpgrade("earthChunkSizeI"))
                && plrData.canUseUpgrade("earthBlockSpeedII")
                && (plrData.canUseUpgrade("earthRavineRangeI") || plrData.canUseUpgrade("earthRavineSpreadI") || plrData.canUseUpgrade("earthSpikesRangeI") || plrData.canUseUpgrade("earthSpikesSpreadI"))
                && plrData.canUseUpgrade("earthJumpRangeII")
                && plrData.canUseUpgrade("earthPillarTallI")
                && plrData.canUseUpgrade("earthArmor")
        ;
    }
}

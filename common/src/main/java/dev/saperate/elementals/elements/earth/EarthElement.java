package dev.saperate.elementals.elements.earth;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.effects.ElementalsStatusEffects;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.Upgrade;
import dev.saperate.elementals.elements.metal.MetalElement;
import dev.saperate.elementals.items.DirtBottleItem;
import dev.saperate.elementals.items.ElementalItems;
import dev.saperate.elementals.items.WaterPouchItem;
import dev.saperate.elementals.misc.BlockRestoreManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.MobEffectInstance;
import net.minecraft.entity.player.Player;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.Level;
import org.joml.Vector3f;

import java.util.*;

import static dev.saperate.elementals.Elementals.BENDING_GRIEFING;
import static dev.saperate.elementals.entities.ElementalEntities.EARTHBLOCK;
import static dev.saperate.elementals.misc.ElementalsCustomTags.EARTH_BENDABLE_BLOCKS;
import static dev.saperate.elementals.misc.ElementalsCustomTags.METAL_BENDABLE_BLOCKS;
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
        return getElement("Earth");
    }

    /**
     * Calculates if the block can be bent, if it can, we return an array of Object which contains in this order:
     * <br>0 - Vec3 -> the position where the hit intercepted
     * <br>1 - BlockState -> the block state before we consumed it
     * <br>2 - BlockPos -> the block pos where the raycast hit
     * <br>3 - Direction -> the direction of the face where the raycast hit
     * @return Array of calculated items
     */
    public static Object[] canBend(Player player, boolean consumeBlock) {
        PlayerData plrData = PlayerData.get(player);
        int range = 5;
        if (plrData.canUseUpgrade("earthPickupRangeII")) {
            range = 15;
        } else if (plrData.canUseUpgrade("earthPickupRangeI")) {
            range = 10;
        }
        BlockHitResult hit = raycastCollidableBlocks(player.getCameraPosVec(1), getEntityLookVector(player, range), player, 0);
        if (hit == null) {
            return null;
        }

        if (isBlockBendable(hit.getOnPos(), Bender.getBender((ServerPlayerEntity) player))) {
            BlockState blockState = player.getEntityWorld().getBlockState(hit.getOnPos());
            if (consumeBlock) {
                player.level().setBlockState(hit.getOnPos(), Blocks.AIR.getDefaultState());
                if (!player.level().getGameRules().getBoolean(BENDING_GRIEFING)) {
                    BlockRestoreManager.addBlockToRestore(new BlockRestoreManager.BlockInformation(
                            hit.getOnPos(),
                            blockState,
                            player.level().getRegistryKey(),
                            40 + player.level().random.nextBetween(0, 140)
                    ));
                }
            }
            return new Object[]{hit.getPos(), blockState, hit.getOnPos(), hit.getSide()};
        }


        return null;
    }


    public static BlockHitResult raycastCollidableBlocks(Vec3 start, Vec3 end, Entity origin, int depth) {
        if (depth >= 20) {
            return null;
        }
        BlockHitResult bHit = origin.level().raycast(
                new RaycastContext(
                        start, end, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, origin
                ));
        if (bHit.getType().equals(HitResult.Type.MISS)) {
            return null;
        }

        BlockState state = origin.level().getBlockState(bHit.getOnPos());
        if (state.isSolid()) {
            return bHit;
        }
        return raycastCollidableBlocks(bHit.getPos(), end, origin, depth + 1);
    }


    public static boolean isBlockBendable(BlockPos pos, Bender bender) {
        BlockState bState = bender.player.level().getBlockState(pos);
        return isBlockBendable(bState, bender);
    }

    //TODO check for netherite and ancient debris and make it more expensive
    public static boolean isBlockBendable(BlockState bState, Bender bender) {
        return bState.isIn(EARTH_BENDABLE_BLOCKS) || (bender.hasElement(MetalElement.get()) && bState.isIn(METAL_BENDABLE_BLOCKS));
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
            BlockState bState = bender.player.level().getBlockState(bPos);
            if (EarthElement.isBlockBendable(bState, bender)) {
                BlockRestoreManager.addBlockToRestore(new BlockRestoreManager.BlockInformation(
                        bPos,
                        bState,
                        bender.player.level().getRegistryKey(),
                        40 + bender.player.level().random.nextBetween(0, 140)
                ));

                bender.player.level().breakBlock(bPos, false);
            }
        }

        if (damagedEntities != null) {
            damageEntityAboveBlock(bender.player, pos, damagedEntities, 2);
        }
    }

    /**
     * Damages entities above a certain block. This is made for full blocks, stairs and slabs might not work
     *
     * @param player The caster of the move as to not damage them
     * @param pos The position of the block where we want to damage entities above
     * @param amount The amount of damage dealt
     */
    public static void damageEntityAboveBlock(Player player, BlockPos pos, float amount) {
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
    public static void damageEntityAboveBlock(Player player, BlockPos pos, ArrayList<LivingEntity> damagedEntities, float amount) {
        List<LivingEntity> hits = player.level().getEntitiesByClass(LivingEntity.class,
                EARTHBLOCK.getSpawnBox(pos.getX(), pos.getY() + 1, pos.getZ()), LivingEntity::isOnGround);
        for (LivingEntity entity : hits) {
            if (entity == player) {
                continue;
            }
            damagedEntities.add(entity);
            entity.hurt(player.damageSources().playerAttack(player), 2.5f);
            entity.addEffect(new MobEffectInstance(ElementalsStatusEffects.STUNNED, 200, 1, false,false,true));
        }
    }

    @Override
    public int getColor() {
        return 0xFF34a830;
    }

    @Override
    public int getSecondaryColor() {
        return 0xFF058901;
    }

    @Override
    public int getTertiaryColor() {
        return 0xFF025400;
    }

    @Override
    public String[] getBackgroundTextures() {
        return new String[]{"bottom.png"};
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

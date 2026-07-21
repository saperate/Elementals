package dev.saperate.elementals.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.items.ElementalsItems;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

/**
 * A collection of methods that makes some redundant stuff easier to use
 */
public final class SapsUtils {

    /**
     * This checks if an entity collides with blocks. it uses a float to see how close we need to be for it
     * to count as a collision. Lower is not necessarily better since there is a chance that if you set it
     * too low it will miss the target.
     * <br><br>Recommended sensitivity is 0.1f.
     * <br><b>This includes fluids in the collision check</b>
     * <br><b>This only checks for solid blocks</b>
     * @param entity The entity that we are checking collisions for
     * @param sensitivity How close does it have to be to a block to collide
     * @return The block position of the hit or null if none is found
     * @see Entity
     * @see BlockPos
     */
    public static BlockPos checkBlockCollision(Entity entity, float sensitivity) {
        return checkBlockCollision(entity, sensitivity, true, true, entity.getBoundingBox());
    }

    /**
     * This checks if an entity collides with blocks. it uses a float to see how close we need to be for it
     * to count as a collision. Lower is not necessarily better since there is a chance that if you set it
     * too low it will miss the target.
     * <br><br>Recommended sensitivity is 0.1f.
     * <br><b>This only checks for solid blocks</b>
     * @param entity The entity that we are checking collisions for
     * @param sensitivity How close does it have to be to a block to collide
     * @param includeFluids Whether fluids count in the collision check
     * @return The block position of the hit or null if none is found
     * @see Entity
     * @see BlockPos
     */
    public static BlockPos checkBlockCollision(Entity entity, float sensitivity, boolean includeFluids) {
        return checkBlockCollision(entity, sensitivity, includeFluids, true, entity.getBoundingBox());
    }

    /**
     * This checks if an entity collides with blocks. it uses a float to see how close we need to be for it
     * to count as a collision. Lower is not necessarily better since there is a chance that if you set it
     * too low it will miss the target.
     * <br><br>Recommended sensitivity is 0.1f.
     * @param entity The entity that we are checking collisions for
     * @param sensitivity How close does it have to be to a block to collide
     * @param includeFluids Whether fluids count in the collision check
     * @param requireSolid Whether not solid blocks like grass should be included
     * @return The block position of the hit or null if none is found
     * @see Entity
     * @see BlockPos
     */
    public static BlockPos checkBlockCollision(Entity entity, float sensitivity, boolean includeFluids, boolean requireSolid) {
        return checkBlockCollision(entity, sensitivity, includeFluids, requireSolid, entity.getBoundingBox());
    }

    /**
     * This checks if an entity collides with blocks. it uses a float to see how close we need to be for it
     * to count as a collision. Lower is not necessarily better since there is a chance that if you set it
     * too low it will miss the target.
     * <br><br>Recommended sensitivity is 0.1f.
     * @param entity The entity that we are checking collisions for
     * @param sensitivity How close does it have to be to a block to collide
     * @param includeFluids Whether fluids count in the collision check
     * @param bounds The bounding box that we are checking
     * @return The block position of the hit or null if none is found
     * @see Entity
     * @see BlockPos
     * @see AABB
     */
    public static BlockPos checkBlockCollision(Entity entity, float sensitivity, boolean includeFluids, boolean requireSolid, AABB bounds) {
        AABB box = bounds.inflate(sensitivity);
        BlockPos blockPos = BlockPos.containing(box.minX + 1.0E-7, box.minY + 1.0E-7, box.minZ + 1.0E-7);
        BlockPos blockPos2 = BlockPos.containing(box.maxX - 1.0E-7, box.maxY - 1.0E-7, box.maxZ - 1.0E-7);

        //There is a weird bug where if you didn't move the entity yet, the bounding box doesn't add position
        //So this is here to fix that
        if (!isAboutEquals(box.minX, entity.getX(), box.maxX - box.minX)) {
            blockPos = blockPos.offset(entity.getOnPos());
            blockPos2 = blockPos2.offset(entity.getOnPos());
        }

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        List<BlockPos> possibleHits = new ArrayList<>();

        for (int i = blockPos.getX(); i <= blockPos2.getX(); ++i) {
            for (int j = blockPos.getY(); j <= blockPos2.getY(); ++j) {
                for (int k = blockPos.getZ(); k <= blockPos2.getZ(); ++k) {
                    mutable.set(i, j, k);
                    BlockState blockState = entity.level().getBlockState(mutable);
                    if (blockState.isAir()
                            || (!includeFluids && blockState.getBlock() instanceof LiquidBlock)
                            || (requireSolid && !blockState.isSolid())
                    ) {
                        continue;
                    }
                    blockState.entityInside(entity.level(), mutable, entity);
                    possibleHits.add(new BlockPos(mutable));
                }
            }
        }


        //Get the closest block from all possible hits
        BlockPos bestHit = possibleHits.isEmpty() ? null : possibleHits.get(0);
        double bestDistance = possibleHits.isEmpty() ? -1 : entity.distanceToSqr(bestHit.getCenter());
        for (BlockPos hit : possibleHits) {
            double dist = entity.distanceToSqr(hit.getCenter());
            if (dist < bestDistance) {
                bestHit = hit;
                bestDistance = dist;
            }
        }

        return bestHit;
    }


    /**
     * Hacky way to get which blocks can be affected by an explosion
     */
    public static void getAffectedBlocks(Level world, Entity entity, double x, double y, double z, float power) {
        ObjectArrayList<BlockPos> affectedBlocks = new ObjectArrayList<>();
        int l, k;
        world.gameEvent(entity, GameEvent.EXPLODE, new Vec3(x, y, z));
        HashSet<BlockPos> set = Sets.newHashSet();
        for (int j = 0; j < 16; ++j) {
            for (k = 0; k < 16; ++k) {
                block2:
                for (l = 0; l < 16; ++l) {
                    if (j != 0 && j != 15 && k != 0 && k != 15 && l != 0 && l != 15) continue;
                    for (float h = power * (0.7f + world.random.nextFloat() * 0.6f); h > 0.0f; h -= 0.22500001f) {
                        BlockPos blockPos = BlockPos.containing(x, y, z);
                        if (world.isOutsideBuildHeight(blockPos)) continue block2;
                        set.add(blockPos);
                    }
                }
            }
        }

        affectedBlocks.addAll(set);
    }


    public static String elementsArrayToString(ArrayList<Element> elements) {
        StringBuilder builder = new StringBuilder();

        builder.append("elements = {\n");
        for (int i = 0; i < elements.size() - 1; i++) {
            builder.append("\t").append(elements.get(i).name).append("\n");
        }
        builder.append("\t").append(elements.get(elements.size() - 1).name).append("\n");
        builder.append("}");

        return builder.toString();
    }


    public static float calculatePitch(Vec3 direction) {
        double horizontalDistance = Math.sqrt(direction.x * direction.x + direction.z * direction.z);
        double pitch = Math.atan2(direction.y, horizontalDistance);
        return (float) Math.toDegrees(pitch);
    }

    public static float calculateYaw(Vec3 direction) {
        double yaw = Math.atan2(-direction.x, direction.z);
        return (float) Math.toDegrees(yaw);
    }

    public static void summonParticles(Entity entity, RandomSource rnd, ParticleOptions type, float velocity, int density) {
        summonParticles(entity, rnd, type, velocity, density, 1);
    }


    public static void summonParticles(Entity entity, RandomSource rnd, ParticleOptions type, float velocity, int density, float rndYForce) {
        for (int i = 0; i < density; i++) {
            entity.level().addParticle(type,
                    entity.getX() - 0.5f + rnd.nextDouble(),
                    entity.getY() + rnd.nextDouble() * rndYForce,
                    entity.getZ() - 0.5f + rnd.nextDouble(),
                    rnd.nextDouble() * velocity, rnd.nextDouble() * velocity, rnd.nextDouble() * velocity);
        }
    }


    public static void summonParticlesVelocityAwayFromPoint(Entity entity, Random rnd, ParticleOptions type, float velocity, int density, Vec3 randomPosMultiplier) {
        for (int i = 0; i < density; i++) {
            Vec3 particlePos = new Vec3(entity.getX() - 0.5f + rnd.nextDouble() * randomPosMultiplier.x,
                    entity.getY() + rnd.nextDouble() * randomPosMultiplier.y,
                    entity.getZ() - 0.5f + rnd.nextDouble() * randomPosMultiplier.z
            );

            Vec3 dir = particlePos.subtract(entity.position());

            entity.level().addParticle(type,
                    particlePos.x, particlePos.y, particlePos.z,
                    dir.x * velocity,
                    dir.y * velocity,
                    dir.z * velocity);
        }
    }


    public static void serverSummonParticles(ServerLevel world, ParticleOptions type, Entity entity, RandomSource rnd,
                                             double vX, double vY, double vZ, double speed, int count,
                                             float offsetX, float offsetY, float offsetZ, float vAmplitude) {
        serverSummonParticles(world, type, entity.position(), rnd, vX, vY, vZ, speed, count, offsetX, offsetY, offsetZ, vAmplitude);
    }

    public static void serverSummonParticles(ServerLevel world, ParticleOptions type, Vec3 pos, RandomSource rnd,
                                             double vX, double vY, double vZ, double speed, int count,
                                             float offsetX, float offsetY, float offsetZ, float vAmplitude) {
        for (int i = 0; i < count; i++) {
            world.sendParticles(type,
                    pos.x + rnd.nextDouble() - 0.5f + offsetX,
                    pos.y + rnd.nextDouble() + 0.5f + offsetY,
                    pos.z + rnd.nextDouble() - 0.5f + offsetZ,
                    0,
                    vX + rnd.nextDouble() * vAmplitude,
                    vY + rnd.nextDouble() * vAmplitude,
                    vZ + rnd.nextDouble() * vAmplitude,
                    speed
            );
        }


    }


    public static BlockPos vec3fToBlockPos(Vector3f vec) {
        return new BlockPos(
                (int) Math.floor(vec.x),
                (int) Math.floor(vec.y),
                (int) Math.floor(vec.z)
        );
    }

    public static Vec3 getEntityLookVector(Entity e, float distance) {
        if (e == null) {
            return new Vec3(0, 0, 0);
        }
        double rYaw = Math.toRadians(e.getYRot() + 90);
        double rPitch = Math.toRadians(-e.getXRot());

        float x = (float) (Math.cos(rPitch) * Math.cos(rYaw));
        float y = (float) Math.sin(rPitch);
        float z = (float) (Math.cos(rPitch) * Math.sin(rYaw));

        return new Vec3(x, y, z).scale(distance).add(e.getEyePosition());
    }

    public static Vec3 getEntityLookVectorIgnorePitch(Entity e, float distance) {
        if (e == null) {
            return new Vec3(0, 0, 0);
        }
        double rYaw = Math.toRadians(e.getYRot() + 90);
        double rPitch = Math.toRadians(-e.getXRot());

        float x = (float) (Math.cos(rYaw));
        float z = (float) (Math.sin(rYaw));

        return new Vec3(x, 0, z).scale(distance).add(e.getEyePosition());
    }

    public static EntityHitResult raycastEntity(Entity origin, double maxDistance, Predicate<Entity> predicate) {
        Vec3 cameraPos = origin.getEyePosition(1.0f);
        Vec3 rot = origin.getViewVector(1.0f);
        Vec3 context = cameraPos.add(rot.x * maxDistance, rot.y * maxDistance, rot.z * maxDistance);
        AABB box = origin.getBoundingBox().expandTowards(rot.scale(maxDistance)).inflate(1d);
        return ProjectileUtil.getEntityHitResult(origin, origin.getEyePosition(), context, box, predicate.and(entity -> entity instanceof LivingEntity && !entity.isSpectator() && entity.isPickable()), maxDistance * maxDistance);
    }

    public static BlockHitResult raycastBlockCustomRotation(Entity origin, float maxDistance, boolean includeFluids, Vec3 rotation) {
        Vec3 cameraPos = origin.getEyePosition(1);
        Vec3 context = cameraPos.add(rotation.x * maxDistance, rotation.y * maxDistance, rotation.z * maxDistance);
        return origin.level().clip(new ClipContext(cameraPos, context, ClipContext.Block.OUTLINE, includeFluids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, origin));

    }

    //TODO maybe use a discard block list so that if we hit that and we got both and entity hit and a block hit we only keep the entity hit
    public static HitResult raycastFull(Entity origin, double maxDistance, boolean includeFluids) {
        return raycastFull(origin, maxDistance, includeFluids, Entity::isAlive);
    }

    public static HitResult raycastFull(Entity origin, double maxDistance, boolean includeFluids, Predicate<Entity> entityPredicate) {
        EntityHitResult eHit = raycastEntity(origin, maxDistance, entityPredicate);
        BlockHitResult bHit = (BlockHitResult) origin.pick(maxDistance, 1.0f, includeFluids);

        if (eHit == null) {
            return bHit;
        }

        if (eHit.distanceTo(origin) < bHit.distanceTo(origin)) {
            return eHit;
        } else {
            return bHit;
        }
    }

    /**
     * Checks if the hit result contains an entity.
     * If it does contain an entity we return it, if it doesn't we return null.
     * @return An entity or null
     */
    public static Entity entityFromHitResult(HitResult result) {
        if (result.getType().equals(HitResult.Type.ENTITY)) {
            return ((EntityHitResult) result).getEntity();
        } else {
            return null;
        }
    }

    public static Boolean isAboutEquals(double a, double b, double errorMargin) {
        return Math.abs(a - b) <= errorMargin;
    }

    public static Boolean isAboutEquals(Vec3 a, Vec3 b, double errorMargin) {
        return a.distanceTo(b) <= errorMargin;
    }

    /**
     * This method checks safely if an entity has a status effect.
     * It will return true if the entity has the effect <b>AND</b>
     * if the entity is not null
     * @param effect The effect we want to check
     * @param entity The entity that we check
     * @return true if the entity has the status effect
     */
    public static boolean safeHasStatusEffect(Holder<MobEffect> effect, LivingEntity entity) {
        boolean hasEffect = false;
        try {
            if (entity != null) {
                hasEffect = entity.hasEffect(effect);
            } else {
                return false;
            }
        } catch (Exception ignored) {
        }
        return hasEffect;
    }


    /**
     * Searches for "<br>" in the translatable to be able to actually do line breaks in tooltips
     * @return the number of args used in the translatable
     */
    public static int addTranslatable(List<Component> tooltip, String key, Object... args) {
        String raw = Component.translatable(key, args).getString();
        if (raw.equals(key)) {
            return 0;
        }
        for (String str : raw.split("<br>")) {
            tooltip.add(Component.literal(str));
        }
        return raw.split("%d").length;
    }

    /**
     * Takes in a translatable and makes a new line every N amount of spaces (defined by max)
     *
     * @param tooltip the list where we will add lines
     * @param key Where we will find the translatable
     * @param max The maximum amount of words per line (recommended: 6)
     * @param args The arguments used by the translatable
     * @return the number of args used in the translatable
     */
    public static int addTranslatableAutomaticLineBreaks(List<Component> tooltip, String key, int max, Object... args) {
        String raw = Component.translatable(key, args).getString();
        if (raw.equals(key)) {
            return 0;
        }
        StringBuilder builder = new StringBuilder();
        String[] arr = raw.split(" ");
        for (int i = 0; i < arr.length; i++) {
            builder.append(arr[i]).append(" ");
            if (i % max == 0 && i != arr.length - 1) {
                tooltip.add(Component.literal(builder.toString()));
                builder = new StringBuilder();
            }
        }
        tooltip.add(Component.literal(builder.toString()));
        return raw.split("%d").length;
    }

    public static void launchEntity(Entity entity, float power) {
        launchEntity(entity, power, true);
    }

    public static void launchEntity(Entity entity, float power, boolean reduceYVelocity) {
        if (entity instanceof Player player && !player.onGround()
                && hasItemInEitherHands(player, ElementalsItems.GLIDER_ITEM.get())) {
            power *= 0.5f;
        }

        Vector3f velocity = getEntityLookVector(entity, 1)
                .subtract(entity.getEyePosition())
                .normalize().multiply(power, reduceYVelocity ? Math.sqrt(power * 0.5) : power * 0.5f, power).toVector3f();
        //returns the root vehicle or itself if there are none
        Entity vehicle = entity.getRootVehicle();

        vehicle.setDeltaMovement(velocity.x,
                velocity.y,
                velocity.z);
        vehicle.hurtMarked = true;
        vehicle.move(MoverType.PLAYER, vehicle.getDeltaMovement());
    }

    /**
     * This is used from the server to make a sound. It is designed to be called every tick,
     * and it will play the sound every X ticks.
     * If you just want to play the sound once (or every tick), set the interval to 1.
     * @param entity The entity where we will play the sound
     * @param interval The interval at which we will play the sound
     * @param sound The sound which will be played
     */
    public static void playSoundAtEntity(Entity entity, SoundEvent sound, int interval) {
        if (entity.tickCount % interval == 0) {
            entity.level().playSound(null, entity.getOnPos(), sound, SoundSource.NEUTRAL, 1, (1.0f + (entity.level().random.nextFloat() - entity.level().random.nextFloat()) * 0.2f) * 0.7f);
        }
    }

    /**
     * Checks if the looker is looking at the observed entity.
     * Normally used with a player in order to know if the entity is within the screen or not.
     * The way this works is by making a kind of cone that shoots off from where the looker is looking.
     * @param looker The entity from which we are casting the top of the cone
     * @param observed The entity used to check if it is within that cone
     * @param maxDistance The height of the cone (put negative numbers for no limit)
     * @param angle The "radius" of the base of the cone. Usually 0.75 works best for a screen
     * @return True if the observed is within the cone made by the looker
     */
    public static boolean isLookingAt(Entity looker, Entity observed, int maxDistance, float angle) {
        Vector3f pos = getEntityLookVector(looker, 3).subtract(looker.position()).normalize().scale(3).toVector3f();
        Vector3f dir = observed.position().add(0, observed.getBbHeight() / 2f, 0)
                .subtract(looker.getEyePosition()).toVector3f();
        if (dir.length() > maxDistance && maxDistance >= 0) {
            return false;
        }
        dir = dir.normalize();
        //dot is the cosine of the angle between where we're looking and the direction towards the target.
        //1 means looking dead-on, 0 means perpendicular. This is compared directly against the angle
        //threshold (itself a cosine value) instead of being re-wrapped in Math.cos(), which previously
        //shrank the cone to a near-unusable sliver regardless of the angle passed in.
        float dot = pos.normalize().dot(dir);

        return dot >= angle;
    }

    /**
     * Checks what status effects we can get from the player's hand. Then removes those from the inventory.
     */
    public static List<MobEffectInstance> getEffectsFromHands(Player player) {
        ArrayList<MobEffectInstance> effects = new ArrayList<>();

        for (ItemStack stack : player.getHandSlots()) {
            PotionContents potionContents = stack.get(DataComponents.POTION_CONTENTS);
            if (potionContents == null) {
                continue;
            }
            for (MobEffectInstance statusEffectInstance : potionContents.getAllEffects()) {
                effects.add(statusEffectInstance);
                player.getInventory().removeItem(stack);
                player.getInventory().add(Items.GLASS_BOTTLE.getDefaultInstance());
            }
        }
        return effects;
    }

    public static boolean isBeingRainedOn(Entity entity) {
        BlockPos blockPos = entity.getOnPos();
        return entity.level().isRainingAt(blockPos) || entity.level().isRainingAt(BlockPos.containing(blockPos.getX(), entity.getBoundingBox().maxY, blockPos.getZ()));
    }

    /**
     * Checks whether the entity is currently standing in an area where it's snowing
     * (i.e. it's precipitating AND the biome's precipitation type at that spot is snow).
     */
    public static boolean isBeingSnowedOn(Entity entity) {
        BlockPos blockPos = entity.getOnPos();
        Level level = entity.level();
        BlockPos topPos = BlockPos.containing(blockPos.getX(), entity.getBoundingBox().maxY, blockPos.getZ());

        if (!level.isRainingAt(blockPos) && !level.isRainingAt(topPos)) {
            return false;
        }

        Holder<Biome> biome = level.getBiome(blockPos);
        return biome.value().getPrecipitationAt(blockPos) == Biome.Precipitation.SNOW;
    }

    /**
     * Finds entities in a given radius. This method does not take into account walls.
     * The shape of the thing is a square.
     */
    public static List<LivingEntity> getEntitiesInRadius(Vec3 origin, float radius, Level world, Entity except) {
        return Lists.transform(
                getEntitiesInRadius(origin, radius, world, except, entity -> entity instanceof LivingEntity),
                entity -> (LivingEntity) entity
        );
    }

    /**
     * Finds entities in a given radius. This method does not take into account walls.
     * The shape of the thing is a square.
     */
    public static List<Entity> getEntitiesInRadius(Vec3 origin, float radius, Level world, Entity except, Predicate<Entity> pred) {
        return world.getEntities(
                except,
                new AABB(origin.subtract(radius, radius, radius), origin.add(radius, radius, radius)),
                pred
        );
    }

    public static boolean isLookingForwards(Vector3f direction) {
        double dot = Math.acos(new Vector3f(0, 0, 1).dot(direction) / direction.length());
        return dot >= 1.5;
    }

    /**
     *
     * @return True if the block was broken
     */
    public static boolean mineBlock(BlockPos blockHit, Level world, int entityId, int tickCount, int startMiningAge, float miningSpeed) {
        float progress = calcBlockBreakingDelta(world.getBlockState(blockHit), world, blockHit, miningSpeed)
                * (tickCount - startMiningAge + 1);
        world.destroyBlockProgress(entityId, blockHit, (int) (progress * 10));

        if (progress >= 1) {
            world.destroyBlock(blockHit, true);
            return true;
        }
        return false;
    }

    public static void keepOtherEntityNearEntity(Entity curr, Entity other, float maxDistance) {
        double distanceToOther = other.position().distanceTo(curr.position());
        if (distanceToOther >= maxDistance) {
            Vec3 dirCenter = other.position().subtract(curr.position()).scale(-1).normalize();
            Vec3 velocity = other.getDeltaMovement().scale(1.05);

            Vec3 tangent = velocity.subtract(dirCenter.scale(
                    ((velocity.dot(dirCenter)) / dirCenter.dot(dirCenter))));
            other.setDeltaMovement(tangent.add(dirCenter.scale(Math.min(distanceToOther - maxDistance, 1))));
            other.move(MoverType.SELF, other.getDeltaMovement());
            other.fallDistance = 0;
            //TODO uncomment if this stops working
            other.hurtMarked = true;
        }
    }

    public static boolean hasItemInEitherHands(Player player, Item item) {
        return player.getItemInHand(InteractionHand.MAIN_HAND).is(item)
                || player.getItemInHand(InteractionHand.OFF_HAND).is(item);
    }

    public static ItemStack getFirstItemOfTypeInHands(Player player, Item type) {
        if (player.getItemInHand(InteractionHand.MAIN_HAND).is(type)) {
            return player.getItemInHand(InteractionHand.MAIN_HAND);
        }
        if (player.getItemInHand(InteractionHand.OFF_HAND).is(type)) {
            return player.getItemInHand(InteractionHand.OFF_HAND);
        }
        return ItemStack.EMPTY;
    }

    private static float calcBlockBreakingDelta(BlockState state, Level world, BlockPos pos, float miningSpeed) {
        float f = state.getDestroySpeed(world, pos);
        if (f == -1.0f) {
            return 0.0f;
        }
        return 1 / f / miningSpeed;
    }

    public static void sendCommand(MinecraftServer server, String command) {
        CommandSourceStack sourceStack = server.createCommandSourceStack();

        server.getCommands().performCommand(
                sourceStack.dispatcher().parse(command, sourceStack),
                command
        );
    }

    public static void showActionBarTitle(ServerPlayer serverPlayer, Component title){
        MinecraftServer server = serverPlayer.getServer();
        if (server == null) {
            return;
        }
        CommandSourceStack sourceStack = server.createCommandSourceStack();
        try {
            MutableComponent component = ComponentUtils.updateForEntity(sourceStack, title, serverPlayer, 0);
            serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(component));
        } catch (CommandSyntaxException e) {
            Elementals.LOGGER.error("Could not show title!", e);
        }
    }

    public static void showTitle(ServerPlayer serverPlayer, Component title) {
        showTitle(serverPlayer,title,null);
    }

    public static void showTitle(ServerPlayer serverPlayer, Component title, @Nullable Component subtitle) {
        MinecraftServer server = serverPlayer.getServer();
        if (server == null) {
            return;
        }
        CommandSourceStack sourceStack = server.createCommandSourceStack();
        try {
            MutableComponent component = ComponentUtils.updateForEntity(sourceStack, title, serverPlayer, 0);
            if (subtitle != null) {
                serverPlayer.connection.send(new ClientboundSetSubtitleTextPacket(component));
            }
            serverPlayer.connection.send(new ClientboundSetTitleTextPacket(component));
        } catch (CommandSyntaxException e) {
            Elementals.LOGGER.error("Could not show title!", e);
        }
    }

}
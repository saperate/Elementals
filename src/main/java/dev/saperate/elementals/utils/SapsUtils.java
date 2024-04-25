package dev.saperate.elementals.utils;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.Predicate;

public class SapsUtils {


    public static BlockPos checkBlockCollision(Entity entity) {
        Box box = entity.getBoundingBox().expand(0.25);
        BlockPos blockPos = BlockPos.ofFloored(box.minX + 1.0E-7, box.minY + 1.0E-7, box.minZ + 1.0E-7);
        BlockPos blockPos2 = BlockPos.ofFloored(box.maxX - 1.0E-7, box.maxY - 1.0E-7, box.maxZ - 1.0E-7);

        BlockPos.Mutable mutable = new BlockPos.Mutable();

        List<BlockPos> possibleHits = new ArrayList<>();

        for (int i = blockPos.getX(); i <= blockPos2.getX(); ++i) {
            for (int j = blockPos.getY(); j <= blockPos2.getY(); ++j) {
                for (int k = blockPos.getZ(); k <= blockPos2.getZ(); ++k) {
                    mutable.set(i, j, k);
                    BlockState blockState = entity.getWorld().getBlockState(mutable);
                    if(blockState.isAir()){
                        continue;
                    }

                    try {
                        blockState.onEntityCollision(entity.getWorld(), mutable, entity);
                        possibleHits.add(new BlockPos(mutable));
                    } catch (Throwable var12) {
                        CrashReport crashReport = CrashReport.create(var12, "Colliding entity with block");
                        CrashReportSection crashReportSection = crashReport.addElement("Block being collided with");
                        CrashReportSection.addBlockInfo(crashReportSection, entity.getWorld(), mutable, blockState);
                        throw new CrashException(crashReport);
                    }
                }
            }
        }


        //Get the closest hit from all possible hits
        BlockPos bestHit = possibleHits.isEmpty() ? null : possibleHits.get(0);
        double bestDistance = possibleHits.isEmpty() ? -1 : entity.squaredDistanceTo(bestHit.toCenterPos());
        for( BlockPos hit : possibleHits){
            double dist = entity.squaredDistanceTo(bestHit.toCenterPos());

            if(dist < bestDistance){
                bestHit = hit;
                bestDistance = dist;
            }
        }

        return bestHit;
    }

    public static BlockState checkBlockCollisionBlockState(Entity entity) {
        return entity.getWorld().getBlockState(checkBlockCollision(entity));
    }

    /**
     * Hacky way to get which blocks can be
     */
    public static void getAffectedBlocks(World world, Entity entity, double x, double y, double z, float power) {
        ObjectArrayList<BlockPos> affectedBlocks = new ObjectArrayList<>();
        int l, k;
        world.emitGameEvent(entity, GameEvent.EXPLODE, new Vec3d(x, y, z));
        HashSet<BlockPos> set = Sets.newHashSet();
        for (int j = 0; j < 16; ++j) {
            for (k = 0; k < 16; ++k) {
                block2:
                for (l = 0; l < 16; ++l) {
                    if (j != 0 && j != 15 && k != 0 && k != 15 && l != 0 && l != 15) continue;
                    for (float h = power * (0.7f + world.random.nextFloat() * 0.6f); h > 0.0f; h -= 0.22500001f) {
                        BlockPos blockPos = BlockPos.ofFloored(x, y, z);
                        if (!world.isInBuildLimit(blockPos)) continue block2;
                        set.add(blockPos);
                    }
                }
            }
        }

        affectedBlocks.addAll(set);
    }


    public static int extractBits(int num, int start, int length) {
        int mask = (1 << length) - 1;
        mask <<= start;
        int result = num & mask;
        result >>= start;
        return result;
    }

    //Doesn't do anything anymore
    public void parseUpgradeInt(int obj) {

        int i = 0;
        while (true) {
            int len = extractBits(obj, i, 3);
            if (len == 0) {
                break;
            }
            i += 3;
            byte r = (byte) extractBits(obj, i, len);


            i += len;
        }
    }

    public static float calculatePitch(Vec3d direction) {
        double horizontalDistance = Math.sqrt(direction.x * direction.x + direction.z * direction.z);
        double pitch = Math.atan2(direction.y, horizontalDistance);
        return (float) Math.toDegrees(pitch);
    }

    public static float calculateYaw(Vec3d direction) {
        double yaw = Math.atan2(-direction.x, direction.z);
        return (float) Math.toDegrees(yaw);
    }

    public static void summonParticles(Entity entity, Random rnd, ParticleEffect type, float velocity, int density) {
        summonParticles(entity,rnd,type,velocity,density,1);
    }


    public static void summonParticles(Entity entity, Random rnd, ParticleEffect type, float velocity, int density, float rndYForce) {
        for (int i = 0; i < density; i++) {
            entity.getWorld().addParticle(type,
                    entity.getX() - 0.5f + rnd.nextDouble(),
                    entity.getY() + rnd.nextDouble() * rndYForce,
                    entity.getZ() - 0.5f + rnd.nextDouble(),
                    rnd.nextBetween(-1, 1) * velocity, rnd.nextBetween(-1, 1) * velocity, rnd.nextBetween(-1, 1) * velocity);
        }
    }


    public static void serverSummonParticles(ServerWorld world, ParticleEffect type, Entity entity, Random rnd,
                                             double vX, double vY, double vZ, double speed, int count,
                                             float offsetX, float offsetY, float offsetZ, float vAmplitude) {
        for (int i = 0; i < count; i++) {
            world.spawnParticles(type,
                    entity.getX() + rnd.nextDouble() - 0.5f + offsetX,
                    entity.getY() + rnd.nextDouble() + 0.5f + offsetY,
                    entity.getZ() + rnd.nextDouble() - 0.5f + offsetZ,
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

    public static Vec3d getEntityLookVector(Entity e, float distance) {
        double rYaw = Math.toRadians(e.getYaw() + 90);
        double rPitch = Math.toRadians(-e.getPitch());

        float x = (float) (Math.cos(rPitch) * Math.cos(rYaw));
        float y = (float) Math.sin(rPitch);
        float z = (float) (Math.cos(rPitch) * Math.sin(rYaw));

        return new Vec3d(x, y, z).multiply(distance).add(e.getEyePos());
    }

    public static HitResult raycastEntity(Entity origin, double maxDistance, Predicate<Entity> predicate) {
            Vec3d cameraPos = origin.getCameraPosVec(1.0f);
            Vec3d rot = origin.getRotationVec(1.0f);
            Vec3d rayCastContext = cameraPos.add(rot.x * maxDistance, rot.y * maxDistance, rot.z * maxDistance);
            Box box = origin.getBoundingBox().stretch(rot.multiply(maxDistance)).expand(1d, 1d, 1d);
            return ProjectileUtil.raycast(origin, origin.getEyePos(), rayCastContext, box, predicate.and(entity -> entity instanceof LivingEntity && !entity.isSpectator() && entity.canHit()), maxDistance * maxDistance);
    }

    //TODO maybe use a discard block list so that if we hit that and we got both and entity hit and a block hit we only keep the entity hit
    public static HitResult raycastFull(Entity origin, double maxDistance, boolean includeFluids) {
       return raycastFull(origin,maxDistance,includeFluids, Entity::isAlive);
    }

    public static HitResult raycastFull(Entity origin, double maxDistance, boolean includeFluids, Predicate<Entity> entityPredicate) {
        EntityHitResult eHit = (EntityHitResult) raycastEntity(origin, maxDistance, entityPredicate);
        BlockHitResult bHit = (BlockHitResult) origin.raycast(maxDistance, 1.0f, includeFluids);

        if(eHit == null){
            return bHit;
        } else if (bHit == null) {
            return eHit;
        }

        if(eHit.squaredDistanceTo(origin) < bHit.squaredDistanceTo(origin)){
            return eHit;
        }else {
            return bHit;
        }
    }

    public static Entity entityFromHitResult(HitResult result){
        if(result.getType().equals(HitResult.Type.ENTITY)){
            return ((EntityHitResult) result).getEntity();
        }else {
            return null;
        }
    }


}

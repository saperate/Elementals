package dev.saperate.elementals.utils;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

public class SapsUtils {


    public static BlockState checkBlockCollision(Entity entity) {
        Box box = entity.getBoundingBox().expand(0.25);
        BlockPos blockPos = BlockPos.ofFloored(box.minX + 1.0E-7, box.minY + 1.0E-7, box.minZ + 1.0E-7);
        BlockPos blockPos2 = BlockPos.ofFloored(box.maxX - 1.0E-7, box.maxY - 1.0E-7, box.maxZ - 1.0E-7);

        BlockPos.Mutable mutable = new BlockPos.Mutable();

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
                        return blockState;
                    } catch (Throwable var12) {
                        CrashReport crashReport = CrashReport.create(var12, "Colliding entity with block");
                        CrashReportSection crashReportSection = crashReport.addElement("Block being collided with");
                        CrashReportSection.addBlockInfo(crashReportSection, entity.getWorld(), mutable, blockState);
                        throw new CrashException(crashReport);
                    }
                }
            }
        }

        return null;
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
        for (int i = 0; i < density; i++) {
            entity.getWorld().addParticle(type,
                    entity.getX() + rnd.nextDouble(),
                    entity.getY() + rnd.nextDouble(),
                    entity.getZ() + rnd.nextDouble(),
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

    public static Vector3f getEntityLookVector(Entity e, float distance) {
        double rYaw = Math.toRadians(e.getYaw() + 90);
        double rPitch = Math.toRadians(-e.getPitch());

        float x = (float) (Math.cos(rPitch) * Math.cos(rYaw));
        float y = (float) Math.sin(rPitch);
        float z = (float) (Math.cos(rPitch) * Math.sin(rYaw));

        return new Vector3f(x, y, z).mul(distance).add(e.getEyePos().toVector3f());
    }
}

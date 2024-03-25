package dev.saperate.elementals.utils;

import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class SapsUtils {

    public static void summonParticles(World world, Entity entity, Random rnd, ParticleEffect type, float velocity, int density) {
        for (int i = 0; i < density; i++) {
            world.addParticle(type,
                    entity.getX() + rnd.nextDouble(),
                    entity.getY() + rnd.nextDouble(),
                    entity.getZ() + rnd.nextDouble(),
                    rnd.nextGaussian(), velocity, rnd.nextFloat());
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

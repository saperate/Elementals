package dev.saperate.elementals.utils;

import net.minecraft.entity.Entity;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class SapsUtils {


    public static Vector3f getEntityLookVector(Entity e, float distance) {
        double rYaw = Math.toRadians(e.getYaw() + 90);
        double rPitch = Math.toRadians(-e.getPitch());

        float x = (float) (Math.cos(rPitch) * Math.cos(rYaw));
        float y = (float) Math.sin(rPitch);
        float z = (float) (Math.cos(rPitch) * Math.sin(rYaw));

        return new Vector3f(x, y, z).mul(distance).add(e.getEyePos().toVector3f());
    }
}

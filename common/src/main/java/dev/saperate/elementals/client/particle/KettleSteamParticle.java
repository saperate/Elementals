package dev.saperate.elementals.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class KettleSteamParticle extends RisingParticle {
    private final RandomSource random;
    KettleSteamParticle(ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
        super(clientWorld, d, e, f, g, h + 0.25f, i);
        random = RandomSource.create();
        scale( (1f + 0.5f * random.nextFloat()) * 0.5f);
        yd = 0.1 + 0.02 * random.nextFloat();
        
    }

    @Override
    public void tick() {
        super.tick();
        yd = Math.max(0.001, yd - 0.005f);
        xd = Math.signum(xd) * Math.max(0.001f, xd - 0.00001f);
        zd = Math.signum(zd) * Math.max(0.001f, zd - 0.00001f);
        quadSize += 0.005f;
        
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public void move(double dx, double dy, double dz) {
        this.setBoundingBox(this.getBoundingBox().move(dx, dy, dz));
        this.setLocationFromBoundingbox();
    }

    public float getSize(float tickDelta) {
        float f = ((float) this.age + tickDelta) / (float) this.lifetime;
        return this.quadSize * (1.0F - f * f * 0.5F);
    }

    public int getBrightness(float tint) {
        float f = ((float) this.age + tint) / (float) this.lifetime;
        f = Mth.clamp(f, 0.0F, 1.0F);
        int i = super.getLightColor(tint);
        int j = i & 255;
        int k = i >> 16 & 255;
        j += (int) (f * 15.0F * 16.0F);
        if (j > 240) {
            j = 240;
        }

        return j | k << 16;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteProvider;

        public Provider(SpriteSet spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType defaultParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
            KettleSteamParticle particle = new KettleSteamParticle(clientWorld, d, e, f, g, h, i);
            particle.pickSprite(this.spriteProvider);
            return particle;
        }
    }
}

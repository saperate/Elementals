package dev.saperate.elementals.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class MetalShardParticle extends RisingParticle {
    private final RandomSource random;
    MetalShardParticle(ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
        super(clientWorld, d, e, f, g, h + 0.25f, i);
        random = RandomSource.create();
        scale(1f + 0.5f * random.nextFloat());
        
    }

    @Override
    public void tick() {
        super.tick();
        yd -= 0.05f;
        quadSize -= 0.01f;
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

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteProvider;

        public Factory(SpriteSet spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType defaultParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
            MetalShardParticle particle = new MetalShardParticle(clientWorld, d, e, f, g, h, i);
            particle.pickSprite(this.spriteProvider);
            return particle;
        }
    }
}

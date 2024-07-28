package dev.saperate.elementals.misc;

import com.google.common.collect.Sets;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.EntityExplosionBehavior;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class FireExplosion extends Explosion {
    private static final ExplosionBehavior DEFAULT_BEHAVIOR = new ExplosionBehavior();
    private World world;
    private double x, y, z;
    private float power, maxDamage, velocityMultiplier = 1;
    private ExplosionBehavior behavior;
    private DamageSource damageSource;
    private final Entity owner;


    public FireExplosion(World world, @Nullable Entity entity, double x, double y, double z, float power, boolean createFire, DestructionType destructionType, float maxDamage, Entity owner) {
        super(world, entity, x, y, z, power, createFire, destructionType);
        damageSource = world.getDamageSources().explosion(this);
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.power = power;
        this.behavior = chooseBehavior(entity);
        this.maxDamage = maxDamage;
        this.owner = owner;
    }

    public FireExplosion(World world, @Nullable Entity entity, double x, double y, double z, float power, boolean createFire, DestructionType destructionType, float maxDamage, float velocityMultiplier, Entity owner) {
        this(world, entity, x, y, z, power, createFire, destructionType, maxDamage, owner);
        this.velocityMultiplier = velocityMultiplier;
    }

    @Override
    public void collectBlocksAndDamageEntities() {
        int l;
        int k;
        this.world.emitGameEvent(this.getEntity(), GameEvent.EXPLODE, new Vec3d(this.x, this.y, this.z));
        HashSet<BlockPos> set = Sets.newHashSet();
        int i = 16;
        for (int j = 0; j < 16; ++j) {
            for (k = 0; k < 16; ++k) {
                block2:
                for (l = 0; l < 16; ++l) {
                    if (j != 0 && j != 15 && k != 0 && k != 15 && l != 0 && l != 15) continue;
                    double d = (float) j / 15.0f * 2.0f - 1.0f;
                    double e = (float) k / 15.0f * 2.0f - 1.0f;
                    double f = (float) l / 15.0f * 2.0f - 1.0f;
                    double g = Math.sqrt(d * d + e * e + f * f);
                    d /= g;
                    e /= g;
                    f /= g;
                    double m = this.x;
                    double n = this.y;
                    double o = this.z;
                    float p = 0.3f;
                    for (float h = this.power * (0.7f + this.world.random.nextFloat() * 0.6f); h > 0.0f; h -= 0.22500001f) {
                        BlockPos blockPos = BlockPos.ofFloored(m, n, o);
                        BlockState blockState = this.world.getBlockState(blockPos);
                        FluidState fluidState = this.world.getFluidState(blockPos);
                        if (!this.world.isInBuildLimit(blockPos)) continue block2;
                        Optional<Float> optional = this.behavior.getBlastResistance(this, this.world, blockPos, blockState, fluidState);
                        if (optional.isPresent()) {
                            h -= (optional.get().floatValue() + 0.3f) * 0.3f;
                        }
                        if (h > 0.0f && this.behavior.canDestroyBlock(this, this.world, blockPos, blockState, h)) {
                            set.add(blockPos);
                        }
                        m += d * (double) 0.3f;
                        n += e * (double) 0.3f;
                        o += f * (double) 0.3f;
                    }
                }
            }
        }
        this.getAffectedBlocks().addAll((Collection<BlockPos>) set);
        float q = this.power * 2.0f;
        k = MathHelper.floor(this.x - (double) q - 1.0);
        l = MathHelper.floor(this.x + (double) q + 1.0);
        int r = MathHelper.floor(this.y - (double) q - 1.0);
        int s = MathHelper.floor(this.y + (double) q + 1.0);
        int t = MathHelper.floor(this.z - (double) q - 1.0);
        int u = MathHelper.floor(this.z + (double) q + 1.0);
        List<Entity> list = this.world.getOtherEntities(null, new Box(k, r, t, l, s, u));
        Vec3d vec3d = new Vec3d(this.x, this.y, this.z);
        for (Entity entity : list) {
            PlayerEntity playerEntity;
            double ab;
            double y;
            double x;
            double w;
            double z;
            double v;
            if (entity.isImmuneToExplosion() || !((v = Math.sqrt(entity.squaredDistanceTo(vec3d)) / (double) q) <= 1.0) || (z = Math.sqrt((w = entity.getX() - this.x) * w + (x = (entity instanceof TntEntity ? entity.getY() : entity.getEyeY()) - this.y) * x + (y = entity.getZ() - this.z) * y)) == 0.0)
                continue;
            w /= z;
            x /= z;
            y /= z;
            ab = (double) getExposure(vec3d, entity);
            double ac = (1.0 - w) * ab;
            if (entity != owner) {
                entity.damage(this.getDamageSource(), Math.min(maxDamage, (float) ((int) ((ac * ac + ac) / 2.0 * 7.0 * (double) q + 1.0))));
            }
            double aa = (1.0 - v) * (double) Explosion.getExposure(vec3d, entity);
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                ab = ProtectionEnchantment.transformExplosionKnockback(livingEntity, aa);
            } else {
                ab = aa;
            }
            Vec3d vec3d2 = new Vec3d(w *= ab, x *= ab, y *= ab).multiply(velocityMultiplier);
            entity.setVelocity(entity.getVelocity().add(vec3d2));
            if (!(entity instanceof PlayerEntity) || (playerEntity = (PlayerEntity) entity).isSpectator() || playerEntity.isCreative() && playerEntity.getAbilities().flying)
                continue;
            playerEntity.velocityModified = true;
            getAffectedPlayers().put(playerEntity, vec3d2);
        }

    }

    private ExplosionBehavior chooseBehavior(@Nullable Entity entity) {
        return entity == null ? DEFAULT_BEHAVIOR : new EntityExplosionBehavior(entity);
    }

}

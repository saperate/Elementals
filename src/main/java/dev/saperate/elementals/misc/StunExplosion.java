package dev.saperate.elementals.misc;

import com.google.common.collect.Sets;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
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

import static dev.saperate.elementals.effects.StunnedStatusEffect.STUNNED_EFFECT;

public class StunExplosion extends Explosion {
    private static final ExplosionBehavior DEFAULT_BEHAVIOR = new ExplosionBehavior();
    private World world;
    private double x, y, z;
    private float power, maxDamage, velocityMultiplier = 1;
    private ExplosionBehavior behavior;
    private DamageSource damageSource;
    private final Entity owner;


    public StunExplosion(World world, @Nullable Entity entity, double x, double y, double z, float power, boolean createFire, DestructionType destructionType, float maxDamage, Entity owner) {
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

    public StunExplosion(World world, @Nullable Entity entity, double x, double y, double z, float power, boolean createFire, DestructionType destructionType, float maxDamage, float velocityMultiplier, Entity owner) {
        this(world, entity, x, y, z, power, createFire, destructionType, maxDamage, owner);
        this.velocityMultiplier = velocityMultiplier;
    }

    @Override
    public void collectBlocksAndDamageEntities() {
        int l;
        int k;
        this.world.emitGameEvent(this.getEntity(), GameEvent.EXPLODE, new Vec3d(this.x, this.y, this.z));
        float q = this.power * 2.0f;
        k = MathHelper.floor(this.x - (double) q - 1.0);
        l = MathHelper.floor(this.x + (double) q + 1.0);
        int r = MathHelper.floor(this.y - (double) q - 1.0);
        int s = MathHelper.floor(this.y + (double) q + 1.0);
        int t = MathHelper.floor(this.z - (double) q - 1.0);
        int u = MathHelper.floor(this.z + (double) q + 1.0);
        List<Entity> list = this.world.getOtherEntities(owner, new Box(k, r, t, l, s, u));
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
            if (entity instanceof LivingEntity living) {
                ab = ProtectionEnchantment.transformExplosionKnockback(living, aa);
                living.addStatusEffect(new StatusEffectInstance(STUNNED_EFFECT,200,0,false,false,true));
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

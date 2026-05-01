package dev.saperate.elementals.misc;

import com.google.common.collect.Sets;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.attribute.EntityAttributes;
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

import java.util.*;

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
        this.world.emitGameEvent(getEntity(), GameEvent.EXPLODE, new Vec3d(this.x, this.y, this.z));
        Set<BlockPos> set = Sets.newHashSet();

        int k;
        int l;
        for(int j = 0; j < 16; ++j) {
            for(k = 0; k < 16; ++k) {
                for(l = 0; l < 16; ++l) {
                    if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                        double d = (double)((float)j / 15.0F * 2.0F - 1.0F);
                        double e = (double)((float)k / 15.0F * 2.0F - 1.0F);
                        double f = (double)((float)l / 15.0F * 2.0F - 1.0F);
                        double g = Math.sqrt(d * d + e * e + f * f);
                        d /= g;
                        e /= g;
                        f /= g;
                        float h = this.power * (0.7F + this.world.random.nextFloat() * 0.6F);
                        double m = this.x;
                        double n = this.y;
                        double o = this.z;

                        for(float p = 0.3F; h > 0.0F; h -= 0.22500001F) {
                            BlockPos blockPos = BlockPos.ofFloored(m, n, o);
                            BlockState blockState = this.world.getBlockState(blockPos);
                            FluidState fluidState = this.world.getFluidState(blockPos);
                            if (!this.world.isInBuildLimit(blockPos)) {
                                break;
                            }

                            Optional<Float> optional = this.behavior.getBlastResistance(this, this.world, blockPos, blockState, fluidState);
                            if (optional.isPresent()) {
                                h -= ((Float)optional.get() + 0.3F) * 0.3F;
                            }

                            if (h > 0.0F && this.behavior.canDestroyBlock(this, this.world, blockPos, blockState, h)) {
                                set.add(blockPos);
                            }

                            m += d * 0.30000001192092896;
                            n += e * 0.30000001192092896;
                            o += f * 0.30000001192092896;
                        }
                    }
                }
            }
        }

        getAffectedBlocks().addAll(set);
        float q = this.power * 2.0F;
        k = MathHelper.floor(this.x - (double)q - 1.0);
        l = MathHelper.floor(this.x + (double)q + 1.0);
        int r = MathHelper.floor(this.y - (double)q - 1.0);
        int s = MathHelper.floor(this.y + (double)q + 1.0);
        int t = MathHelper.floor(this.z - (double)q - 1.0);
        int u = MathHelper.floor(this.z + (double)q + 1.0);
        List<Entity> list = this.world.getOtherEntities(getEntity(), new Box((double)k, (double)r, (double)t, (double)l, (double)s, (double)u));
        Vec3d vec3d = new Vec3d(this.x, this.y, this.z);
        Iterator var34 = list.iterator();

        while(true) {
            Entity entity;
            double w;
            double x;
            double y;
            double v;
            double z;
            do {
                do {
                    do {
                        if (!var34.hasNext()) {
                            return;
                        }

                        entity = (Entity)var34.next();
                    } while(entity.isImmuneToExplosion(this));

                    v = Math.sqrt(entity.squaredDistanceTo(vec3d)) / (double)q;
                } while(!(v <= 1.0));

                w = entity.getX() - this.x;
                x = (entity instanceof TntEntity ? entity.getY() : entity.getEyeY()) - this.y;
                y = entity.getZ() - this.z;
                z = Math.sqrt(w * w + x * x + y * y);
            } while(z == 0.0);

            w /= z;
            x /= z;
            y /= z;
            if (this.behavior.shouldDamage(this, entity)) {
                entity.damage(this.damageSource, Math.min(maxDamage, this.behavior.calculateDamage(this, entity)));
            }

            double aa = (1.0 - v) * (double)getExposure(vec3d, entity) * (double)this.behavior.getKnockbackModifier(entity);
            double ab;
            if (entity instanceof LivingEntity livingEntity) {
                ab = aa * (1.0 - livingEntity.getAttributeValue(EntityAttributes.GENERIC_EXPLOSION_KNOCKBACK_RESISTANCE));
            } else {
                ab = aa;
            }

            w *= ab;
            x *= ab;
            y *= ab;
            Vec3d vec3d2 = new Vec3d(w, x, y);
            entity.setVelocity(entity.getVelocity().add(vec3d2));
            if (entity instanceof PlayerEntity playerEntity) {
                if (!playerEntity.isSpectator() && (!playerEntity.isCreative() || !playerEntity.getAbilities().flying)) {
                    getAffectedPlayers().put(playerEntity, vec3d2);
                }
            }

            //entity.onExplodedBy(getEntity()); fixme
            entity.setVelocity(entity.getVelocity().add(vec3d2));
            if (!(entity instanceof PlayerEntity playerEntity) || (playerEntity = (PlayerEntity) entity).isSpectator() || playerEntity.isCreative() && playerEntity.getAbilities().flying){
                continue;
            }
            playerEntity.velocityModified = true;
        }
    }

    private ExplosionBehavior chooseBehavior(@Nullable Entity entity) {
        return entity == null ? DEFAULT_BEHAVIOR : new EntityExplosionBehavior(entity);
    }

}

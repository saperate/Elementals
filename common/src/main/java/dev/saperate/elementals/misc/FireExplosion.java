package dev.saperate.elementals.misc;

import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FireExplosion extends Explosion {
    private static final ExplosionDamageCalculator DEFAULT_BEHAVIOR = new ExplosionDamageCalculator();
    private Level world;
    private double x, y, z;
    private float power, maxDamage, velocityMultiplier = 1;
    private ExplosionDamageCalculator behavior;
    private DamageSource damageSource;
    private final Entity owner;


    public FireExplosion(Level world, @Nullable Entity entity, double x, double y, double z, float power, boolean createFire, BlockInteraction destructionType, float maxDamage, Entity owner) {
        super(world, entity, x, y, z, power, createFire, destructionType);
        damageSource = world.damageSources().explosion(this);
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.power = power;
        this.behavior = chooseBehavior(entity);
        this.maxDamage = maxDamage;
        this.owner = owner;
    }

    public FireExplosion(Level world, @Nullable Entity entity, double x, double y, double z, float power, boolean createFire, BlockInteraction destructionType, float maxDamage, float velocityMultiplier, Entity owner) {
        this(world, entity, x, y, z, power, createFire, destructionType, maxDamage, owner);
        this.velocityMultiplier = velocityMultiplier;
    }

    @Override
    public void explode() {
        this.world.gameEvent(getDirectSourceEntity(), GameEvent.EXPLODE, new Vec3(this.x, this.y, this.z));
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
                            BlockPos blockPos = BlockPos.containing(m, n, o);
                            BlockState blockState = this.world.getBlockState(blockPos);
                            FluidState fluidState = this.world.getFluidState(blockPos);
                            if (this.world.isOutsideBuildHeight(blockPos)) {
                                break;
                            }

                            Optional<Float> optional = this.behavior.getBlockExplosionResistance(this, this.world, blockPos, blockState, fluidState);
                            if (optional.isPresent()) {
                                h -= ((Float)optional.get() + 0.3F) * 0.3F;
                            }

                            if (h > 0.0F && this.behavior.shouldBlockExplode(this, this.world, blockPos, blockState, h)) {
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

        //FIXME verify this works
        //getAffectedBlocks.addAll(set);
        float q = this.power * 2.0F;
        k = Mth.floor(this.x - (double)q - 1.0);
        l = Mth.floor(this.x + (double)q + 1.0);
        int r = Mth.floor(this.y - (double)q - 1.0);
        int s = Mth.floor(this.y + (double)q + 1.0);
        int t = Mth.floor(this.z - (double)q - 1.0);
        int u = Mth.floor(this.z + (double)q + 1.0);
        List<Entity> list = this.world.getEntities(getDirectSourceEntity(), new AABB((double)k, (double)r, (double)t, (double)l, (double)s, (double)u));
        Vec3 vec3d = new Vec3(this.x, this.y, this.z);
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
                    } while(entity.ignoreExplosion(this));

                    v = Math.sqrt(entity.distanceToSqr(vec3d)) / (double)q;
                } while(!(v <= 1.0));

                w = entity.getX() - this.x;
                x = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - this.y;
                y = entity.getZ() - this.z;
                z = Math.sqrt(w * w + x * x + y * y);
            } while(z == 0.0);

            w /= z;
            x /= z;
            y /= z;
            if (this.behavior.shouldDamageEntity(this, entity)) {
                entity.hurt(this.damageSource, Math.min(maxDamage, this.behavior.getEntityDamageAmount(this, entity)));
            }

            double aa = (1.0 - v) * (double)getSeenPercent(vec3d, entity) * (double)this.behavior.getKnockbackMultiplier(entity);
            double ab;
            if (entity instanceof LivingEntity livingEntity) {
                ab = aa * (1.0 - livingEntity.getAttributeValue(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE));
            } else {
                ab = aa;
            }

            w *= ab;
            x *= ab;
            y *= ab;
            Vec3 vec3d2 = new Vec3(w, x, y);
            entity.setDeltaMovement(entity.getDeltaMovement().add(vec3d2));
            if (entity instanceof Player playerEntity) {
                if (!playerEntity.isSpectator() && (!playerEntity.isCreative() || !playerEntity.getAbilities().flying)) {
                    getHitPlayers().put(playerEntity, vec3d2);
                }
            }

            //entity.onExplodedBy(getEntity()); fixme
            entity.setDeltaMovement(entity.getDeltaMovement().add(vec3d2));
            if (!(entity instanceof Player playerEntity) || (playerEntity = (Player) entity).isSpectator() || playerEntity.isCreative() && playerEntity.getAbilities().flying){
                continue;
            }
            playerEntity.hasImpulse = true;
        }
    }

    private ExplosionDamageCalculator chooseBehavior(@Nullable Entity entity) {
        return entity == null ? DEFAULT_BEHAVIOR : new ExplosionDamageCalculator();
    }

}

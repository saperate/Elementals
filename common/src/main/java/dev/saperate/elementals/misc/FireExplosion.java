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
    private float maxDamage, velocityMultiplier = 1;
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
        this.world.gameEvent(this.getDirectSourceEntity(), GameEvent.EXPLODE, new Vec3(this.x, this.y, this.z));
        Set<BlockPos> set = Sets.newHashSet();
        int i = 16;

        for(int j = 0; j < 16; ++j) {
            for(int k = 0; k < 16; ++k) {
                for(int l = 0; l < 16; ++l) {
                    if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                        double d0 = (double)((float)j / 15.0F * 2.0F - 1.0F);
                        double d1 = (double)((float)k / 15.0F * 2.0F - 1.0F);
                        double d2 = (double)((float)l / 15.0F * 2.0F - 1.0F);
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 /= d3;
                        d1 /= d3;
                        d2 /= d3;
                        float f = this.radius() * (0.7F + this.world.random.nextFloat() * 0.6F);
                        double d4 = this.x;
                        double d6 = this.y;
                        double d8 = this.z;

                        for(float f1 = 0.3F; f > 0.0F; f -= 0.22500001F) {
                            BlockPos blockpos = BlockPos.containing(d4, d6, d8);
                            BlockState blockstate = this.world.getBlockState(blockpos);
                            FluidState fluidstate = this.world.getFluidState(blockpos);
                            if (!this.world.isInWorldBounds(blockpos)) {
                                break;
                            }

                            Optional<Float> optional = this.behavior.getBlockExplosionResistance(this, this.world, blockpos, blockstate, fluidstate);
                            if (optional.isPresent()) {
                                f -= ((Float)optional.get() + 0.3F) * 0.3F;
                            }

                            if (f > 0.0F && this.behavior.shouldBlockExplode(this, this.world, blockpos, blockstate, f)) {
                                set.add(blockpos);
                            }

                            d4 += d0 * (double)0.3F;
                            d6 += d1 * (double)0.3F;
                            d8 += d2 * (double)0.3F;
                        }
                    }
                }
            }
        }

        this.getToBlow().addAll(set);
        float f2 = this.radius() * 2.0F;
        int k1 = Mth.floor(this.x - (double)f2 - (double)1.0F);
        int l1 = Mth.floor(this.x + (double)f2 + (double)1.0F);
        int i2 = Mth.floor(this.y - (double)f2 - (double)1.0F);
        int i1 = Mth.floor(this.y + (double)f2 + (double)1.0F);
        int j2 = Mth.floor(this.z - (double)f2 - (double)1.0F);
        int j1 = Mth.floor(this.z + (double)f2 + (double)1.0F);
        List<Entity> list = this.world.getEntities(this.getDirectSourceEntity(), new AABB((double)k1, (double)i2, (double)j2, (double)l1, (double)i1, (double)j1));
        Vec3 vec3 = new Vec3(this.x, this.y, this.z);

        for(Entity entity : list) {
            if (!entity.ignoreExplosion(this)) {
                double d11 = Math.sqrt(entity.distanceToSqr(vec3)) / (double)f2;
                if (d11 <= (double)1.0F) {
                    double d5 = entity.getX() - this.x;
                    double d7 = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - this.y;
                    double d9 = entity.getZ() - this.z;
                    double d12 = Math.sqrt(d5 * d5 + d7 * d7 + d9 * d9);
                    if (d12 != (double)0.0F) {
                        d5 /= d12;
                        d7 /= d12;
                        d9 /= d12;
                        if (this.behavior.shouldDamageEntity(this, entity) && entity != owner) {
                            entity.hurt(this.damageSource, 
                                    Math.min(this.behavior.getEntityDamageAmount(this, entity), maxDamage));
                        }

                        double d13 = ((double)1.0F - d11) * (double)getSeenPercent(vec3, entity) * (double)this.behavior.getKnockbackMultiplier(entity);
                        double d10;
                        if (entity instanceof LivingEntity) {
                            LivingEntity livingentity = (LivingEntity)entity;
                            d10 = d13 * ((double)1.0F - livingentity.getAttributeValue(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE));
                        } else {
                            d10 = d13;
                        }

                        d5 *= d10;
                        d7 *= d10;
                        d9 *= d10;
                        Vec3 vec31 = new Vec3(d5, d7, d9);
                        entity.setDeltaMovement(entity.getDeltaMovement().add(vec31).scale(velocityMultiplier));
                        if (entity instanceof Player) {
                            Player player = (Player)entity;
                            if (!player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying)) {
                                player.hurtMarked = true;
                            }
                        }

                        entity.onExplosionHit(this.getDirectSourceEntity());
                    }
                }
            }
        }
    }

    private ExplosionDamageCalculator chooseBehavior(@Nullable Entity entity) {
        return entity == null ? DEFAULT_BEHAVIOR : new ExplosionDamageCalculator();
    }

}

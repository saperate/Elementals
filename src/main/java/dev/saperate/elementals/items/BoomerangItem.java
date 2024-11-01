package dev.saperate.elementals.items;

import dev.saperate.elementals.entities.common.BoomerangEntity;
import dev.saperate.elementals.entities.common.DirtBottleEntity;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;


public class BoomerangItem extends Item implements DispenserBehavior {

    public BoomerangItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack handStack = user.getStackInHand(hand);

        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_ENDER_PEARL_THROW,
                SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));

        if (!world.isClient) {
            BoomerangEntity entity = new BoomerangEntity(world, user, user.getEyePos());
            entity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, .75f, 0f);
            world.spawnEntity(entity);
            user.getInventory().removeOne(handStack);
        }
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        return TypedActionResult.success(handStack, world.isClient());
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack dispense(BlockPointer pointer, ItemStack stack) {
        stack.decrement(1);
        World world = pointer.world();
        world.playSound(null, pointer.pos(), SoundEvents.ENTITY_ENDER_PEARL_THROW,
                SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));

        if (!world.isClient) {
            Direction direction = pointer.state().get(DispenserBlock.FACING);
            BoomerangEntity boomerangEntity = getBoomerangEntity(pointer, world, direction);
            world.spawnEntity(boomerangEntity);
        }
        return stack;
    }

    @NotNull
    private static BoomerangEntity getBoomerangEntity(BlockPointer pointer, World world, Direction direction) {
        BoomerangEntity boomerangEntity = new BoomerangEntity(world,
                new Vec3d(
                        pointer.pos().getX() + direction.getOffsetX() + 0.5,
                        pointer.pos().getY() + direction.getOffsetY() + 0.5,
                        pointer.pos().getZ() + direction.getOffsetZ() + 0.5
                )
        );

        boomerangEntity.setVelocity(
                direction.getOffsetX(),
                direction.getOffsetY(),
                direction.getOffsetZ(),
                1f, 0f
        );
        return boomerangEntity;
    }
}
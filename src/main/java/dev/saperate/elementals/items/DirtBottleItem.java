package dev.saperate.elementals.items;

import dev.saperate.elementals.entities.common.BoomerangEntity;
import dev.saperate.elementals.entities.common.DirtBottleEntity;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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


public class DirtBottleItem extends Item implements DispenserBehavior {

    public DirtBottleItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack handStack = user.getStackInHand(hand);

        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_ENDER_PEARL_THROW,
                SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));

        if (!world.isClient) {
            DirtBottleEntity entity = new DirtBottleEntity(world, user);
            entity.setItem(handStack);
            entity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, .75f, 0f);
            world.spawnEntity(entity);
        }
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!user.getAbilities().creativeMode) {
            handStack.decrement(1);
        }

        return TypedActionResult.success(handStack, world.isClient());
    }

    @Override
    public ItemStack dispense(BlockPointer pointer, ItemStack stack) {
        stack.decrement(1);
        World world = pointer.getWorld();
        world.playSound(null, pointer.getPos(), SoundEvents.ENTITY_ENDER_PEARL_THROW,
                SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));

        if (!world.isClient) {
            Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
            DirtBottleEntity dirtBottle = getDirtBottleEntity(pointer, world, direction);
            world.spawnEntity(dirtBottle);
        }
        return stack;
    }

    @NotNull
    private static DirtBottleEntity getDirtBottleEntity(BlockPointer pointer, World world, Direction direction) {
        DirtBottleEntity dirtBottleEntity = new DirtBottleEntity(world,
                new Vec3d(
                        pointer.getPos().getX() + direction.getOffsetX() + 0.5,
                        pointer.getPos().getY() + direction.getOffsetY() + 0.5,
                        pointer.getPos().getZ() + direction.getOffsetZ() + 0.5
                )
        );

        dirtBottleEntity.setVelocity(
                direction.getOffsetX(),
                direction.getOffsetY(),
                direction.getOffsetZ(),
                1f, 0f
        );
        return dirtBottleEntity;
    }
}
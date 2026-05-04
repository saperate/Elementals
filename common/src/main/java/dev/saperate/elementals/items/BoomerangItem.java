package dev.saperate.elementals.items;

import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;


public class BoomerangItem extends Item implements DispenseItemBehavior {

    public BoomerangItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack handStack = user.getItemInHand(hand);

        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENDER_PEARL_THROW,
                SoundSource.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));

        if (!world.isClientSide) {
            BoomerangEntity entity = new BoomerangEntity(world, user.getEyePosition(), handStack);
            entity.setOwner(user);
            entity.setVelocity(user, user.getXRot(), user.getYRot(), 0.0f, .75f, 0f);
            world.spawnEntity(entity);
            user.getInventory().removeItem(handStack);
        }
        user.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.success(handStack);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }
    
    @Override
    public ItemStack dispense(BlockSource blockSource, ItemStack itemStack) {
        Level world = blockSource.level();
        world.playSound(null, blockSource.pos(), SoundEvents.ENDER_PEARL_THROW,
                SoundSource.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));

        if (!world.isClientSide) {
            Direction direction = blockSource.state().getValue(DispenserBlock.FACING);
            BoomerangEntity boomerangEntity = getBoomerangEntity(blockSource, world, direction, itemStack);
            world.spawnEntity(boomerangEntity);
        }
        itemStack.shrink(1);
        return itemStack;
    }
    @NotNull
    private static BoomerangEntity getBoomerangEntity(BlockSource pointer, Level world, Direction direction, ItemStack stack) {
        BoomerangEntity boomerangEntity = new BoomerangEntity(world,
                new Vec3(
                        pointer.pos().getX() + direction.getStepX() + 0.5,
                        pointer.pos().getY() + direction.getStepY() + 0.5,
                        pointer.pos().getZ() + direction.getStepZ() + 0.5
                ), stack
        );

        boomerangEntity.setVelocity(
                direction.getStepX(),
                direction.getStepY(),
                direction.getStepZ(),
                1f, 0f
        );
        return boomerangEntity;
    }

}
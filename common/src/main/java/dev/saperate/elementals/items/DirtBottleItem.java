package dev.saperate.elementals.items;


import dev.saperate.elementals.entities.common.DirtBottleEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stat;
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

public class DirtBottleItem extends Item implements DispenseItemBehavior {

    public DirtBottleItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player user, InteractionHand hand) {
        ItemStack handStack = user.getItemInHand(hand);

        level.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENDER_PEARL_THROW,
                SoundSource.NEUTRAL, 0.5f, 0.4f / (level.getRandom().nextFloat() * 0.4f + 0.8f));

        if (!level.isClientSide) {
            DirtBottleEntity entity = new DirtBottleEntity(level, user);
            entity.setItem(handStack);
            entity.shootFromRotation(user, user.getXRot(), user.getYRot(), 0.0f, .75f, 0f);
            level.addFreshEntity(entity);
        }
        user.awardStat(Stats.ITEM_USED.get(this));
        if (!user.getAbilities().instabuild) {
            handStack.shrink(1);
        }

        return InteractionResultHolder.success(handStack);
    }

    @Override
    public ItemStack dispense(BlockSource blockSource, ItemStack itemStack) {
        Level level = blockSource.level();
        level.playSound(null, blockSource.pos(), SoundEvents.ENDER_PEARL_THROW,
                SoundSource.NEUTRAL, 0.5f, 0.4f / (level.getRandom().nextFloat() * 0.4f + 0.8f));

        if (!level.isClientSide) {
            Direction direction = blockSource.state().getValue(DispenserBlock.FACING);
            DirtBottleEntity dirtBottle = getDirtBottleEntity(blockSource, level, direction);
            level.addFreshEntity(dirtBottle);
        }
        itemStack.shrink(1);
        return itemStack;
    }
    
    @NotNull
    private static DirtBottleEntity getDirtBottleEntity(BlockSource pointer, Level level, Direction direction) {
        DirtBottleEntity dirtBottleEntity = new DirtBottleEntity(level,
                new Vec3(
                        pointer.pos().getX() + direction.getStepX() + 0.5,
                        pointer.pos().getY() + direction.getStepY() + 0.5,
                        pointer.pos().getZ() + direction.getStepZ() + 0.5
                )
        );

        dirtBottleEntity.shoot(
                direction.getStepX(),
                direction.getStepY(),
                direction.getStepZ(),
                1f, 0f
        );
        return dirtBottleEntity;
    }
}
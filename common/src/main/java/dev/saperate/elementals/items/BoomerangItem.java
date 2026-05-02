package dev.saperate.elementals.items;

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
            BoomerangEntity entity = new BoomerangEntity(world, user.getEyePos(), handStack);
            entity.setOwner(user);
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
        World world = pointer.world();
        world.playSound(null, pointer.pos(), SoundEvents.ENTITY_ENDER_PEARL_THROW,
                SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));

        if (!world.isClient) {
            Direction direction = pointer.state().get(DispenserBlock.FACING);
            BoomerangEntity boomerangEntity = getBoomerangEntity(pointer, world, direction, stack);
            world.spawnEntity(boomerangEntity);
        }
        stack.decrement(1);
        return stack;
    }

    @NotNull
    private static BoomerangEntity getBoomerangEntity(BlockPointer pointer, World world, Direction direction, ItemStack stack) {
        BoomerangEntity boomerangEntity = new BoomerangEntity(world,
                new Vec3d(
                        pointer.pos().getX() + direction.getOffsetX() + 0.5,
                        pointer.pos().getY() + direction.getOffsetY() + 0.5,
                        pointer.pos().getZ() + direction.getOffsetZ() + 0.5
                ), stack
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
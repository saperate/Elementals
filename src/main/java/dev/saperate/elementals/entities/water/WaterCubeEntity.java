package dev.saperate.elementals.entities.water;

import dev.saperate.elementals.entities.ElementalEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class WaterCubeEntity extends ElementalEntity {
    public static final EntityType<WaterCubeEntity> WATERCUBE = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "water_cube"),
            FabricEntityTypeBuilder.<WaterCubeEntity>create(SpawnGroup.MISC, WaterCubeEntity::new)
                    .dimensions(EntityDimensions.fixed(1, 1)).build());

    public WaterCubeEntity(EntityType<WaterCubeEntity> type, World world) {
        super(type, world);
    }

    public WaterCubeEntity(World world, LivingEntity owner){
        super(WATERCUBE, world);
        setOwner(owner);
        setPos(owner.getX(),owner.getY(),owner.getZ());
    }

    public WaterCubeEntity(World world, LivingEntity owner, double x, double y, double z){
        super(WATERCUBE, world);
        setOwner(owner);
        setPos(x,y,z);
    }

    @Override
    public void tick() {
        super.tick();
        if(this.getOwner() == null){
            discard();
        }
    }
}

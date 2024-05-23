package dev.saperate.elementals.entities;



import dev.saperate.elementals.entities.air.AirShieldEntity;
import dev.saperate.elementals.entities.air.AirTornadoEntity;
import dev.saperate.elementals.entities.earth.EarthBlockEntity;
import dev.saperate.elementals.entities.fire.FireArcEntity;
import dev.saperate.elementals.entities.fire.FireBallEntity;
import dev.saperate.elementals.entities.fire.FireBlockEntity;
import dev.saperate.elementals.entities.fire.FireShieldEntity;
import dev.saperate.elementals.entities.water.*;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static dev.saperate.elementals.entities.water.WaterTowerEntity.heightLimit;

public class ElementalEntities {
    //Water
    public static final EntityType<WaterArcEntity> WATERARC = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "water_arc"),
            FabricEntityTypeBuilder.<WaterArcEntity>create(SpawnGroup.MISC, WaterArcEntity::new)
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build());
    public static final EntityType<WaterArmEntity> WATERARM = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "water_arm"),
            FabricEntityTypeBuilder.<WaterArmEntity>create(SpawnGroup.MISC, WaterArmEntity::new)
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build());
    public static final EntityType<WaterBladeEntity> WATERBLADE = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "water_blade"),
            FabricEntityTypeBuilder.<WaterBladeEntity>create(SpawnGroup.MISC, WaterBladeEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 0.125f)).build());
    public static final EntityType<WaterBulletEntity> WATERBULLET = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "water_bullet"),
            FabricEntityTypeBuilder.<WaterBulletEntity>create(SpawnGroup.MISC, WaterBulletEntity::new)
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build());
    public static final EntityType<WaterCubeEntity> WATERCUBE = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "water_cube"),
            FabricEntityTypeBuilder.<WaterCubeEntity>create(SpawnGroup.MISC, WaterCubeEntity::new)
                    .dimensions(EntityDimensions.fixed(1, 1)).build());
    public static final EntityType<WaterHealingEntity> WATERHEALING = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "water_healing"),
            FabricEntityTypeBuilder.<WaterHealingEntity>create(SpawnGroup.MISC, WaterHealingEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build());
    public static final EntityType<WaterHelmetEntity> WATERHELMET = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "water_helmet"),
            FabricEntityTypeBuilder.<WaterHelmetEntity>create(SpawnGroup.MISC, WaterHelmetEntity::new)
                    .dimensions(EntityDimensions.changing(1, 1)).build());
    public static final EntityType<WaterJetEntity> WATERJET = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "water_jet"),
            FabricEntityTypeBuilder.<WaterJetEntity>create(SpawnGroup.MISC, WaterJetEntity::new)
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build());
    public static final EntityType<WaterShieldEntity> WATERSHIELD = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "water_shield"),
            FabricEntityTypeBuilder.<WaterShieldEntity>create(SpawnGroup.MISC, WaterShieldEntity::new)
                    .dimensions(EntityDimensions.changing(3, 3)).build());
    public static final EntityType<WaterTowerEntity> WATERTOWER = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "water_tower"),
            FabricEntityTypeBuilder.<WaterTowerEntity>create(SpawnGroup.MISC, WaterTowerEntity::new)
                    .dimensions(EntityDimensions.fixed(1, heightLimit)).build());

    //Fire
    public static final EntityType<FireArcEntity> FIREARC = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "fire_arc"),
            FabricEntityTypeBuilder.<FireArcEntity>create(SpawnGroup.MISC, FireArcEntity::new)
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build());
    public static final EntityType<FireBallEntity> FIREBALL = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "fire_ball"),
            FabricEntityTypeBuilder.<FireBallEntity>create(SpawnGroup.MISC, FireBallEntity::new)
                    .dimensions(EntityDimensions.fixed(1, 1)).build());
    public static final EntityType<FireBlockEntity> FIREBLOCK = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "fire_block"),
            FabricEntityTypeBuilder.<FireBlockEntity>create(SpawnGroup.MISC, FireBlockEntity::new)
                    .dimensions(EntityDimensions.changing(1, FireBlockEntity.MAX_FLAME_SIZE)).build());
    public static final EntityType<FireShieldEntity> FIRESHIELD = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "fire_shield"),
            FabricEntityTypeBuilder.<FireShieldEntity>create(SpawnGroup.MISC, FireShieldEntity::new)
                    .dimensions(EntityDimensions.changing(3.5f, FireShieldEntity.MAX_FLAME_SIZE)).build());

    //Earth
    public static final EntityType<EarthBlockEntity> EARTHBLOCK = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "earth_block"),
            FabricEntityTypeBuilder.<EarthBlockEntity>create(SpawnGroup.MISC, EarthBlockEntity::new)
                    .dimensions(EntityDimensions.fixed(.9f, .9f)).build());

    //Air
    public static final EntityType<AirShieldEntity> AIRSHIELD = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "air_shield"),
            FabricEntityTypeBuilder.<AirShieldEntity>create(SpawnGroup.MISC, AirShieldEntity::new)
                    .dimensions(EntityDimensions.changing(3, 3)).build());
    public static final EntityType<AirTornadoEntity> AIRTORNADO = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "air_tornado"),
            FabricEntityTypeBuilder.<AirTornadoEntity>create(SpawnGroup.MISC, AirTornadoEntity::new)
                    .dimensions(EntityDimensions.changing(1.5f, 4)).build());
    public static void register() {

    }
}

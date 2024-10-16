package dev.saperate.elementals.entities;



import dev.saperate.elementals.entities.blood.BloodShotEntity;
import dev.saperate.elementals.entities.common.BoomerangEntity;
import dev.saperate.elementals.entities.common.DecoyPlayerEntity;
import dev.saperate.elementals.entities.air.*;
import dev.saperate.elementals.entities.common.DirtBottleEntity;
import dev.saperate.elementals.entities.earth.EarthBlockEntity;
import dev.saperate.elementals.entities.fire.*;
import dev.saperate.elementals.entities.lightning.LightningArcEntity;
import dev.saperate.elementals.entities.lightning.VoltArcEntity;
import dev.saperate.elementals.entities.water.*;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
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
                    .disableSummon()
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build());
    public static final EntityType<WaterArmEntity> WATERARM = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "water_arm"),
            FabricEntityTypeBuilder.<WaterArmEntity>create(SpawnGroup.MISC, WaterArmEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build());
    public static final EntityType<WaterBladeEntity> WATERBLADE = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "water_blade"),
            FabricEntityTypeBuilder.<WaterBladeEntity>create(SpawnGroup.MISC, WaterBladeEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.fixed(0.6f, 0.125f)).build());
    public static final EntityType<WaterBulletEntity> WATERBULLET = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "water_bullet"),
            FabricEntityTypeBuilder.<WaterBulletEntity>create(SpawnGroup.MISC, WaterBulletEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build());
    public static final EntityType<WaterCubeEntity> WATERCUBE = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "water_cube"),
            FabricEntityTypeBuilder.<WaterCubeEntity>create(SpawnGroup.MISC, WaterCubeEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.fixed(1, 1)).build());
    public static final EntityType<WaterHealingEntity> WATERHEALING = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "water_healing"),
            FabricEntityTypeBuilder.<WaterHealingEntity>create(SpawnGroup.MISC, WaterHealingEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build());
    public static final EntityType<WaterHelmetEntity> WATERHELMET = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "water_helmet"),
            FabricEntityTypeBuilder.<WaterHelmetEntity>create(SpawnGroup.MISC, WaterHelmetEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.changing(1, 1)).build());
    public static final EntityType<WaterJetEntity> WATERJET = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "water_jet"),
            FabricEntityTypeBuilder.<WaterJetEntity>create(SpawnGroup.MISC, WaterJetEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build());
    public static final EntityType<WaterShieldEntity> WATERSHIELD = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "water_shield"),
            FabricEntityTypeBuilder.<WaterShieldEntity>create(SpawnGroup.MISC, WaterShieldEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.changing(3, 3)).build());
    public static final EntityType<WaterTowerEntity> WATERTOWER = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "water_tower"),
            FabricEntityTypeBuilder.<WaterTowerEntity>create(SpawnGroup.MISC, WaterTowerEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.fixed(1, heightLimit)).build());

    //Fire
    public static final EntityType<FireArcEntity> FIREARC = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "fire_arc"),
            FabricEntityTypeBuilder.<FireArcEntity>create(SpawnGroup.MISC, FireArcEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build());
    public static final EntityType<FireBallEntity> FIREBALL = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "fire_ball"),
            FabricEntityTypeBuilder.<FireBallEntity>create(SpawnGroup.MISC, FireBallEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.fixed(1, 1)).build());
    public static final EntityType<FireBlockEntity> FIREBLOCK = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "fire_block"),
            FabricEntityTypeBuilder.<FireBlockEntity>create(SpawnGroup.MISC, FireBlockEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.changing(1, FireBlockEntity.MAX_FLAME_SIZE)).build());
    public static final EntityType<FireShieldEntity> FIRESHIELD = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "fire_shield"),
            FabricEntityTypeBuilder.<FireShieldEntity>create(SpawnGroup.MISC, FireShieldEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.changing(3.5f, FireShieldEntity.MAX_FLAME_SIZE)).build());

    public static final EntityType<FireWispEntity> FIREWISP = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "fire_wisp"),
            FabricEntityTypeBuilder.<FireWispEntity>create(SpawnGroup.MISC, FireWispEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build());

    //Earth
    public static final EntityType<EarthBlockEntity> EARTHBLOCK = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "earth_block"),
            FabricEntityTypeBuilder.<EarthBlockEntity>create(SpawnGroup.MISC, EarthBlockEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.fixed(.9f, .9f)).build());

    //Air
    public static final EntityType<AirShieldEntity> AIRSHIELD = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "air_shield"),
            FabricEntityTypeBuilder.<AirShieldEntity>create(SpawnGroup.MISC, AirShieldEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.changing(3, 3)).build());
    public static final EntityType<AirTornadoEntity> AIRTORNADO = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "air_tornado"),
            FabricEntityTypeBuilder.<AirTornadoEntity>create(SpawnGroup.MISC, AirTornadoEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.changing(1.5f, 4)).build());
    public static final EntityType<AirStreamEntity> AIRSTREAM = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "air_stream"),
            FabricEntityTypeBuilder.<AirStreamEntity>create(SpawnGroup.MISC, AirStreamEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.changing(0.25f, 0.25f)).build());
    public static final EntityType<AirBallEntity> AIRBALL = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "air_ball"),
            FabricEntityTypeBuilder.<AirBallEntity>create(SpawnGroup.MISC, AirBallEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.fixed(1, 1)).build());
    public static final EntityType<AirBulletEntity> AIRBULLET = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "air_bullet"),
            FabricEntityTypeBuilder.<AirBulletEntity>create(SpawnGroup.MISC, AirBulletEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build());
    public static final EntityType<AirScooterEntity> AIRSCOOTER = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "air_scooter"),
            FabricEntityTypeBuilder.<AirScooterEntity>create(SpawnGroup.MISC, AirScooterEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.fixed(1, 1)).build());

    //LIGHTNING
    public static final EntityType<LightningArcEntity> LIGHTNINGARC = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "lightning_arc"),
            FabricEntityTypeBuilder.<LightningArcEntity>create(SpawnGroup.MISC, LightningArcEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.changing(0.25f, 0.25f)).build());
    public static final EntityType<VoltArcEntity> VOLTARC = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "volt_arc"),
            FabricEntityTypeBuilder.<VoltArcEntity>create(SpawnGroup.MISC, VoltArcEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.changing(0.125f, 0.125f)).build());

    //BLOOD
    public static final EntityType<BloodShotEntity> BLOODSHOT = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "blood_shot"),
            FabricEntityTypeBuilder.<BloodShotEntity>create(SpawnGroup.MISC, BloodShotEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.changing(0.125f, 0.125f)).build());

    //Common
    public static final EntityType<DecoyPlayerEntity> DECOYPLAYER = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "decoy_player"),
            FabricEntityTypeBuilder.<DecoyPlayerEntity>create(SpawnGroup.MISC, DecoyPlayerEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.fixed(0.6f, 2)).build());

    public static final EntityType<DirtBottleEntity> DIRTBOTTLEENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "dirt_bottle"),
            FabricEntityTypeBuilder.<DirtBottleEntity>create(SpawnGroup.MISC, DirtBottleEntity::new)
                    .dimensions(EntityDimensions.fixed(.75f, .75f)).build());
    public static final EntityType<BoomerangEntity> BOOMERANGENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("elementals", "boomerang"),
            FabricEntityTypeBuilder.<BoomerangEntity>create(SpawnGroup.MISC, BoomerangEntity::new)
                    .dimensions(EntityDimensions.fixed(.6f, .2f)).build());
    public static void register() {
        FabricDefaultAttributeRegistry.register(DECOYPLAYER, DecoyPlayerEntity.createMobAttributes());
    }

}

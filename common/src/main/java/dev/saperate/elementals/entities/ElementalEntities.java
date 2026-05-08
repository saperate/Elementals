package dev.saperate.elementals.entities;



import dev.saperate.elementals.Constants;
import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.entities.blood.BloodShotEntity;
import dev.saperate.elementals.entities.common.BoomerangEntity;
import dev.saperate.elementals.entities.common.DecoyPlayerEntity;
import dev.saperate.elementals.entities.air.*;
import dev.saperate.elementals.entities.common.DirtBottleEntity;
import dev.saperate.elementals.entities.common.sky_bison.SkyBisonEntity;
import dev.saperate.elementals.entities.earth.EarthBlockEntity;
import dev.saperate.elementals.entities.fire.*;
import dev.saperate.elementals.entities.lightning.LightningArcEntity;
import dev.saperate.elementals.entities.lightning.VoltArcEntity;
import dev.saperate.elementals.entities.metal.MetalBindEntity;
import dev.saperate.elementals.entities.metal.MetalBulletEntity;
import dev.saperate.elementals.entities.metal.MetalCableEntity;
import dev.saperate.elementals.entities.metal.MetalLanceEntity;
import dev.saperate.elementals.entities.water.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;

import static dev.saperate.elementals.entities.water.WaterTowerEntity.heightLimit;

public class ElementalEntities {
    //Water
    public static final EntityType<WaterArcEntity> WATERARC = registerAbilityEntity(
            "water_arc", WaterArcEntity::new, 0.25f, 0.25f);
    public static final EntityType<WaterArmEntity> WATERARM = registerAbilityEntity(
            "water_arm", WaterArmEntity::new, 0.25f, 0.25f);
    public static final EntityType<WaterBladeEntity> WATERBLADE = registerAbilityEntity(
            "water_blade", WaterBladeEntity::new, 0.6f, 0.125f);
    public static final EntityType<WaterBulletEntity> WATERBULLET = registerAbilityEntity(
            "water_bullet", WaterBulletEntity::new, 0.25f, 0.25f);
    public static final EntityType<WaterCubeEntity> WATERCUBE = registerAbilityEntity(
            "water_cube", WaterCubeEntity::new, 1, 1);
    public static final EntityType<WaterHealingEntity> WATERHEALING = registerAbilityEntity(
            "water_healing", WaterHealingEntity::new, 0.5f, 0.5f);
    public static final EntityType<WaterHelmetEntity> WATERHELMET = registerAbilityEntity(
            "water_helmet", WaterHelmetEntity::new, 1, 1);
    public static final EntityType<WaterJetEntity> WATERJET = registerAbilityEntity(
            "water_jet", WaterJetEntity::new, 0.25f, 0.25f);
    public static final EntityType<WaterShieldEntity> WATERSHIELD = registerAbilityEntity(
            "water_shield", WaterShieldEntity::new, 3, 3);
    public static final EntityType<WaterTowerEntity> WATERTOWER = registerAbilityEntity(
            "water_tower", WaterTowerEntity::new, 1, heightLimit);

    //Fire
    public static final EntityType<FireArcEntity> FIREARC = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("assets/elementals", "fire_arc"),
            FabricEntityTypeBuilder.<FireArcEntity>create(SpawnGroup.MISC, FireArcEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build());
    public static final EntityType<FireBallEntity> FIREBALL = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("assets/elementals", "fire_ball"),
            FabricEntityTypeBuilder.<FireBallEntity>create(SpawnGroup.MISC, FireBallEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.fixed(1, 1)).build());
    public static final EntityType<FireBlockEntity> FIREBLOCK = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("assets/elementals", "fire_block"),
            FabricEntityTypeBuilder.<FireBlockEntity>create(SpawnGroup.MISC, FireBlockEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.changing(1, FireBlockEntity.MAX_FLAME_SIZE)).build());
    public static final EntityType<FireShieldEntity> FIRESHIELD = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("assets/elementals", "fire_shield"),
            FabricEntityTypeBuilder.<FireShieldEntity>create(SpawnGroup.MISC, FireShieldEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.changing(3.5f, FireShieldEntity.MAX_FLAME_SIZE)).build());

    public static final EntityType<FireWispEntity> FIREWISP = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("assets/elementals", "fire_wisp"),
            FabricEntityTypeBuilder.<FireWispEntity>create(SpawnGroup.MISC, FireWispEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build());

    //Earth
    public static final EntityType<EarthBlockEntity> EARTHBLOCK = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("assets/elementals", "earth_block"),
            FabricEntityTypeBuilder.<EarthBlockEntity>create(SpawnGroup.MISC, EarthBlockEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.fixed(.9f, .9f)).build());

    //Air
    public static final EntityType<AirShieldEntity> AIRSHIELD = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("assets/elementals", "air_shield"),
            FabricEntityTypeBuilder.<AirShieldEntity>create(SpawnGroup.MISC, AirShieldEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.changing(3, 3)).build());
    public static final EntityType<AirTornadoEntity> AIRTORNADO = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("assets/elementals", "air_tornado"),
            FabricEntityTypeBuilder.<AirTornadoEntity>create(SpawnGroup.MISC, AirTornadoEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.changing(1.5f, 4)).build());
    public static final EntityType<AirStreamEntity> AIRSTREAM = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("assets/elementals", "air_stream"),
            FabricEntityTypeBuilder.<AirStreamEntity>create(SpawnGroup.MISC, AirStreamEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.changing(0.25f, 0.25f)).build());
    public static final EntityType<AirBallEntity> AIRBALL = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("assets/elementals", "air_ball"),
            FabricEntityTypeBuilder.<AirBallEntity>create(SpawnGroup.MISC, AirBallEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.fixed(1, 1)).build());
    public static final EntityType<AirBulletEntity> AIRBULLET = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("assets/elementals", "air_bullet"),
            FabricEntityTypeBuilder.<AirBulletEntity>create(SpawnGroup.MISC, AirBulletEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build());
    public static final EntityType<AirScooterEntity> AIRSCOOTER = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("assets/elementals", "air_scooter"),
            FabricEntityTypeBuilder.<AirScooterEntity>create(SpawnGroup.MISC, AirScooterEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.fixed(1, 1)).build());

    //LIGHTNING
    public static final EntityType<LightningArcEntity> LIGHTNINGARC = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("assets/elementals", "lightning_arc"),
            FabricEntityTypeBuilder.<LightningArcEntity>create(SpawnGroup.MISC, LightningArcEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.changing(0.25f, 0.25f)).build());
    public static final EntityType<VoltArcEntity> VOLTARC = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("assets/elementals", "volt_arc"),
            FabricEntityTypeBuilder.<VoltArcEntity>create(SpawnGroup.MISC, VoltArcEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.changing(0.125f, 0.125f)).build());

    //BLOOD
    public static final EntityType<BloodShotEntity> BLOODSHOT = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("assets/elementals", "blood_shot"),
            FabricEntityTypeBuilder.<BloodShotEntity>create(SpawnGroup.MISC, BloodShotEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.changing(0.125f, 0.125f)).build());

    //METAL
    public static final EntityType<MetalCableEntity> METALCABLE = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("assets/elementals", "metal_cable"),
            FabricEntityTypeBuilder.<MetalCableEntity>create(SpawnGroup.MISC, MetalCableEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.changing(0.25f, 0.25f)).build());
    public static final EntityType<MetalBindEntity> METALBIND = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("assets/elementals", "metal_bind"),
            FabricEntityTypeBuilder.<MetalBindEntity>create(SpawnGroup.MISC, MetalBindEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.changing(0.25f, 0.25f)).build());
    public static final EntityType<MetalBulletEntity> METALBULLET = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("assets/elementals", "metal_bullet"),
            FabricEntityTypeBuilder.<MetalBulletEntity>create(SpawnGroup.MISC, MetalBulletEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build());
    
    public static final EntityType<MetalLanceEntity> METALLANCE = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(Elementals.MODID,"metal_lance"),
            FabricEntityTypeBuilder.<MetalLanceEntity>create(SpawnGroup.MISC, MetalLanceEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.fixed(.4f, .4f)).build());

    //Common
    public static final EntityType<DecoyPlayerEntity> DECOYPLAYER = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("assets/elementals", "decoy_player"),
            FabricEntityTypeBuilder.<DecoyPlayerEntity>create(SpawnGroup.MISC, DecoyPlayerEntity::new)
                    .disableSummon()
                    .dimensions(EntityDimensions.fixed(0.6f, 2)).build());

    public static final EntityType<DirtBottleEntity> DIRTBOTTLEENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("assets/elementals", "dirt_bottle"),
            FabricEntityTypeBuilder.<DirtBottleEntity>create(SpawnGroup.MISC, DirtBottleEntity::new)
                    .dimensions(EntityDimensions.fixed(.75f, .75f)).build());
    public static final EntityType<BoomerangEntity> BOOMERANGENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("assets/elementals", "boomerang"),
            FabricEntityTypeBuilder.<BoomerangEntity>create(SpawnGroup.MISC, BoomerangEntity::new)
                    .dimensions(EntityDimensions.fixed(.6f, .2f)).build());

    public static final EntityType<SkyBisonEntity> SKYBISON = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("assets/elementals", "sky_bison"),
            FabricEntityTypeBuilder.<SkyBisonEntity>create(SpawnGroup.MISC, SkyBisonEntity::new)
                    .dimensions(EntityDimensions.fixed(3.25f, 3.25f)).build());

    private static <T extends Entity> EntityType<T> registerAbilityEntity(String name, EntityType.EntityFactory<T> factory, float width, float height) {
        return Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(Constants.MODID, name),
                EntityType.Builder.of(factory, MobCategory.MISC)
                        .noSummon()
                        .sized(width, height).build(name));
    }
    
    public static void register() {
        FabricDefaultAttributeRegistry.register(DECOYPLAYER, DecoyPlayerEntity.createMobAttributes());
        FabricDefaultAttributeRegistry.register(SKYBISON, SkyBisonEntity.createAttributes());
    }

}

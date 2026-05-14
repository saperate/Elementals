package dev.saperate.elementals.entities;

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
import dev.saperate.elementals.platform.Services;
import net.minecraft.world.entity.*;

import java.util.function.Supplier;

import static dev.saperate.elementals.entities.water.WaterTowerEntity.heightLimit;

public class ElementalEntities {
    //Water
    public static final Supplier<EntityType<WaterArcEntity>> WATERARC = Services.REGISTRY.registerEntity(
            "water_arc", WaterArcEntity::new, 0.25f, 0.25f);
    public static final Supplier<EntityType<WaterArmEntity>> WATERARM = Services.REGISTRY.registerEntity(
            "water_arm", WaterArmEntity::new, 0.25f, 0.25f);
    public static final Supplier<EntityType<WaterBladeEntity>> WATERBLADE = Services.REGISTRY.registerEntity(
            "water_blade", WaterBladeEntity::new, 0.6f, 0.125f);
    public static final Supplier<EntityType<WaterBulletEntity>> WATERBULLET = Services.REGISTRY.registerEntity(
            "water_bullet", WaterBulletEntity::new, 0.25f, 0.25f);
    public static final Supplier<EntityType<WaterCubeEntity>> WATERCUBE = Services.REGISTRY.registerEntity(
            "water_cube", WaterCubeEntity::new, 1, 1);
    public static final Supplier<EntityType<WaterHealingEntity>> WATERHEALING = Services.REGISTRY.registerEntity(
            "water_healing", WaterHealingEntity::new, 0.5f, 0.5f);
    public static final Supplier<EntityType<WaterHelmetEntity>> WATERHELMET = Services.REGISTRY.registerEntity(
            "water_helmet", WaterHelmetEntity::new, 1, 1);
    public static final Supplier<EntityType<WaterJetEntity>> WATERJET = Services.REGISTRY.registerEntity(
            "water_jet", WaterJetEntity::new, 0.25f, 0.25f);
    public static final Supplier<EntityType<WaterShieldEntity>> WATERSHIELD = Services.REGISTRY.registerEntity(
            "water_shield", WaterShieldEntity::new, 3, 3);
    public static final Supplier<EntityType<WaterTowerEntity>> WATERTOWER = Services.REGISTRY.registerEntity(
            "water_tower", WaterTowerEntity::new, 1, heightLimit);

    //Fire
    public static final Supplier<EntityType<FireArcEntity>> FIREARC = Services.REGISTRY.registerEntity(
            "fire_arc", FireArcEntity::new, 0.25f, 0.25f);
    public static final Supplier<EntityType<FireBallEntity>> FIREBALL = Services.REGISTRY.registerEntity(
            "fire_ball", FireBallEntity::new, 1, 1);
    public static final Supplier<EntityType<FireBlockEntity>> FIREBLOCK = Services.REGISTRY.registerEntity(
            "fire_block", FireBlockEntity::new, 1, FireBlockEntity.MAX_FLAME_SIZE);
    public static final Supplier<EntityType<FireShieldEntity>> FIRESHIELD = Services.REGISTRY.registerEntity(
            "fire_shield", FireShieldEntity::new, 3.5f, 3f);
    public static final Supplier<EntityType<FireWispEntity>> FIREWISP = Services.REGISTRY.registerEntity(
            "fire_wisp", FireWispEntity::new, 0.25f, 0.25f);

    //Earth
    public static final Supplier<EntityType<EarthBlockEntity>> EARTHBLOCK = Services.REGISTRY.registerEntity(
            "earth_block", EarthBlockEntity::new, .9f, .9f);


    //Air
    public static final Supplier<EntityType<AirShieldEntity>> AIRSHIELD = Services.REGISTRY.registerEntity(
            "air_shield", AirShieldEntity::new, 3, 3);
    public static final Supplier<EntityType<AirTornadoEntity>> AIRTORNADO = Services.REGISTRY.registerEntity(
            "air_tornado", AirTornadoEntity::new, 1.5f, 4);
    public static final Supplier<EntityType<AirStreamEntity>> AIRSTREAM = Services.REGISTRY.registerEntity(
            "air_stream", AirStreamEntity::new, 0.25f, 0.25f);
    public static final Supplier<EntityType<AirBallEntity>> AIRBALL = Services.REGISTRY.registerEntity(
            "air_ball", AirBallEntity::new, 1, 1);
    public static final Supplier<EntityType<AirBulletEntity>> AIRBULLET = Services.REGISTRY.registerEntity(
            "air_bullet", AirBulletEntity::new, 0.25f, 0.25f);
    public static final Supplier<EntityType<AirScooterEntity>> AIRSCOOTER = Services.REGISTRY.registerEntity(
            "air_scooter", AirScooterEntity::new, 1, 1);

    //LIGHTNING
    public static final Supplier<EntityType<LightningArcEntity>> LIGHTNINGARC = Services.REGISTRY.registerEntity(
            "lightning_arc", LightningArcEntity::new, 0.25f, 0.25f);
    public static final Supplier<EntityType<VoltArcEntity>> VOLTARC = Services.REGISTRY.registerEntity(
            "volt_arc", VoltArcEntity::new, 0.125f, 0.125f);

    //BLOOD
    public static final Supplier<EntityType<BloodShotEntity>> BLOODSHOT = Services.REGISTRY.registerEntity(
            "blood_shot", BloodShotEntity::new, 0.125f, 0.125f);

    //METAL
    public static final Supplier<EntityType<MetalCableEntity>> METALCABLE = Services.REGISTRY.registerEntity(
            "metal_cable", MetalCableEntity::new, 0.25f, 0.25f);
    public static final Supplier<EntityType<MetalBindEntity>> METALBIND = Services.REGISTRY.registerEntity(
            "metal_bind", MetalBindEntity::new, 0.25f, 0.25f);
    public static final Supplier<EntityType<MetalBulletEntity>> METALBULLET = Services.REGISTRY.registerEntity(
            "metal_bullet", MetalBulletEntity::new, 0.25f, 0.25f);
    public static final Supplier<EntityType<MetalLanceEntity>> METALLANCE = Services.REGISTRY.registerEntity(
            "metal_lance", MetalLanceEntity::new, .4f, .4f);

    //Common
    public static final Supplier<EntityType<DecoyPlayerEntity>> DECOYPLAYER = Services.REGISTRY.registerEntity(
            "decoy_player", DecoyPlayerEntity::new, 0.6f, 2);
    public static final Supplier<EntityType<DirtBottleEntity>> DIRTBOTTLEENTITY = Services.REGISTRY.registerEntity(
            "dirt_bottle", DirtBottleEntity::new, .75f, .75f);
    public static final Supplier<EntityType<BoomerangEntity>> BOOMERANGENTITY = Services.REGISTRY.registerEntity(
            "boomerang", BoomerangEntity::new, .6f, .2f);
    public static final Supplier<EntityType<SkyBisonEntity>> SKYBISON = Services.REGISTRY.registerEntity(
            "sky_bison", SkyBisonEntity::new, 3.25f, 3.25f);
    
    public static void register() {
        Services.REGISTRY.registerDefaultEntityAttribute(DECOYPLAYER, DecoyPlayerEntity::createMobAttributes);
        Services.REGISTRY.registerDefaultEntityAttribute(SKYBISON, SkyBisonEntity::createMobAttributes);
    }

}

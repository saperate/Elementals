package dev.saperate.elementals.entities;



import dev.saperate.elementals.Constants;
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
import dev.saperate.elementals.mixin.DefaultAttributeRegistryAccessor;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;

import static dev.saperate.elementals.entities.water.WaterTowerEntity.heightLimit;

public class ElementalEntities {
    //Water
    public static final EntityType<WaterArcEntity> WATERARC = registerEntity(
            "water_arc", WaterArcEntity::new, 0.25f, 0.25f);
    public static final EntityType<WaterArmEntity> WATERARM = registerEntity(
            "water_arm", WaterArmEntity::new, 0.25f, 0.25f);
    public static final EntityType<WaterBladeEntity> WATERBLADE = registerEntity(
            "water_blade", WaterBladeEntity::new, 0.6f, 0.125f);
    public static final EntityType<WaterBulletEntity> WATERBULLET = registerEntity(
            "water_bullet", WaterBulletEntity::new, 0.25f, 0.25f);
    public static final EntityType<WaterCubeEntity> WATERCUBE = registerEntity(
            "water_cube", WaterCubeEntity::new, 1, 1);
    public static final EntityType<WaterHealingEntity> WATERHEALING = registerEntity(
            "water_healing", WaterHealingEntity::new, 0.5f, 0.5f);
    public static final EntityType<WaterHelmetEntity> WATERHELMET = registerEntity(
            "water_helmet", WaterHelmetEntity::new, 1, 1);
    public static final EntityType<WaterJetEntity> WATERJET = registerEntity(
            "water_jet", WaterJetEntity::new, 0.25f, 0.25f);
    public static final EntityType<WaterShieldEntity> WATERSHIELD = registerEntity(
            "water_shield", WaterShieldEntity::new, 3, 3);
    public static final EntityType<WaterTowerEntity> WATERTOWER = registerEntity(
            "water_tower", WaterTowerEntity::new, 1, heightLimit);

    //Fire
    public static final EntityType<FireArcEntity> FIREARC = registerEntity(
            "fire_arc", FireArcEntity::new, 0.25f, 0.25f);
    public static final EntityType<FireBallEntity> FIREBALL = registerEntity(
            "fire_ball", FireBallEntity::new, 1, 1);
    public static final EntityType<FireBlockEntity> FIREBLOCK = registerEntity(
            "fire_block", FireBlockEntity::new, 1, FireBlockEntity.MAX_FLAME_SIZE);
    public static final EntityType<FireShieldEntity> FIRESHIELD = registerEntity(
            "fire_shield", FireShieldEntity::new, 3.5f, 3f);
    public static final EntityType<FireWispEntity> FIREWISP = registerEntity(
            "fire_wisp", FireWispEntity::new, 0.25f, 0.25f);

    //Earth
    public static final EntityType<EarthBlockEntity> EARTHBLOCK = registerEntity(
            "earth_block", EarthBlockEntity::new, .9f, .9f);


    //Air
    public static final EntityType<AirShieldEntity> AIRSHIELD = registerEntity(
            "air_shield", AirShieldEntity::new, 3, 3);
    public static final EntityType<AirTornadoEntity> AIRTORNADO = registerEntity(
            "air_tornado", AirTornadoEntity::new, 1.5f, 4);
    public static final EntityType<AirStreamEntity> AIRSTREAM = registerEntity(
            "air_stream", AirStreamEntity::new, 0.25f, 0.25f);
    public static final EntityType<AirBallEntity> AIRBALL = registerEntity(
            "air_ball", AirBallEntity::new, 1, 1);
    public static final EntityType<AirBulletEntity> AIRBULLET = registerEntity(
            "air_bullet", AirBulletEntity::new, 0.25f, 0.25f);
    public static final EntityType<AirScooterEntity> AIRSCOOTER = registerEntity(
            "air_scooter", AirScooterEntity::new, 1, 1);

    //LIGHTNING
    public static final EntityType<LightningArcEntity> LIGHTNINGARC = registerEntity(
            "lightning_arc", LightningArcEntity::new, 0.25f, 0.25f);
    public static final EntityType<VoltArcEntity> VOLTARC = registerEntity(
            "volt_arc", VoltArcEntity::new, 0.125f, 0.125f);

    //BLOOD
    public static final EntityType<BloodShotEntity> BLOODSHOT = registerEntity(
            "blood_shot", BloodShotEntity::new, 0.125f, 0.125f);

    //METAL
    public static final EntityType<MetalCableEntity> METALCABLE = registerEntity(
            "metal_cable", MetalCableEntity::new, 0.25f, 0.25f);
    public static final EntityType<MetalBindEntity> METALBIND = registerEntity(
            "metal_bind", MetalBindEntity::new, 0.25f, 0.25f);
    public static final EntityType<MetalBulletEntity> METALBULLET = registerEntity(
            "metal_bullet", MetalBulletEntity::new, 0.25f, 0.25f);
    public static final EntityType<MetalLanceEntity> METALLANCE = registerEntity(
            "metal_lance", MetalLanceEntity::new, .4f, .4f);

    //Common
    public static final EntityType<DecoyPlayerEntity> DECOYPLAYER = registerEntity(
            "decoy_player", DecoyPlayerEntity::new, 0.6f, 2);
    public static final EntityType<DirtBottleEntity> DIRTBOTTLEENTITY = registerEntity(
            "dirt_bottle", DirtBottleEntity::new, .75f, .75f);
    public static final EntityType<BoomerangEntity> BOOMERANGENTITY = registerEntity(
            "boomerang", BoomerangEntity::new, .6f, .2f);
    public static final EntityType<SkyBisonEntity> SKYBISON = registerEntity(
            "sky_bison", SkyBisonEntity::new, 3.25f, 3.25f);

    private static <T extends Entity> EntityType<T> registerEntity(String name, EntityType.EntityFactory<T> factory, float width, float height) {
        return Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(Constants.MODID, name),
                EntityType.Builder.of(factory, MobCategory.MISC)
                        .noSummon()
                        .sized(width, height).build(name));
    }
    
    public static void register() {
        DefaultAttributeRegistryAccessor.getRegistry().put(DECOYPLAYER, DecoyPlayerEntity.createMobAttributes().build());
        DefaultAttributeRegistryAccessor.getRegistry().put(SKYBISON, SkyBisonEntity.createAttributes().build());
    }

}

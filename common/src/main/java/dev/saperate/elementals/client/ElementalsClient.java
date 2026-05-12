package dev.saperate.elementals.client;

import commonnetwork.api.Network;
import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.client.entities.models.metal.MetalLanceModel;
import dev.saperate.elementals.client.entities.models.water.WaterBladeModel;
import dev.saperate.elementals.client.entities.water.*;
import dev.saperate.elementals.client.entities.earth.*;
import dev.saperate.elementals.client.entities.fire.*;
import dev.saperate.elementals.client.entities.air.*;
import dev.saperate.elementals.client.entities.common.*;
import dev.saperate.elementals.client.entities.blood.*;
import dev.saperate.elementals.client.entities.lightning.*;
import dev.saperate.elementals.client.entities.metal.*;
import dev.saperate.elementals.client.features.MetalArmorRenderer;
import dev.saperate.elementals.client.gui.CastTimerHudOverlay;
import dev.saperate.elementals.client.gui.ChiHudOverlay;
import dev.saperate.elementals.client.items.GliderItemRenderer;
import dev.saperate.elementals.client.keys.KeyCycleBending;
import dev.saperate.elementals.client.keys.abilities.*;
import dev.saperate.elementals.client.keys.gui.GuiKey;
import dev.saperate.elementals.network.packets.C2S.SyncVersionPacket;
import dev.saperate.elementals.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;

import static dev.saperate.elementals.Constants.MODID;
import static dev.saperate.elementals.entities.ElementalEntities.*;

public class ElementalsClient {
    public static final ModelLayerLocation MODEL_WATER_BLADE_LAYER = (new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(MODID, "water_blade"), "bb_main"));
    public static final ModelLayerLocation MODEL_METAL_LANCE_LAYER = (new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(MODID, "metal_lance"),"bb_main"));
    
    
    public static void init(){
		registerEntityRenderers();

        new KeyAbility1();
        new KeyAbility2();
        new KeyAbility3();
        new KeyAbility4();
        new GuiKey();
        new KeyCycleBending();

        Services.EVENTS.onClientRenderOverlay(new CastTimerHudOverlay());
        Services.EVENTS.onClientRenderOverlay(new ChiHudOverlay());
        Services.EVENTS.onClientJoin(ElementalsClient::onClientJoin);
        
        Services.REGISTRY.registerClientParticles();
        Services.REGISTRY.registerClientColorProviders();
        Services.REGISTRY.registerClientModelLayer(MODEL_WATER_BLADE_LAYER, WaterBladeModel::getTexturedModelData);
        Services.REGISTRY.registerClientModelLayer(MODEL_METAL_LANCE_LAYER, MetalLanceModel::getTexturedModelData);

        Elementals.GLIDER_ITEM_RENDER_PROVIDER = () -> new GeoRenderProvider(){
            private final GliderItemRenderer renderer = new GliderItemRenderer();

            @Override
            public @Nullable BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                return renderer;
            }
        };

        Elementals.METAL_ARMOR_RENDER_PROVIDER = () -> new GeoRenderProvider(){
            private MetalArmorRenderer renderer;
            
            @Override
            public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(@Nullable T livingEntity, ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable HumanoidModel<T> original) {
                if(this.renderer == null)
                    this.renderer = new MetalArmorRenderer();

                return renderer;
            }
        };
    }


    public static void registerEntityRenderers() {
        //WATER
        Services.REGISTRY.registerClientEntityRenderer(WATERCUBE, WaterCubeEntityRenderer::new);
        Services.REGISTRY.registerClientEntityRenderer(WATERHELMET, WaterHelmetEntityRenderer::new);
        Services.REGISTRY.registerClientEntityRenderer(WATERSHIELD, WaterShieldEntityRenderer::new);
        Services.REGISTRY.registerClientEntityRenderer(WATERARC, WaterArcEntityRenderer::new);
        Services.REGISTRY.registerClientEntityRenderer(WATERJET, WaterJetEntityRenderer::new);
        Services.REGISTRY.registerClientEntityRenderer(WATERARM, WaterArmEntityRenderer::new);
        Services.REGISTRY.registerClientEntityRenderer(WATERBLADE, WaterBladeEntityRenderer::new);
        Services.REGISTRY.registerClientEntityRenderer(WATERBULLET, WaterBulletEntityRenderer::new);
        Services.REGISTRY.registerClientEntityRenderer(WATERHEALING, WaterHealingEntityRenderer::new);
        Services.REGISTRY.registerClientEntityRenderer(WATERTOWER, WaterTowerEntityRenderer::new);

        //FIRE
        Services.REGISTRY.registerClientEntityRenderer(FIREBLOCK, FireBlockEntityRenderer::new);
        Services.REGISTRY.registerClientEntityRenderer(FIREARC, FireArcEntityRenderer::new);
        Services.REGISTRY.registerClientEntityRenderer(FIREBALL, FireBallEntityRenderer::new);
        Services.REGISTRY.registerClientEntityRenderer(FIRESHIELD, FireShieldEntityRenderer::new);
        Services.REGISTRY.registerClientEntityRenderer(FIREWISP, FireWispEntityRenderer::new);

        //EARTH
        Services.REGISTRY.registerClientEntityRenderer(EARTHBLOCK, EarthBlockEntityRenderer::new);

        //AIR
        Services.REGISTRY.registerClientEntityRenderer(AIRSHIELD, AirShieldEntityRenderer::new);
        Services.REGISTRY.registerClientEntityRenderer(AIRTORNADO, AirTornadoEntityRenderer::new);
        Services.REGISTRY.registerClientEntityRenderer(AIRSTREAM, AirStreamEntityRenderer::new);
        Services.REGISTRY.registerClientEntityRenderer(AIRBALL, AirBallEntityRenderer::new);
        Services.REGISTRY.registerClientEntityRenderer(AIRBULLET, AirBulletEntityRenderer::new);
        Services.REGISTRY.registerClientEntityRenderer(AIRSCOOTER, AirScooterEntityRenderer::new);

        //COMMON
        Services.REGISTRY.registerClientEntityRenderer(DECOYPLAYER, (context) -> new DecoyPlayerEntityRenderer(context, true));
        Services.REGISTRY.registerClientEntityRenderer(DIRTBOTTLEENTITY, DirtBottleEntityRenderer::new);
        Services.REGISTRY.registerClientEntityRenderer(BOOMERANGENTITY, BoomerangEntityRenderer::new);
		Services.REGISTRY.registerClientEntityRenderer(SKYBISON, SkyBisonEntityRenderer::new);
        
        //LIGHTNING
        Services.REGISTRY.registerClientEntityRenderer(LIGHTNINGARC, LightningArcEntityRenderer::new);
        Services.REGISTRY.registerClientEntityRenderer(VOLTARC, VoltArcEntityRenderer::new);

        //BLOOD
        Services.REGISTRY.registerClientEntityRenderer(BLOODSHOT, BloodShotEntityRenderer::new);

		//METAL
		Services.REGISTRY.registerClientEntityRenderer(METALCABLE, MetalCableEntityRenderer::new);
		Services.REGISTRY.registerClientEntityRenderer(METALBIND, MetalBindEntityRenderer::new);
		Services.REGISTRY.registerClientEntityRenderer(METALBULLET, MetalBulletEntityRenderer::new);
		Services.REGISTRY.registerClientEntityRenderer(METALLANCE, MetalLanceRenderer::new);
	}

    private static void onClientJoin(Minecraft client) {
        Network.getNetworkHandler().sendToServer(new SyncVersionPacket(SyncVersionPacket.getModVersion()));
    }
    
}
package dev.saperate.elementals.client;

import dev.saperate.elementals.Elementals;
import dev.saperate.elementals.blocks.ElementalsBlocks;
import dev.saperate.elementals.client.entities.models.common.DecoyPlayerModel;
import dev.saperate.elementals.client.entities.models.metal.MetalLanceModel;
import dev.saperate.elementals.client.entities.models.water.WaterBladeModel;
import dev.saperate.elementals.client.entities.water.WaterShieldEntityRenderer;
import dev.saperate.elementals.client.features.MetalArmorRenderer;
import dev.saperate.elementals.client.gui.CastTimerHudOverlay;
import dev.saperate.elementals.client.gui.ChiHudOverlay;
import dev.saperate.elementals.client.items.GliderItemRenderer;
import dev.saperate.elementals.client.keys.KeyCycleBending;
import dev.saperate.elementals.client.keys.abilities.*;
import dev.saperate.elementals.client.keys.gui.GuiKey;
import dev.saperate.elementals.client.particle.MetalShardParticle;
import dev.saperate.elementals.entities.air.*;
import dev.saperate.elementals.items.ElementalsItems;
import dev.saperate.elementals.items.WaterPouchItem;
import dev.saperate.elementals.mixin.client.GuiAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;

import java.util.Optional;

import static dev.saperate.elementals.Elementals.*;
import static dev.saperate.elementals.entities.ElementalEntities.*;

public class ElementalsClient {
    public static final ModelLayerLocation MODEL_DECOY_PLAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(MODID, "decoy_player"), "main");
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


        LayeredDraw hudLayers = ((GuiAccessor) Minecraft.getInstance().gui).elementals$getLayers();
        hudLayers.add(new CastTimerHudOverlay());
        hudLayers.add(new ChiHudOverlay());
        


		EntityModelLayerRegistry.registerModelLayer(MODEL_WATER_BLADE_LAYER, WaterBladeModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(MODEL_DECOY_PLAYER, DecoyPlayerModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(MODEL_METAL_LANCE_LAYER, MetalLanceModel::getTexturedModelData);
        
        ParticleFactoryRegistry.getInstance().register(LIGHTNING_PARTICLE_TYPE, FlameParticle.Provider::new);
		ParticleFactoryRegistry.getInstance().register(METAL_SHARD_PARTICLE_TYPE, MetalShardParticle.Factory::new);

        ClientPlayConnectionEvents.JOIN.register(ElementalsClient::onClientJoin);
        
        ColorProviderRegistry.ITEM.register(
                (stack, tintIndex) -> tintIndex == 0 ? ((WaterPouchItem) stack.getItem()).getColor(stack) : 0xFFFFFFFF,
                ElementalsItems.WATER_POUCH_ITEM
        );

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
        
        BlockRenderLayerMap.INSTANCE.putBlock(ElementalsBlocks.MOON_PEACH_LEAVES, RenderType.cutout());
        ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> 0x4253ed, ElementalsBlocks.MOON_PEACH_LEAVES);
    }


    public static void registerEntityRenderers() {
        //WATER
        EntityRendererRegistry.register(WATERCUBE, WaterCubeEntityRenderer::new);
        EntityRendererRegistry.register(WATERHELMET, WaterHelmetEntityRenderer::new);
        EntityRendererRegistry.register(WATERSHIELD, WaterShieldEntityRenderer::new);
        EntityRendererRegistry.register(WATERARC, WaterArcEntityRenderer::new);
        EntityRendererRegistry.register(WATERJET, WaterJetEntityRenderer::new);
        EntityRendererRegistry.register(WATERARM, WaterArmEntityRenderer::new);
        EntityRendererRegistry.register(WATERBLADE, WaterBladeEntityRenderer::new);
        EntityRendererRegistry.register(WATERBULLET, WaterBulletEntityRenderer::new);
        EntityRendererRegistry.register(WATERHEALING, WaterHealingEntityRenderer::new);
        EntityRendererRegistry.register(WATERTOWER, WaterTowerEntityRenderer::new);

        //FIRE
        EntityRendererRegistry.register(FIREBLOCK, FireBlockEntityRenderer::new);
        EntityRendererRegistry.register(FIREARC, FireArcEntityRenderer::new);
        EntityRendererRegistry.register(FIREBALL, FireBallEntityRenderer::new);
        EntityRendererRegistry.register(FIRESHIELD, FireShieldEntityRenderer::new);
        EntityRendererRegistry.register(FIREWISP, FireWispEntityRenderer::new);

        //EARTH
        EntityRendererRegistry.register(EARTHBLOCK, EarthBlockEntityRenderer::new);

        //AIR
        EntityRendererRegistry.register(AIRSHIELD, AirShieldEntityRenderer::new);
        EntityRendererRegistry.register(AIRTORNADO, AirTornadoEntityRenderer::new);
        EntityRendererRegistry.register(AIRSTREAM, AirStreamEntityRenderer::new);
        EntityRendererRegistry.register(AIRBALL, AirBallEntityRenderer::new);
        EntityRendererRegistry.register(AIRBULLET, AirBulletEntityRenderer::new);
        EntityRendererRegistry.register(AIRSCOOTER, AirScooterEntityRenderer::new);

        //COMMON
        EntityRendererRegistry.register(DECOYPLAYER, (context) -> new DecoyPlayerEntityRenderer(context, true));
        EntityRendererRegistry.register(DIRTBOTTLEENTITY, DirtBottleEntityRenderer::new);
        EntityRendererRegistry.register(BOOMERANGENTITY, BoomerangEntityRenderer::new);
		EntityRendererRegistry.register(SKYBISON, SkyBisonEntityRenderer::new);
        
        //LIGHTNING
        EntityRendererRegistry.register(LIGHTNINGARC, LightningArcEntityRenderer::new);
        EntityRendererRegistry.register(VOLTARC, VoltArcEntityRenderer::new);

        //BLOOD
        EntityRendererRegistry.register(BLOODSHOT, BloodShotEntityRenderer::new);

		//METAL
		EntityRendererRegistry.register(METALCABLE, MetalCableEntityRenderer::new);
		EntityRendererRegistry.register(METALBIND, MetalBindEntityRenderer::new);
		EntityRendererRegistry.register(METALBULLET, MetalBulletEntityRenderer::new);
		EntityRendererRegistry.register(METALLANCE, MetalLanceRenderer::new);
	}

    private static void onClientJoin(ClientPlayNetworkHandler clientPlayNetworkHandler, PacketSender packetSender, Minecraft client) {
        String response;

        Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(Elementals.MODID);
        if (container.isPresent()) {
            Version modVersion = container.get().getMetadata().getVersion();
            response = modVersion.getFriendlyString();
        } else {
            response = "No ModContainer found";
        }

        ClientPlayNetworking.send(new SyncVersionPayload(response));
    }
}
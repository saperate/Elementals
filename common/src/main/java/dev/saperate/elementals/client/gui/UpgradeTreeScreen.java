package dev.saperate.elementals.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import commonnetwork.api.Network;
import dev.saperate.elementals.client.data.ClientBender;
import dev.saperate.elementals.client.keys.KeyInput;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.Upgrade;
import dev.saperate.elementals.network.packets.C2S.BuyUpgradePacket;
import dev.saperate.elementals.network.packets.C2S.ToggleUpgradePacket;
import dev.saperate.elementals.network.packets.common.SyncLevelPacket;
import dev.saperate.elementals.network.packets.common.SyncUpgradeListPacket;
import dev.saperate.elementals.utils.SapsUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static dev.saperate.elementals.Elementals.MODID;

public class UpgradeTreeScreen extends Screen {
    private ClientBender bender;
    private final Screen parent;
    private int tileSize = 32, pathSize = 2;
    private int spacing = tileSize * 2;
    private double originX = 0, originY = 0, textureSize = tileSize * 1.25f;

    //we only need to store the center point since all the upgrade buttons are of equal sizes
    public HashMap<Upgrade, Point> upgradeButtons = new HashMap<>();
    public Upgrade hoveredUpgrade = null;
    public int keybindID = 0;

    private int lineColor = 0xFFa0e8e6, secondaryColor = 0xFF002E2C, tertiaryColor = 0xFFffef00;


    public UpgradeTreeScreen(@Nullable Screen parent) {
        super(Component.literal("why are you looking at this? :)"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        originX = (double) width / 2 - (double) tileSize / 2;
        originY = (double) height / 2 - (double) tileSize / 2;
        
        //We pass in dummy values, since the server doesn't read them before sending what we need back
        Network.getNetworkHandler().sendToServer(new SyncUpgradeListPacket(new CompoundTag()));
        Network.getNetworkHandler().sendToServer(new SyncLevelPacket(0,0));
        
        bender = ClientBender.get();
        bender.getElement().root.calculateXPos();
        lineColor = bender.getElement().getColor();
        secondaryColor = bender.getElement().getSecondaryColor();
        tertiaryColor = bender.getElement().getTertiaryColor();
    }
    

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float partialTick) {
        super.render(context, mouseX, mouseY, partialTick);
        int oX = Mth.floor(originX);
        int oY = Mth.floor(originY);

        Element element = bender.getElement();
        String[] backgroundTextures = element.getBackgroundTextures();
        TextureManager texManager = Minecraft.getInstance().getTextureManager();
        for (String texName : backgroundTextures){
            ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(MODID,"textures/gui/backgrounds/" + element.name.toLowerCase(Locale.ROOT) + "/" + texName);
            context.blit(resourceLocation, 0, 0, -6, -oX, -oY, width, height, 16, 16);
        }
        if(element.getOverlayTexture() != null){
            context.blit(element.getOverlayTexture(), 0, 0, -4, 0, 0, width, height, context.guiWidth() , context.guiHeight());
        }

        Upgrade root = element.root;
        int len = root.children.length;
        int halfSize = tileSize / 2;
        if (len >= 1) {
            context.fill((oX + halfSize - pathSize) - 4, oY + tileSize - 2,
                    oX + root.children[0].mod + halfSize + pathSize + 4, oY + spacing + 2,
                    tertiaryColor
            );
            context.fill((oX + halfSize - pathSize) - 2, oY + tileSize - 2,
                    oX + root.children[0].mod + halfSize + pathSize + 2, oY + spacing + 2,
                    secondaryColor
            );
            context.fill(oX + halfSize - pathSize, oY + tileSize - 2,
                    oX + root.children[0].mod + halfSize + pathSize, oY + spacing,
                    lineColor
            );

            drawTree(root.children[0], context, oX + root.children[0].mod, oY + spacing, 1);
        }
        if (len >= 2) {
            context.fill(oX + 4, oY + halfSize - pathSize - 4,
                    oX - spacing + 2, oY + halfSize + pathSize + 4,
                    tertiaryColor
            );
            context.fill(oX + 4, oY + halfSize - pathSize - 2,
                    oX - spacing + 2, oY + halfSize + pathSize + 2,
                    secondaryColor
            );
            context.fill(oX + 4, oY + halfSize - pathSize,
                    oX - spacing + 2, oY + halfSize + pathSize,
                    lineColor
            );
            drawMirroredTree(root.children[1], context, oX - spacing, oY + root.children[1].mod, -1);
        }
        if (len >= 3) {
            context.fill(oX + tileSize - 4, oY + halfSize - pathSize - 4,
                    oX + tileSize + spacing - 2, oY + halfSize + pathSize + 4,
                    tertiaryColor
            );
            context.fill(oX + tileSize - 4, oY + halfSize - pathSize - 2,
                    oX + tileSize + spacing - 2, oY + halfSize + pathSize + 2,
                    secondaryColor
            );
            context.fill(oX + tileSize - 4, oY + halfSize - pathSize,
                    oX + tileSize + spacing - 2, oY + halfSize + pathSize,
                    lineColor
            );
            drawMirroredTree(root.children[2], context, oX + spacing, oY + root.children[2].mod, 1);
        }
        if (len == 4) {
            context.fill(oX + halfSize - pathSize - 4, oY + 2,
                    oX + root.children[0].mod + halfSize + pathSize + 4, oY - spacing + 1,
                    tertiaryColor
            );
            context.fill(oX + halfSize - pathSize - 2, oY + 2,
                    oX + root.children[0].mod + halfSize + pathSize + 2, oY - spacing + 1,
                    secondaryColor
            );
            context.fill(oX + halfSize - pathSize, oY + 2,
                    oX + root.children[0].mod + halfSize + pathSize, oY - spacing,
                    lineColor
            );
            drawTree(root.children[3], context, oX + root.children[1].mod, oY - spacing, -1);
        }

        context.blit(ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/" + ClientBender.get().getElement().getName().toLowerCase(Locale.ROOT) + "_upgrade_button.png"),
                oX - 2, oY - 2, 0, 0, tileSize + 4, tileSize + 4, tileSize + 4, tileSize + 4);
        ResourceLocation symbolID = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/symbol/" + ClientBender.get().getElement().getName().toLowerCase(Locale.ROOT) + ".png");
        context.blit(symbolID,
                oX - 2, oY - 2, 0, 0, tileSize + 4, tileSize + 4, tileSize + 4, tileSize + 4);
        renderExperienceBar(context);
        renderTitle(context, mouseX, mouseY, partialTick);
    }

    public Upgrade mouseOnUpgrade(double mouseX, double mouseY) {
        Point mousePos = new Point((int) mouseX, (int) mouseY);
        for (Map.Entry<Upgrade, Point> entry : upgradeButtons.entrySet()) {
            if (entry.getValue().distanceSq(mousePos) <= tileSize * tileSize) {
                if (hoveredUpgrade != entry.getKey()) {
                    Upgrade head = entry.getKey().getHead();
                    Upgrade[] root = ClientBender.get().getElement().root.children;
                    for (int i = 0; i < root.length; i++) {
                        if (root[i].equals(head)) {
                            keybindID = i;
                        }
                    }
                }
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Upgrade upgrade = mouseOnUpgrade(mouseX, mouseY);
        if (upgrade != null) {
            if(PlayerData.canBuyUpgrade(bender.upgrades, bender.getElement(), upgrade.name, new AtomicInteger(ClientBender.get().level))) {
                Network.getNetworkHandler().sendToServer(new BuyUpgradePacket(upgrade.name));
            }else{
                Network.getNetworkHandler().sendToServer(new ToggleUpgradePacket(upgrade.name));
            }
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        originX += deltaX;
        originY += deltaY;
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
        hoveredUpgrade = mouseOnUpgrade(mouseX, mouseY);
    }

    public void renderTitle(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        String upgradeName = hoveredUpgrade == null ? "" : hoveredUpgrade.name;

        //graphics.drawCenteredString(this.textRenderer, bender.element.name, this.width / 2, 8, 0xFFFFFFFF);

        //Use this when you wanna know what the upgrade name is
        graphics.drawCenteredString(this.font, upgradeName, this.width / 2, 24, 0xFFc4c4c4);

        if (!upgradeName.isEmpty()) {
            ArrayList<Component> tooltip = new ArrayList<>();
            SapsUtils.addTranslatable(tooltip, "upgrade.elementals." + upgradeName);
            SapsUtils.addTranslatableAutomaticLineBreaks(tooltip, "upgrade.elementals." + upgradeName + ".description", 5);



            SapsUtils.addTranslatableAutomaticLineBreaks(tooltip, "upgrade.elementals." + upgradeName + ".use", 6, getKeyName());

            if (hoveredUpgrade.price > 0) {
                SapsUtils.addTranslatableAutomaticLineBreaks(tooltip, "upgrade.elementals.price", 6, hoveredUpgrade.price);
            }
            if (hoveredUpgrade.parent.exclusive) {
                SapsUtils.addTranslatableAutomaticLineBreaks(tooltip, "upgrade.elementals.exclusive", 5);
            }
            
            graphics.renderTooltip(this.font, tooltip, Optional.empty(), mouseX, mouseY);
        }
    }

    /**
     * Slightly modified vanilla code to render bending levels
     * @author Mojang
     */
    public void renderExperienceBar(GuiGraphics context) {
        ResourceLocation ICONS = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/icons.png");

        int scaledWidth = context.guiWidth();

        int x = scaledWidth / 2 - 91;
        int y = 6;

        int level = ClientBender.get().level;
        int progressWidth = (int) (ClientBender.get().xp / Bender.getMaxXp(level) * 183.0F);

        context.blit(ICONS, x, y, 0, 0.0f, 64.0f, 182, 5, 256, 256);
        if (progressWidth > 0) {
            context.blit(ICONS, x, y, 0, 69, progressWidth, 5);
        }

        if (level > 0) {
            String title = "" + level;
            progressWidth = (scaledWidth - font.width(title)) / 2;
            y = 5;

            context.drawString(font, title, progressWidth + 1, y, 0, false);
            context.drawString(font, title, progressWidth - 1, y, 0, false);
            context.drawString(font, title, progressWidth, y + 1, 0, false);
            context.drawString(font, title, progressWidth, y - 1, 0, false);
            context.drawString(font, title, progressWidth, y, 0x47e2dd, false);
        }

    }

    public void drawUpgradeButton(int x1, int y1, GuiGraphics context, Upgrade upgrade) {
        String icon = Component.translatable("upgrade.elementals." + upgrade.name + ".icon").getString();
        float color = bender.upgrades.containsKey(upgrade) ? 1 : 0.25f;
        boolean hasIcon = !icon.equals("upgrade.elementals." + upgrade.name + ".icon");

        drawTexture(context, ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/" + ClientBender.get().getElement().getName().toLowerCase() + "_" + (hasIcon ? "" : "plain_") + "upgrade_button.png"),
                x1, y1, (int) textureSize, (int) textureSize, (float) 0, (float) 0, (int) textureSize, color, color, color, 1
                , 0);

        if (hasIcon) {

            drawTexture(context, ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/" + icon + "_icon.png"),
                    x1, y1, (int) textureSize, (int) textureSize, (float) 0, (float) 0, (int) textureSize, color, color, color, 1
                    , 0);

        } else if (upgrade.name.contains("IV")) {
            drawTexture(context, ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/iv_icon.png"),
                    x1, y1, (int) textureSize, (int) textureSize, (float) 0, (float) 0, (int) textureSize, color, color, color, 1, 0);
        } else if (upgrade.name.contains("III")) {
            drawTexture(context, ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/iii_icon.png"),
                    x1, y1, (int) textureSize, (int) textureSize, (float) 0, (float) 0, (int) textureSize, color, color, color, 1
                    , 0);
        } else if (upgrade.name.contains("II")) {
            drawTexture(context, ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/ii_icon.png"),
                    x1, y1, (int) textureSize, (int) textureSize, (float) 0, (float) 0, (int) textureSize, color, color, color, 1
                    , 0);
        } else if (upgrade.name.contains("I")) {
            drawTexture(context, ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/i_icon.png"),
                    x1, y1, (int) textureSize, (int) textureSize, (float) 0, (float) 0, (int) textureSize, color, color, color, 1
                    , 0);
        }


        upgradeButtons.put(upgrade, new Point(x1 + tileSize / 2, y1 + tileSize / 2));
    }


    //BEWARE: beyond this point is shitty code that might be hard to understand, read at your own peril traveller
    public void drawTree(Upgrade parent, GuiGraphics context, int oX, int oY, int mult) {
        //Draw the node
        if (parent.children.length == 0) {
            drawUpgradeButton((int) ((oX + tileSize / 2) - textureSize / 2), oY - pathSize * 2, context, parent);
            return;
        }

        //Path directly down that stops halfway to the next tile
        context.fill(
                oX + tileSize / 2 - pathSize, oY + (mult > 0 ? tileSize : 0),
                oX + tileSize / 2 + pathSize, oY + (tileSize / 2 * mult) + (mult > 0 ? tileSize : 0),
                -1, lineColor
        );
        context.fill(
                oX + tileSize / 2 - pathSize - 2, oY + (mult > 0 ? tileSize : 0) - 2,
                oX + tileSize / 2 + pathSize + 2, oY + (tileSize / 2 * mult) + (mult > 0 ? tileSize : 0) + 2,
                -2, secondaryColor
        );
        context.fill(
                oX + tileSize / 2 - pathSize - 4, oY + (mult > 0 ? tileSize : 0) - 4,
                oX + tileSize / 2 + pathSize + 4, oY + (tileSize / 2 * mult) + (mult > 0 ? tileSize : 0) + 4,
                -3, tertiaryColor
        );

        int firstChildPosX = 0;
        int lastChildPosX = 0;

        for (int i = 0; i < parent.children.length; i++) {
            Upgrade child = parent.children[i];

            int pX = oX + Math.round(child.getPositionX() * spacing);

            if (i == 0) {
                firstChildPosX = pX;
            }
            if (i == parent.children.length - 1) {
                lastChildPosX = pX;
            }

            //Path directly up that stop halfway through
            context.fill(
                    pX + tileSize / 2 - pathSize, oY + (mult > 0 ? spacing : -tileSize / 2),
                    pX + tileSize - tileSize / 2 + pathSize, oY + (mult > 0 ? spacing - tileSize / 2 : -tileSize) - pathSize / 2,
                    -1, lineColor
            );
            context.fill(
                    pX + tileSize / 2 - pathSize - 2, oY + (mult > 0 ? spacing : -tileSize / 2) - 2,
                    pX + tileSize - tileSize / 2 + pathSize + 2, oY + (mult > 0 ? spacing - tileSize / 2 : -tileSize) - pathSize / 2 + 2,
                    -2, secondaryColor
            );
            context.fill(
                    pX + tileSize / 2 - pathSize - 4, oY + (mult > 0 ? spacing : -tileSize / 2) - 2,
                    pX + tileSize - tileSize / 2 + pathSize + 4, oY + (mult > 0 ? spacing - tileSize / 2 : -tileSize) - pathSize / 2 + 2,
                    -3, tertiaryColor
            );

            drawTree(child, context, pX, oY + (spacing * mult), mult);
        }

        //Path is on the halfway point of this depth and the previous depth, and it spans the whole width of the path
        context.fill(
                firstChildPosX + tileSize / 2 - pathSize, oY + (mult > 0 ? spacing : 0) - tileSize / 2 - pathSize,
                lastChildPosX + tileSize - tileSize / 2 + pathSize, oY + (mult > 0 ? spacing : 0) - tileSize / 2 + pathSize,
                -1, lineColor
        );
        context.fill(
                firstChildPosX + tileSize / 2 - pathSize - 2, oY + (mult > 0 ? spacing : 0) - tileSize / 2 - pathSize - 2,
                lastChildPosX + tileSize - tileSize / 2 + pathSize + 2, oY + (mult > 0 ? spacing : 0) - tileSize / 2 + pathSize + 2,
                -2, secondaryColor
        );
        context.fill(
                firstChildPosX + tileSize / 2 - pathSize - 4, oY + (mult > 0 ? spacing : 0) - tileSize / 2 - pathSize - 4,
                lastChildPosX + tileSize - tileSize / 2 + pathSize + 4, oY + (mult > 0 ? spacing : 0) - tileSize / 2 + pathSize + 4,
                -3, tertiaryColor
        );

        drawUpgradeButton((int) ((oX + tileSize / 2) - textureSize / 2), oY - pathSize * 2, context, parent);
    }


    public void drawMirroredTree(Upgrade parent, GuiGraphics context, int oX, int oY, int mult) {
        //Draw the node
        if (parent.children.length == 0) {
            drawUpgradeButton(oX - pathSize * 2, (int) ((oY + tileSize / 2) - textureSize / 2), context, parent);
            return;
        }


        //Path directly down that stops halfway to the next tile
        context.fill(
                oX + (mult > 0 ? tileSize - 1 : 0), oY + tileSize / 2 - pathSize,
                oX + (tileSize / 2 * mult) + (mult > 0 ? tileSize : 0), oY + tileSize / 2 + pathSize,
                -1, lineColor
        );
        context.fill(
                oX + (mult > 0 ? tileSize - 1 : 0), oY + tileSize / 2 - pathSize - 2,
                oX + (tileSize / 2 * mult) + (mult > 0 ? tileSize : 0) + 2, oY + tileSize / 2 + pathSize + 2,
                -2, secondaryColor
        );

        context.fill(
                oX + (mult > 0 ? tileSize - 1 : 0), oY + tileSize / 2 - pathSize - 4,
                oX + (tileSize / 2 * mult) + (mult > 0 ? tileSize : 0) + 4, oY + tileSize / 2 + pathSize + 4,
                -3, tertiaryColor
        );


        int firstChildPosY = 0;
        int lastChildPosY = 0;

        for (int i = 0; i < parent.children.length; i++) {
            Upgrade child = parent.children[i];

            int pY = oY + Math.round(child.getPositionX() * spacing);

            if (i == 0) {
                firstChildPosY = pY;
            }
            if (i == parent.children.length - 1) {
                lastChildPosY = pY;
            }

            //Path directly up that stop halfway through
            context.fill(
                    oX + (mult > 0 ? spacing : -tileSize / 2), pY + tileSize / 2 - pathSize,
                    oX + (mult > 0 ? spacing - tileSize / 2 : -tileSize - 1) - pathSize / 2, pY + tileSize - tileSize / 2 + pathSize,
                    -1, lineColor
            );
            context.fill(
                    oX + (mult > 0 ? spacing : -tileSize / 2 ) + 2, pY + tileSize / 2 - pathSize - 2,
                    oX + (mult > 0 ? spacing - tileSize / 2 : -tileSize - 2) - pathSize / 2, pY + tileSize - tileSize / 2 + pathSize + 2,
                    -2, secondaryColor
            );

            context.fill(
                    oX + (mult > 0 ? spacing : -tileSize / 2 ) + 4, pY + tileSize / 2 - pathSize - 4,
                    oX + (mult > 0 ? spacing - tileSize / 2 : -tileSize - 4) - pathSize / 2, pY + tileSize - tileSize / 2 + pathSize + 4,
                    -3, tertiaryColor
            );

            drawMirroredTree(child, context, oX + (spacing * mult), pY, mult);
        }

        //Path is on the halfway point of this depth and the previous depth, and it spans the whole width of the path
        context.fill(
                oX + (mult > 0 ? spacing : 0) - tileSize / 2 - pathSize, firstChildPosY + tileSize / 2 - pathSize,
                oX + (mult > 0 ? spacing : 0) - tileSize / 2 + pathSize, lastChildPosY + tileSize - tileSize / 2 + pathSize,
                -1, lineColor
        );
        context.fill(
                oX + (mult > 0 ? spacing : 0) - tileSize / 2 - pathSize - 2, firstChildPosY + tileSize / 2 - pathSize - 2,
                oX + (mult > 0 ? spacing : 0) - tileSize / 2 + pathSize + 2, lastChildPosY + tileSize - tileSize / 2 + pathSize + 2,
                -2, secondaryColor
        );
        context.fill(
                oX + (mult > 0 ? spacing : 0) - tileSize / 2 - pathSize - 4, firstChildPosY + tileSize / 2 - pathSize - 4,
                oX + (mult > 0 ? spacing : 0) - tileSize / 2 + pathSize + 4, lastChildPosY + tileSize - tileSize / 2 + pathSize + 4,
                -3, tertiaryColor
        );

        drawUpgradeButton(oX - pathSize * 2, (int) ((oY + tileSize / 2) - textureSize / 2), context, parent);
    }

    public void drawTexture(GuiGraphics context, ResourceLocation texture, int x, int y, int width, int height, float u1, float v1, int textureSize, float red, float green, float blue, float alpha, float z) {
        float u2 = (u1 + width) / textureSize;
        float v2 = (v1 + height) / textureSize;

        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.enableBlend();
        Matrix4f matrix4f = context.pose().last().pose();
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferBuilder.addVertex(matrix4f, (float) x, (float) y, (float) z).setColor(red, green, blue, alpha).setUv(u1, v1);
        bufferBuilder.addVertex(matrix4f, (float) x, (float) y + height, (float) z).setColor(red, green, blue, alpha).setUv(u1, v2);
        bufferBuilder.addVertex(matrix4f, (float) x + width, (float) y + height, (float) z).setColor(red, green, blue, alpha).setUv(u2, v2);
        bufferBuilder.addVertex(matrix4f, (float) x + width, (float) y, (float) z).setColor(red, green, blue, alpha).setUv(u2, v1);
        BufferUploader.drawWithShader(bufferBuilder.build());
        RenderSystem.disableBlend();
    }

    private String getKeyName(){
        String key = KeyInput.bindings.get(keybindID).getName();
        String raw = Component.translatable(key).getString();

        //if we were able to find a translation
        if(!raw.equals(key)){
            return raw;
        }

        if(raw.contains("keyboard")){
            return raw.split("\\.")[2].toUpperCase(Locale.ROOT);
        } else if (raw.contains("mouse")) {
            return "Mouse " + raw.split("\\.")[2];
        }else {
            return "UNKNOWN";
        }
    }
}

package dev.saperate.elementals.gui;


import com.mojang.blaze3d.systems.RenderSystem;
import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.ClientBender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.Upgrade;
import dev.saperate.elementals.keys.KeyInput;
import dev.saperate.elementals.keys.abilities.KeyAbility1;
import dev.saperate.elementals.packets.SyncLevelS2CPacket;
import dev.saperate.elementals.packets.SyncUpgradeListS2CPacket;
import dev.saperate.elementals.utils.SapsUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static dev.saperate.elementals.Elementals.MODID;
import static dev.saperate.elementals.network.ModMessages.BUY_UPGRADE_PACKET_ID;
import static dev.saperate.elementals.network.ModMessages.GET_UPGRADE_LIST_PACKET_ID;

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

    private int lineColor = 0xFFa0e8e6, outlineColor = 0xFF002E2C;


    public UpgradeTreeScreen(@Nullable Screen parent) {
        super(Text.literal("why are you looking at this? :)"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        originX = width / 2 - tileSize / 2;
        originY = height / 2 - tileSize / 2;
        SyncUpgradeListS2CPacket.send();
        SyncLevelS2CPacket.send();
        bender = ClientBender.get();
        bender.getElement().root.calculateXPos();
        lineColor = bender.getElement().getColor();
        outlineColor = bender.getElement().getAccentColor();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        int oX = MathHelper.floor(originX);
        int oY = MathHelper.floor(originY);
        context.drawTexture(CreateWorldScreen.LIGHT_DIRT_BACKGROUND_TEXTURE, 0, 0, -5, -oX, -oY, width, height, 32, 32);

        context.drawTexture(new Identifier(MODID, "textures/gui/" + ClientBender.get().getElement().getName().toLowerCase() + "_upgrade_button.png"),
                oX - 2, oY - 2, 0, 0, tileSize + 4, tileSize + 4, tileSize + 4, tileSize + 4);


        Upgrade root = bender.getElement().root;

        int len = root.children.length;
        int halfSize = tileSize / 2;
        if (len >= 1) {
            context.fill((oX + halfSize - pathSize) - 1, oY + tileSize - 2,
                    oX + root.children[0].mod + halfSize + pathSize + 1, oY + spacing + 2,
                    outlineColor
            );
            context.fill(oX + halfSize - pathSize, oY + tileSize - 2,
                    oX + root.children[0].mod + halfSize + pathSize, oY + spacing,
                    lineColor
            );

            drawTree(root.children[0], context, oX + root.children[0].mod, oY + spacing, 1);
        }
        if (len >= 2) {
            context.fill(oX + 4, oY + halfSize - pathSize - 1,
                    oX - spacing + 2, oY + halfSize + pathSize + 1,
                    outlineColor
            );
            context.fill(oX + 4, oY + halfSize - pathSize,
                    oX - spacing + 2, oY + halfSize + pathSize,
                    lineColor
            );
            drawMirroredTree(root.children[1], context, oX - spacing, oY + root.children[1].mod, -1);
        }
        if (len >= 3) {
            context.fill(oX + tileSize - 4, oY + halfSize - pathSize - 1,
                    oX + tileSize + spacing - 2, oY + halfSize + pathSize + 1,
                    outlineColor
            );
            context.fill(oX + tileSize - 4, oY + halfSize - pathSize,
                    oX + tileSize + spacing - 2, oY + halfSize + pathSize,
                    lineColor
            );
            drawMirroredTree(root.children[2], context, oX + spacing, oY + root.children[2].mod, 1);
        }
        if (len == 4) {
            context.fill(oX + halfSize - pathSize - 1, oY + 2,
                    oX + root.children[0].mod + halfSize + pathSize + 1, oY - spacing + 1,
                    outlineColor
            );
            context.fill(oX + halfSize - pathSize, oY + 2,
                    oX + root.children[0].mod + halfSize + pathSize, oY - spacing,
                    lineColor
            );
            drawTree(root.children[3], context, oX + root.children[1].mod, oY - spacing, -1);
        }

        renderExperienceBar(context);
        renderTitle(context, mouseX, mouseY, delta);
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
        if (button == 0) {
            Upgrade upgrade = mouseOnUpgrade(mouseX, mouseY);
            if (upgrade != null && PlayerData.canBuyUpgrade(bender.upgrades, bender.getElement(), upgrade.name, new AtomicInteger(ClientBender.get().level))) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeString(upgrade.name);
                ClientPlayNetworking.send(BUY_UPGRADE_PACKET_ID, buf);
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

    public void renderTitle(DrawContext graphics, int mouseX, int mouseY, float delta) {
        String upgradeName = hoveredUpgrade == null ? "" : hoveredUpgrade.name;

        //graphics.drawCenteredTextWithShadow(this.textRenderer, bender.element.name, this.width / 2, 8, 0xFFFFFFFF);

        //Use this when you wanna know what the upgrade name is
        //graphics.drawCenteredTextWithShadow(this.textRenderer, upgradeName, this.width / 2, 24, 0xFFc4c4c4);

        if (!upgradeName.isEmpty()) {
            ArrayList<Text> tooltip = new ArrayList<>();
            SapsUtils.addTranslatable(tooltip, "upgrade.elementals." + upgradeName);
            SapsUtils.addTranslatableAutomaticLineBreaks(tooltip, "upgrade.elementals." + upgradeName + ".description", 5);



            SapsUtils.addTranslatableAutomaticLineBreaks(tooltip, "upgrade.elementals." + upgradeName + ".use", 6, getKeyName());

            if (hoveredUpgrade.price > 0) {
                SapsUtils.addTranslatableAutomaticLineBreaks(tooltip, "upgrade.elementals.price", 6, hoveredUpgrade.price);
            }
            if (hoveredUpgrade.parent.exclusive) {
                SapsUtils.addTranslatableAutomaticLineBreaks(tooltip, "upgrade.elementals.exclusive", 5);
            }

            graphics.drawTooltip(this.textRenderer, tooltip, mouseX, mouseY);
        }
    }

    /**
     * Slightly modified vanilla code to render bending levels
     * @author Mojang
     */
    public void renderExperienceBar(DrawContext context) {
        Identifier ICONS = new Identifier(MODID, "textures/gui/icons.png");

        int scaledWidth = context.getScaledWindowWidth();

        int x = scaledWidth / 2 - 91;
        int y = 6;

        int level = ClientBender.get().level;
        int progressWidth = (int) (ClientBender.get().xp / Bender.getMaxXp(level) * 183.0F);

        context.drawTexture(ICONS, x, y, 0, 0.0f, 64.0f, 182, 5, 256, 256);
        if (progressWidth > 0) {
            context.drawTexture(ICONS, x, y, 0, 69, progressWidth, 5);
        }

        if (level > 0) {
            String title = "" + level;
            progressWidth = (scaledWidth - textRenderer.getWidth(title)) / 2;
            y = 5;

            context.drawText(textRenderer, title, progressWidth + 1, y, 0, false);
            context.drawText(textRenderer, title, progressWidth - 1, y, 0, false);
            context.drawText(textRenderer, title, progressWidth, y + 1, 0, false);
            context.drawText(textRenderer, title, progressWidth, y - 1, 0, false);
            context.drawText(textRenderer, title, progressWidth, y, 0x47e2dd, false);
        }

    }

    public void drawUpgradeButton(int x1, int y1, DrawContext context, Upgrade upgrade) {
        String icon = Text.translatable("upgrade.elementals." + upgrade.name + ".icon").getString();
        float color = bender.upgrades.containsKey(upgrade) ? 1 : 0.25f;
        boolean isUpgrade = !icon.equals("upgrade.elementals." + upgrade.name + ".icon");

        drawTexturedQuad(context, new Identifier(MODID, "textures/gui/" + ClientBender.get().getElement().getName().toLowerCase() + "_" + (isUpgrade ? "" : "plain_") + "upgrade_button.png"),
                x1, y1, (int) textureSize, (int) textureSize, (float) 0, (float) 0, (int) textureSize, color, color, color, 1
                , 0);

        if (isUpgrade) {

            drawTexturedQuad(context, new Identifier(MODID, "textures/gui/" + icon + "_icon.png"),
                    x1, y1, (int) textureSize, (int) textureSize, (float) 0, (float) 0, (int) textureSize, color, color, color, 1
                    , 0);

        }


        upgradeButtons.put(upgrade, new Point(x1 + tileSize / 2, y1 + tileSize / 2));
    }


    //BEWARE: beyond this point is shitty code that might be hard to understand, read at your own peril traveller
    public void drawTree(Upgrade parent, DrawContext context, int oX, int oY, int mult) {
        //Draw the node
        drawUpgradeButton((int) ((oX + tileSize / 2) - textureSize / 2), oY - pathSize * 2, context, parent);
        if (parent.children.length == 0) {
            return;
        }

        //Path directly down that stops halfway to the next tile
        context.fill(
                oX + tileSize / 2 - pathSize, oY + (mult > 0 ? tileSize : 0),
                oX + tileSize / 2 + pathSize, oY + (tileSize / 2 * mult) + (mult > 0 ? tileSize : 0),
                -1, lineColor
        );
        context.fill(
                oX + tileSize / 2 - pathSize - 1, oY + (mult > 0 ? tileSize : 0) - 1,
                oX + tileSize / 2 + pathSize + 1, oY + (tileSize / 2 * mult) + (mult > 0 ? tileSize : 0) + 1,
                -2, outlineColor
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
                    pX + tileSize / 2 - pathSize - 1, oY + (mult > 0 ? spacing : -tileSize / 2) - 1,
                    pX + tileSize - tileSize / 2 + pathSize + 1, oY + (mult > 0 ? spacing - tileSize / 2 : -tileSize) - pathSize / 2 + 1,
                    -2, outlineColor
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
                firstChildPosX + tileSize / 2 - pathSize - 1, oY + (mult > 0 ? spacing : 0) - tileSize / 2 - pathSize - 1,
                lastChildPosX + tileSize - tileSize / 2 + pathSize + 1, oY + (mult > 0 ? spacing : 0) - tileSize / 2 + pathSize + 1,
                -2, outlineColor
        );

    }


    public void drawMirroredTree(Upgrade parent, DrawContext context, int oX, int oY, int mult) {
        //Draw the node
        drawUpgradeButton(oX - pathSize * 2, (int) ((oY + tileSize / 2) - textureSize / 2), context, parent);
        if (parent.children.length == 0) {
            return;
        }


        //Path directly down that stops halfway to the next tile
        context.fill(
                oX + (mult > 0 ? tileSize : 0), oY + tileSize / 2 - pathSize,
                oX + (tileSize / 2 * mult) + (mult > 0 ? tileSize : 0), oY + tileSize / 2 + pathSize,
                -1, lineColor
        );
        context.fill(
                oX + (mult > 0 ? tileSize : 0), oY + tileSize / 2 - pathSize - 1,
                oX + (tileSize / 2 * mult) + (mult > 0 ? tileSize : 0) + 1, oY + tileSize / 2 + pathSize + 1,
                -2, outlineColor
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
                    oX + (mult > 0 ? spacing - tileSize / 2 : -tileSize) - pathSize / 2, pY + tileSize - tileSize / 2 + pathSize,
                    -1, lineColor
            );
            context.fill(
                    oX + (mult > 0 ? spacing : -tileSize / 2) + 2, pY + tileSize / 2 - pathSize - 1,
                    oX + (mult > 0 ? spacing - tileSize / 2 : -tileSize) - pathSize / 2, pY + tileSize - tileSize / 2 + pathSize + 1,
                    -2, outlineColor
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
                oX + (mult > 0 ? spacing : 0) - tileSize / 2 - pathSize - 1, firstChildPosY + tileSize / 2 - pathSize - 1,
                oX + (mult > 0 ? spacing : 0) - tileSize / 2 + pathSize + 1, lastChildPosY + tileSize - tileSize / 2 + pathSize + 1,
                -2, outlineColor
        );

    }

    public void drawTexturedQuad(DrawContext context, Identifier texture, int x, int y, int width, int height, float u1, float v1, int textureSize, float red, float green, float blue, float alpha, float z) {
        float u2 = (u1 + width) / textureSize;
        float v2 = (v1 + height) / textureSize;

        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
        RenderSystem.enableBlend();
        Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
        bufferBuilder.vertex(matrix4f, (float) x, (float) y, (float) z).color(red, green, blue, alpha).texture(u1, v1).next();
        bufferBuilder.vertex(matrix4f, (float) x, (float) y + height, (float) z).color(red, green, blue, alpha).texture(u1, v2).next();
        bufferBuilder.vertex(matrix4f, (float) x + width, (float) y + height, (float) z).color(red, green, blue, alpha).texture(u2, v2).next();
        bufferBuilder.vertex(matrix4f, (float) x + width, (float) y, (float) z).color(red, green, blue, alpha).texture(u2, v1).next();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();
    }

    private String getKeyName(){
        String key = KeyInput.bindings.get(keybindID).getBoundKeyTranslationKey();
        String raw = Text.translatable(key).getString();

        //if we were able to find a translation
        if(!raw.equals(key)){
            return raw;
        }

        if(raw.contains("keyboard")){
            return raw.split("\\.")[2].toUpperCase();
        } else if (raw.contains("mouse")) {
            return "Mouse " + raw.split("\\.")[2];
        }else {
            return "UNKNOWN";
        }
    }
}

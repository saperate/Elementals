package gui;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.background.Background;
import dev.lambdaurora.spruceui.background.SimpleColorBackground;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import dev.lambdaurora.spruceui.widget.SpruceIconButtonWidget;
import dev.lambdaurora.spruceui.widget.container.SpruceContainerWidget;
import dev.lambdaurora.spruceui.widget.text.SpruceTextAreaWidget;
import dev.saperate.elementals.data.ClientBender;
import dev.saperate.elementals.elements.Upgrade;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class UpgradeTreeScreen extends SpruceScreen {
    private ClientBender bender;
    private final Screen parent;
    private int tileSize = 32;
    private int spacing = tileSize * 2;
    private double originX = 0, originY = 0;

    public UpgradeTreeScreen(@Nullable Screen parent) {
        super(Text.literal("test"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        originX = width / 2 - tileSize / 2;
        originY = height / 2 - tileSize / 2;
        bender = ClientBender.get();
        bender.element.root.calculateXPos();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        int oX = MathHelper.floor(originX);
        int oY = MathHelper.floor(originY);

        context.drawTexture(CreateWorldScreen.LIGHT_DIRT_BACKGROUND_TEXTURE, 0, 0, -oX, -oY, width, height, 32, 32);
        context.fill(oX, oY, oX + tileSize, oY + tileSize, 0xFFFFFFFF);

        for (int i = 0; i < bender.element.root.children.length; i++) {
            if(i != 0){
                return;
            }
            Upgrade child = bender.element.root.children[i];
            drawTree(child, context, oX + child.mod, oY + spacing);
        }
    }

    public void drawTree(Upgrade parent, DrawContext context, int oX, int oY) {
        context.fill(oX, oY, oX + tileSize, oY + tileSize, 0xFFFFFFFF);
        if(parent.children.length == 0){
            return;
        }

        for (int i = 0; i < parent.children.length; i++) {
            Upgrade child = parent.children[i];


            drawTree(child, context, oX + Math.round((child.localX + child.mod )* spacing), oY + spacing);
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        originX += deltaX;
        originY += deltaY;
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public void renderTitle(DrawContext graphics, int mouseX, int mouseY, float delta) {
        graphics.drawCenteredTextWithShadow(this.textRenderer, bender.element.name, this.width / 2, 8, 0xFFFFFFFF);
    }
}

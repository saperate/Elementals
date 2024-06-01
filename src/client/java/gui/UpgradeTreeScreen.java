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
    private int tileSize = 32, pathSize = 2;
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

        Upgrade root = bender.element.root;

        int len = root.children.length;
        if(len >= 1){
            drawMirroredTree(root.children[0], context, oX - spacing, oY + root.children[0].mod, -1);
        }
        if(len >= 2){
            drawTree(root.children[1], context, oX + root.children[0].mod, oY + spacing, 1);
        }
        if(len >= 3){
            drawMirroredTree(root.children[2], context, oX + spacing, oY + root.children[2].mod, 1);
        }
        if(len == 4){
            drawTree(root.children[3], context, oX + root.children[1].mod, oY - spacing, -1);
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


    //BEWARE: beyond this point is shitty code that might be hard to understand, read at your own peril traveller
    public void drawTree(Upgrade parent, DrawContext context, int oX, int oY, int mult) {
        //Draw the node
        context.fill(oX, oY, oX + tileSize, oY + tileSize, 0xFFFFFFFF);
        if(parent.children.length == 0){
            return;
        }

        //Path directly down that stops halfway to the next tile
        context.fill(
                oX + tileSize / 2 - pathSize, oY + (mult > 0 ? tileSize : 0),
                oX + tileSize /2 + pathSize, oY + (tileSize /2 * mult) + (mult > 0 ? tileSize : 0),
                0xFF454545
        );

        int firstChildPosX = 0;
        int lastChildPosX = 0;

        for (int i = 0; i < parent.children.length; i++) {
            Upgrade child = parent.children[i];

            int pX = oX + Math.round(child.getPositionX() * spacing);

            if(i == 0){
                firstChildPosX = pX;
            }
            if (i == parent.children.length - 1) {
                lastChildPosX = pX;
            }

            //Path directly up that stop halfway through
            context.fill(
                    pX + tileSize /2 - pathSize, oY + (mult > 0 ? spacing : - tileSize/2),
                    pX + tileSize - tileSize/2 + pathSize, oY + (mult > 0 ? spacing - tileSize/2 : - tileSize) - pathSize/2,
                    0xFF454545
            );

            drawTree(child, context, pX, oY + (spacing * mult), mult);
        }

        //Path is on the halfway point of this depth and the previous depth, and it spans the whole width of the path
        context.fill(
                firstChildPosX + tileSize/2 - pathSize, oY + (mult > 0 ? spacing : 0) -tileSize/2  - pathSize,
                lastChildPosX + tileSize - tileSize/2 + pathSize, oY + (mult > 0 ? spacing: 0) -tileSize/2  + pathSize,
                0xFF454545
        );

    }


    public void drawMirroredTree(Upgrade parent, DrawContext context, int oX, int oY, int mult) {
        //Draw the node
        context.fill(oX, oY, oX + tileSize, oY + tileSize, 0xFFFFFFFF);
        if(parent.children.length == 0){
            return;
        }


        //Path directly down that stops halfway to the next tile
        context.fill(
                oX + (mult > 0 ? tileSize : 0), oY + tileSize / 2 - pathSize,
                oX + (tileSize /2 * mult) + (mult > 0 ? tileSize : 0), oY + tileSize /2 + pathSize,
                0xFF454545
        );



        int firstChildPosY = 0;
        int lastChildPosY = 0;

        for (int i = 0; i < parent.children.length; i++) {
            Upgrade child = parent.children[i];

            int pY = oY + Math.round(child.getPositionX() * spacing);

            if(i == 0){
                firstChildPosY = pY;
            }
            if (i == parent.children.length - 1) {
                lastChildPosY = pY;
            }

            //Path directly up that stop halfway through
            context.fill(
                    oX + (mult > 0 ? spacing : - tileSize/2) , pY + tileSize /2 - pathSize,
                    oX + (mult > 0 ? spacing - tileSize/2 : - tileSize) - pathSize/2, pY + tileSize - tileSize/2 + pathSize,
                    0xFF454545
            );

            drawMirroredTree(child, context, oX + (spacing * mult), pY, mult);
        }

        //Path is on the halfway point of this depth and the previous depth, and it spans the whole width of the path
        context.fill(
                oX + (mult > 0 ? spacing : 0) -tileSize/2  - pathSize, firstChildPosY + tileSize/2 - pathSize,
                oX + (mult > 0 ? spacing: 0) -tileSize/2  + pathSize, lastChildPosY + tileSize - tileSize/2 + pathSize,
                0xFF454545
        );

    }
}

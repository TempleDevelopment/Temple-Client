package xyz.templecheats.templeclient.features.gui.clickgui.csgo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.Panel;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.buttons.ModuleButton;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.Item;
import xyz.templecheats.templeclient.features.gui.font.CFont;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.features.module.modules.client.FontSettings;
import xyz.templecheats.templeclient.manager.ModuleManager;
import xyz.templecheats.templeclient.util.color.impl.RectBuilder;
import xyz.templecheats.templeclient.util.color.impl.RoundedTexture;
import xyz.templecheats.templeclient.util.math.Vec2d;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import static xyz.templecheats.templeclient.util.render.RenderUtil.drawHead;
import static xyz.templecheats.templeclient.util.render.StencilUtil.*;


public class CsgoGuiScreen extends GuiScreen {
    private static final ResourceLocation LOGO = new ResourceLocation("textures/icons/logo.png");

    private final ArrayList<Panel> navs = new ArrayList<>();
    private static CsgoGuiScreen instance;

    public boolean drag;

    public int x, y, oldX, oldY;
    public int oldAbsY, width, height, minHeight, absY;

    public CsgoGuiScreen() {
        this.x = 250;
        this.y = 70;

        this.absY = this.y;

        this.width = 528;
        this.height = 380;
        this.minHeight = 380;

        this.load();
    }
    
    public void load() {
        this.navs.forEach(nav -> nav.getItems().sort(Comparator.comparing(Item::getLabel)));

        this.getNavs().clear();
        
        int y = this.y + 30;
        for(final Module.Category category : Module.Category.values()) {
            this.getNavs().add(new Panel(category.name(), this.x + 40, y += 25, false) {
                @Override
                public void setupItems() {
                    ModuleManager.getModules().forEach(module -> {
                        if(module.getCategory() == category && !module.parent) {
                            this.addButton(new ModuleButton(module));
                        }
                    });
                }
            });
        }
        
    }

    @Override
    public void drawScreen(int unscaledMouseX, int unscaledMouseY, float partialTicks) {
        final int mouseX = (int) (unscaledMouseX / ClickGUI.INSTANCE.scale.doubleValue());
        final int mouseY = (int) (unscaledMouseY / ClickGUI.INSTANCE.scale.doubleValue());

        this.drag(mouseX, mouseY);

        GlStateManager.pushMatrix();
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(GL11.GL_LEQUAL);
        GlStateManager.scale(ClickGUI.INSTANCE.scale.doubleValue(), ClickGUI.INSTANCE.scale.doubleValue(), 1);

        final int scroll = Mouse.getDWheel();
        if (scroll > 0) {
            if(y < this.absY) {
                y += 10;
            }
        } else if (scroll < 0) {
            if(y+this.height > this.absY+this.minHeight) {
                y -= 10;
            }
        }

        if(this.y > this.absY) {
            this.y = this.absY;
        }
        if(this.y+this.height < this.absY+this.minHeight) {
            this.y = this.absY+this.minHeight - this.height;
        }

        new RectBuilder(new Vec2d(x, absY), new Vec2d(x + 118, absY + minHeight))
                .outlineColor(new Color(0xFF121210)).width(1.0).color(new Color(0xFF0C0C0C)).radius(5.0).draw();
        new RectBuilder(new Vec2d(x + 109, absY), new Vec2d(x + width, absY + minHeight))
                .color(new Color(0xFF131313)).outlineColor(new Color(0xFF282826)).width(1.5).radius(6.0).draw();
        new RectBuilder(new Vec2d(x + 109, absY), new Vec2d(x + width / 2, absY + minHeight))
                .colorH(new Color(0xFF131313), new Color(0x0131313, true)).outlineColorH(new Color(0xFF282826), new Color(0x0131313, true)).width(1.5).draw();
        new RectBuilder(new Vec2d(x, absY + minHeight - 40), new Vec2d(x + 110, absY + minHeight - 41))
                .color(Color.DARK_GRAY).draw();
        // kys
        drawHead(mc.player, new Vec2d(x + 6, absY + minHeight - 35), new Vec2d(x + 31, absY + minHeight - 10), 6f);

        CFont cFont = FontSettings.getFont(16);
        cFont.drawString(mc.player.getName(), x + 38, absY + minHeight - 32, -1, false);
        cFont.drawString("UID: " + mc.player.getEntityId(), x + 38, absY + minHeight - 22, Color.DARK_GRAY, false);

        GlStateManager.pushMatrix();
        Minecraft.getMinecraft().getTextureManager().bindTexture(LOGO);
        GlStateManager.color(1, 1, 1, 1);
        Gui.drawScaledCustomSizeModalRect(x + 38, absY + 10, 0, 0, 32, 32, 32, 32, 32, 32);
        GlStateManager.popMatrix();

        for (int i=0, y = this.absY + 80; i < this.navs.size(); i++, y+=30) {
            Panel nav = this.navs.get(i);
            nav.setPos(this.x, y);

            GlStateManager.pushMatrix();
            initStencilToWrite();
            new RectBuilder(new Vec2d(x, absY + 15), new Vec2d(x + 600, absY + minHeight - 10)).color(Color.WHITE).radius(10.0).draw();
            readStencilBuffer(1);

            nav.drawScreen(mouseX, mouseY, partialTicks);

            uninitStencilBuffer();
            GlStateManager.popMatrix();
        }

        this.navs.forEach(nav -> nav.drawScreenPost(mouseX, mouseY));
        GlStateManager.popMatrix();
    }
    
    @Override
    public void mouseClicked(int unscaledMouseX, int unscaledMouseY, int clickedButton) {
        final int mouseX = (int) (unscaledMouseX / ClickGUI.INSTANCE.scale.doubleValue());
        final int mouseY = (int) (unscaledMouseY / ClickGUI.INSTANCE.scale.doubleValue());

        if(clickedButton == 1 && this.isHovering(mouseX, mouseY)) {
            this.oldX = this.x - mouseX;
            this.oldY = this.y - mouseY;
            this.oldAbsY = this.absY - mouseY;

            this.drag = true;
        }

        this.navs.forEach(nav -> nav.mouseClicked(mouseX, mouseY, clickedButton));
    }

    @Override
    public void mouseReleased(int unscaledMouseX, int unscaledMouseY, int releaseButton) {
        final int mouseX = (int) (unscaledMouseX / ClickGUI.INSTANCE.scale.doubleValue());
        final int mouseY = (int) (unscaledMouseY / ClickGUI.INSTANCE.scale.doubleValue());

        if(releaseButton == 1) {
            this.drag = false;
        }

        this.navs.forEach(nav -> nav.mouseReleased(mouseX, mouseY, releaseButton));
    }
    
    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        try {
            for(Panel nav : navs) {
                nav.keyTyped(typedChar, keyCode);
            }
            super.keyTyped(typedChar, keyCode);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    public ArrayList<Panel> getNavs() {
        return this.navs;
    }

    private void drag(int mouseX, int mouseY) {
        if(!this.drag) {
            return;
        }
        this.x = this.oldX + mouseX;
        this.y = this.oldY + mouseY;
        this.absY = this.oldAbsY + mouseY;
    }

    private boolean isHovering(int mouseX, int mouseY) {
        return this.x+40 <= mouseX && mouseX <= this.x+this.width && this.y <= mouseY && mouseY <= this.y+this.height;
    }

    public static CsgoGuiScreen getInstance() {
        return instance == null ? (instance = new CsgoGuiScreen()) : instance;
    }
}
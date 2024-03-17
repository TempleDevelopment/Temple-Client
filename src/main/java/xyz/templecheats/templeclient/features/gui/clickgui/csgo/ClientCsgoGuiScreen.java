package xyz.templecheats.templeclient.features.gui.clickgui.csgo;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.ModuleButton;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.manager.ModuleManager;
import xyz.templecheats.templeclient.util.render.RenderUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class ClientCsgoGuiScreen extends GuiScreen {
    public static ModuleButton openedModule = null;
    private final ArrayList<NavLink> navs = new ArrayList<>();
    private static ClientCsgoGuiScreen instance;

    public boolean drag;

    public int x;
    public int y;

    public int oldX;
    public int oldY;
    public int oldAbsY;

    public int width;
    public int height;
    public int minHeight;

    public int absY;

    public ClientCsgoGuiScreen() {
        this.x = 200;
        this.y = 70;

        this.absY = this.y;

        this.width = 492;
        this.height = 400;
        this.minHeight = 400;

        this.load();
    }
    
    public void load() {
        this.navs.forEach(nav -> nav.getItems().sort(Comparator.comparing(Item::getLabel)));

        this.getNavs().clear();
        
        int y = this.y + 30;
        for(final Module.Category category : Module.Category.values()) {
            this.getNavs().add(new NavLink(category.name(), this.x+25, y+=25, false) {
                @Override
                public void setupItems() {
                    ModuleManager.getModules().forEach(module -> {
                        if(module.getCategory() == category) {
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
        GlStateManager.translate(0.0F, 0.0F, 1); // Translate along z-axis by depth
        GlStateManager.enableDepth(); // Enable depth testing
        GlStateManager.depthFunc(GL11.GL_LEQUAL); // Configure depth function        
        GlStateManager.scale(ClickGUI.INSTANCE.scale.doubleValue(), ClickGUI.INSTANCE.scale.doubleValue(), 1);

        final int scroll = Mouse.getDWheel();
        if (scroll > 0) {
            if(this.y < this.absY) {
                // Scroll up
                this.y += 10;
            }
        } else if (scroll < 0) {
            if(this.y+this.height > this.absY+this.minHeight) {
                // Scroll down
                this.y -= 10;
            }
        }

        // board
        RenderUtil.drawRect(this.x+48, this.y, this.x+this.width, this.y+this.height, 0xFF131313);

        if(this.y > this.absY) {
            this.y = this.absY;
        }
        if(this.y+this.height < this.absY+this.minHeight) {
            this.y = this.absY+this.minHeight - this.height;
        }

        // board around 3px 0xFF333331
        RenderUtil.drawOutBorderedRect(this.x, this.y, this.x+this.width, this.y+this.height, 4, 0xFF333331);
        // board around end

        // board around 1px 0xFF282826
        RenderUtil.drawOutBorderedRect(this.x, this.y, this.x+this.width, this.y+this.height, 1, 0xFF282826);
        // board around end

        // vertical split border start
        RenderUtil.drawRect(this.x, this.absY, this.x+48, this.absY+this.minHeight, 0xFF0C0C0C);
        RenderUtil.drawRect(this.x+48, this.absY, this.x+48+1, this.absY+this.minHeight, 0xFF282826);
        RenderUtil.drawInBorderedRect(this.x, this.y, this.x+48, this.absY+this.minHeight, 1, 0xFF121210);

        for (int i=0, y = this.absY + 8; i<this.navs.size(); i++, y+=48) {
            NavLink nav = this.navs.get(i);
            nav.setPos(this.x, y);
            nav.drawScreen(mouseX, mouseY, partialTicks);
        }

        this.navs.forEach(nav -> nav.drawScreenPost(mouseX, mouseY));

        // Draw the rainbow
        RenderUtil.drawHorizontalGradientRect(this.x, this.y, this.x+this.width/2, this.y+2, 0xFF4D7186, 0xFF6A4367);
        RenderUtil.drawHorizontalGradientRect(this.x+this.width/2, this.y, this.x+this.width, this.y+2, 0xFF6A4367, 0xFF747A3A);
        
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
            for(NavLink nav : navs) {
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
    
    public ArrayList<NavLink> getNavs() {
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

    public static ClientCsgoGuiScreen getInstance() {
        return instance == null ? (instance = new ClientCsgoGuiScreen()) : instance;
    }
}
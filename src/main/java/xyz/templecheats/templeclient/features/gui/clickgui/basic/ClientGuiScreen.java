package xyz.templecheats.templeclient.features.gui.clickgui.basic;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.Panel;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.Item;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.util.render.RenderUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class ClientGuiScreen extends GuiScreen {
    private final ArrayList<Panel> panels = new ArrayList<>();

    public ClientGuiScreen() {
        this.load();
    }

    public void load() {
        this.panels.forEach(panel -> panel.getItems().sort(Comparator.comparing(Item::getLabel)));
    }

    @Override
    public void drawScreen(int unscaledMouseX, int unscaledMouseY, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        RenderUtil.drawGradientRect(0.0F, 0.0F, mc.displayWidth, mc.displayHeight, 536870912, -1879048192);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.scale(this.getScale(), this.getScale(), 0);
        final int mouseX = (int) (unscaledMouseX / this.getScale());
        final int mouseY = (int) (unscaledMouseY / this.getScale());
        final int scroll = Mouse.getDWheel();
        this.panels.forEach(panel -> {
            if(scroll < 0) {
                panel.setY(panel.getY() - ClickGUI.INSTANCE.scrollSpeed.intValue());
            } else if(scroll > 0) {
                panel.setY(panel.getY() + ClickGUI.INSTANCE.scrollSpeed.intValue());
            }
            panel.drawScreen(mouseX, mouseY, partialTicks);
        });
        this.panels.forEach(panel -> panel.drawScreenPost(mouseX, mouseY));
        GlStateManager.popMatrix();
    }

    @Override
    public void mouseClicked(int unscaledMouseX, int unscaledMouseY, int clickedButton) {
        final int mouseX = (int) (unscaledMouseX / this.getScale());
        final int mouseY = (int) (unscaledMouseY / this.getScale());
        this.panels.forEach(panel -> panel.mouseClicked(mouseX, mouseY, clickedButton));
    }

    @Override
    public void mouseReleased(int unscaledMouseX, int unscaledMouseY, int releaseButton) {
        final int mouseX = (int) (unscaledMouseX / this.getScale());
        final int mouseY = (int) (unscaledMouseY / this.getScale());
        this.panels.forEach(panel -> panel.mouseReleased(mouseX, mouseY, releaseButton));
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        try {
            for(Panel panel : panels) {
                panel.keyTyped(typedChar, keyCode);
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

    public ArrayList<Panel> getPanels() {
        return this.panels;
    }


    protected double getScale() {
        return ClickGUI.INSTANCE.scale.doubleValue();
    }
}

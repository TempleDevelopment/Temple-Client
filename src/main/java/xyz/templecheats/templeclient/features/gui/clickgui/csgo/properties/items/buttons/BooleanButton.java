package xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.buttons;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.CsgoGuiScreen;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.util.color.impl.RectBuilder;
import xyz.templecheats.templeclient.util.math.Vec2d;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;

import java.awt.*;

import static xyz.templecheats.templeclient.features.gui.font.Fonts.font16;

public class BooleanButton extends Button {
    private final Button parentButton;
    private final BooleanSetting setting;
    
    public BooleanButton(BooleanSetting setting, Button parentButton) {
        super(setting.name);
        this.setting = setting;
        this.parentButton = parentButton;
        this.height = 12;
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {

        int color = ClickGUI.INSTANCE.getClientColor(0);

        GlStateManager.pushMatrix();
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(GL11.GL_LEQUAL);
        new RectBuilder(new Vec2d(x + 5, y + 5), new Vec2d(x + 13, y + 13))
                .outlineColor(new Color(color))
                .width(0.9)
                .color(new Color(45, 45, 45))
                .radius(1.0)
                .draw();
        new RectBuilder(new Vec2d(x + 6.5, y + 6.5), new Vec2d(x + 11.5, y + 11.5))
                .color(this.getState() ? new Color(color) : new Color(45, 45, 45, 0))
                .radius(0.5)
                .draw();

        font16.drawString(getLabel(), (int) x + 3 + (10 / 0.6), y + 6, new Color(208, 208, 208).getRGB(), false);
        GlStateManager.disableDepth();
        GlStateManager.popMatrix();
    }
    
    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        if(mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
            this.toggle();
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
    
    @Override
    public void toggle() {
        setting.setBooleanValue(!setting.booleanValue());
    }
    
    @Override
    public boolean getState() {
        return setting.booleanValue();
    }
    
    @Override
    public CsgoGuiScreen getClientScreen() {
        return this.parentButton.getClientScreen();
    }
}

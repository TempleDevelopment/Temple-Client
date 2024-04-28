package xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.buttons;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import xyz.templecheats.templeclient.features.gui.clickgui.csgo.CsgoGuiScreen;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.render.shader.impl.RectBuilder;
import xyz.templecheats.templeclient.util.math.Vec2d;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;

import java.awt.*;
import java.io.IOException;

import static xyz.templecheats.templeclient.features.gui.font.Fonts.*;
import static xyz.templecheats.templeclient.util.math.MathUtil.lerp;

public class BindButton extends Button {
    private final Module module;
    private boolean listening;

    public BindButton(Module module) {
        super("Keybind");
        this.module = module;
        this.height = 10;
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(GL11.GL_LEQUAL);

        drawCheckBox(new Vec2d(x + getWidth() * 1.91 + 3, y - ((font14.getStringWidth(module.getDescription()) > 196 - 36) ? 6 : 1.3)));

        String str;
        if (listening) {
            str = "...";
        } else {
            String keyName = Keyboard.getKeyName(module.getKey());
            str = (module.getKey() != Keyboard.KEY_NONE) ? keyName : "NONE";
        }
        double textWidth = font14.getStringWidth(str);

        double boxWidth = lerp(25F, (float) (textWidth + 8), 1.0F);

        new RectBuilder(new Vec2d(x + (5 / 0.8) + font20.getStringWidth(module.getName()) + 2,  y + font14.getFontHeight() - ((font14.getStringWidth(module.getDescription()) > 188 - 32) ? 19 : 15f)), new Vec2d(this.x + (5 / 0.8) + font20.getStringWidth(module.getName()) + boxWidth, y + 10 + font14.getFontHeight() - ((font14.getStringWidth(module.getDescription()) > 188 - 32) ? 18 : 14f)))
                .outlineColor(new Color(55, 55, 55))
                .width(0.8)
                .color(new Color(35, 35, 35))
                .radius(1.2)
                .draw();

        font14.drawString(str, (float) ((x + 5 / 0.8) + font20.getStringWidth(module.getName()) + 5), y + font14.getFontHeight() - ((font14.getStringWidth(module.getDescription()) > 188 - 32) ? 16 : 12f), module.isEnabled() ? Color.WHITE : new Color(155, 155, 155), false);
        GlStateManager.disableDepth();
        GlStateManager.popMatrix();
    }

    private void drawCheckBox(Vec2d pos) {
        double radius = 2.5;

        Vec2d p1 = pos.minus(radius * 2.5, radius).minus(2.5, 1.5);
        Vec2d p2 = pos.plus(radius * 2.5, radius).plus(2.5, 1.5);

        new RectBuilder(p1, p2)
                .outlineColor(this.getState() ? ClickGUI.INSTANCE.getStartColor() : new Color(99, 104, 107))
                .width(0.8)
                .color(new Color(35, 35, 35))
                .radius(15.0)
                .draw();
        Vec2d cp = pos.minus(radius * 1.5, 0.0).plus(radius * 3.0 - (!getState() ? 8 : 0), 0.0);

        Color color = this.getState() ? ClickGUI.INSTANCE.getStartColor() : Color.WHITE;
        new RectBuilder(cp.minus(radius + 0.2), cp.plus(radius + 0.2)).color(color).radius(radius + 0.2).draw();
    }

    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0
                && this.x + getWidth() * 1.88 <= mouseX
                && mouseX <= this.x + getWidth() * 1.9 + 11
                && this.y - ((font14.getStringWidth(module.getDescription()) > 188 - 32) ? 13 : 10) <= mouseY
                && mouseY <= this.y + ((font14.getStringWidth(module.getDescription()) > 188 - 32) ? 13 : 10)
        ) {
            this.toggle();
        }
        if (mouseButton == 0) {
            listening = this.x + (5 / 0.8) + font20.getStringWidth(module.getName()) + 5 <= mouseX && mouseX <= this.x + (5 / 0.8) + font20.getStringWidth(module.getName()) + 25 && this.y + font14.getFontHeight() - ((font14.getStringWidth(module.getDescription()) > 188 - 32) ? 21 : 18) <= mouseY && mouseY <= this.y + ((font14.getStringWidth(module.getDescription()) > 188 - 32) ? 13 : 10) + font14.getFontHeight() - 12f;
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if(listening) {
            if(keyCode != Keyboard.KEY_ESCAPE && keyCode != Keyboard.KEY_DELETE && keyCode != Keyboard.KEY_BACK) {
                module.setKey(keyCode);
            } else {
                module.setKey(Keyboard.KEY_NONE);
            }
            listening = false;
        }
    }
    
    @Override
    public void toggle() {
        module.toggle();
    }
    
    @Override
    public boolean getState() {
        return module.isEnabled();
    }

    @Override
    public CsgoGuiScreen getClientScreen() {
        return CsgoGuiScreen.getInstance();
    }
}

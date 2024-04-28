package xyz.templecheats.templeclient.features.gui.clickgui.basic;

import net.minecraft.client.gui.ScaledResolution;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.Panel;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.buttons.ModuleButton;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.manager.ModuleManager;
import xyz.templecheats.templeclient.util.render.animation.Animation;
import xyz.templecheats.templeclient.util.render.animation.Easing;
import xyz.templecheats.templeclient.util.render.shader.impl.GaussianBlur;

import static xyz.templecheats.templeclient.util.math.MathUtil.coerceIn;

public class ClickGuiScreen extends ClientGuiScreen {
    private static ClickGuiScreen instance;
    public final Animation animation = new Animation(Easing.InOutQuint, 500);
    public boolean open = true;

    @Override
    public void load() {
        this.getPanels().clear();

        int x = -42;
        for (final Module.Category category : Module.Category.values()) {
            this.getPanels().add(new Panel(category.name(), x += 90, 25, true) {
                @Override
                public void setupItems() {
                    ModuleManager.getModules().forEach(module -> {
                        if (module.getCategory() == category && !module.submodule) {
                            this.addButton(new ModuleButton(module));
                        }
                    });
                }
            });
        }

        super.load();
    }

    @Override
    public void onGuiClosed() {
        open = false;
        animation.reset();
    }

    public static ClickGuiScreen getInstance() {
        return instance == null ? (instance = new ClickGuiScreen()) : instance;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (ClickGUI.INSTANCE.blur.booleanValue()) {
            if(mc.currentScreen instanceof ClickGuiScreen || open) {
                animation.progress(getScale());
            }
            float progress = (float) animation.getProgress();
            float radius = coerceIn(ClickGUI.INSTANCE.radius.floatValue() * progress, (float) ClickGUI.INSTANCE.radius.min , (float) ClickGUI.INSTANCE.radius.max);
            float compression = coerceIn(ClickGUI.INSTANCE.compression.floatValue() * progress, (float) ClickGUI.INSTANCE.compression.min , (float) ClickGUI.INSTANCE.compression.max);

            GaussianBlur.startBlur();
            this.drawDefaultBackground();
            GaussianBlur.endBlur(radius, compression);
        }
        if (ClickGUI.INSTANCE.tint.booleanValue()) {
            this.drawDefaultBackground();
        }
        if (ClickGUI.INSTANCE.particles.booleanValue()) {
            ClickGUI.INSTANCE.particleUtil.drawParticles();
            ScaledResolution scaledResolution = new ScaledResolution(mc);
            ClickGUI.INSTANCE.snow.drawSnow(scaledResolution);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}

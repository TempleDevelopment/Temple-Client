package xyz.templecheats.templeclient.features.module.modules.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.gui.clickgui.hud.HudEditorScreen;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.client.hud.*;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.setting.SettingHolder;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;

import java.util.ArrayList;
import java.util.List;

public class HUD extends Module {
    /**
     * Instance
     */
    public static HUD INSTANCE;

    /**
     * Settings
     */
    public final DoubleSetting hudScale = new DoubleSetting("HUD Scale", this, 0.5d, 2d, 1.0d);
    public final BooleanSetting clamping = new BooleanSetting("Clamping", this, true);

    /**
     * Variables
     */
    private final List<HudElement> hudElements = new ArrayList<>();

    public HUD() {
        super("HUD", "Shows HUD Variants in your HUD", Keyboard.KEY_GRAVE, Category.Client);
        INSTANCE = this;

        this.registerSettings(hudScale, clamping);

        this.setToggled(true);

        this.hudElements.add(new Armor());
        this.hudElements.add(new Coords());
        this.hudElements.add(new FPS());
        this.hudElements.add(new Friends());
        this.hudElements.add(new Inventory());
        this.hudElements.add(new ModuleList());
        this.hudElements.add(new Notifications());
        this.hudElements.add(new Ping());
        this.hudElements.add(new PlayerView());
        this.hudElements.add(new PlayerName());
        this.hudElements.add(new PotionHUD());
        this.hudElements.add(new Server());
        this.hudElements.add(new Speed());
        this.hudElements.add(new TargetHUD());
        this.hudElements.add(new Watermark());
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if(event.getType() != RenderGameOverlayEvent.ElementType.TEXT || Panic.isPanic) {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.scale(this.hudScale.doubleValue(), this.hudScale.doubleValue(), 0);
        this.hudElements.forEach(element -> {
            if(!element.isEnabled()) {
                return;
            }

            if(mc.currentScreen instanceof HudEditorScreen && element.getWidth() > -1 && element.getHeight() > -1) {
                RenderUtil.drawRect((float) element.getX(), (float) element.getY(), (float) (element.getX() + element.getWidth()), (float) (element.getY() + element.getHeight()), element.isDragging() ? 0x802D2D2D : 0x80000000);
            }

            final ScaledResolution sr = new ScaledResolution(mc);
            final double screenWidth = sr.getScaledWidth() / this.hudScale.doubleValue();
            final double screenHeight = sr.getScaledHeight() / this.hudScale.doubleValue();

            if(this.clamping.booleanValue()) {
                element.setX(MathHelper.clamp(element.getX(), 0, screenWidth - element.getWidth()));
                element.setY(MathHelper.clamp(element.getY(), 0, screenHeight - element.getHeight()));
            }

            element.setLeftOfCenter(element.getX() + (element.getWidth() / 2) < screenWidth / 2D);
            element.setTopOfCenter(element.getY() + (element.getHeight() / 2) < screenHeight / 2D);
            element.renderElement(sr);
        });
        GlStateManager.popMatrix();
    }

    public List<HudElement> getHudElements() {
        return this.hudElements;
    }

    public static abstract class HudElement extends SettingHolder {
        protected static final Minecraft mc = Minecraft.getMinecraft();
        private final String description;
        private double x = 100, y = 100, width = -1, height = -1;
        private boolean enabled, dragging, leftOfCenter, topOfCenter;

        public HudElement(String name, String description) {
            super(name);
            this.description = description;
        }

        protected abstract void renderElement(ScaledResolution sr);

        public String getDescription() {
            return this.description;
        }

        public double getX() {
            return this.x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return this.y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public double getWidth() {
            return this.width;
        }

        public void setWidth(double width) {
            if(!this.leftOfCenter && width != this.width && width >= 0 && this.width >= 0) {
                final double screenWidth = new ScaledResolution(mc).getScaledWidth() / ClickGUI.INSTANCE.scale.doubleValue();

                final double newWidth = Math.max(width, 0);
                final double newX = this.x + (this.width - newWidth);

                this.setX(MathHelper.clamp(newX + ((this.x + ((Math.max(0, this.width)))) - (newX + newWidth)), 0, screenWidth - width));
            }

            this.width = width;
        }

        public double getHeight() {
            return this.height;
        }

        public void setHeight(double height) {
            if(!this.topOfCenter && height != this.height && height >= 0 && this.height >= 0) {
                final double screenHeight = new ScaledResolution(mc).getScaledHeight() / ClickGUI.INSTANCE.scale.doubleValue();

                final double newHeight = Math.max(height, 0);
                final double newY = this.y + (this.height - newHeight);

                this.setY(MathHelper.clamp(newY + ((this.y + ((Math.max(0, this.height)))) - (newY + newHeight)), 0, screenHeight - height));
            }

            this.height = height;
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isDragging() {
            return this.dragging;
        }

        public void setDragging(boolean dragging) {
            this.dragging = dragging;
        }

        public boolean isLeftOfCenter() {
            return this.leftOfCenter;
        }

        public void setLeftOfCenter(boolean leftOfCenter) {
            this.leftOfCenter = leftOfCenter;
        }

        public boolean isTopOfCenter() {
            return this.topOfCenter;
        }

        public void setTopOfCenter(boolean topOfCenter) {
            this.topOfCenter = topOfCenter;
        }
    }
}
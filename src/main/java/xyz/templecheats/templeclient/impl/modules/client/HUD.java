package xyz.templecheats.templeclient.impl.modules.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.api.setting.SettingHolder;
import xyz.templecheats.templeclient.api.util.render.RenderUtil;
import xyz.templecheats.templeclient.impl.gui.clickgui.HudEditorScreen;
import xyz.templecheats.templeclient.impl.modules.Module;
import xyz.templecheats.templeclient.impl.modules.client.hud.*;

import java.util.ArrayList;
import java.util.List;

public class HUD extends Module {
    /**
     * Instance
     */
    public static HUD INSTANCE;
    
    /**
     * Variables
     */
    private final List<HudElement> hudElements = new ArrayList<>();
    
    public HUD() {
        super("HUD", "Shows HUD Variants in your HUD", Keyboard.KEY_GRAVE, Category.Client);
        INSTANCE = this;
        
        this.setToggled(true);
        
        this.hudElements.add(new ArmorHUD());
        this.hudElements.add(new Coords());
        this.hudElements.add(new FPS());
        this.hudElements.add(new InventoryHUD());
        this.hudElements.add(new ModuleList());
        this.hudElements.add(new Ping());
        this.hudElements.add(new PlayerModel());
        this.hudElements.add(new PlayerName());
        this.hudElements.add(new TargetHUD());
        this.hudElements.add(new Watermark());
    }
    
    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if(event.getType() != RenderGameOverlayEvent.ElementType.TEXT || Panic.isPanic) {
            return;
        }
        
        this.hudElements.forEach(element -> {
            if(!element.isEnabled()) {
                return;
            }
            
            if(mc.currentScreen instanceof HudEditorScreen) {
                RenderUtil.drawRect((float) element.getX(), (float) element.getY(), (float) (element.getX() + element.getWidth()), (float) (element.getY() + element.getHeight()), element.isDragging() ? 0x802D2D2D : 0x80000000);
            }
            
            final ScaledResolution sr = new ScaledResolution(mc);
            element.setLeftOfCenter(element.getX() + (element.getWidth() / 2) < sr.getScaledWidth() / 2D);
            element.setTopOfCenter(element.getY() + (element.getHeight() / 2) < sr.getScaledHeight() / 2D);
            element.renderElement(sr);
        });
    }
    
    public List<HudElement> getHudElements() {
        return this.hudElements;
    }

    public static abstract class HudElement extends SettingHolder {
        protected static final Minecraft mc = Minecraft.getMinecraft();
        private final String description;
        private double x = 100, y = 100, width, height;
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
            if(!leftOfCenter && width != this.width && this.width != 0) {
                this.setX(this.x + this.width - width);
            }
            this.width = width;
        }
        
        public double getHeight() {
            return this.height;
        }
        
        public void setHeight(double height) {
            if(!topOfCenter && height != this.height && this.height != 0) {
                this.setY(this.y + this.height - height);
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

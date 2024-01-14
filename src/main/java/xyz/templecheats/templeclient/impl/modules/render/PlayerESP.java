package xyz.templecheats.templeclient.impl.modules.render;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.impl.modules.Module;
import xyz.templecheats.templeclient.api.util.render.RenderUtil;
import xyz.templecheats.templeclient.impl.gui.clickgui.setting.Setting;

import java.util.ArrayList;
import java.util.List;

public class PlayerESP extends Module {
    private AxisAlignedBB box = null;
    private Setting red;
    private Setting green;
    private Setting blue;
    private Setting mode;
    private static List<Entity> glowed = new ArrayList<>();

    public PlayerESP() {
        super("PlayerESP", Keyboard.KEY_NONE, Category.RENDER);
        ArrayList<String> options = new ArrayList<>();
        options.add("Box");
        options.add("Glow");
        mode = new Setting("Mode", this, options, "Box");
        red = new Setting("Red", this, 255, 0, 255, true);
        green = new Setting("Green", this, 255, 0, 255, true);
        blue = new Setting("Blue", this, 255, 0, 255, true);
        TempleClient.settingsManager.rSetting(mode);
        TempleClient.settingsManager.rSetting(red);
        TempleClient.settingsManager.rSetting(green);
        TempleClient.settingsManager.rSetting(blue);
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent e) {
        String currentMode = mode.getValString();
        float r = red.getValInt() / 255.0f;
        float g = green.getValInt() / 255.0f;
        float b = blue.getValInt() / 255.0f;

        for (Entity entity : Minecraft.getMinecraft().world.playerEntities) {
            if (entity != Minecraft.getMinecraft().player && entity != null) {
                if (currentMode.equals("Box")) {
                    if (!glowed.isEmpty()) {
                        for (Entity glowEntity : glowed) {
                            glowEntity.setGlowing(false);
                        }
                        glowed.clear();
                    }
                    box = new AxisAlignedBB(
                            entity.getEntityBoundingBox().minX - 0.05 - entity.posX
                                    + (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * e.getPartialTicks() - Minecraft.getMinecraft().getRenderManager().viewerPosX),
                            entity.getEntityBoundingBox().minY - entity.posY
                                    + (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * e.getPartialTicks() - Minecraft.getMinecraft().getRenderManager().viewerPosY),
                            entity.getEntityBoundingBox().minZ - 0.05 - entity.posZ
                                    + (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * e.getPartialTicks() - Minecraft.getMinecraft().getRenderManager().viewerPosZ),
                            entity.getEntityBoundingBox().maxX + 0.05 - entity.posX
                                    + (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * e.getPartialTicks() - Minecraft.getMinecraft().getRenderManager().viewerPosX),
                            entity.getEntityBoundingBox().maxY + 0.1 - entity.posY
                                    + (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * e.getPartialTicks() - Minecraft.getMinecraft().getRenderManager().viewerPosY),
                            entity.getEntityBoundingBox().maxZ + 0.05 - entity.posZ
                                    + (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * e.getPartialTicks() - Minecraft.getMinecraft().getRenderManager().viewerPosZ)
                    );
                    RenderUtil.FillOnlyLinePlayerESP(entity, box, r, g, b);
                } else if (currentMode.equals("Glow")) {
                    if (!glowed.contains(entity)) {
                        entity.setGlowing(true);
                        glowed.add(entity);
                    }
                }
            }
        }
    }

    @Override
    public void onDisable() {
        for (Entity entity : glowed) {
            entity.setGlowing(false);
        }
        glowed.clear();
        super.onDisable();
    }
}

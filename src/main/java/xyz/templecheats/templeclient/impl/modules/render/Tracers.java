package xyz.templecheats.templeclient.impl.modules.render;

import xyz.templecheats.templeclient.impl.modules.Module;
import xyz.templecheats.templeclient.api.util.render.RenderUtil;
import xyz.templecheats.templeclient.api.setting.Setting;
import xyz.templecheats.templeclient.TempleClient;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class Tracers extends Module {
    private Setting red;
    private Setting green;
    private Setting blue;

    public Tracers() {
        super("Tracers","Draws lines directing towards entities", Keyboard.KEY_NONE, Category.Render);
        red = new Setting("Red", this, 255, 0, 255, true);
        green = new Setting("Green", this, 255, 0, 255, true);
        blue = new Setting("Blue", this, 255, 0, 255, true);

        TempleClient.settingsManager.rSetting(red);
        TempleClient.settingsManager.rSetting(green);
        TempleClient.settingsManager.rSetting(blue);
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent e) {
        float r = red.getValInt() / 255.0f;
        float g = green.getValInt() / 255.0f;
        float b = blue.getValInt() / 255.0f;

        for (Entity playerEntity : mc.world.playerEntities) {
            if (playerEntity != null && playerEntity != mc.player) {
                RenderUtil.trace(mc, playerEntity, mc.getRenderPartialTicks(), 1, r, g, b);
            }
        }
    }
}
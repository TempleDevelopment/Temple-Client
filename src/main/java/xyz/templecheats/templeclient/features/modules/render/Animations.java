package xyz.templecheats.templeclient.features.modules.render;

import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import xyz.templecheats.templeclient.features.modules.Module;
import xyz.templecheats.templeclient.gui.clickgui.setting.Setting;
import xyz.templecheats.templeclient.gui.clickgui.setting.SettingsManager;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.util.time.Timer;


// Animations Dont Work.

public class Animations extends Module {

    private Setting enableAnimation;
    private Setting enableSlowMotion;

    private Timer slowMotionTimer;

    public Animations() {
        super("Animations", Keyboard.KEY_NONE, Category.RENDER);

        SettingsManager settingsManager = TempleClient.instance.settingsManager;

        enableAnimation = new Setting("360 Swing", this, false);
        enableSlowMotion = new Setting("Slow Motion", this, false);
        enableSlowMotion = new Setting("1.7 Sword Anim", this, false);

        settingsManager.rSetting(enableAnimation);
        settingsManager.rSetting(enableSlowMotion);

        slowMotionTimer = new Timer();
    }

    @SubscribeEvent
    public void onRenderHand(RenderSpecificHandEvent event) {
        if (enableAnimation.getValBoolean()) {
            if (enableSlowMotion.getValBoolean()) {
                if (!slowMotionTimer.hasReached(200)) {
                    return;
                }
                slowMotionTimer.reset();
            }


            float rotatePitch = 360.0f;

            GL11.glPushMatrix();
            GL11.glRotatef(rotatePitch, 1, 0, 0);
            GL11.glPopMatrix();
        }
    }
}

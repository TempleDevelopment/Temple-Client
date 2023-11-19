package xyz.templecheats.templeclient.modules.RENDER;

import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.modules.Module;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import xyz.templecheats.templeclient.setting.Setting;
import xyz.templecheats.templeclient.setting.SettingsManager;

public class ViewModel extends Module {
    private Setting xPos;
    private Setting yPos;
    private Setting zPos;
    private Setting rotation;

    public ViewModel() {
        super("ViewModel", Keyboard.KEY_NONE, Category.RENDER);

        SettingsManager settingsManager = TempleClient.instance.settingsManager;

        double defaultX = 0.0;
        double defaultY = 0.0;
        double defaultZ = 0.0;
        double defaultRotation = 0.0;

        xPos = new Setting("X Position", this, defaultX, -10.0, 10.0, false);
        yPos = new Setting("Y Position", this, defaultY, -10.0, 10.0, false);
        zPos = new Setting("Z Position", this, defaultZ, -10.0, 10.0, false);
        rotation = new Setting("Rotation", this, defaultRotation, -45.0, 45.0, true);

        settingsManager.rSetting(xPos);
        settingsManager.rSetting(yPos);
        settingsManager.rSetting(zPos);
        settingsManager.rSetting(rotation);
    }

    @SubscribeEvent
    public void onRender(RenderSpecificHandEvent event) {
        double x = TempleClient.instance.settingsManager.getSettingByName(this.name, "X Position").getValDouble();
        double y = TempleClient.instance.settingsManager.getSettingByName(this.name, "Y Position").getValDouble();
        double z = TempleClient.instance.settingsManager.getSettingByName(this.name, "Z Position").getValDouble();
        double rot = TempleClient.instance.settingsManager.getSettingByName(this.name, "Rotation").getValDouble();

        GL11.glTranslated(x, y, z);

        GL11.glRotatef((float) rot, 0, 1, 0);
    }
}

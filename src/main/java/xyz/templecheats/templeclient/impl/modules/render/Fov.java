package xyz.templecheats.templeclient.impl.modules.render;

import org.lwjgl.input.Keyboard;
import net.minecraft.client.Minecraft;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.impl.modules.Module;
import xyz.templecheats.templeclient.api.setting.Setting;

public class Fov extends Module {

    private float originalFOV = 0;

    public Fov() {
        super("Fov","Increases your field of view", Keyboard.KEY_NONE, Category.Render);
        TempleClient.settingsManager.rSetting(new Setting("FOV", this, 70, 5, 150, true));
    }

    @Override
    public void onEnable() {
        originalFOV = Minecraft.getMinecraft().gameSettings.fovSetting;
    }

    @Override
    public void onDisable() {
        Minecraft.getMinecraft().gameSettings.fovSetting = originalFOV;
    }

    @Override
    public void onUpdate() {
        if(this.isEnabled()) {
            double newFOV = TempleClient.settingsManager.getSettingByName(this.getName(), "FOV").getValDouble();
            Minecraft.getMinecraft().gameSettings.fovSetting = (float)newFOV;
        }
    }
}

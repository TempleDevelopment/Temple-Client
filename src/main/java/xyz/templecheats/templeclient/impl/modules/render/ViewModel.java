package xyz.templecheats.templeclient.impl.modules.render;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.impl.modules.Module;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import xyz.templecheats.templeclient.impl.gui.clickgui.setting.Setting;
import xyz.templecheats.templeclient.impl.gui.clickgui.setting.SettingsManager;

public class ViewModel extends Module {
    private Setting xPosMain, yPosMain, zPosMain;
    private Setting xSizeMain, ySizeMain, zSizeMain;
    public ViewModel() {
        super("ViewModel", Keyboard.KEY_NONE, Category.RENDER);
        SettingsManager settingsManager = TempleClient.settingsManager;

        initializeSettings(settingsManager);
    }

    private void initializeSettings(SettingsManager settingsManager) {

        xPosMain = new Setting("OffSet X", this, 0.0, -3.0, 3.0, false);
        yPosMain = new Setting("OffSet Y", this, 0.0, -3.0, 3.0, false);
        zPosMain = new Setting("OffSet Z", this, 0.0, -3.0, 3.0, false);
        xSizeMain = new Setting("Size X", this, 1.0, 0.0, 4.0, false);
        ySizeMain = new Setting("Size Y", this, 1.0, 0.0, 4.0, false);
        zSizeMain = new Setting("Size Z", this, 1.0, 0.0, 4.0, false);

        settingsManager.rSetting(xPosMain);
        settingsManager.rSetting(yPosMain);
        settingsManager.rSetting(zPosMain);
        settingsManager.rSetting(xSizeMain);
        settingsManager.rSetting(ySizeMain);
        settingsManager.rSetting(zSizeMain);
    }

    @SubscribeEvent
    public void onRender(RenderSpecificHandEvent event) {
        EnumHandSide dominantHand = Minecraft.getMinecraft().player.getPrimaryHand();
        EnumHandSide renderedHandSide = (event.getHand() == EnumHand.MAIN_HAND) ? dominantHand : dominantHand.opposite();
        if (renderedHandSide == EnumHandSide.RIGHT) {
            if (event.getHand() == EnumHand.MAIN_HAND) {
                applyMainHandTransformations();
            }
        }
    }

    private void applyMainHandTransformations() {
        applyTransformations(xPosMain, yPosMain, zPosMain, xSizeMain, ySizeMain, zSizeMain);
    }

    private void applyTransformations(Setting xPos, Setting yPos, Setting zPos, Setting xSize, Setting ySize, Setting zSize) {
        GL11.glTranslated(xPos.getValDouble(), yPos.getValDouble(), zPos.getValDouble());
        GL11.glScaled(xSize.getValDouble(), ySize.getValDouble(), zSize.getValDouble());
    }
}

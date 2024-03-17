package xyz.templecheats.templeclient.util.keys;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.ClickGuiScreen;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.ClientCsgoGuiScreen;
import xyz.templecheats.templeclient.features.gui.clickgui.hud.HudEditorScreen;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.features.module.modules.client.HUD;
import xyz.templecheats.templeclient.manager.ModuleManager;

public class KeyUtil {
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent e) {
        final int key = Keyboard.getEventKey();

        if (Keyboard.isKeyDown(key) && key != Keyboard.KEY_NONE) {
            if (key == ClickGUI.INSTANCE.getKey()) {
                if (ClickGUI.INSTANCE.theme.value() != ClickGUI.Theme.CSGO) {
                    Minecraft.getMinecraft().displayGuiScreen(ClickGuiScreen.getInstance());
                } else {
                    Minecraft.getMinecraft().displayGuiScreen(ClientCsgoGuiScreen.getInstance());
                }
                return;
            }

            if (key == HUD.INSTANCE.getKey()) {
                Minecraft.getMinecraft().displayGuiScreen(HudEditorScreen.getInstance());
                return;
            }
            
            ModuleManager.keyPress(key);
        }
    }
}

package xyz.templecheats.templeclient.api.util.keys;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.ModuleManager;
import xyz.templecheats.templeclient.impl.gui.clickgui.ClickGuiScreen;
import xyz.templecheats.templeclient.impl.gui.clickgui.HudEditorScreen;
import xyz.templecheats.templeclient.impl.modules.client.ClickGUI;
import xyz.templecheats.templeclient.impl.modules.client.HUD;

public class KeyUtil {
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent e) {
        final int key = Keyboard.getEventKey();
        
        if(Keyboard.isKeyDown(key) && key != Keyboard.KEY_NONE) {
            if(key == ClickGUI.INSTANCE.getKey()) {
                Minecraft.getMinecraft().displayGuiScreen(ClickGuiScreen.getInstance());
                return;
            }
            
            if(key == HUD.INSTANCE.getKey()) {
                Minecraft.getMinecraft().displayGuiScreen(HudEditorScreen.getInstance());
                return;
            }
            
            ModuleManager.keyPress(key);
        }
    }
}

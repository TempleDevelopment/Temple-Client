package xyz.templecheats.templeclient.api.util.keys;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.ModuleManager;
import xyz.templecheats.templeclient.impl.gui.clickgui.ClickGuiScreen;

public class key {
	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent e) {
		if(Keyboard.isKeyDown(Keyboard.getEventKey())) {
			if(Keyboard.getEventKey() != Keyboard.KEY_NONE) {
				ModuleManager.keyPress(Keyboard.getEventKey());
				if(Keyboard.getEventKey() == Keyboard.KEY_RSHIFT) {
					Minecraft.getMinecraft().displayGuiScreen(ClickGuiScreen.getClickGui());
				}
			}
		}
	}
}

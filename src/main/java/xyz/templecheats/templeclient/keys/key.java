package xyz.templecheats.templeclient.keys;

import xyz.templecheats.templeclient.Client;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class key {
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent e) {
       if (Keyboard.isKeyDown(Keyboard.getEventKey())){
         if (Keyboard.getEventKey() != Keyboard.KEY_NONE) {
             Client.keyPress(Keyboard.getEventKey());
             if (Keyboard.getEventKey() == Keyboard.KEY_RSHIFT) {
                 Minecraft.getMinecraft().displayGuiScreen(Client.clickGui);
             }
         }
       }
    }
}

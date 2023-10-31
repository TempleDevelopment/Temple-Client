package com.example.examplemod.Module.RENDER;

import com.example.examplemod.Module.Module;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class ViewModel extends Module {
    public ViewModel() {
        super("ViewModel", Keyboard.KEY_NONE, Category.RENDER);
    }

    @SubscribeEvent
    public void onRender(RenderSpecificHandEvent e) {
        // Translate the hands to move them further apart
        GL11.glTranslated(1.0, 0, -3.0);

        // Rotate the hands sideways
        GL11.glRotatef(45, 0, 1, 0);
    }
}

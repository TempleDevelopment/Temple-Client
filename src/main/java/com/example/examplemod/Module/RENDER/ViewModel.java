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
        GL11.glTranslated(0, 0, -7);
    }
}
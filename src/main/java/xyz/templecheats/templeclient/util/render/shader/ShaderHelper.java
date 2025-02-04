package xyz.templecheats.templeclient.util.render.shader;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;
import xyz.templecheats.templeclient.TempleClient;

import java.util.HashMap;

import static xyz.templecheats.templeclient.util.Globals.mc;

public class ShaderHelper {
    private final HashMap<String, Framebuffer> frameBufferMap = new HashMap<>();
    public ShaderGroup shader;
    private boolean frameBuffersInitialized = false;

    /****************************************************************
     *                    Constructor
     ****************************************************************/
    public ShaderHelper(ResourceLocation location, String... names) {
        if (!OpenGlHelper.shadersSupported)
            TempleClient.logger.warn("Shaders are unsupported by OpenGL!");

        if (isIntegrated()) {
            TempleClient.logger.warn("Running on Intel Integrated Graphics!");
        }

        try {
            ShaderLinkHelper.setNewStaticShaderLinkHelper();

            shader = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), location);
            shader.createBindFramebuffers(mc.displayWidth, mc.displayHeight);

        } catch (Exception e) {
            TempleClient.logger.warn("Failed to load shaders");
            e.printStackTrace();
        }
        for (final String name : names) {
            this.frameBufferMap.put(name, this.shader.getFramebufferRaw(name));
        }

        MinecraftForge.EVENT_BUS.register(this);
    }

    /****************************************************************
     *                    Event Handlers
     ****************************************************************/

    @SubscribeEvent
    public void onTick0(TickEvent.ClientTickEvent event) {
        if (shader == null) return;
        int w = 0;
        int h = 0;
        if (event.phase == TickEvent.Phase.START) {
            w = mc.displayWidth;
            h = mc.displayHeight;
        } else if (event.phase == TickEvent.Phase.END) {
            if (w != mc.displayWidth || h != mc.displayHeight) {
                shader.createBindFramebuffers(mc.displayWidth, mc.displayHeight); // this will not run if on Intel GPU or unsupported Shaders
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!frameBuffersInitialized && shader != null) {
            shader.createBindFramebuffers(mc.displayWidth, mc.displayHeight);

            frameBuffersInitialized = true;
        }
    }

    /****************************************************************
     *                    Framebuffer Accessor
     ****************************************************************/
    public Framebuffer getFrameBuffer(String name) {
        return frameBufferMap.get(name);
    }

    /****************************************************************
     *                    Utility Methods
     ****************************************************************/
    private boolean isIntegrated() {
        return GlStateManager.glGetString(GL11.GL_VENDOR).contains("Intel");
    }
}

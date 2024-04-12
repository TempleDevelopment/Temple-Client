package xyz.templecheats.templeclient.util.color.impl;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import xyz.templecheats.templeclient.util.color.ShaderUtil;
import xyz.templecheats.templeclient.util.Globals;
import xyz.templecheats.templeclient.util.render.RenderUtil;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

public class BlackShader implements Globals {
    private final static ShaderUtil shader = new ShaderUtil("black");
    private static Framebuffer framebuffer = new Framebuffer(1, 1, false);

    public static void setupUniforms() {
        shader.setUniformi("texture", 0);
    }

    public static void setup() {
        GlStateManager.enableBlend();
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);

        framebuffer = RenderUtil.createFrameBuffer(framebuffer);

        mc.getFramebuffer().bindFramebuffer(true);
        shader.attachShader();
        setupUniforms();

        glBindTexture(GL_TEXTURE_2D, framebuffer.framebufferTexture);
    }

    public static void finish() {
        shader.releaseShader();
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.bindTexture(0);
    }
}
package xyz.templecheats.templeclient.util.render.shader.impl;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import xyz.templecheats.templeclient.util.Globals;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.render.StencilUtil;
import xyz.templecheats.templeclient.util.render.shader.ShaderUtil;

import java.nio.FloatBuffer;

import static net.minecraft.client.renderer.GlStateManager.resetColor;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL20.glUniform1;
import static xyz.templecheats.templeclient.util.math.MathUtil.calculateGaussianValue;

public class GaussianBlur implements Globals {
    private final static ShaderUtil shader = new ShaderUtil("gaussian");
    private static Framebuffer framebuffer = new Framebuffer(1, 1, false);

    private static void setupUniforms(float dir1, float dir2, float radius) {
        shader.setUniformi("textureIn", 0);
        shader.setUniformf("texelSize", 1.0F / (float) mc.displayWidth, 1.0F / (float) mc.displayHeight);
        shader.setUniformf("direction", dir1, dir2);
        shader.setUniformf("radius", radius);

        final FloatBuffer weightBuffer = BufferUtils.createFloatBuffer(256);
        for (int i = 0; i <= radius; i++) {
            weightBuffer.put(calculateGaussianValue(i, radius / 2));
        }

        weightBuffer.rewind();
        glUniform1(shader.getUniform("weights"), weightBuffer);
    }

    public static void startBlur(){
        StencilUtil.initStencilToWrite();
    }

    public static void endBlur(float radius, float compression) {
        ScaledResolution sr = new ScaledResolution(mc);
        float width = (float) sr.getScaledWidth_double();
        float height = (float) sr.getScaledHeight_double();

        StencilUtil.readStencilBuffer(1);
        framebuffer = RenderUtil.createFrameBuffer(framebuffer);

        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(false);
        shader.attachShader();
        setupUniforms(compression, 0, radius);
        glBindTexture(GL_TEXTURE_2D, mc.getFramebuffer().framebufferTexture);
        shader.render(0, 0, width, height);
        framebuffer.unbindFramebuffer();
        shader.releaseShader();

        mc.getFramebuffer().bindFramebuffer(false);
        shader.attachShader();
        setupUniforms(0, compression, radius);

        glBindTexture(GL_TEXTURE_2D, framebuffer.framebufferTexture);
        shader.render(0, 0, width, height);
        shader.releaseShader();

        StencilUtil.uninitStencilBuffer();
        resetColor();
        GlStateManager.bindTexture(0);

    }

}

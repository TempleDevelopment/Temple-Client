package xyz.templecheats.templeclient.util.render.shader.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import xyz.templecheats.templeclient.util.render.shader.ShaderUtil;

import static net.minecraft.client.renderer.GlStateManager.resetColor;
import static org.lwjgl.opengl.GL11.*;

public class RoundedTexture {

    private final static ShaderUtil rectTexture = new ShaderUtil("texturerect");

    public void drawRoundTextured(float x, float y, float width, float height, float radius, float alpha) {
        resetColor();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL_GREATER, (float) (0 * .01));
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        rectTexture.attachShader();
        rectTexture.setUniformi("textureIn", 0);
        setupRoundedRectUniforms(x, y, width, height, radius, rectTexture);
        rectTexture.setUniformf("alpha", alpha);
        rectTexture.render(x - 1, y - 1, width + 2, height + 2);
        rectTexture.releaseShader();
        GlStateManager.disableBlend();
    }


    private void setupRoundedRectUniforms(float x, float y, float width, float height, float radius, ShaderUtil roundedTexturedShader) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        roundedTexturedShader.setUniformf("location", x * sr.getScaleFactor(), (Minecraft.getMinecraft().displayHeight - (height * sr.getScaleFactor())) - (y * sr.getScaleFactor()));
        roundedTexturedShader.setUniformf("rectSize", width * sr.getScaleFactor(), height * sr.getScaleFactor());
        roundedTexturedShader.setUniformf("radius", radius * sr.getScaleFactor());
    }
}

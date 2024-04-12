package xyz.templecheats.templeclient.util.color.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import xyz.templecheats.templeclient.util.color.ShaderUtil;
import xyz.templecheats.templeclient.util.math.Quad;
import xyz.templecheats.templeclient.util.math.Vec2d;

import java.awt.*;

import static net.minecraft.client.renderer.GlStateManager.resetColor;
import static org.lwjgl.opengl.GL11.*;

public class RectBuilder {
    private final Vec2d pos1;
    private final Vec2d pos2;
    private final static ShaderUtil rectShader = new ShaderUtil("rect");
    private Quad<Color> colorIn = new Quad<>(Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE);
    private Quad<Color> colorOut = new Quad<>(Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE);
    private float roundRadius = 0.0f;
    private float outlineWidth = 0.0f;

    public RectBuilder(Vec2d pos1, Vec2d pos2) {
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    public void draw(RectBuilder block) {
        block.draw();
    }

    // region color
    public RectBuilder color(Color leftTop, Color rightTop, Color leftBottom, Color rightBottom) {
        this.colorIn = new Quad<>(leftTop, rightTop, leftBottom, rightBottom);
        return this;
    }

    public RectBuilder color(Color color) {
        return color(color, color, color, color);
    }

    public RectBuilder colorV(Color top, Color bottom) {
        return color(top, top, bottom, bottom);
    }

    public RectBuilder colorH(Color left, Color right) {
        return color(left, right, left, right);
    }
    // endregion

    // region outlineColor
    public RectBuilder outlineColor(Color leftTop, Color rightTop, Color leftBottom, Color rightBottom) {
        colorOut = new Quad<>(leftTop, rightTop, leftBottom, rightBottom);
        return this;
    }

    public RectBuilder outlineColor(Color color) {
        return outlineColor(color, color, color, color);
    }

    public RectBuilder outlineColorV(Color top, Color bottom) {
        return outlineColor(top, top, bottom, bottom);
    }

    public RectBuilder outlineColorH(Color left, Color right) {
        return outlineColor(left, right, left, right);
    }
    // endregion

    public RectBuilder radius(double value) {
        roundRadius = (float) value;
        return this;
    }

    public RectBuilder width(double value) {
        outlineWidth = (float) value;
        return this;
    }

    public RectBuilder draw() {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);

        float x1 = (float) pos1.x;
        float y1 = (float) pos1.y;
        float x2 = (float) pos2.x;
        float y2 = (float) pos2.y;

        if (x1 > x2) {
            float i = x1;
            x1 = x2;
            x2 = i;
        }

        if (y1 > y2) {
            float j = y1;
            y1 = y2;
            y2 = j;
        }

        float x = x1;
        float y = y1;
        float width = x2 - x1;
        float height = y2 - y1;
        float scale = sr.getScaleFactor();
        float radius = Math.min(roundRadius, Math.min(width, height) / 2.0f);

        resetColor();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL_ALPHA_TEST);

        rectShader.attachShader();
        rectShader.setUniformf("size", width * scale, height * scale);
        rectShader.setUniformf("roundRadius", radius * scale);
        rectShader.setUniformf("smoothFactor", 1.2f);
        rectShader.setUniformf("outlineWidth", outlineWidth * scale);

        rectShader.colorUniform("colorIn1", colorIn.getFirst());
        rectShader.colorUniform("colorIn2", colorIn.getThird());
        rectShader.colorUniform("colorIn3", colorIn.getSecond());
        rectShader.colorUniform("colorIn4", colorIn.getFourth());

        rectShader.colorUniform("colorOut1", colorOut.getFirst());
        rectShader.colorUniform("colorOut2", colorOut.getThird());
        rectShader.colorUniform("colorOut3", colorOut.getSecond());
        rectShader.colorUniform("colorOut4", colorOut.getFourth());

        float s = 0.4f;
        rectShader.render(x - s, y - s, width + s * 2.0f, height + s * 2.0f);
        rectShader.releaseShader();

        GL11.glEnable(GL_ALPHA_TEST);
        GlStateManager.disableBlend();

        return this;
    }
}

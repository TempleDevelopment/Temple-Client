package xyz.templecheats.templeclient.features.module.modules.render;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.client.Colors;
import xyz.templecheats.templeclient.util.color.ColorUtil;
import xyz.templecheats.templeclient.util.setting.impl.*;
import java.awt.*;

import static org.lwjgl.opengl.GL11.*;
import static xyz.templecheats.templeclient.util.color.ColorUtil.lerpColor;
import static xyz.templecheats.templeclient.util.color.ColorUtil.setAlpha;
import static xyz.templecheats.templeclient.util.render.RenderUtil.interpolateEntity;
import static xyz.templecheats.templeclient.util.math.MathUtil.lerp;

public class ChinaHat extends Module {
    /*
     * Settings
     */
    private final BooleanSetting self = new BooleanSetting("Self", this, true);
    private final BooleanSetting other = new BooleanSetting("Other", this, false);
    private final BooleanSetting firstPerson = new BooleanSetting("FirstPerson", this, true);
    private final DoubleSetting heightValue = new DoubleSetting("Height", this, 0.0, 0.7, 0.3);
    private final DoubleSetting radiusValue = new DoubleSetting("Radius", this, 0.3, 2.0, 1.0);
    private final DoubleSetting rotateSpeed = new DoubleSetting("Rotate Speed", this, 0.0, 10.0, 2.0);
    private final BooleanSetting fill = new BooleanSetting("Fill", this, true);
    private final BooleanSetting outline = new BooleanSetting("Outline", this, false);
    private final DoubleSetting outlineWidth = new DoubleSetting("Outline Width", outline.parent, 1.0, 5.0, 2.0);
    private final IntSetting outlineOpacity = new IntSetting("Outline Opacity", outline.parent, 0, 255, 255);
    private final IntSetting fillOpacity1 = new IntSetting("Fill 1 Opacity", outline.parent, 0, 255, 255);
    private final IntSetting fillOpacity2 = new IntSetting("Fill 2 Opacity", outline.parent, 0, 255, 255);

    public ChinaHat() {
        super("ChinaHat", "Draw a traditional hats of some East and South Asian countries", Keyboard.KEY_NONE, Category.Render);
        registerSettings(self, other, firstPerson, fill, outline, heightValue, radiusValue, rotateSpeed, outlineWidth, outlineOpacity, fillOpacity1, fillOpacity2);
    }

    private boolean check() {
        if (firstPerson.booleanValue() && mc.gameSettings.thirdPersonView == 0) {
            return true;
        }
        else return mc.gameSettings.thirdPersonView != 0;
    }
    @Override
    public void onRenderWorld(float partialTicks) {
        if (mc.player == null || mc.world == null) return;

        for (EntityPlayer player : mc.world.playerEntities) {
            if (player.isDead || player.isInvisible()) {
                continue;
            }
            if ((other.booleanValue() && player.getEntityId() != mc.player.getEntityId()) || self.booleanValue() && player == mc.player && check()) {
                float radius = (float) ((player.getEntityBoundingBox().maxX - player.getEntityBoundingBox().minX) * 0.9 * radiusValue.floatValue());

                Vec3d pos = interpolateEntity(player);

                setup();
                GlStateManager.pushMatrix();
                GlStateManager.translate(0, pos.y + player.height, 0);
                GlStateManager.translate(pos.x, 0.1 - (player.isSneaking() ? 0.23 : 0), pos.z);
                GlStateManager.rotate((player.ticksExisted + mc.getRenderPartialTicks()) * -rotateSpeed.floatValue(), 0.0F, 1.0F, 0.0F);
                if (fill.booleanValue()) {
                    drawHat(radius);
                }
                if (outline.booleanValue()) {
                    drawHatOutline(radius);
                }
                restore();
                GlStateManager.popMatrix();
            }
        }
    }

    private void drawHat(float radius) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        GlStateManager.color(-1f, -1f, -1f, -1f);
        buffer.begin(GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(0.0, heightValue.doubleValue(), 0.0).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        for (int i = 0; i <= 360; i++) {
            double percent = (double) i / 360;
            double progress = ((percent > 0.5) ? 1.0 - percent : percent) * 2.0;
            Color color = lerpColor(setAlpha(Colors.INSTANCE.getGradient()[1], fillOpacity1.intValue()), setAlpha(Colors.INSTANCE.getGradient()[0], fillOpacity2.intValue()), (float) progress);

            double dir = Math.toRadians(i - 180.0);
            double x = -Math.sin(dir) * radius;
            double z = Math.cos(dir) * radius;
            buffer.pos(x, 0.0, z).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        }
        buffer.pos(0.0, heightValue.doubleValue(), 0.0).color(0, 0 ,0 ,0).endVertex();
        tessellator.draw();
    }

    private void drawHatOutline(float radius) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        GlStateManager.glLineWidth(outlineWidth.floatValue());
        GlStateManager.color(-1f, -1f, -1f, -1f);
        buffer.begin(GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i <= 360; i++) {
            double percent = (double) i / 360;
            double progress = ((percent > 0.5) ? 1.0 - percent : percent) * 2.0;
            Color color = ColorUtil.setAlpha(lerpColor(Colors.INSTANCE.getGradient()[1], Colors.INSTANCE.getGradient()[0], (float) progress), outlineOpacity.intValue());

            double dir = Math.toRadians(i - 180.0);
            double x = -Math.sin(dir) * radius;
            double z = Math.cos(dir) * radius;
            buffer.pos(x, 0.0, z).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        }
        tessellator.draw();
    }

    private void setup() {
        GlStateManager.pushMatrix();
        glDepthMask(false);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.disableLighting();
        GlStateManager.disableTexture2D();
        GlStateManager.shadeModel(GL_SMOOTH);
        GlStateManager.disableCull();
    }

    private void restore() {
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        glDepthMask(true);
        glDisable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.popMatrix();
    }
}

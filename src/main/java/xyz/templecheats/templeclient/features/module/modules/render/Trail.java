package xyz.templecheats.templeclient.features.module.modules.render;

import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.client.Colors;
import xyz.templecheats.templeclient.util.render.shader.RainbowUtil;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;

import java.util.ArrayList;
import java.util.List;

import static xyz.templecheats.templeclient.util.math.MathUtil.lerp;

public class Trail extends Module {
    public static Trail INSTANCE;
    /*
     * Settings
     */
    private final DoubleSetting length = new DoubleSetting("Length", this, 1, 25, 10);
    private final BooleanSetting firstPerson = new BooleanSetting("FirstPerson", this, true);
    private final DoubleSetting lineWidth = new DoubleSetting("Line Width", this, 1.0, 10.0, 2.0);
    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", this, Mode.Fill);
    private final List<Point> points = new ArrayList<>();
    public final RainbowUtil rainbow = new RainbowUtil();

    public Trail() {
        super("Trail", "Draw trail behind player", Keyboard.KEY_NONE, Category.Render);
        registerSettings( firstPerson, length, lineWidth, mode);
        INSTANCE = this;
    }
    private enum Mode {
        Line,
        Fill
    }

    private boolean check() {
        if (firstPerson.booleanValue() && mc.gameSettings.thirdPersonView == 0) {
            return true;
        }
        else return mc.gameSettings.thirdPersonView != 0;
    }

    @Override
    public void onRenderWorld(float partialTicks) {
        long currentTime = System.currentTimeMillis();

        points.removeIf(point -> (currentTime - point.time) > length.doubleValue() * 100);
        if (mc.player == null || mc.world == null) return;

        for (EntityPlayer player : mc.world.playerEntities) {
            if (player.isDead || player.isInvisible()) {
                continue;
            }
            if (player == mc.player && check()) {

                double x = lerp((float) player.lastTickPosX, (float) player.posX, partialTicks);
                double y = lerp((float) player.lastTickPosY, (float) player.posY, partialTicks);
                double z = lerp((float) player.lastTickPosZ, (float) player.posZ, partialTicks);

                points.add(new Point(new Vec3d(x, y, z)));
                setup();
                switch (mode.value()) {
                    case Line:
                        renderLineStrip(points, false);
                        break;
                    case Fill:
                        drawTrail();
                        break;
                }
                restore();
                GL11.glColor4f(1,1,1,1);
            }
        }
    }

    private void drawTrail() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(GL11.GL_QUAD_STRIP, DefaultVertexFormats.POSITION_COLOR);

        double x = mc.getRenderManager().viewerPosX;
        double y = mc.getRenderManager().viewerPosY;
        double z = mc.getRenderManager().viewerPosZ;

        int index = 0;
        for (Point point : points) {
            int color = rainbow.rainbowProgress(5, index, Colors.INSTANCE.gradientColor1.getColor().getRGB(), Colors.INSTANCE.gradientColor2.getColor().getRGB());
            float red = (float) (color >> 16 & 255) / 255.0F;
            float green = (float) (color >> 8 & 255) / 255.0F;
            float blue = (float) (color & 255) / 255.0F;
            float alpha = (float) index / (float) points.size() * 0.7f;

            Vec3d vec = point.pos.subtract(x, y, z);

            buffer.pos(vec.x, vec.y + mc.player.height, vec.z).color(red, green, blue, alpha).endVertex();
            buffer.pos(vec.x, vec.y, vec.z).color(red, green, blue, alpha).endVertex();
            index++;
        }
        tessellator.draw();

        renderLineStrip(points, true);
        renderLineStrip(points, false);

    }

    private void renderLineStrip(List<Point> points, boolean withHeight) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        GlStateManager.glLineWidth(lineWidth.floatValue());
        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);

        double x = mc.getRenderManager().viewerPosX;
        double y = mc.getRenderManager().viewerPosY;
        double z = mc.getRenderManager().viewerPosZ;

        int index = 0;
        for (Point point : points) {
            int color = rainbow.rainbowProgress(5, index, Colors.INSTANCE.gradientColor1.getColor().getRGB(), Colors.INSTANCE.gradientColor2.getColor().getRGB());
            float red = (float) (color >> 16 & 255) / 255.0F;
            float green = (float) (color >> 8 & 255) / 255.0F;
            float blue = (float) (color & 255) / 255.0F;
            float alpha = (float) index / (float) points.size() * 0.7f;
            alpha = Math.min(alpha, 1);

            Vec3d vec = point.pos.subtract(x, y, z);
            if (withHeight) {
                buffer.pos(vec.x, vec.y + mc.player.height, vec.z).color(red, green, blue, alpha).endVertex();
            }
            else {
                buffer.pos(vec.x, vec.y, vec.z).color(red, green, blue, alpha).endVertex();
            }
            index++;
        }

        tessellator.draw();
    }

    private void setup() {
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        GlStateManager.disableAlpha();
        GL11.glColor4f(1, 1, 1, 0.5f);

    }

    private void restore() {
        GlStateManager.enableAlpha();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    static class Point {
        public Vec3d pos;
        public long time;

        public Point(Vec3d pos) {
            this.pos = pos;
            this.time = System.currentTimeMillis();
        }
    }
}

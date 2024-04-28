package xyz.templecheats.templeclient.util.render.enums;

import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.GL11;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.render.shader.impl.GradientShader;

public enum ProgressBoxModifiers {
    Static {
        @Override
        public void renderBreaking(AxisAlignedBB bb, float progress, float opacity) {
            renderBreaking(bb, opacity - 0.05f);
        }
    },
    Grow {
        @Override
        public void renderBreaking(AxisAlignedBB bb, float progress, float opacity) {
            renderBreaking(bb.shrink(0.5 - progress * 0.5), opacity);
        }
    },
    Shrink {
        @Override
        public void renderBreaking(AxisAlignedBB bb, float progress, float opacity) {
            renderBreaking(bb.shrink(progress * 0.5), opacity);
        }
    },
    Cross {
        @Override
        public void renderBreaking(AxisAlignedBB bb, float progress, float opacity) {
            renderBreaking(bb.shrink(0.5 - progress * 0.5), opacity);
            renderBreaking(bb.shrink(progress * 0.5), opacity);
        }
    },
    UnFill {
        @Override
        public void renderBreaking(AxisAlignedBB bb, float progress, float opacity) {
            renderBreaking(bb.contract(0, progress, 0), opacity);
        }
    },
    Fill {
        @Override
        public void renderBreaking(AxisAlignedBB bb, float progress, float opacity) {
            renderBreaking(bb.contract(0, 1 - progress, 0), opacity);
        }
    },
    Fade {
        @Override
        public void renderBreaking(AxisAlignedBB bb, float progress, float opacity) {
            renderBreaking(bb, opacity * (1 - progress));
        }
    };

    void renderBreaking(AxisAlignedBB bb, float opacity) {
        GradientShader.setup(opacity / 255f);
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        RenderUtil.boxShader(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
        RenderUtil.outlineShader(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);

        GL11.glPopAttrib();
        GL11.glPopMatrix();
        GradientShader.finish();
    }

    public abstract void renderBreaking(AxisAlignedBB bb, float progress, float opacity);
}

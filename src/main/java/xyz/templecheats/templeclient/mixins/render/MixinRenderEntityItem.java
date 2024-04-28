package xyz.templecheats.templeclient.mixins.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.templecheats.templeclient.features.module.modules.render.ItemPhysic;
import xyz.templecheats.templeclient.mixins.accessor.IRender;

import java.util.Random;

import static xyz.templecheats.templeclient.util.Globals.mc;

@Mixin(RenderEntityItem.class)
public abstract class MixinRenderEntityItem {

    @Unique private double rotation;
    @Final @Shadow private Random random;

    @Inject(method = "transformModelCount", at = @At("HEAD"), cancellable = true)
    private void onItemAnim(EntityItem itemIn, double p_177077_2_, double p_177077_4_, double p_177077_6_, float p_177077_8_, IBakedModel p_177077_9_, CallbackInfoReturnable<Integer> cir) {
        if (ItemPhysic.INSTANCE.isEnabled()) {
            float tick = ItemPhysic.INSTANCE.tick;
            float rotate = ItemPhysic.INSTANCE.rotateSpeed.floatValue();
            boolean oldRotation = ItemPhysic.INSTANCE.oldRotation.booleanValue();

            RenderEntityItem renderer = (RenderEntityItem) (Object) this;

            rotation = (System.nanoTime() - tick) / 10000000.0D * rotate / 12.5F;
            if (!mc.inGameHasFocus) {
                rotation = 0.0D;
            }

            ItemStack itemstack = itemIn.getItem();
            random.setSeed((itemstack != null) && (itemstack.getItem() != null) ? Item.getIdFromItem(itemstack.getItem()) + itemstack.getMetadata() : 187);

            GlStateManager.pushMatrix();

            renderer.bindTexture(getEntityTexture());
            renderer.getRenderManager().renderEngine.getTexture(getEntityTexture()).setBlurMipmap(false , false);

            GlStateManager.enableRescaleNormal();
            GlStateManager.alphaFunc(516 , 0.1F);
            GlStateManager.enableBlend();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA , GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA , GlStateManager.SourceFactor.ONE , GlStateManager.DestFactor.ZERO);
            GlStateManager.pushMatrix();
            IBakedModel ibakedmodel = mc.getRenderItem().getItemModelWithOverrides(itemstack, itemIn.world, null);

            boolean is3D = ibakedmodel.isGui3d();

            ItemStack stack = itemIn.getItem();
            int count = 1;
            if (stack.getCount() > 48) {
                count = 5;
            } else if (stack.getCount() > 32) {
                count = 4;
            } else if (stack.getCount() > 16) {
                count = 3;
            } else if (stack.getCount() > 1) {
                count = 2;
            }

            GlStateManager.translate((float) p_177077_2_ , (float) p_177077_4_ , (float) p_177077_6_);

            GL11.glRotatef(90.0F , 1.0F , 0.0F , 0.0F);
            GL11.glRotatef(itemIn.rotationYaw , 0.0F , 0.0F , 1.0F);
            if (is3D) {
                GlStateManager.translate(0.0D , -0.2D , -0.08D);
            } else {
                GlStateManager.translate(0.0D , 0.0D , -0.04D);
            }
            if ((is3D) || (mc.getRenderManager().options != null)) {
                if (is3D) {
                    if (!itemIn.onGround) {
                        itemIn.rotationPitch = ((float) (itemIn.rotationPitch + rotation));
                    } else if (oldRotation) if (itemIn.rotationPitch != 0.0F && itemIn.rotationPitch != 90.0F && itemIn.rotationPitch != 180.0F && itemIn.rotationPitch != 270.0F) {
                        int minIndex = getIndex(itemIn);

                        switch (minIndex) {
                            case 0:
                                itemIn.rotationPitch += (float) ((itemIn.rotationPitch < 0.0F) ? rotation : -rotation);
                                break;
                            case 1:
                                itemIn.rotationPitch += (float) ((itemIn.rotationPitch - 90.0F < 0.0F) ? rotation : -rotation);
                                break;
                            case 2:
                                itemIn.rotationPitch += (float) ((itemIn.rotationPitch - 180.0F < 0.0F) ? rotation : -rotation);
                                break;
                            case 3:
                                itemIn.rotationPitch += (float) ((itemIn.rotationPitch - 270.0F < 0.0F) ? rotation : -rotation);
                                break;
                        }
                    }
                } else if (!Double.isNaN(itemIn.posX) && !Double.isNaN(itemIn.posY) && !Double.isNaN(itemIn.posZ) && itemIn.world != null) {
                    if (itemIn.onGround) {
                        itemIn.rotationPitch = 180.0F;
                    } else {
                        itemIn.rotationPitch = ((float) (itemIn.rotationPitch + rotation));
                    }
                }
                double height = 0.2D;
                if (is3D) {
                    GlStateManager.translate(0.0D , height , 0.0D);
                }
                GlStateManager.rotate(itemIn.rotationPitch , 1.0F , 0.0F , 0.0F);
                if (is3D) {
                    GlStateManager.translate(0.0D , -height , 0.0D);
                }
            }
            GlStateManager.color(1.0F , 1.0F , 1.0F , 1.0F);

            double scale = ItemPhysic.INSTANCE.scale.floatValue();
            GlStateManager.scale(scale , scale , scale);

            boolean renderOutlines = ((IRender) renderer).getRenderOutlines();

            float xScale = ibakedmodel.getItemCameraTransforms().ground.scale.x;
            float yScale = ibakedmodel.getItemCameraTransforms().ground.scale.y;
            float zScale = ibakedmodel.getItemCameraTransforms().ground.scale.z;

            if (!is3D) {
                float xTranslation = -0.0F * (count - 1) * xScale;
                float yTranslation = -0.0F * (count - 1) * yScale;
                float zTranslation = -0.09375F * (count - 1) * 0.5f * zScale;
                GlStateManager.translate(xTranslation , yTranslation , zTranslation);
            }
            if (renderOutlines) {
                GlStateManager.enableColorMaterial();
                try {
                    GlStateManager.enableOutlineMode(((IRender) renderer).callGetTeamColor(itemIn));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for (int k = 0; k < count; k++) {
                GlStateManager.pushMatrix();
                if (is3D) {
                    if (k > 0) {
                        float xTranslation = (random.nextFloat() * 2.0F - 1.0F) * 0.15F + 0.2f;
                        float yTranslation = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        float zTranslation = (random.nextFloat() * 2.0F - 1.0F) * 0.15F + 0.2f;
                        GlStateManager.translate(xTranslation, yTranslation, zTranslation);
                    }
                    mc.getRenderItem().renderItem(itemstack , ibakedmodel);
                    GlStateManager.popMatrix();
                } else {
                    mc.getRenderItem().renderItem(itemstack , ibakedmodel);
                    GlStateManager.popMatrix();
                    GlStateManager.translate(0.0F , 0.0F , 0.09375);
                }
            }
            if (renderOutlines) {
                GlStateManager.disableOutlineMode();
                GlStateManager.disableColorMaterial();
            }
            GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
            renderer.bindTexture(getEntityTexture());
            renderer.getRenderManager().renderEngine.getTexture(getEntityTexture()).restoreLastBlurMipmap();
            GlStateManager.popMatrix();

            cir.cancel();
        }
    }

    @Unique
    private static int getIndex(EntityItem itemIn) {
        double[] dirs = {
                Math.abs(itemIn.rotationPitch),
                Math.abs(itemIn.rotationPitch - 90.0F),
                Math.abs(itemIn.rotationPitch - 180.0F),
                Math.abs(itemIn.rotationPitch - 270.0F)
        };
        double minDir = dirs[0];
        int minIndex = 0;

        for (int i = 1; i < dirs.length; i++) {
            if (dirs[i] < minDir) {
                minDir = dirs[i];
                minIndex = i;
            }
        }
        return minIndex;
    }

    @Unique
    private ResourceLocation getEntityTexture() {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}
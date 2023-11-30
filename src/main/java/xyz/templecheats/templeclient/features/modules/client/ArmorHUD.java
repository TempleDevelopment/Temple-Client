package xyz.templecheats.templeclient.features.modules.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.modules.Module;

public class ArmorHUD extends Module {

    public static final ArmorHUD INSTANCE = new ArmorHUD();

    private final Minecraft mc = Minecraft.getMinecraft();

    public ArmorHUD() {
        super("ArmorHUD", Keyboard.KEY_NONE, Module.Category.CLIENT);
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            ScaledResolution sr = new ScaledResolution(mc);
            renderArmorHUD(sr);
        }
    }

    private void renderArmorHUD(ScaledResolution sr) {
        int width = sr.getScaledWidth();
        int height = sr.getScaledHeight();

        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();

        int x = width / 2 + 74;

        for (ItemStack stack : mc.player.inventory.armorInventory) {
            if (!stack.isEmpty()) {
                mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, height - 58);
                mc.getRenderItem().renderItemOverlays(mc.fontRenderer, stack, x, height - 58);
            }

            x -= 21;
        }

        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }
}

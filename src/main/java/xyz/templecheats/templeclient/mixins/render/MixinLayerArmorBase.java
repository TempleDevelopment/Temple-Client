package xyz.templecheats.templeclient.mixins.render;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import xyz.templecheats.templeclient.manager.ModuleManager;
import xyz.templecheats.templeclient.features.module.modules.render.EnchantColor;

@Mixin(LayerArmorBase.class)
public class MixinLayerArmorBase {
    @Redirect(method = {
            "renderEnchantedGlint" }, at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/GlStateManager.color(FFFF)V", ordinal = 1))
    private static void renderEnchantedGlint(final float red, final float green, final float blue, final float alpha) {

        GlStateManager.color(
                ModuleManager.getModule(EnchantColor.class).isEnabled() ? ((float) EnchantColor.getColor().getRed() / 255.0f)
                        : red,
                ModuleManager.getModule(EnchantColor.class).isEnabled()
                        ? ((float) EnchantColor.getColor().getGreen() / 255.0f)
                        : green,
                ModuleManager.getModule(EnchantColor.class).isEnabled()
                        ? ((float) EnchantColor.getColor().getBlue() / 255.0f)
                        : blue,
                ModuleManager.getModule(EnchantColor.class).isEnabled()
                        ? ((float) EnchantColor.getColor().getAlpha() / 255.0f)
                        : alpha);
    }
}
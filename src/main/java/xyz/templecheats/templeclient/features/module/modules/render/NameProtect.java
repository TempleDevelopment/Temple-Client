package xyz.templecheats.templeclient.features.module.modules.render;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;

public class NameProtect extends Module {
    public NameProtect() {
        super("NameProtect", "Hides player names", Keyboard.KEY_NONE, Category.Render);
    }

    @SubscribeEvent
    public void onRenderLiving(RenderLivingEvent.Specials.Pre event) {
        if (isEnabled() && event.getEntity() != null) {
            EntityLivingBase entity = event.getEntity();

            if (entity instanceof net.minecraft.entity.player.EntityPlayer) {
                event.setCanceled(true);
            }
        }
    }
}
package xyz.templecheats.templeclient.features.modules.render;

import xyz.templecheats.templeclient.features.modules.Module;
import org.lwjgl.input.Keyboard;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NameProtect extends Module {
    public NameProtect() {
        super("NameProtect", Keyboard.KEY_NONE, Category.RENDER);
    }

    @SubscribeEvent
    public void onRenderLiving(RenderLivingEvent.Specials.Pre event) {
        if (isEnabled() && event.getEntity() != null) {
            EntityLivingBase entity = event.getEntity();

            // Check if the entity is a player and hide their name tag
            if (entity instanceof net.minecraft.entity.player.EntityPlayer) {
                event.setCanceled(true);
            }
        }
    }
}

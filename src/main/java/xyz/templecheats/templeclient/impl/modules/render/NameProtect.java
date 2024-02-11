package xyz.templecheats.templeclient.impl.modules.render;

import xyz.templecheats.templeclient.impl.modules.Module;
import org.lwjgl.input.Keyboard;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NameProtect extends Module {
    public NameProtect() {
        super("NameProtect","Hides player names", Keyboard.KEY_NONE, Category.Render);
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

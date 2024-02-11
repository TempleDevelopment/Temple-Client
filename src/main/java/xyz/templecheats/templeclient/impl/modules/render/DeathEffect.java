package xyz.templecheats.templeclient.impl.modules.render;

import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.api.setting.Setting;
import xyz.templecheats.templeclient.impl.modules.Module;

public class DeathEffect extends Module {

    public DeathEffect() {
        super("DeathEffect", "Spawns a thunder at player's location when they die", Keyboard.KEY_NONE, Category.Render);
    }

    @SubscribeEvent
    public void onLivingDeathEvent(LivingDeathEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            player.world.addWeatherEffect(new EntityLightningBolt(player.world, player.posX, player.posY, player.posZ, false));
        }
    }
}
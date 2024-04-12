package xyz.templecheats.templeclient.features.module.modules.misc;

import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;

public class Particles extends Module {
    public Particles() {
        super("Particles", "Customizes particle volume and texture", Keyboard.KEY_NONE, Category.Misc);
    }

    @SubscribeEvent
    public void onAttack(AttackEntityEvent e) {
        for (int i = 12; i >= 0; i = i - 1) {
            mc.player.onCriticalHit(e.getTarget());
        }
    }
}
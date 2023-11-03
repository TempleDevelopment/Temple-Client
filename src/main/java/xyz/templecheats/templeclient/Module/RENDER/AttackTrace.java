package xyz.templecheats.templeclient.Module.RENDER;

import xyz.templecheats.templeclient.Module.Module;
import xyz.templecheats.templeclient.Utils.RenderUtils;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class AttackTrace extends Module {
    public static Entity attackEntity = null;

    public AttackTrace() {
        super("AttackTrace", Keyboard.KEY_NONE, Category.RENDER);
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent e) {
        if (attackEntity != null && !attackEntity.isDead) {
            if (mc.player.getDistance(attackEntity) < 10) {
                RenderUtils.trace(mc, attackEntity, mc.getRenderPartialTicks(), 0);
            }
        }
    }

    @SubscribeEvent
    public void onAttack(AttackEntityEvent e) {
        attackEntity = e.getTarget();
    }
}
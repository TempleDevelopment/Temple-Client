package xyz.templecheats.templeclient.features.module.modules.chat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.event.events.network.PacketEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.client.hud.notification.NotificationType;
import xyz.templecheats.templeclient.features.module.modules.client.hud.notification.Notifications;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TotemPopNotify extends Module {
    /*
     * Settings
     */
    private final EnumSetting<Mode> mode = new EnumSetting <>("Mode", this, Mode.Chat);
    private final BooleanSetting countSelf = new BooleanSetting("Count Self", this, true);

    /*
     * Variables
     */ 
    private final Map<EntityPlayer, Integer> popped = new HashMap <>();

    public TotemPopNotify() {
        super("PopNotify", "Count popped totems", Keyboard.KEY_NONE, Category.Chat);
        registerSettings(countSelf, mode);
    }

    @Override
    public void onEnable() {
        popped.clear();
    }

    @Override
    public void onDisable() {
        popped.clear();
    }

    @Listener
    public void onTotemPop(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketEntityStatus && ((SPacketEntityStatus) event.getPacket()).getOpCode() == 35 && mc.player.isEntityAlive()) {
            Entity player = ((SPacketEntityStatus) event.getPacket()).getEntity(mc.world);
            if (player instanceof EntityPlayer) {
                boolean self = player == mc.player;

                if (!countSelf.booleanValue() && self) return;

                int count = (popped.getOrDefault(player, 0)) + 1;
                popped.put((EntityPlayer) player, count);

                if (mode.value() == Mode.Chat || mode.value() == Mode.Both) {
                    sendMessage(TextFormatting.RED + "[Temple] " + TextFormatting.WHITE + player.getName() + " popped " + formatCount(count) + "!");
                }
                if (mode.value() == Mode.Bar || mode.value() == Mode.Both) {
                    Notifications.addMessage("Totem pop", player.getName() + " popped " + formatCount(count) + "!", NotificationType.INFO);
                }
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.player == null || mc.world == null || event.phase != TickEvent.Phase.END) return;

        if (!mc.player.isEntityAlive()) {
            popped.clear();
            return;
        }
        Iterator <Map.Entry<EntityPlayer, Integer>> iterator = popped.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<EntityPlayer, Integer> entry = iterator.next();
            EntityPlayer diedPlayer = entry.getKey();
            Integer poppedCount = entry.getValue();

            if (diedPlayer == mc.player || diedPlayer.isEntityAlive()) continue;

            if (mode.value() == Mode.Chat || mode.value() == Mode.Both) {
                sendMessage(diedPlayer.getName() + " died after popping " + formatCount(poppedCount) + "!");
            }
            if (mode.value() == Mode.Bar || mode.value() == Mode.Both) {
                Notifications.addMessage("Totem pop", diedPlayer.getName() + " died after popping " + formatCount(poppedCount) + "!", NotificationType.INFO);
            }
            iterator.remove();
        }
    }

    protected void sendMessage(String message) {
        if (mc.player != null) {
            mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString(message));
        }
    }

    private String formatCount(int count) {
        return count + ((count > 1) ? " totems" : " totem");
    }

    enum Mode {
        Chat,
        Bar,
        Both
    }
}

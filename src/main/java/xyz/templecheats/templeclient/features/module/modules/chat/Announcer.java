package xyz.templecheats.templeclient.features.module.modules.chat;

import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemFood;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.math.MathUtil;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;
import xyz.templecheats.templeclient.util.time.TimerUtil;

import java.text.DecimalFormat;
import java.util.Random;

public class Announcer extends Module {
    /*
     * Settings
     */
    public final BooleanSetting move = new BooleanSetting("Move", this, true);
    public final BooleanSetting eat = new BooleanSetting("Eat", this, true);
    public final IntSetting delay = new IntSetting("Delay", this, 1, 10, 5);

    /*
     * Variables
     */
    private double lastPositionX;
    private double lastPositionY;
    private double lastPositionZ;
    private int eaten;
    private final TimerUtil walkTimer = new TimerUtil();

    public Announcer() {
        super("Announcer", "Announces what you are doing in chat", 0, Module.Category.Chat);
        registerSettings(move, eat, delay);
    }

    @Override
    public void onEnable() {
        eaten = 0;
        walkTimer.reset();
    }

    @Override
    public void onUpdate() {
        if (walkTimer.hasReached(delay.intValue() * 1000)) {
            double traveledX = lastPositionX - mc.player.lastTickPosX;
            double traveledY = lastPositionY - mc.player.lastTickPosY;
            double traveledZ = lastPositionZ - mc.player.lastTickPosZ;

            double traveledDistance = Math.sqrt(traveledX * traveledX + traveledY * traveledY + traveledZ * traveledZ);

            if (move.booleanValue() && traveledDistance >= 0) {
                mc.player.sendChatMessage(getWalkMessage()
                        .replace("{blocks}", new DecimalFormat("0.00").format(traveledDistance)));

                lastPositionX = mc.player.lastTickPosX;
                lastPositionY = mc.player.lastTickPosY;
                lastPositionZ = mc.player.lastTickPosZ;
            }

            walkTimer.reset();
        }
    }

    @SubscribeEvent
    public void onUseItem(LivingEntityUseItemEvent.Finish event) {
        int random = MathUtil.randomBetween(1, 6);

        if (eat.booleanValue()
                && event.getEntity() == mc.player
                && (event.getItem().getItem() instanceof ItemFood
                || event.getItem().getItem() instanceof ItemAppleGold)) {

            ++eaten;

            if (eaten >= random) {
                mc.player.sendChatMessage(getEatMessage()
                        .replace("{amount}", "" + eaten)
                        .replace("{name}", event.getItem().getDisplayName()));

                eaten = 0;
            }
        }
    }

    private String getWalkMessage() {
        String[] walkMessage = {
                "I just walked {blocks} blocks",
        };
        return walkMessage[new Random().nextInt(walkMessage.length)];
    }

    private String getEatMessage() {
        String[] eatMessage = {
                "I just ate {amount} {name}",
        };
        return eatMessage[new Random().nextInt(eatMessage.length)];
    }
}

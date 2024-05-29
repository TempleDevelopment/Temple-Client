package xyz.templecheats.templeclient.features.module.modules.chat;

import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.event.events.network.PacketEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.math.MathUtil;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;
import xyz.templecheats.templeclient.util.time.TimerUtil;
import java.text.DecimalFormat;
import java.util.Random;

public class Announcer extends Module {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    public final BooleanSetting move = new BooleanSetting("Move", this, true);
    public final BooleanSetting place = new BooleanSetting("Place", this, true);
    public final BooleanSetting attack = new BooleanSetting("Attack", this, true);
    public final BooleanSetting eat = new BooleanSetting("Eat", this, true);
    public final IntSetting delay = new IntSetting("Delay", this, 1, 10, 5);

    /****************************************************************
     *                      Variables
     ****************************************************************/
    private double lastPositionX;
    private double lastPositionY;
    private double lastPositionZ;
    private int blocksPlaced = 0;
    private int eaten = 0;
    private final TimerUtil walkTimer = new TimerUtil();
    private final TimerUtil actionTimer = new TimerUtil();

    public Announcer() {
        super("Announcer", "Announces what you are doing in chat", 0, Module.Category.Chat);
        registerSettings(move, place, attack, eat, delay);
    }

    @Override
    public void onEnable() {
        blocksPlaced = 0;
        eaten = 0;
        walkTimer.reset();
        actionTimer.reset();
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

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent event) {
        if (attack.booleanValue() && !(event.getTarget() instanceof EntityEnderCrystal)) {
            if (actionTimer.hasReached(delay.intValue() * 300)) {
                mc.player.sendChatMessage(getAttackMessage()
                        .replace("{name}", event.getTarget().getName())
                        .replace("{item}", mc.player.getHeldItemMainhand().getDisplayName()));

                actionTimer.reset();
            }
        }
    }

    @Listener
    public void onBlockPlace(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemBlock) {
            ++blocksPlaced;
            int random = MathUtil.randomBetween(1, 6);

            if (place.booleanValue() && blocksPlaced >= random) {
                mc.player.sendChatMessage(getPlaceMessage()
                        .replace("{amount}", "" + blocksPlaced)
                        .replace("{name}", mc.player.getHeldItemMainhand().getDisplayName()));

                blocksPlaced = 0;
            }
        }
    }

    private String getWalkMessage() {
        String[] walkMessages = {
                "I just walked {blocks} blocks",
        };
        return walkMessages[new Random().nextInt(walkMessages.length)];
    }

    private String getEatMessage() {
        String[] eatMessages = {
                "I just ate {amount} {name}",
        };
        return eatMessages[new Random().nextInt(eatMessages.length)];
    }

    private String getAttackMessage() {
        String[] attackMessages = {
                "I just attacked {name} with {item}",
        };
        return attackMessages[new Random().nextInt(attackMessages.length)];
    }

    private String getPlaceMessage() {
        String[] placeMessages = {
                "I just placed {amount} {name}",
        };
        return placeMessages[new Random().nextInt(placeMessages.length)];
    }
}

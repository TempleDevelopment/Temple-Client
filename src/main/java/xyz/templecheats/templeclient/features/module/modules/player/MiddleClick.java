package xyz.templecheats.templeclient.features.module.modules.player;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.friend.Friend;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;

public class MiddleClick extends Module {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final BooleanSetting friend = new BooleanSetting("Friend", this, true);
    private final BooleanSetting pearl = new BooleanSetting("Pearl", this, false);
    /****************************************************************
     *                      Variables
     ****************************************************************/
    private boolean isButtonPressed = false;

    public MiddleClick() {
        super("MiddleClick", "Actions for middle clicking", Keyboard.KEY_NONE, Category.Player);
        registerSettings(friend, pearl);
    }

    @Override
    public void onUpdate() {
        if (mc.currentScreen == null) {
            if (Mouse.isButtonDown(2)) {
                if (!isButtonPressed) {
                    isButtonPressed = true;
                    if (friend.booleanValue()) {
                        friend();
                    } else if (pearl.booleanValue()) {
                        final RayTraceResult result = mc.objectMouseOver;
                        if (result != null && result.typeOfHit == RayTraceResult.Type.MISS) {
                            final int pearlSlot = findPearlInHotbar(mc);
                            if (pearlSlot != -1) {
                                final int oldSlot = mc.player.inventory.currentItem;
                                mc.player.inventory.currentItem = pearlSlot;
                                mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);
                                mc.player.inventory.currentItem = oldSlot;
                            }
                        }
                    }
                }
            } else {
                isButtonPressed = false;
            }
        }
    }

    private void friend() {
        RayTraceResult rayTraceResult = mc.objectMouseOver;
        if (rayTraceResult != null && rayTraceResult.entityHit instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) rayTraceResult.entityHit;
            Friend friend = TempleClient.friendManager.getFriends().stream().filter(f -> f.getName().equalsIgnoreCase(player.getName())).findFirst().orElse(null);
            if (friend == null) {
                TempleClient.friendManager.addFriend(player.getName());
                mc.player.sendMessage(new TextComponentString(TextFormatting.AQUA + "[TempleClient] " + TextFormatting.RESET + "Friend added: " + player.getName()));
            } else {
                TempleClient.friendManager.removeFriend(player.getName());
                mc.player.sendMessage(new TextComponentString(TextFormatting.AQUA + "[TempleClient] " + TextFormatting.RESET + "Friend removed: " + player.getName()));
            }
        }
    }

    private boolean isItemStackPearl(final ItemStack itemStack) {
        return itemStack.getItem() instanceof ItemEnderPearl;
    }

    private int findPearlInHotbar(final Minecraft mc) {
        for (int index = 0; InventoryPlayer.isHotbar(index); index++) {
            if (isItemStackPearl(mc.player.inventory.getStackInSlot(index))) return index;
        }
        return -1;
    }
}
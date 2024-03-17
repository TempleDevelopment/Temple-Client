package xyz.templecheats.templeclient.features.module.modules.client;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.friend.Friend;

public class MiddleClick extends Module {

    private boolean isButtonPressed = false;

    public MiddleClick() {
        super("MiddleClick", "Adds a player to your friend list when you middle click them", Keyboard.KEY_NONE, Category.Client);
    }

    @Override
    public void onUpdate() {
        if (mc.currentScreen == null) {
            if (Mouse.isButtonDown(2)) {
                if (!isButtonPressed) {
                    isButtonPressed = true;
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
            } else {
                isButtonPressed = false;
            }
        }
    }
}
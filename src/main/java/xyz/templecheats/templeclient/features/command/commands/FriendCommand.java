package xyz.templecheats.templeclient.features.command.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.features.command.Command;
import xyz.templecheats.templeclient.util.friend.Friend;

public class FriendCommand extends Command {

    @Override
    public String getName() {
        return ".friend";
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            sendMessage("Invalid syntax. Use .friend add <name>, .friend remove <name> or .friend list", true);
            return;
        }

        String action = args[1];

        if (action.equalsIgnoreCase("add")) {
            if (args.length != 3) {
                sendMessage("Invalid syntax. Use .friend add <name>", true);
                return;
            }
            String name = args[2];
            TempleClient.friendManager.addFriend(name);
            sendMessage("Added " + name + " to friends list.", false);
        } else if (action.equalsIgnoreCase("remove")) {
            if (args.length != 3) {
                sendMessage("Invalid syntax. Use .friend remove <name>", true);
                return;
            }
            String name = args[2];
            TempleClient.friendManager.removeFriend(name);
            sendMessage("Removed " + name + " from friends list.", false);
        } else if (action.equalsIgnoreCase("list")) {
            StringBuilder friendsList = new StringBuilder("Friends: ");
            for (Friend friend: TempleClient.friendManager.getFriends()) {
                friendsList.append(friend.getName()).append(", ");
            }
            sendMessage(friendsList.toString(), false);
        } else {
            sendMessage("Invalid action. Use .friend add <name>, .friend remove <name> or .friend list", true);
        }
    }

    private void sendMessage(String message, boolean isError) {
        String templePrefix = isError ? TextFormatting.RED + "[Temple] " : TextFormatting.AQUA + "[Temple] ";
        String formattedMessage = isError ? TextFormatting.WHITE + message : TextFormatting.RESET + message;
        Minecraft.getMinecraft().player.sendMessage(
                new TextComponentString(templePrefix + formattedMessage)
        );
    }
}
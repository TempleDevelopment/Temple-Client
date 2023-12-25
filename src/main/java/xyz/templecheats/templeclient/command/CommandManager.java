package xyz.templecheats.templeclient.command;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private final Map<String, Command> commands = new HashMap<>();

    public void registerCommand(Command command) {
        commands.put(command.getName(), command);
    }

    public boolean executeCommand(String message) {
        String[] parts = message.split(" ");
        Command command = commands.get(parts[0]);
        if (command != null) {
            command.execute(parts);
            return true;
        }
        return false;
    }
}

package xyz.templecheats.templeclient.impl.command;

import xyz.templecheats.templeclient.impl.command.commands.BindCommand;
import xyz.templecheats.templeclient.impl.command.commands.CoordsCommand;
import xyz.templecheats.templeclient.impl.command.commands.HelpCommand;
import xyz.templecheats.templeclient.impl.command.commands.IpCommand;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private static CommandManager commandManager;
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

    public void commandInit() {
        registerCommand(new BindCommand());
        registerCommand(new CoordsCommand());
        registerCommand(new HelpCommand());
        registerCommand(new IpCommand());
    }
}

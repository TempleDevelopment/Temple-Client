package xyz.templecheats.templeclient.manager;

import xyz.templecheats.templeclient.features.command.Command;
import xyz.templecheats.templeclient.features.command.commands.*;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    public CommandManager() {
        commandInit();
    }

    private final Map < String, Command > commands = new HashMap < > ();

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
        registerCommand(new ClickguiCommand());
        registerCommand(new CoordsCommand());
        registerCommand(new DisconnectCommand());
        registerCommand(new FakePlayerCommand());
        registerCommand(new FriendCommand());
        registerCommand(new HelpCommand());
        registerCommand(new IpCommand());
        registerCommand(new NameMcCommand());
        registerCommand(new PanicCommand());
        registerCommand(new ToggleCommand());
    }
}
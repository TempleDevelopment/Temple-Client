package xyz.templecheats.templeclient.features.command.commands;

import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.features.command.Command;
import xyz.templecheats.templeclient.features.module.Module;

public class PanicCommand extends Command {

    @Override
    public String getName() {
        return ".panic";
    }

    @Override
    public void execute(String[] args) {
        Module panicModule = TempleClient.moduleManager.getModuleByName("Panic");
        panicModule.setToggled(true);
    }
}
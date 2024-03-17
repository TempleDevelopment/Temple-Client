package xyz.templecheats.templeclient.features.command;

public abstract class Command {
    public abstract String getName();

    public abstract void execute(String[] args);
}

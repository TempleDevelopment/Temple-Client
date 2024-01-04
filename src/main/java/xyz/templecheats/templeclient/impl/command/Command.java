package xyz.templecheats.templeclient.impl.command;

public interface Command {
    String getName();

    void execute(String[] args);
}

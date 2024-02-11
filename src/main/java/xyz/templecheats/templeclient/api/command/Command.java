package xyz.templecheats.templeclient.api.command;

public interface Command {
    String getName();

    void execute(String[] args);
}

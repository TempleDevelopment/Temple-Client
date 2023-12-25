package xyz.templecheats.templeclient.command;

public interface Command {
    String getName();

    void execute(String[] args);
}

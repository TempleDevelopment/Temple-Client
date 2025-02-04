package xyz.templecheats.templeclient.features.module.modules.misc;

import net.minecraft.world.GameType;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;

import static xyz.templecheats.templeclient.features.module.modules.misc.Gamemode.ClientGameType.CREATIVE;

public class Gamemode extends Module {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final EnumSetting<ClientGameType> gameType = new EnumSetting<>("Gamemode", this, CREATIVE);

    public Gamemode() {
        super("Gamemode", "Change your gamemode client-side", Keyboard.KEY_NONE, Category.Misc);
        this.registerSettings(gameType);
    }

    @Override
    public void onUpdate() {
        switch (gameType.value()) {
            case SURVIVAL:
                mc.playerController.setGameType(GameType.SURVIVAL);
                break;
            case CREATIVE:
                mc.playerController.setGameType(GameType.CREATIVE);
                break;
            case ADVENTURE:
                mc.playerController.setGameType(GameType.ADVENTURE);
                break;
            case SPECTATOR:
                mc.playerController.setGameType(GameType.SPECTATOR);
                break;
        }
    }

    public enum ClientGameType {
        SURVIVAL("Survival"),
        CREATIVE("Creative"),
        ADVENTURE("Adventure"),
        SPECTATOR("Spectator");

        private final String name;

        ClientGameType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
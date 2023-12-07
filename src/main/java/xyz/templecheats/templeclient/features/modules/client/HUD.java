package xyz.templecheats.templeclient.features.modules.client;

import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.features.modules.Module;
import xyz.templecheats.templeclient.features.modules.client.hud.ArmorHUD;
import xyz.templecheats.templeclient.features.modules.client.hud.Coords;
import xyz.templecheats.templeclient.features.modules.client.hud.InventoryHUD;
import xyz.templecheats.templeclient.features.modules.client.hud.PlayerModel;
import xyz.templecheats.templeclient.gui.clickgui.setting.Setting;
import org.lwjgl.input.Keyboard;

public class HUD extends Module {

    public HUD() {
        super("HUD", Keyboard.KEY_NONE, Category.CLIENT);

        Setting armorSetting = new Setting("Armor", this, false);
        Setting coordsSetting = new Setting("Coords", this, false);
        Setting inventorySetting = new Setting("Inventory", this, false);
        Setting modelSetting = new Setting("PlayerModel", this, false);

        TempleClient.instance.settingsManager.rSetting(armorSetting);
        TempleClient.instance.settingsManager.rSetting(coordsSetting);
        TempleClient.instance.settingsManager.rSetting(inventorySetting);
        TempleClient.instance.settingsManager.rSetting(modelSetting);
    }

    @Override
    public void onUpdate() {
        boolean armorEnabled = TempleClient.instance.settingsManager.getSettingByName(this.name, "Armor").getValBoolean();
        boolean coordsEnabled = TempleClient.instance.settingsManager.getSettingByName(this.name, "Coords").getValBoolean();
        boolean inventoryEnabled = TempleClient.instance.settingsManager.getSettingByName(this.name, "Inventory").getValBoolean();
        boolean modelEnabled = TempleClient.instance.settingsManager.getSettingByName(this.name, "PlayerModel").getValBoolean();

        ArmorHUD armorHUD = ArmorHUD.INSTANCE;
        Coords coords = Coords.INSTANCE;
        InventoryHUD inventoryHUD = InventoryHUD.INSTANCE;
        PlayerModel playerModel = PlayerModel.INSTANCE;

        armorHUD.setToggled(armorEnabled);
        coords.setToggled(coordsEnabled);
        inventoryHUD.setToggled(inventoryEnabled);
        playerModel.setToggled(modelEnabled);
    }
}

package xyz.templecheats.templeclient.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.client.HUD;
import xyz.templecheats.templeclient.manager.ModuleManager;
import xyz.templecheats.templeclient.util.friend.Friend;
import xyz.templecheats.templeclient.util.setting.Setting;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final File mainDirectory;
    private final File modulesDirectory;
    private final File hudElementsDirectory;
    private final File friendsDirectory;
    private File altsDirectory;
    private File altsFile;

    public ConfigManager() {
        this.mainDirectory = new File(System.getProperty("user.dir") + File.separator + "Temple Client");
        this.modulesDirectory = new File(this.mainDirectory, "Modules");
        this.hudElementsDirectory = new File(this.mainDirectory, "Hud Elements");
        this.friendsDirectory = new File(this.mainDirectory, "Friends");
        this.altsDirectory = new File(this.mainDirectory, "Alts");

        if (!this.modulesDirectory.exists()) {
            this.modulesDirectory.mkdirs();
        }

        if (!this.hudElementsDirectory.exists()) {
            this.hudElementsDirectory.mkdirs();
        }

        if (!this.friendsDirectory.exists()) {
            this.friendsDirectory.mkdirs();
        }

        if (!this.altsDirectory.exists()) {
            this.altsDirectory.mkdirs();
        }
        this.altsFile = new File(this.altsDirectory, "alt_accounts.json");
    }

    public void saveAll() {
        this.saveModules();
        this.saveHud();
        this.saveFriends();
    }

    public void loadAll() {
        this.loadModules();
        this.loadHud();
        this.loadFriends();
    }

    private void saveModules() {

        //iterate through each module to save its config
        for (Module module: ModuleManager.getModules()) {

            //file that the config will be saved to
            final File moduleConfigFile = new File(this.modulesDirectory, module.getName() + ".json");

            //json object that will store panels of the module
            final JsonObject moduleObject = new JsonObject();

            //add panels
            moduleObject.addProperty("toggled", module.isToggled());
            moduleObject.addProperty("key", module.getKey());

            //add settings
            final List < Setting < ? >> settings = TempleClient.settingsManager.getSettingsByMod(module);
            if (!settings.isEmpty()) {
                final JsonObject settingsObject = new JsonObject();

                for (Setting < ? > setting : settings) {
                    setting.serialize(settingsObject);
                }

                moduleObject.add("settings", settingsObject);
            }

            //write to file
            try {
                final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(moduleConfigFile));
                bufferedWriter.write(GSON.toJson(moduleObject));
                bufferedWriter.close();
            } catch (Throwable t) {
                System.err.println("Could not save config for " + module.getName() + "!");
                t.printStackTrace();
            }
        }
    }

    private void loadModules() {

        for (Module module: ModuleManager.getModules()) {

            //file that the config was saved to
            final File moduleConfigFile = new File(this.modulesDirectory, module.getName() + ".json");

            //dont try to load config file if it doesnt exist
            if (!moduleConfigFile.exists()) {
                continue;
            }

            try {
                //open a file reader and json reader to read the config file
                final FileReader fileReader = new FileReader(moduleConfigFile);
                final JsonReader jsonReader = GSON.newJsonReader(fileReader);

                //get json object from file
                final JsonObject moduleObject = GSON.fromJson(jsonReader, JsonObject.class);

                //deserialize panels
                module.setToggled(moduleObject.get("toggled").getAsBoolean());
                module.setKey(moduleObject.get("key").getAsInt());

                //deserialize settings
                if (moduleObject.has("settings")) {
                    final JsonObject settingsObject = moduleObject.get("settings").getAsJsonObject();
                    for (Setting < ? > setting : TempleClient.settingsManager.getSettingsByMod(module)) {
                        if (!settingsObject.has(setting.name)) continue;

                        setting.deserialize(settingsObject);
                    }
                }
            } catch (Throwable t) {
                System.err.println("Could not load config for " + module.getName() + "!");
                t.printStackTrace();
            }
        }
    }

    private void saveHud() {

        for (HUD.HudElement element: HUD.INSTANCE.getHudElements()) {

            //file that the config will be saved to
            final File hudElementConfigFile = new File(this.hudElementsDirectory, element.getName() + ".json");

            //json object that will store panels of the module
            final JsonObject hudElementObject = new JsonObject();

            //add panels
            hudElementObject.addProperty("enabled", element.isEnabled());
            hudElementObject.addProperty("x", element.getX());
            hudElementObject.addProperty("y", element.getY());

            //add settings
            final List < Setting < ? >> settings = TempleClient.settingsManager.getSettingsByMod(element);
            if (!settings.isEmpty()) {
                //json object just for settings
                final JsonObject settingsObject = new JsonObject();

                for (Setting < ? > setting : settings) {
                    setting.serialize(settingsObject);
                }

                hudElementObject.add("settings", settingsObject);
            }

            //write to file
            try {
                final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(hudElementConfigFile));
                bufferedWriter.write(GSON.toJson(hudElementObject));
                bufferedWriter.close();
            } catch (Throwable t) {
                System.err.println("Could not save config for " + element.getName() + "!");
                t.printStackTrace();
            }
        }
    }

    private void loadHud() {
        for (HUD.HudElement element: HUD.INSTANCE.getHudElements()) {

            //file that the config was saved to
            final File hudElementConfigFile = new File(this.hudElementsDirectory, element.getName() + ".json");

            //dont try to load config file if it doesnt exist
            if (!hudElementConfigFile.exists()) {
                continue;
            }

            try {
                //open a file reader and json reader to read the config file
                final FileReader fileReader = new FileReader(hudElementConfigFile);
                final JsonReader jsonReader = GSON.newJsonReader(fileReader);

                //get json object from file
                final JsonObject hudElementObject = GSON.fromJson(jsonReader, JsonObject.class);

                //deserialize panels
                element.setEnabled(hudElementObject.get("enabled").getAsBoolean());
                element.setX(hudElementObject.get("x").getAsDouble());
                element.setY(hudElementObject.get("y").getAsDouble());

                //deserialize settings
                if (hudElementObject.has("settings")) {
                    final JsonObject settingsObject = hudElementObject.get("settings").getAsJsonObject();
                    for (Setting < ? > setting : TempleClient.settingsManager.getSettingsByMod(element)) {
                        if (!settingsObject.has(setting.name)) continue;

                        setting.deserialize(settingsObject);
                    }
                }
            } catch (Throwable t) {
                System.err.println("Could not load config for " + element.getName() + "!");
                t.printStackTrace();
            }
        }
    }

    private void saveFriends() {
        final File friendsFile = new File(this.friendsDirectory, "Friends.json");

        final List < String > friendNames = new ArrayList < > ();
        for (Friend friend: TempleClient.friendManager.getFriends()) {
            friendNames.add(friend.getName());
        }

        try {
            final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(friendsFile));
            bufferedWriter.write(GSON.toJson(friendNames));
            bufferedWriter.close();
        } catch (Throwable t) {
            System.err.println("Could not save friends!");
            t.printStackTrace();
        }
    }

    private void loadFriends() {
        final File friendsFile = new File(this.friendsDirectory, "friends.json");

        if (!friendsFile.exists()) {
            return;
        }

        try {
            final FileReader fileReader = new FileReader(friendsFile);
            final JsonReader jsonReader = GSON.newJsonReader(fileReader);

            final List < String > friendNames = GSON.fromJson(jsonReader, new TypeToken < List < String >> () {}.getType());

            for (String name: friendNames) {
                TempleClient.friendManager.addFriend(name);
            }
        } catch (Throwable t) {
            System.err.println("Could not load friends!");
            t.printStackTrace();
        }
    }
    public List<String> loadAlts() {
        if (!altsFile.exists()) return new ArrayList<>();
        try (Reader reader = new FileReader(altsFile)) {
            return GSON.fromJson(reader, new TypeToken<List<String>>() {}.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void saveAlts(List<String> alts) {
        try (Writer writer = new FileWriter(altsFile)) {
            GSON.toJson(alts, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void resetModule(Module module) {
        final File moduleConfigFile = new File(this.modulesDirectory, module.getName() + ".json");
        if (moduleConfigFile.exists()) {
            moduleConfigFile.delete();
        }
        loadAll();
    }
}
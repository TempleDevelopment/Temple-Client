package xyz.templecheats.templeclient.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.client.HUD;
import xyz.templecheats.templeclient.manager.ModuleManager;
import xyz.templecheats.templeclient.util.friend.Friend;
import xyz.templecheats.templeclient.util.setting.Setting;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigManager {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final File mainDirectory;
    private final File configDirectory;
    private final File friendsDirectory;
    private final File altsDirectory;
    private final File altsFile;
    private final File defaultConfigFile;
    private final File baseFinderLogDirectory;

    /****************************************************************
     *                      Constructor
     ****************************************************************/

    public ConfigManager() {
        this.mainDirectory = new File(System.getProperty("user.dir") + File.separator + "Temple Client");
        this.configDirectory = new File(this.mainDirectory, "1.12.2" + File.separator + "Config");
        this.friendsDirectory = new File(this.mainDirectory, "1.12.2" + File.separator + "Friends");
        this.altsDirectory = new File(this.mainDirectory, "1.12.2" + File.separator + "Alts");
        this.defaultConfigFile = new File(this.configDirectory, "config.cfg");
        this.baseFinderLogDirectory = new File(this.mainDirectory, "1.12.2" + File.separator + "Base Finder");

        if (!this.configDirectory.exists()) {
            this.configDirectory.mkdirs();
        }

        if (!this.friendsDirectory.exists()) {
            this.friendsDirectory.mkdirs();
        }

        if (!this.altsDirectory.exists()) {
            this.altsDirectory.mkdirs();
        }

        if (!this.baseFinderLogDirectory.exists()) {
            this.baseFinderLogDirectory.mkdirs();
        }

        this.altsFile = new File(this.altsDirectory, "alt_accounts.json");
    }

    /****************************************************************
     *                      Directory Getters
     ****************************************************************/

    public File getConfigDirectory() {
        return configDirectory;
    }

    /****************************************************************
     *                      Save and Load Methods
     ****************************************************************/

    public void saveAll() {
        this.saveConfig(this.defaultConfigFile);
        this.saveFriends();
    }

    public void loadAll() {
        this.loadConfig(this.defaultConfigFile);
        this.loadFriends();
    }

    public void saveConfig(File file) {
        final JsonObject configObject = new JsonObject();

        for (Module module : ModuleManager.getModules()) {
            final JsonObject moduleObject = new JsonObject();
            moduleObject.addProperty("toggled", module.isToggled());
            moduleObject.addProperty("key", module.getKey());

            final List<Setting<?>> settings = TempleClient.settingsManager.getSettingsByMod(module);
            if (settings != null && !settings.isEmpty()) {
                final JsonObject settingsObject = new JsonObject();
                for (Setting<?> setting : settings) {
                    setting.serialize(settingsObject);
                }
                moduleObject.add("settings", settingsObject);
            }
            configObject.add(module.getName(), moduleObject);
        }

        for (HUD.HudElement element : HUD.INSTANCE.getHudElements()) {
            final JsonObject hudElementObject = new JsonObject();
            hudElementObject.addProperty("enabled", element.isEnabled());
            hudElementObject.addProperty("x", element.getX());
            hudElementObject.addProperty("y", element.getY());

            final List<Setting<?>> settings = TempleClient.settingsManager.getSettingsByMod(element);
            if (settings != null && !settings.isEmpty()) {
                final JsonObject settingsObject = new JsonObject();
                for (Setting<?> setting : settings) {
                    setting.serialize(settingsObject);
                }
                hudElementObject.add("settings", settingsObject);
            }
            configObject.add(element.getName(), hudElementObject);
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            bufferedWriter.write(GSON.toJson(configObject));
        } catch (IOException e) {
            System.err.println("Could not save config!");
            e.printStackTrace();
        }
    }

    public void loadConfig(File file) {
        if (!file.exists()) {
            return;
        }

        try (FileReader fileReader = new FileReader(file);
             JsonReader jsonReader = GSON.newJsonReader(fileReader)) {

            final JsonObject configObject = GSON.fromJson(jsonReader, JsonObject.class);

            for (Module module : ModuleManager.getModules()) {
                if (configObject.has(module.getName())) {
                    final JsonObject moduleObject = configObject.getAsJsonObject(module.getName());
                    if (moduleObject.has("toggled")) {
                        module.setToggled(moduleObject.get("toggled").getAsBoolean());
                    }
                    if (moduleObject.has("key")) {
                        module.setKey(moduleObject.get("key").getAsInt());
                    }

                    if (moduleObject.has("settings")) {
                        final JsonObject settingsObject = moduleObject.get("settings").getAsJsonObject();
                        for (Setting<?> setting : TempleClient.settingsManager.getSettingsByMod(module)) {
                            if (settingsObject.has(setting.name)) {
                                setting.deserialize(settingsObject);
                            }
                        }
                    }
                }
            }

            for (HUD.HudElement element : HUD.INSTANCE.getHudElements()) {
                if (configObject.has(element.getName())) {
                    final JsonObject hudElementObject = configObject.getAsJsonObject(element.getName());
                    if (hudElementObject.has("enabled")) {
                        element.setEnabled(hudElementObject.get("enabled").getAsBoolean());
                    }
                    if (hudElementObject.has("x")) {
                        element.setX(hudElementObject.get("x").getAsDouble());
                    }
                    if (hudElementObject.has("y")) {
                        element.setY(hudElementObject.get("y").getAsDouble());
                    }

                    if (hudElementObject.has("settings")) {
                        final JsonObject settingsObject = hudElementObject.get("settings").getAsJsonObject();
                        for (Setting<?> setting : TempleClient.settingsManager.getSettingsByMod(element)) {
                            if (settingsObject.has(setting.name)) {
                                setting.deserialize(settingsObject);
                            }
                        }
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Could not load config!");
            e.printStackTrace();
        }
    }

    /****************************************************************
     *                      Friend Management
     ****************************************************************/

    private void saveFriends() {
        final File friendsFile = new File(this.friendsDirectory, "friends.json");

        final List<String> friendNames = new ArrayList<>();
        for (Friend friend : TempleClient.friendManager.getFriends()) {
            friendNames.add(friend.getName());
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(friendsFile))) {
            bufferedWriter.write(GSON.toJson(friendNames));
        } catch (IOException e) {
            System.err.println("Could not save friends!");
            e.printStackTrace();
        }
    }

    private void loadFriends() {
        final File friendsFile = new File(this.friendsDirectory, "friends.json");

        if (!friendsFile.exists()) {
            return;
        }

        try (FileReader fileReader = new FileReader(friendsFile);
             JsonReader jsonReader = GSON.newJsonReader(fileReader)) {

            final List<String> friendNames = GSON.fromJson(jsonReader, new TypeToken<List<String>>() {
            }.getType());

            for (String name : friendNames) {
                TempleClient.friendManager.addFriend(name);
            }
        } catch (IOException e) {
            System.err.println("Could not load friends!");
            e.printStackTrace();
        }
    }

    /****************************************************************
     *                      Alt Account Management
     ****************************************************************/

    public List<String> loadAlts() {
        if (!altsFile.exists()) return new ArrayList<>();
        try (Reader reader = new FileReader(altsFile)) {
            return GSON.fromJson(reader, new TypeToken<List<String>>() {
            }.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * Saves alt accounts to a file.
     *
     * @param alts The list of alt accounts to save.
     */
    public void saveAlts(List<String> alts) {
        try (Writer writer = new FileWriter(altsFile)) {
            GSON.toJson(alts, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /****************************************************************
     *                      Base Finder Logging
     ****************************************************************/

    public void setupBaseFinderLogging() {
        if (!baseFinderLogDirectory.exists()) {
            baseFinderLogDirectory.mkdirs();
        }
    }

    public void logBaseFound(ChunkPos chunkPos, Map<String, List<BlockPos>> foundItems) {
        File logFile = new File(baseFinderLogDirectory, "found_bases.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
            writer.write("Base found at Chunk [" + chunkPos.x + ", " + chunkPos.z + "]:\n");
            foundItems.forEach((item, positions) -> {
                try {
                    writer.write(" - " + positions.size() + " x " + item + " at positions:\n");
                    for (BlockPos pos : positions) {
                        writer.write("   [" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "]\n");
                    }
                } catch (IOException e) {
                    System.err.println("Error writing to base log file: " + e.getMessage());
                }
            });
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error opening base log file: " + e.getMessage());
        }
    }
}

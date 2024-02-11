package xyz.templecheats.templeclient.api.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import xyz.templecheats.templeclient.ModuleManager;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.api.setting.Setting;
import xyz.templecheats.templeclient.impl.modules.Module;
import xyz.templecheats.templeclient.impl.modules.client.HUD;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class ConfigManager {
    
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    private final File mainDirectory;
    private final File modulesDirectory;
    private final File hudElementsDirectory;
    
    public ConfigManager() {
        this.mainDirectory = new File(System.getProperty("user.dir") + File.separator + "Temple Client");
        this.modulesDirectory = new File(this.mainDirectory, "Modules");
        this.hudElementsDirectory = new File(this.mainDirectory, "Hud Elements");
        
        if(!this.modulesDirectory.exists()) {
            this.modulesDirectory.mkdirs();
        }
        
        if(!this.hudElementsDirectory.exists()) {
            this.hudElementsDirectory.mkdirs();
        }
    }
    
    public void saveAll() {
        this.saveModules();
        this.saveHud();
    }
    
    public void loadAll() {
        this.loadModules();
        this.loadHud();
    }
    
    private void saveModules() {
        
        //iterate through each module to save its config
        for(Module module : ModuleManager.getModules()) {
            
            //file that the config will be saved to
            final File moduleConfigFile = new File(this.modulesDirectory, module.getName() + ".json");
            
            //json object that will store properties of the module
            final JsonObject moduleObject = new JsonObject();
            
            //add properties
            moduleObject.addProperty("toggled", module.isToggled());
            moduleObject.addProperty("key", module.getKey());
            
            //add settings
            final ArrayList<Setting> settings = TempleClient.settingsManager.getSettingsByMod(module);
            if(settings != null) {
                
                //json object just for settings
                final JsonObject settingsObject = new JsonObject();
                for(Setting setting : settings) {
                    if(setting.isCheck()) {
                        settingsObject.addProperty(setting.getName(), setting.getValBoolean());
                    } else if(setting.isCombo()) {
                        settingsObject.addProperty(setting.getName(), setting.getValString());
                    } else if(setting.isSlider()) {
                        settingsObject.addProperty(setting.getName(), setting.getValDouble());
                    }
                }
                
                moduleObject.add("settings", settingsObject);
            }
            
            //write to file
            try {
                final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(moduleConfigFile));
                bufferedWriter.write(GSON.toJson(moduleObject));
                bufferedWriter.close();
            } catch(Throwable t) {
                System.err.println("Could not save config for " + module.getName() + "!");
                t.printStackTrace();
            }
        }
    }
    
    private void loadModules() {
        
        for(Module module : ModuleManager.getModules()) {
            
            //file that the config was saved to
            final File moduleConfigFile = new File(this.modulesDirectory, module.getName() + ".json");
            
            //dont try to load config file if it doesnt exist
            if(!moduleConfigFile.exists()) {
                continue;
            }
            
            try {
                //open a file reader and json reader to read the config file
                final FileReader fileReader = new FileReader(moduleConfigFile);
                final JsonReader jsonReader = GSON.newJsonReader(fileReader);
                
                //get json object from file
                final JsonObject moduleObject = GSON.fromJson(jsonReader, JsonObject.class);
                
                //deserialize properties
                module.setToggled(moduleObject.get("toggled").getAsBoolean());
                module.setKey(moduleObject.get("key").getAsInt());
                
                //deserialize settings
                if(moduleObject.has("settings")) {
                    final JsonObject settingsObject = moduleObject.get("settings").getAsJsonObject();
                    for(Setting setting : TempleClient.settingsManager.getSettingsByMod(module)) {
                        
                        //skip if setting was not stored in config
                        if(!settingsObject.has(setting.getName())) {
                            continue;
                        }
                        
                        if(setting.isCheck()) {
                            setting.setValBoolean(settingsObject.get(setting.getName()).getAsBoolean());
                        } else if(setting.isCombo()) {
                            setting.setValString(settingsObject.get(setting.getName()).getAsString());
                        } else if(setting.isSlider()) {
                            setting.setValDouble(settingsObject.get(setting.getName()).getAsDouble());
                        }
                    }
                }
            } catch(Throwable t) {
                System.err.println("Could not load config for " + module.getName() + "!");
                t.printStackTrace();
            }
        }
    }
    
    private void saveHud() {
        
        for(HUD.HudElement element : HUD.INSTANCE.getHudElements()) {
            
            //file that the config will be saved to
            final File hudElementConfigFile = new File(this.hudElementsDirectory, element.getName() + ".json");
            
            //json object that will store properties of the module
            final JsonObject hudElementObject = new JsonObject();
            
            //add properties
            hudElementObject.addProperty("enabled", element.isEnabled());
            hudElementObject.addProperty("x", element.getX());
            hudElementObject.addProperty("y", element.getY());
            
            //add settings
            final ArrayList<Setting> settings = TempleClient.settingsManager.getSettingsByMod(element);
            if(settings != null) {
                
                //json object just for settings
                final JsonObject settingsObject = new JsonObject();
                for(Setting setting : settings) {
                    if(setting.isCheck()) {
                        settingsObject.addProperty(setting.getName(), setting.getValBoolean());
                    } else if(setting.isCombo()) {
                        settingsObject.addProperty(setting.getName(), setting.getValString());
                    } else if(setting.isSlider()) {
                        settingsObject.addProperty(setting.getName(), setting.getValDouble());
                    }
                }
                
                hudElementObject.add("settings", settingsObject);
            }
            
            //write to file
            try {
                final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(hudElementConfigFile));
                bufferedWriter.write(GSON.toJson(hudElementObject));
                bufferedWriter.close();
            } catch(Throwable t) {
                System.err.println("Could not save config for " + element.getName() + "!");
                t.printStackTrace();
            }
        }
    }
    
    private void loadHud() {
        
        for(HUD.HudElement element : HUD.INSTANCE.getHudElements()) {
            
            //file that the config was saved to
            final File hudElementConfigFile = new File(this.hudElementsDirectory, element.getName() + ".json");
            
            //dont try to load config file if it doesnt exist
            if(!hudElementConfigFile.exists()) {
                continue;
            }
            
            try {
                //open a file reader and json reader to read the config file
                final FileReader fileReader = new FileReader(hudElementConfigFile);
                final JsonReader jsonReader = GSON.newJsonReader(fileReader);
                
                //get json object from file
                final JsonObject hudElementObject = GSON.fromJson(jsonReader, JsonObject.class);
                
                //deserialize properties
                element.setEnabled(hudElementObject.get("enabled").getAsBoolean());
                element.setX(hudElementObject.get("x").getAsDouble());
                element.setY(hudElementObject.get("y").getAsDouble());
                
                //deserialize settings
                if(hudElementObject.has("settings")) {
                    final JsonObject settingsObject = hudElementObject.get("settings").getAsJsonObject();
                    for(Setting setting : TempleClient.settingsManager.getSettingsByMod(element)) {
                        
                        //skip if setting was not stored in config
                        if(!settingsObject.has(setting.getName())) {
                            continue;
                        }
                        
                        if(setting.isCheck()) {
                            setting.setValBoolean(settingsObject.get(setting.getName()).getAsBoolean());
                        } else if(setting.isCombo()) {
                            setting.setValString(settingsObject.get(setting.getName()).getAsString());
                        } else if(setting.isSlider()) {
                            setting.setValDouble(settingsObject.get(setting.getName()).getAsDouble());
                        }
                    }
                }
            } catch(Throwable t) {
                System.err.println("Could not load config for " + element.getName() + "!");
                t.printStackTrace();
            }
        }
    }
}
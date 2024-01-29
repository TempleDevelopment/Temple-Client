package xyz.templecheats.templeclient.impl.gui.setting;

import xyz.templecheats.templeclient.impl.modules.Module;

import java.util.ArrayList;
import java.util.Objects;

public class SettingsManager {

	private ArrayList<Setting> settings;

	public SettingsManager(){
		this.settings = new ArrayList<Setting>();
	}

	public void rSetting(Setting in){
		this.settings.add(in);
	}

	public ArrayList<Setting> getSettings(){
		return this.settings;
	}

	public ArrayList<Setting> getSettingsByMod(Module mod){
		ArrayList<Setting> out = new ArrayList<Setting>();
		for(Setting s : getSettings()){
			if(s.getParentMod().equals(mod)){
				out.add(s);
			}
		}
		if(out.isEmpty()){
			return null;
		}
		return out;
	}

	public Setting getSettingByName(String mod, String name){
		for(Setting set : getSettings()){
			if(set.getName().equalsIgnoreCase(name) && Objects.equals(set.getParentMod().name, mod)){
				return set;
			}
		}
		return null;
	}

}

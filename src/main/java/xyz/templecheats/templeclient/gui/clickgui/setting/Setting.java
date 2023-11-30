package xyz.templecheats.templeclient.gui.clickgui.setting;

import xyz.templecheats.templeclient.features.modules.Module;

import java.util.ArrayList;

public class Setting {
	
	private String name;
	private Module parent;
	private String mode;
	
	private String sval;
	private ArrayList<String> options;
	private String title;
	
	private boolean bval;
	
	private double dval;
	private double min;
	private double max;
	private boolean onlyint = false;
	
	private String text;
	
	private int color;
	
	public Setting(String name, Module parent, ArrayList<String> options, String title) {
		this.name = name;
		this.parent = parent;
		this.options = options;
		this.title = title;
		this.mode = "Combo";
	}
	
	public Setting(String name, Module parent, boolean bval) {
		this.name = name;
		this.parent = parent;
		this.bval = bval;
		this.mode = "Check";
	}
	
	public Setting(String name, Module parent, double dval, double min, double max, boolean onlyint) {
		this.name = name;
		this.parent = parent;
		this.dval = dval;
		this.min = min;
		this.max = max;
		this.onlyint = onlyint;
		this.mode = "Slider";
	}

	
	public String getName(){
		return name;
	}
	
	public Module getParentMod(){
		return parent;
	}
	
	public String getValString(){
		return this.sval;
	}
	
	public void setValString(String in) {
		this.sval = in;
	}
	
	public ArrayList<String> getOptions(){
		return this.options;
	}
	
	public String getTitle(){
		return this.title;
	}
	
	public boolean getValBoolean(){
		return this.bval;
	}
	
	public void setValBoolean(boolean in){
		this.bval = in;
	}
	
	public double getValDouble() {
		if(this.onlyint) {
			this.dval = (int)dval;
		}
		return this.dval;
	}

    public int getValInt() {
        return (int)dval;
    }

	public void setValDouble(double in){
		this.dval = in;
	}
	
	public double getMin(){
		return this.min;
	}
	
	public double getMax(){
		return this.max;
	}
	
	public int getColor(){
		return this.color;
	}
	
	public String getString(){
		return this.text;
	}
	
	public boolean isCombo(){
		return this.mode.equalsIgnoreCase("Combo") ? true : false;
	}
	
	public boolean isCheck(){
		return this.mode.equalsIgnoreCase("Check") ? true : false;
	}
	
	public boolean isSlider(){
		return this.mode.equalsIgnoreCase("Slider") ? true : false;
	}
	
	public boolean isMode(){
		return this.mode.equalsIgnoreCase("ModeButton") ? true : false;
	}
	
	public boolean onlyInt(){
		return this.onlyint;
	}
}

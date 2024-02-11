package xyz.templecheats.templeclient.api.setting;

import java.awt.*;
import java.util.ArrayList;

public class Setting {
    private final String name;
    private final SettingHolder parent;
    private String mode;
    
    private String sval;
    private ArrayList<String> options;
    
    private boolean bval;
    
    private double dval;
    private double min;
    private double max;
    private boolean onlyint;
    
    private String text;
    
    private Color color;
    
    public Setting(String name, SettingHolder parent, ArrayList<String> options, String sval) {
        this.name = name;
        this.parent = parent;
        this.options = options;
        this.sval = sval;
        this.mode = "Combo";
    }
    
    public Setting(String name, SettingHolder parent, boolean bval) {
        this.name = name;
        this.parent = parent;
        this.bval = bval;
        this.mode = "Check";
    }
    
    public Setting(String name, SettingHolder parent, double dval, double min, double max, boolean onlyint) {
        this.name = name;
        this.parent = parent;
        this.dval = dval;
        this.min = min;
        this.max = max;
        this.onlyint = onlyint;
        this.mode = "Slider";
    }
    
    public Setting(String name, SettingHolder parent, Color color) {
        this.name = name;
        this.parent = parent;
        this.color = color;
        this.mode = "Color";
    }
    
    public Setting(String name, SettingHolder parent, int red, int green, int blue) {
        this.name = name;
        this.parent = parent;
        this.color = new Color(red, green, blue);
        this.mode = "Color";
    }
    
    public Setting(String name, SettingHolder parent, int hexColor) {
        this.name = name;
        this.parent = parent;
        this.color = new Color(hexColor >> 16 & 255, hexColor >> 8 & 255, hexColor & 255, hexColor >> 24 & 255);
        this.mode = "Color";
    }
    
    public String getName() {
        return name;
    }
    
    public SettingHolder getParentMod() {
        return parent;
    }
    
    public String getValString() {
        return this.sval;
    }
    
    public void setValString(String in) {
        this.sval = in;
    }
    
    public ArrayList<String> getOptions() {
        return this.options;
    }
    
    public boolean getValBoolean() {
        return this.bval;
    }
    
    public void setValBoolean(boolean in) {
        this.bval = in;
    }
    
    public double getValDouble() {
        if(this.onlyint) {
            this.dval = (int) dval;
        }
        return this.dval;
    }
    
    public int getValInt() {
        return (int) dval;
    }
    
    public void setValDouble(double in) {
        this.dval = in;
    }
    
    public double getMin() {
        if(this.onlyint) {
            this.min = (int) min;
        }
        return this.min;
    }
    
    public double getMax() {
        if(this.onlyint) {
            this.max = (int) max;
        }
        return this.max;
    }
    
    public Color getValColor() {
        return this.color;
    }
    
    public void setValColor(Color color) {
        this.color = color;
    }
    
    public void setValColor(int red, int green, int blue) {
        this.color = new Color(red, green, blue);
    }
    
    public String getValText() {
        return this.text;
    }
    
    public boolean isCombo() {
        return this.mode.equalsIgnoreCase("Combo");
    }
    
    public boolean isCheck() {
        return this.mode.equalsIgnoreCase("Check");
    }
    
    public boolean isSlider() {
        return this.mode.equalsIgnoreCase("Slider");
    }
    
    public boolean isMode() {
        return this.mode.equalsIgnoreCase("ModeButton");
    }
    
    public boolean isColor() {
        return this.mode.equalsIgnoreCase("Color");
    }
    
    public boolean onlyInt() {
        return this.onlyint;
    }
}

package xyz.templecheats.templeclient.impl.modules.client.hud;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.gui.ScaledResolution;
import xyz.templecheats.templeclient.ModuleManager;
import xyz.templecheats.templeclient.api.setting.Setting;
import xyz.templecheats.templeclient.impl.gui.font.FontUtils;
import xyz.templecheats.templeclient.impl.modules.Module;
import xyz.templecheats.templeclient.impl.modules.client.ClickGUI;
import xyz.templecheats.templeclient.impl.modules.client.HUD;

import java.util.ArrayList;

public class ModuleList extends HUD.HudElement {
    /**
     * Settings
     */
    private final Setting metadata = new Setting("Metadata", this, true);
    
    public ModuleList() {
        super("ModuleList", "Shows active modules in the HUD");
        this.registerSettings(metadata);
        
        this.setEnabled(true);
        this.setX(2);
        this.setY(FontUtils.getFontHeight() + 4);
    }
    
    @Override
    protected void renderElement(ScaledResolution sr) {
        final ArrayList<Module> mods = ModuleManager.getActiveModules();
        
        mods.removeIf(mod -> mod.getCategory() == Module.Category.Client);
        
        mods.sort((Module mod1, Module mod2) -> {
            final String text1 = this.getModText(mod1);
            final String text2 = this.getModText(mod2);
            return Double.compare(FontUtils.getStringWidth(this.isTopOfCenter() ? text2 : text1), FontUtils.getStringWidth(this.isTopOfCenter() ? text1 : text2));
        });
        
        double y = 0;
        for(Module mod : mods) {
            final String text = this.getModText(mod);
            FontUtils.drawString(text, this.getX() + (!this.isLeftOfCenter() ? this.getWidth() - FontUtils.getStringWidth(text) : 0), this.getY() + y, ClickGUI.INSTANCE.getStartColor(), true);
            
            y += FontUtils.getFontHeight() + 2D;
        }
        
        this.setWidth(!mods.isEmpty() ? FontUtils.getStringWidth(this.getModText(this.isTopOfCenter() ? mods.get(0) : mods.get(mods.size() - 1))) : 0);
        this.setHeight(y);
    }
    
    private String getModText(Module module) {
        if(module.getHudInfo().isEmpty() || !this.metadata.getValBoolean()) {
            return module.getName();
        }
        
        return String.format("%s %s[%s%s%s]", module.getName(), ChatFormatting.DARK_GRAY, ChatFormatting.GRAY, module.getHudInfo(), ChatFormatting.DARK_GRAY);
    }
}
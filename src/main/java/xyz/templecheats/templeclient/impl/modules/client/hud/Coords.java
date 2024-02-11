package xyz.templecheats.templeclient.impl.modules.client.hud;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.text.TextFormatting;
import xyz.templecheats.templeclient.impl.gui.font.FontUtils;
import xyz.templecheats.templeclient.impl.modules.client.ClickGUI;
import xyz.templecheats.templeclient.impl.modules.client.HUD;

public class Coords extends HUD.HudElement {
    public Coords() {
        super("Coords", "Shows your coords in the HUD");
    }
    
    @Override
    protected void renderElement(ScaledResolution sr) {
        float[] playerPos = {
                Math.round(mc.player.posX * 100D) / 100F,
                Math.round(mc.player.posY * 100D) / 100F,
                Math.round(mc.player.posZ * 100D) / 100F
        };
        
        String coordsString = TextFormatting.GRAY + "XYZ: " + TextFormatting.RESET + playerPos[0] + ", " + playerPos[1] + ", " + playerPos[2];
        
        FontUtils.drawString(coordsString, this.getX(), this.getY() + FontUtils.getFontHeight() + 2, ClickGUI.INSTANCE.getStartColor(), true);
        
        double width = 0;
        
        if(mc.player.dimension == 0) {
            float[] netherPos = {
                    Math.round(mc.player.posX * 12.5D) / 100F,
                    playerPos[1],
                    Math.round(mc.player.posZ * 12.5D) / 100F
            };
            
            String netherCoordsString = TextFormatting.GRAY + "Nether: " + TextFormatting.RESET + netherPos[0] + ", " + netherPos[1] + ", " + netherPos[2];
            
            width = Math.max(FontUtils.getStringWidth(coordsString), FontUtils.getStringWidth(netherCoordsString));
            
            FontUtils.drawString(netherCoordsString, this.getX(), this.getY(), 0xFFFFFF, true);
        } else if(mc.player.dimension == -1) {
            float[] overworldPos = {
                    playerPos[0] * 8,
                    playerPos[1],
                    playerPos[2] * 8
            };
            
            String overworldCoordsString = TextFormatting.GRAY + "Overworld: " + TextFormatting.RESET + overworldPos[0] + ", " + overworldPos[1] + ", " + overworldPos[2];
            
            width = Math.max(FontUtils.getStringWidth(coordsString), FontUtils.getStringWidth(overworldCoordsString));
            
            FontUtils.drawString(overworldCoordsString, this.getX(), this.getY(), 0xFFFFFF, true);
        }
        
        this.setWidth(width);
        this.setHeight((FontUtils.getFontHeight() * 2) + 2);
    }
}


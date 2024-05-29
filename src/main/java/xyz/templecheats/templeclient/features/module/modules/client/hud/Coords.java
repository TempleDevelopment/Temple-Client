package xyz.templecheats.templeclient.features.module.modules.client.hud;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.text.TextFormatting;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.features.module.modules.client.HUD;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;

import static xyz.templecheats.templeclient.features.gui.font.Fonts.font18;

public class Coords extends HUD.HudElement {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final BooleanSetting dimensionCoords = new BooleanSetting("Dimension Coords", this, false);

    public Coords() {
        super("Coords", "Shows your coords in the HUD");
        this.registerSettings(dimensionCoords);
    }

    @Override
    public void renderElement(ScaledResolution sr) {
        float[] playerPos = {
                Math.round(mc.player.posX * 100D) / 100F,
                Math.round(mc.player.posY * 100D) / 100F,
                Math.round(mc.player.posZ * 100D) / 100F
        };

        String coordsString = TextFormatting.GRAY + "XYZ: " + TextFormatting.RESET + playerPos[0] + ", " + playerPos[1] + ", " + playerPos[2];

        font18.drawString(coordsString, this.getX(), this.getY() + font18.getFontHeight() + 2, ClickGUI.INSTANCE.getStartColor().getRGB(), true);

        double width = font18.getStringWidth(coordsString);

        if (dimensionCoords.booleanValue()) {
            if (mc.player.dimension == 0) {
                float[] netherPos = {
                        Math.round(mc.player.posX * 12.5D) / 100F,
                        playerPos[1],
                        Math.round(mc.player.posZ * 12.5D) / 100F
                };

                String netherCoordsString = TextFormatting.GRAY + "Nether: " + TextFormatting.RESET + netherPos[0] + ", " + netherPos[1] + ", " + netherPos[2];

                width = Math.max(width, font18.getStringWidth(netherCoordsString));

                font18.drawString(netherCoordsString, this.getX(), this.getY(), 0xFFFFFF, true);
            } else if (mc.player.dimension == -1) {
                float[] overworldPos = {
                        playerPos[0] * 8,
                        playerPos[1],
                        playerPos[2] * 8
                };

                String overworldCoordsString = TextFormatting.GRAY + "Overworld: " + TextFormatting.RESET + overworldPos[0] + ", " + overworldPos[1] + ", " + overworldPos[2];

                width = Math.max(width, font18.getStringWidth(overworldCoordsString));

                font18.drawString(overworldCoordsString, this.getX(), this.getY(), 0xFFFFFF, true);
            }
        }

        this.setWidth(width);
        this.setHeight((font18.getFontHeight() * 2) + 2);
    }
}
package xyz.templecheats.templeclient.features.module.modules.client.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.features.module.modules.client.HUD;

public class Speed extends HUD.HudElement {
    /*
     * Variables
     */
    private Vec3d lastPos = Vec3d.ZERO;

    public Speed() {
        super("Speed", "Shows Speed in the HUD");
    }

    @Override
    public void renderElement(ScaledResolution sr) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null) return;

        Vec3d currentPos = new Vec3d(player.posX, player.posY, player.posZ);
        Vec3d lastTickPos = new Vec3d(player.lastTickPosX, player.lastTickPosY, player.lastTickPosZ);
        double speedValue = (currentPos.distanceTo(lastTickPos)) * 20;

        final String speedText = "Speed ";
        this.setWidth(font.getStringWidth(speedText + String.format("%.2f", speedValue)) + 10);
        this.setHeight(font.getFontHeight());

        font.drawString(speedText, (float) this.getX(), (float) this.getY(), ClickGUI.INSTANCE.getStartColor().getRGB(), true, 1.0f);
        font.drawString(String.format("%.2f", speedValue), (float) (this.getX() + font.getStringWidth(speedText)), (float) this.getY(), 0xFFFFFF, true, 1.0f);
    }
}
package xyz.templecheats.templeclient.features.module.modules.client.hud;

import net.minecraft.client.gui.ScaledResolution;
import xyz.templecheats.templeclient.features.module.modules.client.HUD;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;

import static xyz.templecheats.templeclient.util.render.RenderUtil.drawPlayer;

public class PlayerView extends HUD.HudElement {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final DoubleSetting modelScale = new DoubleSetting("Scale", this, 0.4, 2.0, 0.8);

    public PlayerView() {
        super("PlayerView", "Shows your player model in the HUD");
        registerSettings(modelScale);
    }

    @Override
    public void renderElement(ScaledResolution sr) {
        this.setWidth(90 * modelScale.doubleValue() * 0.65);
        this.setHeight(150 * modelScale.doubleValue() * 0.65);

        drawPlayer(mc.player, modelScale.floatValue(), (float) (getX() + getWidth() * 0.5), (float) (getY() + getHeight() - 10));
    }
}
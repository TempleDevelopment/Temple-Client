package xyz.templecheats.templeclient.features.module.modules.client;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.combat.AutoCrystal;
import xyz.templecheats.templeclient.features.module.modules.render.esp.sub.Block;
import xyz.templecheats.templeclient.features.module.modules.render.PopChams;
import xyz.templecheats.templeclient.features.module.modules.render.esp.sub.Shader;
import xyz.templecheats.templeclient.util.color.RainbowUtil;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.setting.impl.ColorSetting;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;

import java.awt.*;

public class Colors extends Module {
    public static Colors INSTANCE;
    private final RainbowUtil rainbowUtil = new RainbowUtil();

    public Colors() {
        super("Colors", "Configure various color settings", Keyboard.KEY_NONE, Category.Client);
        INSTANCE = this;
        setToggled(true);
        this.registerSettings(step, speed, staticColor, friendColor, gradientColor1, gradientColor2, fogColor, lightMapColor);
    }
    /*
     * Settings
     */
    public final DoubleSetting step = new DoubleSetting("Step", this, 0.1, 5, 0.5);
    public final DoubleSetting speed = new DoubleSetting("Speed", this, 0, 5, 1);
    public final ColorSetting staticColor = new ColorSetting("Static", this, Color.CYAN);
    public final ColorSetting friendColor = new ColorSetting("Friend", this, Color.GREEN);
    public final ColorSetting gradientColor1 = new ColorSetting("Gradient 1", this, Color.BLUE);
    public final ColorSetting gradientColor2 = new ColorSetting("Gradient 2", this, Color.CYAN);
    public final ColorSetting fogColor = new ColorSetting("Fog", this, Color.RED);
    public final ColorSetting lightMapColor = new ColorSetting("LightMap", this, Color.RED);
    public final EnumSetting<Mode> theme = new EnumSetting<>("Mode", this, Mode.Gradient);

    public Color getColor() {
        return this.staticColor.getColor();
    }

    public Color getColorAlpha() {
        Color color = this.staticColor.getColor();
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), 200);
    }

    public Color getColor(double offset) {
        return new Color(rainbowUtil.rainbowProgress(5, (int) (offset * 200), getGradient()[0].getRGB(), getGradient()[1].getRGB()));
    }

    public Color[] getGradient() {
        return new Color[]{
                this.gradientColor1.getColor(),
                this.gradientColor2.getColor(),
        };
    }
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        doBindBlank();
    }
    public void doBindBlank() { //fix items turning white
        if (!(Block.rendering && Shader.rendering && PopChams.rendering && AutoCrystal.rendering)) {
            RenderUtil.bindBlank();
        }
    }

    public Color getFogColor() {
        return this.fogColor.getColor();
    }

    public Color getLightMapColor() {
        return this.lightMapColor.getColor();
    }
    public Color getFriendColor() {
        return this.friendColor.getColor();
    }
    public enum Mode{
        Gradient,
        Normal
    }
}

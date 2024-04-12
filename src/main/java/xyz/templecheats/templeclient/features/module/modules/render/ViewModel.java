package xyz.templecheats.templeclient.features.module.modules.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.event.events.render.TransformSideFirstPersonEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;

public class ViewModel extends Module {
    /**
     * Settings
     */
    public final BooleanSetting cancelEating = new BooleanSetting("No Eat", this, false);
    private final DoubleSetting fov = new DoubleSetting("FOV", this, 70d, 200d, 130d);
    private final DoubleSetting xLeft = new DoubleSetting("Left X", this, -2d, 2d, 0d);
    private final DoubleSetting yLeft = new DoubleSetting("Left Y", this, -2d, 2d, 0.2);
    private final DoubleSetting zLeft = new DoubleSetting("Left Z", this, -2d, 2d, -1.2);
    private final DoubleSetting xRight = new DoubleSetting("Right X", this, -2d, 2d, 0);
    private final DoubleSetting yRight = new DoubleSetting("Right Y", this, -2d, 2d, 0.2);
    private final DoubleSetting zRight = new DoubleSetting("Right Z", this, -2d, 2d, -1.2);
    private final DoubleSetting xScale = new DoubleSetting("X Scale", this, 0.1, 2d, 1d);
    private final DoubleSetting yScale = new DoubleSetting("Y Scale", this, 0.1, 2d, 1d);
    private final DoubleSetting zScale = new DoubleSetting("Z Scale", this, 0.1, 2d, 1d);
    private final EnumSetting < Mode > mode = new EnumSetting < > ("Mode", this, Mode.Value);

    public ViewModel() {
        super("ViewModel", "Modify your viewmodel", Keyboard.KEY_NONE, Category.Render);

        registerSettings(cancelEating, fov, xLeft, yLeft, zLeft, xRight, yRight, zRight, xScale, yScale, zScale, mode);
    }

    @Listener
    public void onTransformSideFirstPerson(TransformSideFirstPersonEvent event) {
        EnumHandSide renderedHandSide = event.getEnumHandSide();
        if (mode.value() != Mode.FOV) {
            if (renderedHandSide == EnumHandSide.RIGHT) {
                applyTransformations(xRight, yRight, zRight);
            } else if (renderedHandSide == EnumHandSide.LEFT) {
                applyTransformations(xLeft, yLeft, zLeft);
            }
        }
    }

    private void applyTransformations(DoubleSetting xPos, DoubleSetting yPos, DoubleSetting zPos) {
        GlStateManager.translate(xPos.doubleValue(), yPos.doubleValue(), zPos.doubleValue());
        GlStateManager.scale(xScale.doubleValue(), yScale.doubleValue(), zScale.doubleValue());
    }

    @SubscribeEvent
    public void onFOVModifier(EntityViewRenderEvent.FOVModifier event) {
        if (mode.value() != Mode.Value) {
            event.setFOV(fov.floatValue());
        }
    }

    private enum Mode {
        Value,
        FOV,
        Both
    }
}
package xyz.templecheats.templeclient.features.module.modules.render.particle.sub;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.client.Colors;
import xyz.templecheats.templeclient.features.module.modules.render.particle.impl.ParticleTickHandler;
import xyz.templecheats.templeclient.util.render.enums.TextureModifiers;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.min;
import static xyz.templecheats.templeclient.util.color.ColorUtil.setAlpha;
import static xyz.templecheats.templeclient.util.math.MathUtil.*;
import static xyz.templecheats.templeclient.util.world.BlockUtil.air;

public class FireFlies extends Module {

    private final EnumSetting<TextureModifiers> textures = new EnumSetting<>("Texture", this, TextureModifiers.Text);
    private final DoubleSetting size = new DoubleSetting("Size", this, 0.1, 5.0, 1.0);
    private final IntSetting amount = new IntSetting("Amount", this, 3, 200, 30);
    private final IntSetting maxAmount = new IntSetting("Max Amount", this, 100, 500, 250);
    private final DoubleSetting duration = new DoubleSetting("Duration", this, 5.0, 50.0, 30.0);
    private final DoubleSetting speedH = new DoubleSetting("Speed H", this, 0.1, 3.0, 1.0);
    private final DoubleSetting speedV = new DoubleSetting("Speed V", this, 0.1, 3.0, 1.0);
    private final DoubleSetting inertia = new DoubleSetting("Inertia Amount", this, 0.0, 1.0, 0.8);
    private final DoubleSetting gravity = new DoubleSetting("Gravity Amount", this, 0.0, 1.0, 0.8);

    private final ArrayList<ParticleTickHandler> fireFiles = new ArrayList<>();

    public FireFlies() {
        super("FireFlies", "Spawn random particle around u.", Keyboard.KEY_NONE, Category.Render, true);
        registerSettings(size, amount, maxAmount, duration, speedH, speedV, inertia, gravity, textures);
    }

    @Override
    public void onEnable() {
        fireFiles.clear();
    }

    @Override
    public void onUpdate() {
        if(mc.player == null || mc.world == null) {
            return;
        }

        fireFiles.removeIf(ParticleTickHandler::tick);
        while (fireFiles.size() < maxAmount.intValue()) {
            Vec3d vec = mc.player.getPositionVector().add(random(-25f, 25f), random(2.0f, 15.0f), random(-25.0f, 25.0f));

            Random random = new Random();

            double motionX = (random.nextDouble() - 0.5) * 0.2 * speedH.doubleValue();
            double motionY = (random.nextDouble() - 0.5) * 0.2 * speedV.doubleValue();
            double motionZ = (random.nextDouble() - 0.5) * 0.2 * speedH.doubleValue();

            FireFly fireFly = new FireFly(vec, motionX, motionY, motionZ, gravity.doubleValue(), inertia.doubleValue());

            fireFly.motionX *= random(0.5f, 1.5f);
            fireFly.motionY *= random(0.5f, 1.5f);
            fireFly.motionZ *= random(0.5f, 1.5f);

            fireFly.motionX += random(-0.1f, 0.1f);
            fireFly.motionY += random(-0.1f, 0.1f);
            fireFly.motionZ += random(-0.1f, 0.1f);

            fireFly.maxTickLiving = (int) (duration.doubleValue() * 10.0 + random.nextDouble() * 40.0);

            fireFiles.add(fireFly);
        }
        while (fireFiles.size() > maxAmount.intValue()) { fireFiles.remove(0); }
    }

    @Override
    public void onRenderWorld(float partialTicks) {
        fireFiles.forEach(it -> {
            double p1 = min(it.tickLiving, 10.0) / 10.0;
            double p2 = MathHelper.clamp((it.maxTickLiving - it.tickLiving) / 10, 0, 1);
            int alpha = (int) MathHelper.clamp((it.tickLiving <= 10 ? p1 : p2) * 255, 0, 255);
            it.draw(size.floatValue(), setAlpha(Colors.INSTANCE.getColor(5), alpha), textures.value());
        });
    }

    private static class FireFly extends ParticleTickHandler {
        public FireFly(Vec3d posIn, double motionX, double motionY, double motionZ, double gravityAmount, double inertiaAmount) {
            super(posIn, motionX, motionY, motionZ, gravityAmount, inertiaAmount);
        }

        @Override
        public boolean tick() {
            if ( mc.player.getDistanceSq(pos.x, pos.y, pos.z) == 100 ) {
                tickLiving -= 4;
            } else {
                tickLiving -= !air(new BlockPos(pos.x, pos.y, pos.z)) ? 8 : 1;
            }

            if (tickLiving < 0) {
                return true;
            } else {
                prevPos = pos;
                pos = pos.add(motionX, motionY, motionZ);

                if (blockPos(pos.x, pos.y, pos.z)) {
                    motionY = -motionY;
                } else if (collisionCheck(pos.x, pos.y, pos.z, size, MathHelper.sqrt(motionX * motionY + motionZ * motionZ) * 1)) {
                    motionX = -motionX + motionZ;
                    motionZ = -motionZ + motionX;
                }

                motionY -= gravityAmount * 0.001;

                motionX *= 0.9 + (inertiaAmount / 10.0);
                motionY *= 0.2 + (inertiaAmount / 10.0);
                motionZ *= 0.9 + (inertiaAmount / 10.0);

                return false;
            }
        }
    }
}

package xyz.templecheats.templeclient.features.module.modules.render.particle.sub;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.event.events.player.AttackEvent;
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
import static xyz.templecheats.templeclient.util.math.MathUtil.random;

public class HitParticle extends Module {

    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final EnumSetting<TextureModifiers> textures = new EnumSetting<>("Texture", this, TextureModifiers.Text);
    private final DoubleSetting size = new DoubleSetting("Size", this, 0.1, 5.0, 1.0);
    private final IntSetting amount = new IntSetting("Amount", this, 3, 50, 25);
    private final IntSetting maxAmount = new IntSetting("Max Amount", this, 100, 500, 250);
    private final DoubleSetting duration = new DoubleSetting("Duration", this, 5.0, 50.0, 30.0);
    private final DoubleSetting speedH = new DoubleSetting("Speed H", this, 0.1, 3.0, 1.0);
    private final DoubleSetting speedV = new DoubleSetting("Speed V", this, 0.1, 3.0, 1.0);
    private final DoubleSetting inertia = new DoubleSetting("Inertia Amount", this, 0.0, 1.0, 0.8);
    private final DoubleSetting gravity = new DoubleSetting("Gravity Amount", this, 0.0, 1.0, 0.8);

    /****************************************************************
     *                      Variables
     ****************************************************************/
    private final ArrayList<ParticleTickHandler> hitParticle = new ArrayList<>();

    public HitParticle() {
        super("HitParticle", "Spawn particle when entities hurt.", Keyboard.KEY_NONE, Category.Render, true);
        registerSettings(size, amount, maxAmount, duration, speedH, speedV, inertia, gravity, textures);
    }

    @Override
    public void onEnable() {
        hitParticle.clear();
    }

    @SubscribeEvent
    public void onAttack(AttackEvent.Pre event) {
        if (mc.player == null || mc.world == null) {
            return;
        }
        for (int i = 0; i < amount.intValue(); i++) {
            Vec3d vec = event.getEntity().getPositionVector().add(random(-0.5f, 0.5f), random(0.5f, event.getEntity().height), random(-0.5f, 0.5f));

            Random random = new Random();

            double motionX = (random.nextDouble() - 0.5) * 0.2 * speedH.doubleValue();
            double motionY = (random.nextDouble() - 0.5) * 0.2 * speedV.doubleValue();
            double motionZ = (random.nextDouble() - 0.5) * 0.2 * speedH.doubleValue();

            Particle particle = new Particle(vec, motionX, motionY, motionZ, gravity.doubleValue(), inertia.doubleValue());
            particle.maxTickLiving = (int) (duration.doubleValue() * 10.0 + random.nextDouble() * 40.0);

            hitParticle.add(particle);
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        hitParticle.forEach(ParticleTickHandler::tick);
        hitParticle.removeIf(it -> it.tickLiving > it.maxTickLiving);
        while (hitParticle.size() > maxAmount.intValue()) {
            hitParticle.remove(0);
        }
    }

    @Override
    public void onRenderWorld(float partialTicks) {
        hitParticle.forEach(it -> {
            double p1 = min(it.tickLiving, 10.0) / 10.0;
            double p2 = MathHelper.clamp((it.maxTickLiving - it.tickLiving) / 10, 0, 1);
            int alpha = (int) MathHelper.clamp((it.tickLiving <= 10 ? p1 : p2) * 255, 0, 255);
            it.draw(size.floatValue(), setAlpha(Colors.INSTANCE.getColor(0), alpha), setAlpha(Colors.INSTANCE.getColor(5), alpha), textures.value());
        });
    }

    private static class Particle extends ParticleTickHandler {
        public Particle(Vec3d posIn, double motionX, double motionY, double motionZ, double gravityAmount, double inertiaAmount) {
            super(posIn, motionX, motionY, motionZ, gravityAmount, inertiaAmount);
        }

        @Override
        public boolean tick() {
            tickLiving++;

            if (tickLiving < 0) {
                return true;
            } else {
                prevPos = pos;
                pos = pos.add(motionX, motionY, motionZ);

                motionY -= gravityAmount * 0.001;

                motionX *= 0.9 + (inertiaAmount / 10.0);
                motionY *= 0.9 + (inertiaAmount / 10.0);
                motionZ *= 0.9 + (inertiaAmount / 10.0);

                if (blockPos(pos.x, pos.y, pos.z)) {
                    motionY = -motionY / 1.0;
                } else if (collisionCheck(pos.x, pos.y, pos.z, size, MathHelper.sqrt(motionX * motionY + motionZ * motionZ) * 1)) {
                    motionX = -motionX + motionZ;
                    motionZ = -motionZ + motionX;
                }

                return false;
            }
        }
    }
}

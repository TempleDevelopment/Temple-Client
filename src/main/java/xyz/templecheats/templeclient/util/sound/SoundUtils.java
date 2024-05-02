package xyz.templecheats.templeclient.util.sound;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import xyz.templecheats.templeclient.util.Globals;

import javax.sound.sampled.*;

import java.io.IOException;

import static java.lang.Math.*;
import static java.lang.Math.signum;
import static net.minecraft.util.math.MathHelper.wrapDegrees;

public class SoundUtils implements Globals {

    public static void playSound(Vec3d pos, String sound, float volume, float max) {
        try {
            Clip clip = AudioSystem.getClip();
            AudioInputStream audioInputStream = getAudioInputStream(sound);
            if (audioInputStream == null) {
                System.out.println("Sound not found!");
                return;
            }
            clip.open(audioInputStream);
            clip.start();
            adjustSoundPos(clip, pos, volume, max);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void playSound(Entity entity, String sound, float volume, float max) {
        try {
            Clip clip = AudioSystem.getClip();
            AudioInputStream audioInputStream = getAudioInputStream(sound);
            if (audioInputStream == null) {
                System.out.println("Sound not found!");
                return;
            }
            clip.open(audioInputStream);
            clip.start();
            adjustSoundEntity(clip, entity, volume, max);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static AudioInputStream getAudioInputStream(String sound) throws UnsupportedAudioFileException, IOException {
        return AudioSystem.getAudioInputStream(mc.getResourceManager().getResource(new ResourceLocation("sounds/" + sound + ".wav")).getInputStream());
    }

    private static void adjustSoundPos(Clip clip, Vec3d pos, float volume, float max) {
        FloatControl floatControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        FloatControl balance = (FloatControl) clip.getControl(FloatControl.Type.BALANCE);
        if (pos != null) {
            Vec3d vec = pos.subtract(mc.player.getPositionVector());
            double yaw = wrapDegrees(toDegrees(atan2(vec.z, vec.x)) - 90);
            double delta = wrapDegrees(yaw - mc.player.rotationYaw);
            if (abs(delta) > 180) delta -= signum(delta) * 360;
            try {
                balance.setValue((float) delta / 180);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        assert pos != null;
        float f = (float)(mc.player.posX - pos.x);
        float f1 = (float)(mc.player.posY - pos.y);
        float f2 = (float)(mc.player.posZ - pos.z);
        floatControl.setValue(-(MathHelper.sqrt(f * f + f1 * f1 + f2 * f2) * 5) - (max / volume));
    }

    private static void adjustSoundEntity(Clip clip, Entity entity, float volume, float max) {
        FloatControl floatControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        if (entity != null) {
            FloatControl balance = (FloatControl) clip.getControl(FloatControl.Type.BALANCE);
            Vec3d vec = entity.getPositionVector().subtract(mc.player.getPositionVector());
            double yaw = wrapDegrees(toDegrees(atan2(vec.z, vec.x)) - 90);
            double delta = wrapDegrees(yaw - mc.player.rotationYaw);
            if (abs(delta) > 180) delta -= signum(delta) * 360;
            try {
                balance.setValue((float) delta / 180);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        assert entity != null;
        floatControl.setValue(-(mc.player.getDistance(entity) * 5) - (max / volume));
    }

}

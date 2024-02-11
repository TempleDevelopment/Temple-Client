package xyz.templecheats.templeclient.api.util.songs;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import xyz.templecheats.templeclient.api.util.Globals;

import javax.annotation.Nullable;

public class Templeos implements Globals {

    public static final ISound sound;
    private static final String song = "templeos";
    private static final ResourceLocation loc = new ResourceLocation("sounds/" + song + ".ogg");

    static {
        sound = new ISound() {

            private final int pitch = 1;
            private final int volume = 1;

            @Override
            public ResourceLocation getSoundLocation() {
                return loc;
            }

            @Nullable
            @Override
            public SoundEventAccessor createAccessor(SoundHandler soundHandler) {
                return new SoundEventAccessor(loc, "Templeos");
            }

            @Override
            public Sound getSound() {
                return new Sound(song, volume, pitch, 1, Sound.Type.SOUND_EVENT, false);
            }

            @Override
            public SoundCategory getCategory() {
                return SoundCategory.VOICE;
            }

            @Override
            public boolean canRepeat() {
                return true;
            }

            @Override
            public int getRepeatDelay() {
                return 2;
            }

            @Override
            public float getVolume() {
                return volume;
            }

            @Override
            public float getPitch() {
                return pitch;
            }

            @Override
            public float getXPosF() {
                return 1;
            }

            @Override
            public float getYPosF() {
                return 0;
            }

            @Override
            public float getZPosF() {
                return 0;
            }

            @Override
            public AttenuationType getAttenuationType() {
                return AttenuationType.LINEAR;
            }
        };
    }

}
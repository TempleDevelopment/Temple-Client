/**
 * This SoundManager was made by Wurst+3, and was modified.
 */

package xyz.templecheats.templeclient.manager;

import net.minecraft.client.audio.ISound;
import xyz.templecheats.templeclient.util.Globals;
import xyz.templecheats.templeclient.util.sound.songs.Templeos;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SongManager implements Globals {

    private final List < ISound > songs = Arrays.asList(
            Templeos.sound
    );

    private final ISound menuSong;
    private ISound currentSong;

    public SongManager() {
        this.menuSong = this.getRandomSong();
        this.currentSong = this.getRandomSong();
    }

    public ISound getMenuSong() {
        return this.menuSong;
    }

    public void skip() {
        boolean flag = isCurrentSongPlaying();
        if (flag) {
            this.stop();
        }
        this.currentSong = songs.get((songs.indexOf(currentSong) + 1) % songs.size());
        if (flag) {
            this.play();
        }
    }

    public void play() {
        if (!this.isCurrentSongPlaying()) {
            mc.getSoundHandler().playSound(currentSong);
        }
    }

    public void stop() {
        if (this.isCurrentSongPlaying()) {
            mc.getSoundHandler().stopSound(currentSong);
        }
    }

    private boolean isCurrentSongPlaying() {
        return mc.getSoundHandler().isSoundPlaying(currentSong);
    }

    public void shuffle() {
        this.stop();
        Collections.shuffle(this.songs);
    }

    private ISound getRandomSong() {
        return songs.get(random.nextInt(songs.size()));
    }

}
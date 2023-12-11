package xyz.templecheats.templeclient.features.modules.misc;

import xyz.templecheats.templeclient.features.modules.Module;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import com.mojang.authlib.GameProfile;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.modules.client.Panic;

public class FakePlayer extends Module {
    EntityOtherPlayerMP fakePlayer;

    public FakePlayer() {
        super("FakePlayer", Keyboard.KEY_NONE, Category.MISC);
    }

    @Override
    public void onEnable() {
        GameProfile profile = new GameProfile(mc.player.getUniqueID(), "temple-client bot");
        fakePlayer = new EntityOtherPlayerMP(mc.world, profile);
        fakePlayer.setEntityId(-1882);
        fakePlayer.copyLocationAndAnglesFrom(mc.player);
        fakePlayer.rotationYawHead = mc.player.rotationYawHead;
        mc.world.addEntityToWorld(fakePlayer.getEntityId(), fakePlayer);
    }

    @Override
    public void onDisable() {
        if (!Panic.isPanic) {
            mc.world.removeEntityFromWorld(fakePlayer.getEntityId());
        }
    }
}

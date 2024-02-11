package xyz.templecheats.templeclient.impl.modules.misc;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.templecheats.templeclient.api.setting.Setting;
import xyz.templecheats.templeclient.impl.modules.Module;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import com.mojang.authlib.GameProfile;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.impl.modules.client.Panic;

import java.util.Random;

public class FakePlayer extends Module {
    AttackableFakePlayer fakePlayer;
    BlockPos respawnLocation;
    Random random = new Random();
    Setting moveButton;

    public FakePlayer() {
        super("FakePlayer","Spawns in a fake player", Keyboard.KEY_NONE, Category.Miscelleaneous);
        moveButton = new Setting("Move", this, false);
        this.registerSettings(moveButton);
    }

    @Override
    public void onEnable() {
        if(mc.player == null) {
            return;
        }
        GameProfile profile = new GameProfile(mc.player.getUniqueID(), "temple-client bot");
        fakePlayer = new AttackableFakePlayer(mc.world, profile);
        fakePlayer.setEntityId(-1882);
        fakePlayer.copyLocationAndAnglesFrom(mc.player);
        fakePlayer.rotationYawHead = mc.player.rotationYawHead;
        respawnLocation = mc.player.getPosition();
        mc.world.addEntityToWorld(fakePlayer.getEntityId(), fakePlayer);
    }

    @Override
    public void onDisable() {
        if(fakePlayer == null) {
            return;
        }
        if (!Panic.isPanic) {
            mc.world.removeEntityFromWorld(fakePlayer.getEntityId());
        }
    }

    @Override
    public void onUpdate() {
        if (fakePlayer != null && moveButton.getValBoolean()) {
            fakePlayer.moveRandomly();
        }
    }

    class AttackableFakePlayer extends EntityOtherPlayerMP {
        public AttackableFakePlayer(World worldIn, GameProfile gameProfileIn) {
            super(worldIn, gameProfileIn);
        }

        public void moveRandomly() {
            double x = respawnLocation.getX() + (random.nextDouble() - 0.5) * 1;
            double z = respawnLocation.getZ() + (random.nextDouble() - 0.5) * 1;
            this.setPosition(x, this.posY, z);
        }
    }
}
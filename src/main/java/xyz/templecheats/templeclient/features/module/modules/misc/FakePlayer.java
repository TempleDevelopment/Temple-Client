package xyz.templecheats.templeclient.features.module.modules.misc;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.client.Panic;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;

import java.util.Random;

public class FakePlayer extends Module {
    private static final Random random = new Random();
    private final BooleanSetting moveButton = new BooleanSetting("Move", this, false);
    private AttackableFakePlayer fakePlayer;
    private BlockPos respawnLocation;

    public FakePlayer() {
        super("FakePlayer","Spawns in a fake player", Keyboard.KEY_NONE, Category.Miscelleaneous);

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
        if (fakePlayer != null && moveButton.booleanValue()) {
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
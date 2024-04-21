package xyz.templecheats.templeclient.features.module.modules.misc;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.client.Panic;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.StringSetting;

import java.util.Random;

public class FakePlayer extends Module {
    /*
     * Setting
     */
    private final StringSetting name = new StringSetting("Name", this, "FakePlayer");
    private final BooleanSetting moveButton = new BooleanSetting("Move", this, false);

    /*
     * Variables
     */
    private static final Random random = new Random();
    private AttackableFakePlayer fakePlayer;
    private BlockPos respawnLocation;

    public FakePlayer() {
        super("FakePlayer", "Spawns in a fake player", Keyboard.KEY_NONE, Category.Misc);

        this.registerSettings(name, moveButton);
    }

    @Override
    public void onEnable() {
        if (mc.player == null) {
            return;
        }
        GameProfile profile = new GameProfile(mc.player.getUniqueID(), name.getStringValue());
        fakePlayer = new AttackableFakePlayer(mc.world, profile);
        fakePlayer.setEntityId(-1882);
        fakePlayer.copyLocationAndAnglesFrom(mc.player);
        fakePlayer.rotationYawHead = mc.player.rotationYawHead;
        fakePlayer.inventory.copyInventory(mc.player.inventory);
        respawnLocation = mc.player.getPosition();
        mc.world.addEntityToWorld(fakePlayer.getEntityId(), fakePlayer);
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        this.disable();
    }

    @Override
    public void onDisable() {
        if (fakePlayer == null) {
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
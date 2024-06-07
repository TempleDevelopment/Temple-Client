package xyz.templecheats.templeclient.features.module.modules.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.event.EventStageable;
import xyz.templecheats.templeclient.event.events.network.PacketEvent;
import xyz.templecheats.templeclient.event.events.player.MotionEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.render.Freecam;
import xyz.templecheats.templeclient.util.math.MathUtil;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;

import java.util.ArrayList;
import java.util.List;

public class Flight extends Module {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final EnumSetting<FlightMode> mode = new EnumSetting<>("Mode", this, FlightMode.Vanilla);
    private final BooleanSetting noKick = new BooleanSetting("No Kick", this, false);
    private final DoubleSetting speed = new DoubleSetting("Speed", this, 0.1, 5, 1);

    /****************************************************************
     *                      Variables
     ****************************************************************/
    private int teleportId;
    private final List<CPacketPlayer> packets = new ArrayList<>();

    public Flight() {
        super("Flight", "Fly like a bird", Keyboard.KEY_NONE, Category.Movement);
        registerSettings(noKick, speed, mode);
    }

    @Override
    public void onEnable() {
        if (this.mode.value() == FlightMode.Packet) {
            final Minecraft mc = Minecraft.getMinecraft();

            if (mc.world != null) {
                this.teleportId = 0;
                this.packets.clear();
                final CPacketPlayer bounds = new CPacketPlayer.Position(mc.player.posX, 0, mc.player.posZ, mc.player.onGround);
                this.packets.add(bounds);
                mc.player.connection.sendPacket(bounds);
            }
        }
    }

    @Listener
    public void onWalkingUpdate(MotionEvent event) {
        if (Freecam.isFreecamActive()) {
            return;
        }
        if (event.getStage() == EventStageable.EventStage.PRE) {
            final Minecraft mc = Minecraft.getMinecraft();

            if (this.mode.value() == FlightMode.Vanilla) {
                mc.player.setVelocity(0, 0, 0);

                mc.player.jumpMovementFactor = (float) this.speed.doubleValue();

                if (this.noKick.booleanValue()) {
                    if (mc.player.ticksExisted % 4 == 0) {
                        mc.player.motionY = -0.04f;
                    }
                }

                final double[] dir = MathUtil.directionSpeed(this.speed.doubleValue());

                if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0) {
                    mc.player.motionX = dir[0];
                    mc.player.motionZ = dir[1];
                } else {
                    mc.player.motionX = 0;
                    mc.player.motionZ = 0;
                }

                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    if (this.noKick.booleanValue()) {
                        mc.player.motionY = mc.player.ticksExisted % 20 == 0 ? -0.04f : this.speed.doubleValue();
                    } else {
                        mc.player.motionY += this.speed.doubleValue();
                    }
                }

                if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                    mc.player.motionY -= this.speed.doubleValue();
                }
            }

            if (this.mode.value() == FlightMode.Packet) {
                if (this.teleportId <= 0) {
                    final CPacketPlayer bounds = new CPacketPlayer.Position(Minecraft.getMinecraft().player.posX, 0, Minecraft.getMinecraft().player.posZ, Minecraft.getMinecraft().player.onGround);
                    this.packets.add(bounds);
                    Minecraft.getMinecraft().player.connection.sendPacket(bounds);
                    return;
                }

                mc.player.setVelocity(0, 0, 0);

                if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().expand(-0.0625d, 0, -0.0625d)).isEmpty()) {
                    double ySpeed = 0;

                    if (mc.gameSettings.keyBindJump.isKeyDown()) {

                        if (this.noKick.booleanValue()) {
                            ySpeed = mc.player.ticksExisted % 20 == 0 ? -0.04f : 0.062f;
                        } else {
                            ySpeed = 0.062f;
                        }
                    } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                        ySpeed = -0.062d;
                    } else {
                        ySpeed = mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().expand(-0.0625d, -0.0625d, -0.0625d)).isEmpty() ? (mc.player.ticksExisted % 4 == 0) ? (this.noKick.booleanValue() ? -0.04f : 0.0f) : 0.0f : 0.0f;
                    }

                    final double[] directionalSpeed = MathUtil.directionSpeed(this.speed.doubleValue());

                    if (mc.gameSettings.keyBindJump.isKeyDown() || mc.gameSettings.keyBindSneak.isKeyDown() || mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown()) {
                        if (directionalSpeed[0] != 0.0d || ySpeed != 0.0d || directionalSpeed[1] != 0.0d) {
                            if (mc.player.movementInput.jump && (mc.player.moveStrafing != 0 || mc.player.moveForward != 0)) {
                                mc.player.setVelocity(0, 0, 0);
                                move(0, 0, 0);
                                for (int i = 0; i <= 3; i++) {
                                    mc.player.setVelocity(0, ySpeed * i, 0);
                                    move(0, ySpeed * i, 0);
                                }
                            } else {
                                if (mc.player.movementInput.jump) {
                                    mc.player.setVelocity(0, 0, 0);
                                    move(0, 0, 0);
                                    for (int i = 0; i <= 3; i++) {
                                        mc.player.setVelocity(0, ySpeed * i, 0);
                                        move(0, ySpeed * i, 0);
                                    }
                                } else {
                                    for (int i = 0; i <= 2; i++) {
                                        mc.player.setVelocity(directionalSpeed[0] * i, ySpeed * i, directionalSpeed[1] * i);
                                        move(directionalSpeed[0] * i, ySpeed * i, directionalSpeed[1] * i);
                                    }
                                }
                            }
                        }
                    } else {
                        if (this.noKick.booleanValue()) {
                            if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().expand(-0.0625d, -0.0625d, -0.0625d)).isEmpty()) {
                                mc.player.setVelocity(0, (mc.player.ticksExisted % 2 == 0) ? 0.04f : -0.04f, 0);
                                move(0, (mc.player.ticksExisted % 2 == 0) ? 0.04f : -0.04f, 0);
                            }
                        }
                    }
                }
            }
        }
    }

    private void move(double x, double y, double z) {
        final Minecraft mc = Minecraft.getMinecraft();
        final CPacketPlayer pos = new CPacketPlayer.Position(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z, mc.player.onGround);
        this.packets.add(pos);
        mc.player.connection.sendPacket(pos);

        final CPacketPlayer bounds = new CPacketPlayer.Position(mc.player.posX + x, 0, mc.player.posZ + z, mc.player.onGround);
        this.packets.add(bounds);
        mc.player.connection.sendPacket(bounds);

        this.teleportId++;
        mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.teleportId - 1));
        mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.teleportId));
        mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.teleportId + 1));
    }

    @Listener
    public void sendPacket(PacketEvent.Send event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (this.mode.value() == FlightMode.Packet) {
                if (event.getPacket() instanceof CPacketPlayer && !(event.getPacket() instanceof CPacketPlayer.Position)) {
                    event.setCanceled(true);
                }
                if (event.getPacket() instanceof CPacketPlayer) {
                    final CPacketPlayer packet = (CPacketPlayer) event.getPacket();
                    if (packets.contains(packet)) {
                        packets.remove(packet);
                        return;
                    }
                    event.setCanceled(true);
                }
            }
        }
    }

    @Listener
    public void recievePacket(PacketEvent.Receive event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (this.mode.value() == FlightMode.Packet) {
                if (event.getPacket() instanceof SPacketPlayerPosLook) {
                    final SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();
                    if (Minecraft.getMinecraft().player.isEntityAlive() && Minecraft.getMinecraft().world.isBlockLoaded(new BlockPos(Minecraft.getMinecraft().player.posX, Minecraft.getMinecraft().player.posY, Minecraft.getMinecraft().player.posZ)) && !(Minecraft.getMinecraft().currentScreen instanceof GuiDownloadTerrain)) {
                        if (this.teleportId <= 0) {
                            this.teleportId = packet.getTeleportId();
                        } else {
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getHudInfo() {
        return ChatFormatting.WHITE + mode.value().name() + ChatFormatting.RESET;
    }

    private enum FlightMode {
        Vanilla,
        Packet
    }
}
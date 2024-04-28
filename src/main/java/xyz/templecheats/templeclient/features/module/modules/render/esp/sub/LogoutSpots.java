package xyz.templecheats.templeclient.features.module.modules.render.esp.sub;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.event.events.network.PacketEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.client.Colors;
import xyz.templecheats.templeclient.features.module.modules.client.hud.notification.NotificationType;
import xyz.templecheats.templeclient.features.module.modules.client.hud.notification.Notifications;
import xyz.templecheats.templeclient.util.render.shader.impl.GradientShader;
import xyz.templecheats.templeclient.util.render.shader.impl.RectBuilder;
import xyz.templecheats.templeclient.util.math.Vec2d;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.setting.impl.*;
import xyz.templecheats.templeclient.util.time.TimerUtil;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static net.minecraft.client.Minecraft.getMinecraft;
import static xyz.templecheats.templeclient.features.gui.font.Fonts.font18;
import static xyz.templecheats.templeclient.util.math.MathUtil.lerp;

//TODO: render a model shader not just an entity box.
public class LogoutSpots extends Module {

    /*
     * Settings
     */
    private final IntSetting range = new IntSetting("Range" , this , 10 , 250 , 100);
    private final EnumSetting<Notify> notify = new EnumSetting<>("Notify" , this , Notify.None);
    private final BooleanSetting nameTags = new BooleanSetting("NameTags" , this , true);
    public final BooleanSetting fill = new BooleanSetting("Fill", this, true);
    public final BooleanSetting outline = new BooleanSetting("Outline", this, true);
    public final ColorSetting color = new ColorSetting("Fill Color", this, new Color(0, 0, 0, 255));
    public final ColorSetting outlineColor = new ColorSetting("Outline Color", this, new Color(33, 33, 33, 255));
    private final BooleanSetting sync = new BooleanSetting("Outline sync" , this , true);
    public final DoubleSetting outlineWidth = new DoubleSetting("Width", this, 0.1, 5.0, 1.0);
    public final DoubleSetting roundRadius = new DoubleSetting("Radius", this, 0, 10.0, 3.0);
    private final BooleanSetting time = new BooleanSetting("Timer" , this , true);
    private final BooleanSetting coords = new BooleanSetting("Coords" , this , true);
    private final Map<Entity, String> loggedPlayers = new ConcurrentHashMap<>();
    private Set<EntityPlayer> worldPlayers = ConcurrentHashMap.newKeySet();
    private final TimerUtil timer = new TimerUtil();
    private final Date date = new Date();

    public LogoutSpots() {
        super("LogoutSpots" , "Render player logout spots" , Keyboard.KEY_NONE, Category.Render, true);
        registerSettings(
                nameTags, fill, outline, sync, time, coords,
                color, outlineColor,
                range,
                outlineWidth, roundRadius,
                notify
        );
    }

    @Override
    public void onUpdate() {
        mc.world.playerEntities.stream()
                .filter(entityPlayer -> entityPlayer != mc.player)
                .filter(entityPlayer -> entityPlayer.getDistance(mc.player) <= range.intValue())
                .forEach(worldPlayers::add);
    }

    @Override
    public void onEnable() {
        loggedPlayers.clear();
        worldPlayers = ConcurrentHashMap.newKeySet();
    }

    @Override
    public void onDisable() {
        worldPlayers.clear();
    }

    @SubscribeEvent
    public void onRender3d(RenderWorldLastEvent event) {
        if (mc.player != null && mc.world != null) {
            loggedPlayers.forEach(this::drawLogoutSpot);
        }
    }

    @SuppressWarnings("unused")
    @Listener
    public void onReceive(PacketEvent.Receive event) {
        if (!(event.getPacket() instanceof SPacketPlayerListItem)) return;

        SPacketPlayerListItem packet = (SPacketPlayerListItem) event.getPacket();
        if (packet.getAction() == SPacketPlayerListItem.Action.ADD_PLAYER) {
            handlePlayerAddition(packet);
        } else if (packet.getAction() == SPacketPlayerListItem.Action.REMOVE_PLAYER) {
            handlePlayerRemoval(packet);
        }
    }

    private void handlePlayerAddition(SPacketPlayerListItem packet) {
        for (SPacketPlayerListItem.AddPlayerData playerData : packet.getEntries()) {
            if (shouldHandlePlayer(playerData.getProfile().getId())) {
                handlePlayerReconnect(playerData.getProfile().getId());
            }
        }
    }

    private void handlePlayerRemoval(SPacketPlayerListItem packet) {
        for (SPacketPlayerListItem.AddPlayerData playerData : packet.getEntries()) {
            if (shouldHandlePlayer(playerData.getProfile().getId())) {
                handlePlayerDisconnect(playerData.getProfile().getId());
            }
        }
    }

    private boolean shouldHandlePlayer(UUID playerId) {
        return playerId != getMinecraft().getSession().getProfile().getId();
    }

    private void handlePlayerReconnect(UUID playerId) {
        new Thread(() -> {
            String playerName = String.valueOf(mc.world.getPlayerEntityByUUID(playerId));
            if (playerName != null && mc.player != null && mc.player.ticksExisted >= 500 && mc.world != null) {
                loggedPlayers.keySet().removeIf(entity -> {
                    if (entity.getName().equalsIgnoreCase(entity.getName())) {
                        notifyPlayerReconnect(entity.getName());
                        return true;
                    }
                    return false;
                });
            }
        }).start();
    }

    private void handlePlayerDisconnect(UUID playerId) {
        new Thread(() -> {
            String playerName = String.valueOf(mc.world.getPlayerEntityByUUID(playerId));
            if (playerName != null && mc.player != null && mc.player.ticksExisted >= 500 && mc.world != null) {
                worldPlayers.removeIf(entity -> {
                    if (entity.getName().equalsIgnoreCase(entity.getName())) {
                        loggedPlayers.put(entity, getLogOutTime());
                        notifyPlayerDisconnect(entity.getName(), entity);
                        return true;
                    }
                    return false;
                });
            }
        }).start();
    }

    private void notifyPlayerReconnect(String playerName) {
        if (shouldNotifyChat()) {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString(playerName + " reconnected!"));
        }
        if (shouldNotifyBar()) {
            Notifications.addMessage(this.getName(), playerName + " reconnected!", NotificationType.INFO);
        }
    }

    private void notifyPlayerDisconnect(String playerName, Entity entity) {
        String location = "(" + (int) entity.posX + ", " + (int) entity.posY + ", " + (int) entity.posZ + ")";
        if (shouldNotifyChat() && timer.getTimePassed() / 50L >= 5) {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString(playerName + " disconnected at " + location + "!"));
            timer.reset();
        }
        if (shouldNotifyBar()) {
            Notifications.addMessage(this.getName(), playerName + " disconnected at " + location + "!", NotificationType.INFO);
        }
    }

    private boolean shouldNotifyChat() {
        return notify.value() == Notify.Chat || notify.value() == Notify.Both;
    }

    private boolean shouldNotifyBar() {
        return notify.value() == Notify.Bar || notify.value() == Notify.Both;
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        worldPlayers.clear();
        if (mc.player == null || mc.world == null) {
            loggedPlayers.clear();
        }
    }

    @SubscribeEvent
    public void onWorldUnLoad(WorldEvent.Unload event) {
        worldPlayers.clear();
        if (mc.player == null || mc.world == null) {
            loggedPlayers.clear();
        }
    }

    private void drawLogoutSpot(Entity entity, String string) {
        if (entity.getDistance(mc.player) > range.intValue()) {
            return;
        }

        if (entity != getMinecraft().player) {
            double x = lerp(entity.lastTickPosX, entity.posX, mc.getRenderPartialTicks()) - mc.getRenderManager().viewerPosX;
            double y = lerp(entity.lastTickPosY, entity.posY, mc.getRenderPartialTicks()) - mc.getRenderManager().viewerPosY;
            double z = lerp(entity.lastTickPosZ, entity.posZ, mc.getRenderPartialTicks()) - mc.getRenderManager().viewerPosZ;

            if (nameTags.booleanValue()) drawNameTag(entity.getName(), x, y, z);

            AxisAlignedBB box = new AxisAlignedBB(
                    entity.getEntityBoundingBox().minX - 0.05,
                    entity.getEntityBoundingBox().minY,
                    entity.getEntityBoundingBox().minZ - 0.05,
                    entity.getEntityBoundingBox().maxX + 0.05,
                    entity.getEntityBoundingBox().maxY + 0.1,
                    entity.getEntityBoundingBox().maxZ + 0.05
            );

            GradientShader.setup(0.5f);
            RenderUtil.outlineShader(box.minX , box.minY , box.minZ , box.maxX, box.maxY, box.maxZ);
            RenderUtil.boxShader(box.minX , box.minY , box.minZ , box.maxX, box.maxY, box.maxZ);
            GradientShader.finish();
        }
    }

    private void drawNameTag(String name, double x, double y, double z) {
        y += 0.7;

        Entity camera = mc.getRenderViewEntity();

        assert (camera != null);

        double originalPositionX = camera.posX;
        double originalPositionY = camera.posY;
        double originalPositionZ = camera.posZ;

        camera.posX = lerp(camera.prevPosX, camera.posX, mc.getRenderPartialTicks());
        camera.posY = lerp(camera.prevPosY, camera.posY, mc.getRenderPartialTicks());
        camera.posZ = lerp(camera.prevPosZ, camera.posZ, mc.getRenderPartialTicks());

        String displayTag = name
                + (coords.booleanValue()
                ? (" XYZ: " + (int) x + ", " + (int) y + ", " + (int) z)
                : "")
                + (time.booleanValue()
                ? " " + ChatFormatting.GRAY + "(" + getLogOutTime() + ")"
                : "");

        double distance = camera.getDistance(x + mc.getRenderManager().viewerPosX, y + mc.getRenderManager().viewerPosY, z + mc.getRenderManager().viewerPosZ);
        int width = (int) (font18.getStringWidth(displayTag) / 2);
        double scale = (0.0018 + (double) 5.0f * (distance * (double) 0.6f)) / 1000.0;

        if (distance <= 8.0) {
            scale = 0.0245;
        }

        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f, -1500000.0f);
        GlStateManager.disableLighting();

        GlStateManager.translate((float) x, (float) y + 1.4f, (float) z);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(-scale, -scale, scale);

        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.enableBlend();

        new RectBuilder(new Vec2d(-width - 2, -(font18.getFontHeight() + 1)), new Vec2d((float) width + 2.0f, 1.5f))
                .outlineColor(
                        sync.booleanValue() ? Colors.INSTANCE.getColor(width) : outlineColor.getColor(),
                        sync.booleanValue() ? Colors.INSTANCE.getColor(width + 1) : outlineColor.getColor(),
                        sync.booleanValue() ? Colors.INSTANCE.getColor(width + 5) : outlineColor.getColor(),
                        sync.booleanValue() ? Colors.INSTANCE.getColor(0) : outlineColor.getColor()
                )
                .width(outline.booleanValue() ? outlineWidth.doubleValue() : 0)
                .color(fill.booleanValue() ? color.getColor() : new Color(0,0,0,0))
                .radius(roundRadius.doubleValue())
                .draw();

        GlStateManager.disableBlend();
        font18.drawStringWithShadow(displayTag, -width, -(font18.getFontHeight()), -1);

        camera.posX = originalPositionX;
        camera.posY = originalPositionY;
        camera.posZ = originalPositionZ;

        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f, 1500000.0f);
        GlStateManager.popMatrix();
    }

    private String getLogOutTime() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss");

        return dateFormatter.format(date);
    }

    private enum Notify {
        None,
        Chat,
        Bar,
        Both
    }
}

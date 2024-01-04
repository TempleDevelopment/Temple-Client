package xyz.templecheats.templeclient;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.Session;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.ClientChatEvent;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;
import xyz.templecheats.templeclient.gui.clickgui.ClickGuiManager;
import xyz.templecheats.templeclient.gui.clickgui.setting.SettingsManager;
import xyz.templecheats.templeclient.gui.menu.GuiEventsListener;
import xyz.templecheats.templeclient.gui.ui.watermark;
import xyz.templecheats.templeclient.event.EventManager;
import xyz.templecheats.templeclient.util.keys.key;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

import java.lang.reflect.Field;

@Mod(modid = TempleClient.MODID, name = TempleClient.NAME, version = TempleClient.VERSION)
public class TempleClient {
    public static final String MODID = "templeclient";
    public static final String NAME = "Temple Client";
    public static final String VERSION = "1.8.0";

    public static TempleClient instance;
    public SettingsManager settingsManager;
    public static EventManager clientEventManager;
    public ClickGuiManager clickGui;

    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Display.setTitle("Loading " + Client.name);
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        clientEventManager = new EventManager();
        MinecraftForge.EVENT_BUS.register(clientEventManager);

        clickGui = new ClickGuiManager();
        instance = this;
        settingsManager = new SettingsManager();

        Client.startup();

        MinecraftForge.EVENT_BUS.register(new key());
        MinecraftForge.EVENT_BUS.register(new watermark());
        MinecraftForge.EVENT_BUS.register(new GuiEventsListener());
        MinecraftForge.EVENT_BUS.register(this);
        logger.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    @SubscribeEvent
    public void onChat(ClientChatEvent event) {
        if (event.getMessage().startsWith(".")) {
            if (Client.commandManager.executeCommand(event.getMessage())) {
                event.setCanceled(true);
            }
        }
    }

    public static void setSession(Session s) {
        Class<? extends Minecraft> mc = Minecraft.getMinecraft().getClass();

        try {
            Field session = null;

            for (Field f : mc.getDeclaredFields()) {
                if (f.getType().isInstance(s)) {
                    session = f;
                }
            }

            if (session == null) {
                throw new IllegalStateException("Session Null");
            }

            session.setAccessible(true);
            session.set(Minecraft.getMinecraft(), s);
            session.setAccessible(false);

            Client.name = "TempleClient 1.12.2 | User: " + Minecraft.getMinecraft().getSession().getUsername();
            Display.setTitle(Client.name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

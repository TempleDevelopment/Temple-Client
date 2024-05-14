/************************************************************************************************
 *                                               Temple Client                                  *
 *                (c) 2023-2024 Temple Client Development Team. All rights reserved.            *
 ************************************************************************************************/

package xyz.templecheats.templeclient;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;
import team.stiff.pomelo.impl.annotated.AnnotatedEventManager;
import xyz.templecheats.templeclient.config.ConfigManager;
import xyz.templecheats.templeclient.event.ForgeEventManager;
import xyz.templecheats.templeclient.features.gui.font.FontUtils;
import xyz.templecheats.templeclient.features.gui.menu.GuiEventsListener;
import xyz.templecheats.templeclient.features.gui.menu.alt.AltManager;
import xyz.templecheats.templeclient.manager.*;
import xyz.templecheats.templeclient.util.friend.FriendManager;
import xyz.templecheats.templeclient.util.keys.KeyUtil;
import xyz.templecheats.templeclient.util.setting.SettingsManager;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

@Mod(modid = TempleClient.MODID, name = TempleClient.NAME, version = TempleClient.VERSION)
public class TempleClient {
    public static final String MODID = "templeclient";
    public static final String NAME = "Temple Client";
    public static final String VERSION = "1.9.2";
    public static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    public static String name = NAME + " " + VERSION;
    public static AnnotatedEventManager eventBus;
    public static SettingsManager settingsManager;
    public static ModuleManager moduleManager;
    public static ForgeEventManager clientForgeEventManager;
    public static CommandManager commandManager;
    public static ConfigManager configManager;
    public static CapeManager capeManager;
    public static FriendManager friendManager;
    public static AltManager altManager;
    public static SongManager SONG_MANAGER;
    public static HoleManager holeManager = new HoleManager();
    public static InventoryManager inventoryManager = new InventoryManager();
    public static RotationManager rotationManager = new RotationManager();
    public static ThreadManager threadManager;
    public static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Display.setTitle("Loading " + name);
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        Display.setTitle(name);
        MinecraftForge.EVENT_BUS.register(new TempleClient());

        eventBus = new AnnotatedEventManager();
        clientForgeEventManager = new ForgeEventManager();
        MinecraftForge.EVENT_BUS.register(clientForgeEventManager);

        settingsManager = new SettingsManager();

        TempleClient.moduleManager = new ModuleManager();
        logger.info("Module Manager Loaded.");

        TempleClient.commandManager = new CommandManager();
        logger.info("Commands Loaded.");

        capeManager = new CapeManager();

        friendManager = new FriendManager();

        altManager = new AltManager();

        SONG_MANAGER = new SongManager();
        threadManager = new ThreadManager();

        MinecraftForge.EVENT_BUS.register(clientForgeEventManager);
        MinecraftForge.EVENT_BUS.register(commandManager);

        MinecraftForge.EVENT_BUS.register(new KeyUtil());
        MinecraftForge.EVENT_BUS.register(new GuiEventsListener());

        FontUtils.setupFonts();
        FontUtils.setupIcons();

        configManager = new ConfigManager();

        logger.info("Initialized Config!");

        configManager.loadAll();

        Runtime.getRuntime().addShutdownHook(new Thread(TempleClient.configManager::saveAll));
    }

    @SubscribeEvent
    public void onChat(ClientChatEvent event) {

        if (event.getMessage().startsWith("."))
            if (TempleClient.commandManager.executeCommand(event.getMessage()))
                event.setCanceled(true);
    }

    public static void setSession(Session s) {
        Class < ? extends Minecraft > mc = Minecraft.getMinecraft().getClass();

        try {
            Field session = null;

            for (Field f: mc.getDeclaredFields()) {
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

            name = "TempleClient 1.12.2 | User: " + Minecraft.getMinecraft().getSession().getUsername();
            Display.setTitle(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ModuleManager getModuleManager() {
        return TempleClient.moduleManager;
    }
}
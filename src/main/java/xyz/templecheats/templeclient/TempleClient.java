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
import xyz.templecheats.templeclient.api.manager.CapeManager;
import xyz.templecheats.templeclient.api.config.ShutdownHook;
import xyz.templecheats.templeclient.api.config.ConfigManager;
import xyz.templecheats.templeclient.api.event.EventManager;
import xyz.templecheats.templeclient.api.manager.SongManager;
import xyz.templecheats.templeclient.api.util.keys.KeyUtil;
import xyz.templecheats.templeclient.api.command.CommandManager;
import xyz.templecheats.templeclient.api.setting.SettingsManager;
import xyz.templecheats.templeclient.impl.gui.font.FontUtils;
import xyz.templecheats.templeclient.impl.gui.menu.GuiEventsListener;

import java.lang.reflect.Field;

@Mod(modid = TempleClient.MODID, name = TempleClient.NAME, version = TempleClient.VERSION)
public class TempleClient {
    public static final String MODID = "templeclient";
    public static final String NAME = "Temple Client";
    public static final String VERSION = "1.8.6";
    public static String name = NAME + " " + VERSION;

    public static AnnotatedEventManager eventBus;
    public static SettingsManager settingsManager;
    public static ModuleManager moduleManager;
    public static EventManager clientEventManager;
    public static CommandManager commandManager;
    public static ConfigManager configManager;
    public static CapeManager capeManager;
    public static SongManager SONG_MANAGER;
    private static Logger logger;

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
        clientEventManager = new EventManager();
        MinecraftForge.EVENT_BUS.register(clientEventManager);

        settingsManager = new SettingsManager();

        (TempleClient.moduleManager = new ModuleManager()).initMods();
        logger.info("Module Manager Loaded.");

        (TempleClient.commandManager = new CommandManager()).commandInit();
        logger.info("Commands Loaded.");

        capeManager = new CapeManager();

        SONG_MANAGER = new SongManager();

        MinecraftForge.EVENT_BUS.register(clientEventManager);
        MinecraftForge.EVENT_BUS.register(commandManager);

        MinecraftForge.EVENT_BUS.register(new KeyUtil());
        MinecraftForge.EVENT_BUS.register(new GuiEventsListener());

        FontUtils.bootstrap();

        configManager = new ConfigManager();

        logger.info("Initialized Config!");

        configManager.loadAll();

        Runtime.getRuntime().addShutdownHook(new ShutdownHook());
    }

    @SubscribeEvent
    public void onChat(ClientChatEvent event) {

        if(event.getMessage().startsWith(".")) {
            if(TempleClient.commandManager.executeCommand(event.getMessage())) {
                event.setCanceled(true);
            }
        }
    }

    public static void setSession(Session s) {
        Class<? extends Minecraft> mc = Minecraft.getMinecraft().getClass();

        try {
            Field session = null;

            for(Field f : mc.getDeclaredFields()) {
                if(f.getType().isInstance(s)) {
                    session = f;
                }
            }

            if(session == null) {
                throw new IllegalStateException("Session Null");
            }

            session.setAccessible(true);
            session.set(Minecraft.getMinecraft(), s);
            session.setAccessible(false);

            name = "TempleClient 1.12.2 | User: " + Minecraft.getMinecraft().getSession().getUsername();
            Display.setTitle(name);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static ModuleManager getModuleManager() {
        return TempleClient.moduleManager;
    }
}
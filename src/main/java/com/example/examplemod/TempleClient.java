package com.example.examplemod;

import com.example.examplemod.Menu.onGuiOpenEvent;
import com.example.examplemod.UI.ui;
import com.example.examplemod.keys.key;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

import static org.lwjgl.opengl.Display.*;

@Mod(modid = TempleClient.MODID, name = TempleClient.NAME, version = TempleClient.VERSION)
public class TempleClient
{
    public static final String MODID = "templeclient";
    public static final String NAME = "TempleClient";
    public static final String VERSION = "1.0";

    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        setTitle("Loading " + Client.name);
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        Client.startup();
        MinecraftForge.EVENT_BUS.register(new key());
        MinecraftForge.EVENT_BUS.register(new ui());
        MinecraftForge.EVENT_BUS.register(new onGuiOpenEvent());
        logger.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }
}

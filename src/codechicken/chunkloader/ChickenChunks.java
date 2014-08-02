package codechicken.chunkloader;

import java.io.File;

import codechicken.core.launch.CodeChickenCorePlugin;
import codechicken.lib.config.ConfigFile;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(modid = "ChickenChunks", dependencies = "required-after:CodeChickenCore@[" + CodeChickenCorePlugin.version + ",)", acceptedMinecraftVersions = CodeChickenCorePlugin.mcVersion)
public class ChickenChunks
{
    @SidedProxy(clientSide = "codechicken.chunkloader.ChunkLoaderClientProxy", serverSide = "codechicken.chunkloader.ChunkLoaderProxy")
    public static ChunkLoaderProxy proxy;

    public static ConfigFile config;

    public static BlockChunkLoader blockChunkLoader;

    @Instance(value = "ChickenChunks")
    public static ChickenChunks instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        config = new ConfigFile(new File(event.getModConfigurationDirectory(), "ChickenChunks.cfg"))
                .setComment("ChunkLoader Configuration File\nDeleting any element will restore it to it's default value\nBlock ID's will be automatically generated the first time it's run");
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.registerCommands(event);
    }
}

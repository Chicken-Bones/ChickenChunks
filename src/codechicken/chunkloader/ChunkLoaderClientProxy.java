package codechicken.chunkloader;

import net.minecraft.client.Minecraft;
import codechicken.core.CCUpdateChecker;
import codechicken.lib.packet.PacketCustom;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

import static codechicken.chunkloader.ChickenChunks.*;

public class ChunkLoaderClientProxy  extends ChunkLoaderProxy
{
    @Override
    public void init()
    {
        if(config.getTag("checkUpdates").getBooleanValue(true))
            CCUpdateChecker.updateCheck("ChickenChunks");
        
        super.init();
        
        PacketCustom.assignHandler(ChunkLoaderCPH.channel, new ChunkLoaderCPH());
        
        ClientRegistry.bindTileEntitySpecialRenderer(TileChunkLoader.class, new TileChunkLoaderRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileSpotLoader.class, new TileChunkLoaderRenderer());
        RenderingRegistry.registerBlockHandler(new ChunkLoaderSBRH());
        
    }
    
    @Override
    public void openGui(TileChunkLoader tile)
    {
        Minecraft.getMinecraft().displayGuiScreen(new GuiChunkLoader(tile));
    }
}

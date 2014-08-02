package codechicken.chunkloader;

import java.util.List;

import codechicken.core.ServerUtils;
import codechicken.lib.packet.PacketCustom;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.BlockContainer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockChunkLoader extends BlockContainer
{
    @SideOnly(Side.CLIENT)
    IIcon[][] icons;

    public BlockChunkLoader() {
        super(Material.rock);
        setHardness(20F);
        setResistance(100F);
        setStepSound(soundTypeStone);
    }

    @Override
    public boolean isNormalCube(IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        setBlockBoundsForItemRender(world.getBlockMetadata(x, y, z));
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB par5AxisAlignedBB, List par6List, Entity par7Entity) {
        setBlockBoundsBasedOnState(world, x, y, z);
        super.addCollisionBoxesToList(world, x, y, z, par5AxisAlignedBB, par6List, par7Entity);
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        if (world.getBlockMetadata(x, y, z) == 1)
            return false;

        return side == ForgeDirection.DOWN;
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
        return true;
    }

    public void setBlockBoundsForItemRender(int metadata) {
        switch (metadata) {
            case 0:
                setBlockBounds(0, 0, 0, 1, 0.75F, 1);
                break;
            case 1:
                setBlockBounds(0.25F, 0, 0.25F, 0.75F, 0.4375F, 0.75F);
                break;
        }
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return icons[meta][side > 2 ? 2 : side];
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta != 0 || player.isSneaking())
            return false;

        if (!world.isRemote) {
            TileChunkLoader tile = (TileChunkLoader) world.getTileEntity(x, y, z);
            if (tile.owner == null || tile.owner.equals(player.getCommandSenderName()) ||
                    ChunkLoaderManager.opInteract() && ServerUtils.isPlayerOP(player.getCommandSenderName())) {
                PacketCustom packet = new PacketCustom(ChunkLoaderSPH.channel, 12);
                packet.writeCoord(x, y, z);
                packet.sendToPlayer(player);
            } else
                player.addChatMessage(new ChatComponentTranslation("chickenchunks.accessdenied"));
        }
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase entityliving, ItemStack itemstack) {
        if (world.isRemote)
            return;

        TileChunkLoaderBase ctile = (TileChunkLoaderBase) world.getTileEntity(i, j, k);
        ctile.onBlockPlacedBy(entityliving);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        if (meta == 0)
            return new TileChunkLoader();
        else if (meta == 1)
            return new TileSpotLoader();
        else
            return null;
    }

    @Override
    public void registerBlockIcons(IIconRegister par1IconRegister) {
        icons = new IIcon[2][3];
        for (int m = 0; m < icons.length; m++)
            for (int i = 0; i < icons[m].length; i++)
                icons[m][i] = par1IconRegister.registerIcon("chickenchunks:block_" + m + "_" + i);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List list) {
        list.add(new ItemStack(this, 1, 0));
        list.add(new ItemStack(this, 1, 1));
    }

    @Override
    public int damageDropped(int par1) {
        return par1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderType() {
        return ChunkLoaderSBRH.renderID;
    }
}

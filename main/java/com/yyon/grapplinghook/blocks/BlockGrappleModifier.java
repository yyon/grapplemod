package com.yyon.grapplinghook.blocks;

import com.yyon.grapplinghook.GuiModifier;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockGrappleModifier extends Block {

	public BlockGrappleModifier() {
	    super(Material.ROCK);
	    this.setCreativeTab(CreativeTabs.TRANSPORTATION); // the block will appear on the Blocks tab in creative
	}
	
	  @Override
	  public boolean hasTileEntity(IBlockState state)
	  {
	    return true;
	  }

	  // Called when the block is placed or loaded client side to get the tile entity for the block
	  // Should return a new instance of the tile entity for the block
	  @Override
	  public TileEntity createTileEntity(World world, IBlockState state) {
		  return new  TileEntityGrappleModifier();
	  }

	  // the block will render in the SOLID layer.  See http://greyminecraftcoder.blogspot.co.at/2014/12/block-rendering-18.html for more information.
	  @SideOnly(Side.CLIENT)
	  public BlockRenderLayer getBlockLayer()
	  {
	    return BlockRenderLayer.SOLID;
	  }

	  @Override
	  public boolean isOpaqueCube(IBlockState state)
	  {
	    return true;
	  }

	  @Override
	  public boolean isFullCube(IBlockState state)
	  {
	    return true;
	  }

	  // render using a BakedModel
	  // not required because the default (super method) is MODEL
	  @Override
	  public EnumBlockRenderType getRenderType(IBlockState iBlockState) {
	    return EnumBlockRenderType.MODEL;
	  }

	  @Override
      public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		  Minecraft.getMinecraft().displayGuiScreen(new GuiModifier());
		  
	  	return true;
		  
	  }
}

package com.yyon.grapplinghook.blocks;

import java.util.Map;

import javax.annotation.Nullable;

import com.yyon.grapplinghook.GrappleConfig;
import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.items.upgrades.BaseUpgradeItem;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockGrappleModifier extends Block {

	public BlockGrappleModifier() {
		super(Material.ROCK);
		this.setCreativeTab(CreativeTabs.TRANSPORTATION); // the block will appear on the Blocks tab in creative
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	// Called when the block is placed or loaded client side to get the tile entity
	// for the block
	// Should return a new instance of the tile entity for the block
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityGrappleModifier();
	}

	// the block will render in the SOLID layer. See
	// http://greyminecraftcoder.blogspot.co.at/2014/12/block-rendering-18.html for
	// more information.
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.SOLID;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return true;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return true;
	}

	// render using a BakedModel
	// not required because the default (super method) is MODEL
	@Override
	public EnumBlockRenderType getRenderType(IBlockState iBlockState) {
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		super.getDrops(drops, world, pos, state, fortune);
		
		TileEntity ent = world.getTileEntity(pos);
		TileEntityGrappleModifier tileent = (TileEntityGrappleModifier) ent;
		
		for (grapplemod.upgradeCategories category : grapplemod.upgradeCategories.values()) {
			if (tileent.unlockedCategories.containsKey(category) && tileent.unlockedCategories.get(category)) {
				drops.add(new ItemStack(category.getItem()));
			}
		}
	}
	
    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
    {
        if (willHarvest) return true; //If it will harvest, delay deletion of the block until after getDrops
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }
    /**
     * Spawns the block's drops in the world. By the time this is called the Block has possibly been set to air via
     * Block.removedByPlayer
     */
    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack tool)
    {
        super.harvestBlock(world, player, pos, state, te, tool);
        world.setBlockToAir(pos);
    }
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack helditemstack = playerIn.getHeldItemMainhand();
		Item helditem = helditemstack.getItem();

		if (helditem instanceof BaseUpgradeItem) {
			if (!worldIn.isRemote) {
				TileEntity ent = worldIn.getTileEntity(pos);
				TileEntityGrappleModifier tileent = (TileEntityGrappleModifier) ent;
				
				grapplemod.upgradeCategories category = ((BaseUpgradeItem) helditem).category;
				
				if (tileent.isUnlocked(category)) {
					playerIn.sendMessage(new TextComponentString("Already has upgrade: " + category.description));
				} else {
					if (!playerIn.capabilities.isCreativeMode) {
						playerIn.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
					}
					
					tileent.unlockCategory(category);
					
					playerIn.sendMessage(new TextComponentString("Applied upgrade: " + category.description));
				}
			}
		} else if (helditem == grapplemod.grapplebowitem) {
			if (!worldIn.isRemote) {
				TileEntity ent = worldIn.getTileEntity(pos);
				TileEntityGrappleModifier tileent = (TileEntityGrappleModifier) ent;
				
				GrappleCustomization custom = tileent.customization;
				NBTTagCompound nbt = custom.writeNBT();
				
				helditemstack.setTagCompound(nbt);
				
				playerIn.sendMessage(new TextComponentString("Applied configuration"));
			}
		} else if (helditem == Items.DIAMOND_BOOTS) {
			if (!worldIn.isRemote) {
				if (GrappleConfig.getconf().longfallbootsrecipe) {
					boolean gaveitem = false;
					if (helditemstack.isItemEnchanted()) {
						Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(helditemstack);
						if (enchantments.containsKey(Enchantments.FEATHER_FALLING)) {
							if (enchantments.get(Enchantments.FEATHER_FALLING) >= 4) {
								ItemStack newitemstack = new ItemStack(grapplemod.longfallboots);
								EnchantmentHelper.setEnchantments(enchantments, newitemstack);
								playerIn.setHeldItem(EnumHand.MAIN_HAND, newitemstack);
								gaveitem = true;
							}
						}
					}
					if (!gaveitem) {
						playerIn.sendMessage(new TextComponentString("Right click with diamond boots enchanted with feather falling IV to get long fall boots"));
					}
				} else {
					playerIn.sendMessage(new TextComponentString("Making long fall boots this way was disabled in the config. It probably has been replaced by a crafting recipe."));
				}
			}
		} else {
			TileEntity ent = worldIn.getTileEntity(pos);
			TileEntityGrappleModifier tileent = (TileEntityGrappleModifier) ent;
			
			grapplemod.proxy.openModifierScreen(tileent);
		}
		return true;

	}
}

package com.yyon.grapplinghook.blocks;

import java.util.ArrayList;
import java.util.List;

import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.network.GrappleModifierMessage;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

public class BlockGrappleModifier extends Block {

	public BlockGrappleModifier() {
		super(Block.Properties.of(Material.STONE));
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileEntityGrappleModifier();
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder lootctx) {
		List<ItemStack> drops = new ArrayList<ItemStack>();
		drops.add(new ItemStack(this.asItem()));
		TileEntity ent = lootctx.getOptionalParameter(LootParameters.BLOCK_ENTITY);
		if (ent == null || !(ent instanceof TileEntityGrappleModifier)) {
			return drops;
		}
		TileEntityGrappleModifier tileent = (TileEntityGrappleModifier) ent;
		
		for (grapplemod.upgradeCategories category : grapplemod.upgradeCategories.values()) {
			if (tileent.unlockedCategories.containsKey(category) && tileent.unlockedCategories.get(category)) {
				drops.add(new ItemStack(category.getItem()));
			}
		}
		return drops;
	}
	
    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult raytraceresult) {
    	grapplemod.LOGGER.info("Block use");
    	return ActionResultType.SUCCESS;
    	/*
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
		} else if (helditem instanceof grappleBow) {
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
		*/
	}
}

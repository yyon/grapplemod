package com.yyon.grapplinghook.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.yyon.grapplinghook.GrappleConfig;
import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.vec;
import com.yyon.grapplinghook.items.grappleBow;
import com.yyon.grapplinghook.items.upgrades.BaseUpgradeItem;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

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
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult raytraceresult) {
		ItemStack helditemstack = playerIn.getItemInHand(Hand.MAIN_HAND);
		Item helditem = helditemstack.getItem();

		if (helditem instanceof BaseUpgradeItem) {
			if (!worldIn.isClientSide) {
				TileEntity ent = worldIn.getBlockEntity(pos);
				TileEntityGrappleModifier tileent = (TileEntityGrappleModifier) ent;
				
				grapplemod.upgradeCategories category = ((BaseUpgradeItem) helditem).category;
				
				if (tileent.isUnlocked(category)) {
					playerIn.sendMessage(new StringTextComponent("Already has upgrade: " + category.description), playerIn.getUUID());
				} else {
					if (!playerIn.isCreative()) {
						playerIn.setItemInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
					}
					
					tileent.unlockCategory(category);
					
					playerIn.sendMessage(new StringTextComponent("Applied upgrade: " + category.description), playerIn.getUUID());
				}
			}
		} else if (helditem instanceof grappleBow) {
			if (!worldIn.isClientSide) {
				TileEntity ent = worldIn.getBlockEntity(pos);
				TileEntityGrappleModifier tileent = (TileEntityGrappleModifier) ent;
				
				GrappleCustomization custom = tileent.customization;
				((grappleBow) grapplemod.grapplebowitem).setCustomOnServer(helditemstack, custom, playerIn);
				
				playerIn.sendMessage(new StringTextComponent("Applied configuration"), playerIn.getUUID());
			}
		} else if (helditem == Items.DIAMOND_BOOTS) {
			if (!worldIn.isClientSide) {
				if (GrappleConfig.getconf().longfallbootsrecipe) {
					boolean gaveitem = false;
					if (helditemstack.isEnchanted()) {
						Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(helditemstack);
						if (enchantments.containsKey(Enchantments.FALL_PROTECTION)) {
							if (enchantments.get(Enchantments.FALL_PROTECTION) >= 4) {
								ItemStack newitemstack = new ItemStack(grapplemod.longfallboots);
								EnchantmentHelper.setEnchantments(enchantments, newitemstack);
								playerIn.setItemInHand(Hand.MAIN_HAND, newitemstack);
								gaveitem = true;
							}
						}
					}
					if (!gaveitem) {
						playerIn.sendMessage(new StringTextComponent("Right click with diamond boots enchanted with feather falling IV to get long fall boots"), playerIn.getUUID());
					}
				} else {
					playerIn.sendMessage(new StringTextComponent("Making long fall boots this way was disabled in the config. It probably has been replaced by a crafting recipe."), playerIn.getUUID());
				}
			}
		} else if (helditem == Items.DIAMOND) {
			this.easterEgg(state, worldIn, pos, playerIn, hand, raytraceresult);
		} else {
			if (worldIn.isClientSide) {
				TileEntity ent = worldIn.getBlockEntity(pos);
				TileEntityGrappleModifier tileent = (TileEntityGrappleModifier) ent;
				
				grapplemod.proxy.openModifierScreen(tileent);
			}
		}
		return ActionResultType.SUCCESS;
	}

	public void easterEgg(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand,
			BlockRayTraceResult raytraceresult) {
		int spacing = 3;
		vec[] positions = new vec[] {new vec(-spacing*2, 0, 0), new vec(-spacing, 0, 0), new vec(0, 0, 0), new vec(spacing, 0, 0), new vec(2*spacing, 0, 0)};
		int[] colors = new int[] {0x5bcffa, 0xf5abb9, 0xffffff, 0xf5abb9, 0x5bcffa};
		
		for (int i = 0; i < positions.length; i++) {
			vec newpos = new vec(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
			vec toPlayer = vec.positionvec(playerIn).sub(newpos);
			double angle = toPlayer.length() == 0 ? 0 : toPlayer.getYaw();
			newpos = newpos.add(positions[i].rotate_yaw(Math.toRadians(angle)));
			
			CompoundNBT explosion = new CompoundNBT();
	        explosion.putByte("Type", (byte) FireworkRocketItem.Shape.SMALL_BALL.getId());
	        explosion.putBoolean("Trail", true);
	        explosion.putBoolean("Flicker", false);
	        explosion.putIntArray("Colors", new int[] {colors[i]});
	        explosion.putIntArray("FadeColors", new int[] {});
	        ListNBT list = new ListNBT();
	        list.add(explosion);
	        CompoundNBT fireworks = new CompoundNBT();
	        fireworks.put("Explosions", list);
	        CompoundNBT nbt = new CompoundNBT();
	        nbt.put("Fireworks", fireworks);
	        ItemStack stack = new ItemStack(Items.FIREWORK_ROCKET);
	        stack.setTag(nbt);
			FireworkRocketEntity firework = new FireworkRocketEntity(worldIn, playerIn, newpos.x, newpos.y, newpos.z, stack);
			CompoundNBT fireworksave = new CompoundNBT();
			firework.addAdditionalSaveData(fireworksave);
			fireworksave.putInt("LifeTime", 15);
			firework.readAdditionalSaveData(fireworksave);
			worldIn.addFreshEntity(firework);
		}
	}
}

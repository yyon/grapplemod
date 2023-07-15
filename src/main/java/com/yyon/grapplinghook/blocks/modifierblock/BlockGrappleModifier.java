package com.yyon.grapplinghook.blocks.modifierblock;

import com.yyon.grapplinghook.client.ClientProxyInterface;
import com.yyon.grapplinghook.common.CommonSetup;
import com.yyon.grapplinghook.config.GrappleConfig;
import com.yyon.grapplinghook.items.GrapplehookItem;
import com.yyon.grapplinghook.items.upgrades.BaseUpgradeItem;
import com.yyon.grapplinghook.utils.GrappleCustomization;
import com.yyon.grapplinghook.utils.Vec;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BlockGrappleModifier extends BaseEntityBlock {

	public BlockGrappleModifier() {
		super(Block.Properties.of().mapColor(MapColor.STONE).strength(1.5f));
	}


	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileEntityGrappleModifier(pos,state);
	}



	@Override
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder lootctx) {
		List<ItemStack> drops = new ArrayList<ItemStack>();
		drops.add(new ItemStack(this.asItem()));
		BlockEntity ent = lootctx.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
		if (ent == null || !(ent instanceof TileEntityGrappleModifier)) {
			return drops;
		}
		TileEntityGrappleModifier tileent = (TileEntityGrappleModifier) ent;
		
		for (GrappleCustomization.upgradeCategories category : GrappleCustomization.upgradeCategories.values()) {
			if (tileent.unlockedCategories.containsKey(category) && tileent.unlockedCategories.get(category)) {
				drops.add(new ItemStack(category.getItem()));
			}
		}
		return drops;
	}
	
    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult raytraceresult) {
		ItemStack helditemstack = playerIn.getItemInHand(InteractionHand.MAIN_HAND);
		Item helditem = helditemstack.getItem();

		if (helditem instanceof BaseUpgradeItem) {
			if (!worldIn.isClientSide) {
				BlockEntity ent = worldIn.getBlockEntity(pos);
				TileEntityGrappleModifier tileent = (TileEntityGrappleModifier) ent;
				
				GrappleCustomization.upgradeCategories category = ((BaseUpgradeItem) helditem).category;
				if (category != null) {
					if (tileent.isUnlocked(category)) {
						playerIn.sendSystemMessage(Component.literal("Already has upgrade: " + category.getName()));
					} else {
						if (!playerIn.isCreative()) {
							playerIn.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
						}
						
						tileent.unlockCategory(category);
						
						playerIn.sendSystemMessage(Component.literal("Applied upgrade: " + category.getName()));
					}
				}
			}
		} else if (helditem instanceof GrapplehookItem) {
			if (!worldIn.isClientSide) {
				BlockEntity ent = worldIn.getBlockEntity(pos);
				TileEntityGrappleModifier tileent = (TileEntityGrappleModifier) ent;
				
				GrappleCustomization custom = tileent.customization;
				CommonSetup.grapplingHookItem.get().setCustomOnServer(helditemstack, custom, playerIn);
				
				playerIn.sendSystemMessage(Component.literal("Applied configuration"));
			}
		} else if (helditem == Items.DIAMOND_BOOTS) {
			if (!worldIn.isClientSide) {
				if (GrappleConfig.getConf().longfallboots.longfallbootsrecipe) {
					boolean gaveitem = false;
					if (helditemstack.isEnchanted()) {
						Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(helditemstack);
						if (enchantments.containsKey(Enchantments.FALL_PROTECTION)) {
							if (enchantments.get(Enchantments.FALL_PROTECTION) >= 4) {
								ItemStack newitemstack = new ItemStack(CommonSetup.longFallBootsItem.get());
								EnchantmentHelper.setEnchantments(enchantments, newitemstack);
								playerIn.setItemInHand(InteractionHand.MAIN_HAND, newitemstack);
								gaveitem = true;
							}
						}
					}
					if (!gaveitem) {
						playerIn.sendSystemMessage(Component.literal("Right click with diamond boots enchanted with feather falling IV to get long fall boots"));
					}
				} else {
					playerIn.sendSystemMessage(Component.literal("Making long fall boots this way was disabled in the config. It probably has been replaced by a crafting recipe."));
				}
			}
		} else if (helditem == Items.DIAMOND) {
			this.easterEgg(state, worldIn, pos, playerIn, hand, raytraceresult);
		} else {
			if (worldIn.isClientSide) {
				BlockEntity ent = worldIn.getBlockEntity(pos);
				TileEntityGrappleModifier tileent = (TileEntityGrappleModifier) ent;
				
				ClientProxyInterface.proxy.openModifierScreen(tileent);
			}
		}
		return InteractionResult.SUCCESS;
	}
    
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

	public void easterEgg(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand,
			BlockHitResult raytraceresult) {
		int spacing = 3;
		Vec[] positions = new Vec[] {new Vec(-spacing*2, 0, 0), new Vec(-spacing, 0, 0), new Vec(0, 0, 0), new Vec(spacing, 0, 0), new Vec(2*spacing, 0, 0)};
		int[] colors = new int[] {0x5bcffa, 0xf5abb9, 0xffffff, 0xf5abb9, 0x5bcffa};
		
		for (int i = 0; i < positions.length; i++) {
			Vec newpos = new Vec(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
			Vec toPlayer = Vec.positionVec(playerIn).sub(newpos);
			double angle = toPlayer.length() == 0 ? 0 : toPlayer.getYaw();
			newpos = newpos.add(positions[i].rotateYaw(Math.toRadians(angle)));
			
			CompoundTag explosion = new CompoundTag();
	        explosion.putByte("Type", (byte) FireworkRocketItem.Shape.SMALL_BALL.getId());
	        explosion.putBoolean("Trail", true);
	        explosion.putBoolean("Flicker", false);
	        explosion.putIntArray("Colors", new int[] {colors[i]});
	        explosion.putIntArray("FadeColors", new int[] {});
	        ListTag list = new ListTag();
	        list.add(explosion);
	        CompoundTag fireworks = new CompoundTag();
	        fireworks.put("Explosions", list);
	        CompoundTag nbt = new CompoundTag();
	        nbt.put("Fireworks", fireworks);
	        ItemStack stack = new ItemStack(Items.FIREWORK_ROCKET);
	        stack.setTag(nbt);
			FireworkRocketEntity firework = new FireworkRocketEntity(worldIn, playerIn, newpos.x, newpos.y, newpos.z, stack);
			CompoundTag fireworksave = new CompoundTag();
			firework.addAdditionalSaveData(fireworksave);
			fireworksave.putInt("LifeTime", 15);
			firework.readAdditionalSaveData(fireworksave);
			worldIn.addFreshEntity(firework);
		}
	}


}

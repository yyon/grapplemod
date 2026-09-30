package com.yyon.grapplemod.blocks.modifierblock;

import com.mojang.serialization.MapCodec;
import com.yyon.grapplemod.client.ClientProxyInterface;
import com.yyon.grapplemod.config.GrappleConfig;
import com.yyon.grapplemod.init.ItemInit;
import com.yyon.grapplemod.items.GrapplehookItem;
import com.yyon.grapplemod.items.upgrades.BaseUpgradeItem;
import com.yyon.grapplemod.utils.GrappleCustomization;
import com.yyon.grapplemod.utils.Vec;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BlockGrappleModifier extends BaseEntityBlock {
	public static final MapCodec<BlockGrappleModifier> CODEC = simpleCodec(BlockGrappleModifier::new);

	public BlockGrappleModifier() {
		this(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.5f));
	}

	public BlockGrappleModifier(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}


	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileEntityGrappleModifier(pos,state);
	}



	@Override
	protected List<ItemStack> getDrops(BlockState state, LootParams.Builder lootctx) {
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
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult raytraceresult) {
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
				ItemInit.GRAPPLING_HOOK.get().setCustomOnServer(helditemstack, custom, playerIn);
				
				playerIn.sendSystemMessage(Component.literal("Applied configuration"));
			}
		} else if (helditem == Items.DIAMOND_BOOTS) {
			if (!worldIn.isClientSide) {
				if (GrappleConfig.getConf().longfallboots.longfallbootsrecipe) {
					boolean gaveitem = false;
					if (helditemstack.isEnchanted()) {
						Optional<Holder.Reference<Enchantment>> featherFalling = worldIn.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(Enchantments.FEATHER_FALLING);
						if (featherFalling.isPresent() && EnchantmentHelper.getItemEnchantmentLevel(featherFalling.get(), helditemstack) >= 4) {
							ItemStack newitemstack = new ItemStack(ItemInit.LONG_FALL_BOOTS.get());
							EnchantmentHelper.setEnchantments(newitemstack, helditemstack.getEnchantments());
							playerIn.setItemInHand(InteractionHand.MAIN_HAND, newitemstack);
							gaveitem = true;
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
		return ItemInteractionResult.SUCCESS;
	}
    
    @Override
    protected RenderShape getRenderShape(BlockState pState) {
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
			
			FireworkExplosion explosion = new FireworkExplosion(FireworkExplosion.Shape.SMALL_BALL, IntList.of(colors[i]), IntList.of(), true, false);
			ItemStack stack = new ItemStack(Items.FIREWORK_ROCKET);
			stack.set(DataComponents.FIREWORKS, new Fireworks(0, List.of(explosion)));

			FireworkRocketEntity firework = new FireworkRocketEntity(worldIn, playerIn, newpos.x, newpos.y, newpos.z, stack);
			CompoundTag fireworksave = new CompoundTag();
			firework.addAdditionalSaveData(fireworksave);
			fireworksave.putInt("LifeTime", 15);
			firework.readAdditionalSaveData(fireworksave);
			worldIn.addFreshEntity(firework);
		}
	}


}

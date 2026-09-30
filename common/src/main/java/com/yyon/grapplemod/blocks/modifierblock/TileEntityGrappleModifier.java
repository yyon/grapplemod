package com.yyon.grapplemod.blocks.modifierblock;

import com.yyon.grapplemod.init.BlockInit;
import com.yyon.grapplemod.network.GrappleModifierMessage;
import com.yyon.grapplemod.network.GrappleNetwork;
import com.yyon.grapplemod.utils.GrappleCustomization.upgradeCategories;
import com.yyon.grapplemod.utils.GrappleCustomization;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class TileEntityGrappleModifier extends BlockEntity {
	public HashMap<GrappleCustomization.upgradeCategories, Boolean> unlockedCategories = new HashMap<GrappleCustomization.upgradeCategories, Boolean>();
	public GrappleCustomization customization;

	public TileEntityGrappleModifier(BlockPos pos, BlockState state) {
		super(BlockInit.GRAPPLE_MODIFIER_BLOCK_ENTITY.get(),pos,state);
		this.customization = new GrappleCustomization();
	}

	public void unlockCategory(upgradeCategories category) {
		unlockedCategories.put(category, true);
		this.sendUpdates();
		this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
	}

	public void setCustomizationClient(GrappleCustomization customization) {
		this.customization = customization;
		GrappleNetwork.sendToServer(new GrappleModifierMessage(this.worldPosition, this.customization));
		this.sendUpdates();
	}

	public void setCustomizationServer(GrappleCustomization customization) {
		this.customization = customization;
		this.sendUpdates();
	}

	private void sendUpdates() {
		this.setChanged();
	}

	public boolean isUnlocked(upgradeCategories category) {
		return this.unlockedCategories.containsKey(category) && this.unlockedCategories.get(category);
	}

	@Override
	protected void saveAdditional(CompoundTag nbtTagCompound, HolderLookup.Provider registries) {
		super.saveAdditional(nbtTagCompound, registries);

		CompoundTag unlockedNBT = nbtTagCompound.getCompound("unlocked");

		for (GrappleCustomization.upgradeCategories category : GrappleCustomization.upgradeCategories.values()) {
			String num = String.valueOf(category.toInt());
			boolean unlocked = this.isUnlocked(category);

			unlockedNBT.putBoolean(num, unlocked);
		}

		nbtTagCompound.put("unlocked", unlockedNBT);
		nbtTagCompound.put("customization", this.customization.writeNBT());
	}

	@Override
	protected void loadAdditional(CompoundTag parentNBTTagCompound, HolderLookup.Provider registries) {
		super.loadAdditional(parentNBTTagCompound, registries);

		CompoundTag unlockedNBT = parentNBTTagCompound.getCompound("unlocked");

		for (GrappleCustomization.upgradeCategories category : GrappleCustomization.upgradeCategories.values()) {
			String num = String.valueOf(category.toInt());
			boolean unlocked = unlockedNBT.getBoolean(num);

			this.unlockedCategories.put(category, unlocked);
		}

		CompoundTag custom = parentNBTTagCompound.getCompound("customization");
		this.customization.loadNBT(custom);
	}


	@Override
	@Nullable
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag nbtTagCompound = new CompoundTag();
		this.saveAdditional(nbtTagCompound, registries);
		return nbtTagCompound;
	}
}

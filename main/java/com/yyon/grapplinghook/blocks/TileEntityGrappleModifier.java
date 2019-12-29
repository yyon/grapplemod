package com.yyon.grapplinghook.blocks;

import java.util.HashMap;

import javax.annotation.Nullable;

import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.grapplemod.upgradeCategories;
import com.yyon.grapplinghook.network.GrappleModifierMessage;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;

public class TileEntityGrappleModifier extends TileEntity {
	public HashMap<grapplemod.upgradeCategories, Boolean> unlockedCategories = new HashMap<grapplemod.upgradeCategories, Boolean>();
	public GrappleCustomization customization;
	
	public TileEntityGrappleModifier() {
		this.customization = new GrappleCustomization();
	}

	public void unlockCategory(upgradeCategories category) {
		unlockedCategories.put(category, true);
		this.sendUpdates();
	}
	
	public void setCustomizationClient(GrappleCustomization customization) {
		this.customization = customization;
		grapplemod.network.sendToServer(new GrappleModifierMessage(this.pos, this.customization));
		this.sendUpdates();
	}

	public void setCustomizationServer(GrappleCustomization customization) {
		this.customization = customization;
		this.sendUpdates();
	}

	private void sendUpdates() {
//		this.world.markBlockRangeForRenderUpdate(pos, pos);
		this.world.notifyBlockUpdate(pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
//		this.world.scheduleBlockUpdate(pos,this.getBlockType(),0,0);
		markDirty();
	}
	
	public boolean isUnlocked(upgradeCategories category) {
		return this.unlockedCategories.containsKey(category) && this.unlockedCategories.get(category);
	}

	// https://github.com/TheGreyGhost/MinecraftByExample/blob/master/src/main/java/minecraftbyexample/mbe20_tileentity_data/TileEntityData.java

	// When the world loads from disk, the server needs to send the TileEntity
	// information to the client
	// it uses getUpdatePacket(), getUpdateTag(), onDataPacket(), and
	// handleUpdateTag() to do this:
	// getUpdatePacket() and onDataPacket() are used for one-at-a-time TileEntity
	// updates
	// getUpdateTag() and handleUpdateTag() are used by vanilla to collate together
	// into a single chunk update packet
	@Override
	@Nullable
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbtTagCompound = new CompoundNBT();
		writeToNBT(nbtTagCompound);
		int metadata = getBlockMetadata();
		return new SUpdateTileEntityPacket(this.pos, metadata, nbtTagCompound);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	/*
	 * Creates a tag containing the TileEntity information, used by vanilla to
	 * transmit from server to client
	 */
	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbtTagCompound = new CompoundNBT();
		writeToNBT(nbtTagCompound);
		return nbtTagCompound;
	}

	/*
	 * Populates this TileEntity with information from the tag, used by vanilla to
	 * transmit from server to client
	 */
	@Override
	public void handleUpdateTag(CompoundNBT tag) {
		this.readFromNBT(tag);
	}

	// This is where you save any data that you don't want to lose when the tile
	// entity unloads
	// In this case, we only need to store the ticks left until explosion, but we
	// store a bunch of other
	// data as well to serve as an example.
	// NBTexplorer is a very useful tool to examine the structure of your NBT saved
	// data and make sure it's correct:
	// http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-tools/1262665-nbtexplorer-nbt-editor-for-windows-and-mac
	@Override
	public CompoundNBT writeToNBT(CompoundNBT parentNBTTagCompound) {
		super.writeToNBT(parentNBTTagCompound); // The super call is required to save the tiles location
		
		CompoundNBT unlockedNBT = parentNBTTagCompound.getCompoundTag("unlocked");
		
		for (grapplemod.upgradeCategories category : grapplemod.upgradeCategories.values()) {
			String num = String.valueOf(category.toInt());
			boolean unlocked = this.isUnlocked(category);
			
			unlockedNBT.setBoolean(num, unlocked);
		}
		
		parentNBTTagCompound.setTag("unlocked", unlockedNBT);
		parentNBTTagCompound.setTag("customization", this.customization.writeNBT());
		
		
		
		return parentNBTTagCompound;
	}

	// This is where you load the data that you saved in writeToNBT
	@Override
	public void readFromNBT(CompoundNBT parentNBTTagCompound) {
		super.readFromNBT(parentNBTTagCompound); // The super call is required to load the tiles location
		
		CompoundNBT unlockedNBT = parentNBTTagCompound.getCompoundTag("unlocked");
		
		for (grapplemod.upgradeCategories category : grapplemod.upgradeCategories.values()) {
			String num = String.valueOf(category.toInt());
			boolean unlocked = unlockedNBT.getBoolean(num);
			
			this.unlockedCategories.put(category, unlocked);
		}
		
		CompoundNBT custom = parentNBTTagCompound.getCompoundTag("customization");
		this.customization.loadNBT(custom);
	}
}

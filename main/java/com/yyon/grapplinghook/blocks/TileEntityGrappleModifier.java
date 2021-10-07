package com.yyon.grapplinghook.blocks;

import java.util.HashMap;

import javax.annotation.Nullable;

import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.grapplemod.upgradeCategories;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;

public class TileEntityGrappleModifier extends TileEntity {
	public HashMap<grapplemod.upgradeCategories, Boolean> unlockedCategories = new HashMap<grapplemod.upgradeCategories, Boolean>();
	public GrappleCustomization customization;
	
	public TileEntityGrappleModifier() {
		super(grapplemod.tileEntityGrappleModifierType);
		this.customization = new GrappleCustomization();
	}

	public void unlockCategory(upgradeCategories category) {
		unlockedCategories.put(category, true);
		this.sendUpdates();
	}
	
	public void setCustomizationClient(GrappleCustomization customization) {
		this.customization = customization;
//		grapplemod.network.sendToServer(new GrappleModifierMessage(this.pos, this.customization));
		this.sendUpdates();
	}

	public void setCustomizationServer(GrappleCustomization customization) {
		this.customization = customization;
		this.sendUpdates();
	}

	private void sendUpdates() {
//		this.world.notifyBlockUpdate(pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
//		markDirty();
//		this.level.markAndNotifyBlock(this.worldPosition, null, getBlockState(), getBlockState(), 3, 0);
		this.setChanged();
	}
	
	public boolean isUnlocked(upgradeCategories category) {
		return this.unlockedCategories.containsKey(category) && this.unlockedCategories.get(category);
	}
	
	@Override
	public CompoundNBT save(CompoundNBT nbtTagCompound) {
		super.save(nbtTagCompound);

		CompoundNBT unlockedNBT = nbtTagCompound.getCompound("unlocked");
		
		for (grapplemod.upgradeCategories category : grapplemod.upgradeCategories.values()) {
			String num = String.valueOf(category.toInt());
			boolean unlocked = this.isUnlocked(category);
			
			unlockedNBT.putBoolean(num, unlocked);
		}
		
		nbtTagCompound.put("unlocked", unlockedNBT);
		nbtTagCompound.put("customization", this.customization.writeNBT());

		return nbtTagCompound;
	}

	@Override
	public void load(BlockState state, CompoundNBT parentNBTTagCompound) {
		super.load(state, parentNBTTagCompound); // The super call is required to load the tiles location
		
		CompoundNBT unlockedNBT = parentNBTTagCompound.getCompound("unlocked");
		
		for (grapplemod.upgradeCategories category : grapplemod.upgradeCategories.values()) {
			String num = String.valueOf(category.toInt());
			boolean unlocked = unlockedNBT.getBoolean(num);
			
			this.unlockedCategories.put(category, unlocked);
		}
		
		CompoundNBT custom = parentNBTTagCompound.getCompound("customization");
		this.customization.loadNBT(custom);
	}
	
	
	// When the world loads from disk, the server needs to send the TileEntity information to the client
		//  it uses getUpdatePacket(), getUpdateTag(), onDataPacket(), and handleUpdateTag() to do this:
	  //  getUpdatePacket() and onDataPacket() are used for one-at-a-time TileEntity updates
	  //  getUpdateTag() and handleUpdateTag() are used by vanilla to collate together into a single chunk update packet
		//  Not really required for this example since we only use the timer on the client, but included anyway for illustration
		@Override
	  @Nullable
	  public SUpdateTileEntityPacket getUpdatePacket()
	  {
			CompoundNBT nbtTagCompound = new CompoundNBT();
			this.save(nbtTagCompound);
			int tileEntityType = 42;  // arbitrary number; only used for vanilla TileEntities.  You can use it, or not, as you want.
			return new SUpdateTileEntityPacket(this.worldPosition, tileEntityType, nbtTagCompound);
		}

		@Override
		public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
	    BlockState blockState = this.level.getBlockState(this.worldPosition);
	    this.load(blockState, pkt.getTag());   // read from the nbt in the packet
		}

	  /* Creates a tag containing all of the TileEntity information, used by vanilla to transmit from server to client
	 */
	  @Override
	  public CompoundNBT getUpdateTag()
	  {
	    CompoundNBT nbtTagCompound = new CompoundNBT();
	    this.save(nbtTagCompound);
	    return nbtTagCompound;
	  }

	  /* Populates this TileEntity with information from the tag, used by vanilla to transmit from server to client
	 */
	  @Override
	  public void handleUpdateTag(BlockState blockState, CompoundNBT parentNBTTagCompound)
	  {
	    this.load(blockState, parentNBTTagCompound);
	  }
}

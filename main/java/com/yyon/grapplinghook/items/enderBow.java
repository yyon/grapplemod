package com.yyon.grapplinghook.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.entities.enderArrow;
import com.yyon.grapplinghook.entities.grappleArrow;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/*
 * This file is part of GrappleMod.

    GrappleMod is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GrappleMod is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GrappleMod.  If not, see <http://www.gnu.org/licenses/>.
 */

public class enderBow extends grappleBow implements clickitem {
	
//	int reusetime = 50;
	
	public enderBow() {
		super();
		setUnlocalizedName("enderhook");
	}
	
	@Override
	public grappleArrow createarrow(ItemStack stack, World worldIn, EntityPlayer playerIn) {
//		NBTTagCompound compound = stack.getSubCompound("launcher", true);
//		compound.setLong("lastused", 0);
		
		return new enderArrow(worldIn, playerIn, 0);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister)
	{
		 itemIcon = iconRegister.registerIcon("grapplemod:enderhook");
	}
	
	public void onLeftClick(ItemStack stack, EntityPlayer player) {
		if (player.worldObj.isRemote) {
			grapplemod.proxy.launchplayer(player);
			/*
			NBTTagCompound compound = stack.getSubCompound("launcher", true);
			long timer = world.getTotalWorldTime() - compound.getLong("lastused");
			if (timer > reusetime) {
				if (player.getHeldItem().getItem() instanceof enderBow) {
					
		//			playerused = player;
		//			reusetimer = reusetime;
					compound.setLong("lastused", world.getTotalWorldTime());
					
		        	Vec3 facing = player.getLookVec();
					Vec3 playermotion = new Vec3(player.motionX, player.motionY, player.motionZ);
					Vec3 newvec = playermotion.add(multvec(facing, 3));
					
					grappleArrow arrow = this.getArrow(stack, world);
					if (arrow == null) {
	//					player.setVelocity(newvec.xCoord, newvec.yCoord, newvec.zCoord);
						player.motionX = newvec.xCoord;
						player.motionY = newvec.yCoord;
						player.motionZ = newvec.zCoord;
						
						if (player instanceof EntityPlayerMP) {
							((EntityPlayerMP) player).playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(player));
						} else {
							grapplemod.network.sendToServer(new PlayerMovementMessage(player.getEntityId(), player.posX, player.posY, player.posZ, player.motionX, player.motionY, player.motionZ));
						}
					} else {
						facing = multvec(facing, 3);
						if (player instanceof EntityPlayerMP) {
							System.out.println("Sending EnderGrappleLaunchMessage");
							grapplemod.sendtocorrectclient(new EnderGrappleLaunchMessage(player.getEntityId(), facing.xCoord, facing.yCoord, facing.zCoord), player.getEntityId(), player.worldObj);
						} else {
							grapplemod.receiveEnderLaunch(player.getEntityId(), facing.xCoord, facing.yCoord, facing.zCoord);
						}
	
	//					arrow.control.motion = arrow.control.motion.add(newvec);
					}
				}
			}
			*/
		}
	}
	
	/*
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		super.onPlayerTick(event);
		if (!event.player.worldObj.isRemote) {
			ItemStack stack = event.player.getHeldItem();
			if (stack != null) {
				Item item = stack.getItem();
				if (item instanceof enderBow) {
					if (event.player.onGround) {
						NBTTagCompound compound = stack.getSubCompound("launcher", true);
						if (compound.getLong("lastused") != 0) {
							long timer = event.player.worldObj.getTotalWorldTime() - compound.getLong("lastused");
							if (timer > 1000) {
								compound.setLong("lastused", 0);
							}
						}
					}
				}
			}
		}
	}
	*/
}

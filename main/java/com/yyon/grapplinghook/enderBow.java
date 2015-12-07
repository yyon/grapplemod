package com.yyon.grapplinghook;

import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class enderBow extends grappleBow {
	
	int reusetime = 7000;
	
	public enderBow() {
		super();
		setUnlocalizedName("enderhook");
	}
	
	@Override
	public grappleArrow createarrow(ItemStack stack, World worldIn, EntityPlayer playerIn) {
		NBTTagCompound compound = stack.getSubCompound("launcher", true);
		compound.setLong("lastused", 0);
		
		return new enderArrow(worldIn, playerIn, 0);
	}
	
	public Vec3 multvec(Vec3 a, double changefactor) {
		return new Vec3(a.xCoord * changefactor, a.yCoord * changefactor, a.zCoord * changefactor);
	}
	
	public void leftclick(ItemStack stack, World world, EntityPlayer player) {
		NBTTagCompound compound = stack.getSubCompound("launcher", true);
		long timer = Minecraft.getSystemTime() - compound.getLong("lastused");
		if (timer > reusetime) {
			if (player.getHeldItem().getItem() instanceof enderBow) {
	//			playerused = player;
	//			reusetimer = reusetime;
				compound.setLong("lastused", Minecraft.getSystemTime());
				
	        	Vec3 facing = player.getLookVec();
				Vec3 playermotion = new Vec3(player.motionX, player.motionY, player.motionZ);
				Vec3 newvec = playermotion.add(multvec(facing, 3));
				
				grappleArrow arrow = this.getArrow(stack, world);
				if (arrow == null || !arrow.attached) {
					player.setVelocity(newvec.xCoord, newvec.yCoord, newvec.zCoord);
					if (player instanceof EntityPlayerMP) {
						((EntityPlayerMP) player).playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(player));
					}
				} else {
					arrow.motion = arrow.motion.add(newvec);
				}
			}
		}
	}
	
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
							long timer = Minecraft.getSystemTime() - compound.getLong("lastused");
							if (timer > 1000) {
								compound.setLong("lastused", 0);
							}
						}
					}
				}
			}
		}
	}
}

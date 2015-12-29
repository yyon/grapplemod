package com.yyon.grapplinghook.items;

import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.entities.grappleArrow;
import com.yyon.grapplinghook.network.GrappleClickMessage;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
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

public class grappleBow extends Item implements clickitem {
	
	
	public grappleBow() {
		super();
		maxStackSize = 1;
		setFull3D();
		setUnlocalizedName("grapplinghook");
		
		this.setMaxDamage(500);
		
		setCreativeTab(CreativeTabs.tabCombat);
		
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack)
	{
		return 72000;
	}
	
	@Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        itemIcon = iconRegister.registerIcon("grapplemod:grapplinghook");
    }
	
	public grappleArrow getArrow(ItemStack stack, World world) {
		NBTTagCompound compound = grapplemod.getCompound(stack);
//		NBTTagCompound compound = stack.getSubCompound("grapplebow", true);
		int id = compound.getInteger("arrow");
		if (id == 0) {
			return null;
		}
		Entity e = world.getEntityByID(id);
		if (e instanceof grappleArrow) {
			return (grappleArrow) e;
		} else {
			return null;
		}
	}
	
	public void setArrow(ItemStack stack, grappleArrow arrow) {
		int id = 0;
		if (arrow != null) {
			id = arrow.getEntityId();
		}
		
		NBTTagCompound compound = grapplemod.getCompound(stack);
//		NBTTagCompound compound = stack.getSubCompound("grapplebow", true);
		compound.setInteger("arrow", id);
	}
	
	
	public void dorightclick(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        if (!worldIn.isRemote) {
        	grappleArrow entityarrow = getArrow(stack, worldIn);
        	
        	System.out.println("right click");
        	
        	if (entityarrow != null) {
        		int id = entityarrow.shootingEntityID;
        		if (!grapplemod.attached.contains(id)) {
//        		if (entityarrow.isDead) {
        			setArrow(stack, null);
        			entityarrow = null;
        		}
        	}
        	
			float f = 2.0F;
			if (entityarrow == null) {
				entityarrow = this.createarrow(stack, worldIn, playerIn);
				setArrow(stack, entityarrow);
	
				stack.damageItem(1, playerIn);
				worldIn.playSoundAtEntity(playerIn, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
				
				worldIn.spawnEntityInWorld(entityarrow);
			} else {
				System.out.println("right click unattach");
				System.out.println(entityarrow);
//				if (entityarrow.control != null) {
//					entityarrow.control.unattach();
//				} else {
//					entityarrow.removeServer();
//				}
				grapplemod.sendtocorrectclient(new GrappleClickMessage(entityarrow.shootingEntityID, false), entityarrow.shootingEntityID, entityarrow.worldObj);
				grapplemod.attached.remove(new Integer(entityarrow.shootingEntityID));
//				setArrow(stack, null);
			}
    	}
	}
	
	public boolean onItemUse(ItemStack p_77648_1_, EntityPlayer p_77648_2_, World p_77648_3_, int p_77648_4_, int p_77648_5_, int p_77648_6_, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_)
    {
		System.out.println("on use");
		return false;
    }
	
	public grappleArrow createarrow(ItemStack stack, World worldIn, EntityPlayer playerIn) {
		System.out.println("Creating arrow!");
		return new grappleArrow(worldIn, playerIn, 0);
	}
	
	
	public void leftclick(ItemStack stack, World world, EntityPlayer player) {

	}
	
	@Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityPlayer playerIn, int timeLeft)
    {
    }
    
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World worldIn, final EntityPlayer playerIn){
        playerIn.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        
        this.dorightclick(stack, worldIn, playerIn);
        
		return stack;
	}
	
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityPlayer playerIn)
    {
        return stack;
    }


	/**
	 * returns the action that specifies what animation to play when the items is being used
	 */
	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack)
	{
		return EnumAction.none;
	}
	
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
    {
    	return true;
    }
   
    public boolean onEntitySwing(EntityLiving entityLiving, ItemStack stack)
    {
    	return true;
    }
    
	@Override
    public boolean onBlockStartBreak(ItemStack itemstack, int X, int Y, int Z, EntityPlayer player)
    {
      return true;
    }
   
//    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
//    {
//      return true;
//    }
    
    @SubscribeEvent
    public void onBlockBreak(BreakEvent event){
    	EntityPlayer player = event.getPlayer();
    	ItemStack stack = player.getHeldItem();
    	if (stack != null) {
    		Item item = stack.getItem();
    		if (item instanceof grappleBow) {
    			event.setCanceled(true);
    		}
    	}
    }
    
    /*
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		EntityPlayer player = event.player;
		if (player.worldObj.isRemote) {
			if (player == Minecraft.getMinecraft().thePlayer) {
				if (player != null) {
					ItemStack stack = player.getHeldItem();
					if (stack != null) {
						Item item = stack.getItem();
						if (item instanceof grappleBow) {
							if (player.isSwingInProgress) {
								this.leftclick(stack, player.worldObj, player);
							}
						}
					}
				}
			}
		}
    }
    */

	@Override
	public void onLeftClick(ItemStack stack, EntityPlayer player) {
		this.leftclick(stack, player.worldObj, player);
	}
}

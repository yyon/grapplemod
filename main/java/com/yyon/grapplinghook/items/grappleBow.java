package com.yyon.grapplinghook.items;

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

import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.entities.grappleArrow;
import com.yyon.grapplinghook.network.GrappleClickMessage;

/* // 1.8 Compatability
import net.minecraft.util.BlockPos;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/*/ // 1.7.10 Compatability
import net.minecraft.client.renderer.texture.IIconRegister;
import com.yyon.grapplinghook.BlockPos;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
//*/

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

public class grappleBow extends Item {
	
	
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
	
//* // 1.7.10 Compatability
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister)
	{
		itemIcon = iconRegister.registerIcon("grapplemod:grapplinghook");
	}
	
	@Override
//*/

	public int getMaxItemUseDuration(ItemStack par1ItemStack)
	{
		return 72000;
	}
	
	public grappleArrow getArrow(ItemStack stack, World world) {
/* // 1.8 Compatability
		NBTTagCompound compound = stack.getSubCompound("grapplebow", true);
/*/ // 1.7.10 Compatability
		NBTTagCompound compound = grapplemod.getCompound(stack);
//*/

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
		
/* // 1.8 Compatability
		NBTTagCompound compound = stack.getSubCompound("grapplebow", true);
/*/ // 1.7.10 Compatability
		NBTTagCompound compound = grapplemod.getCompound(stack);
//*/

		compound.setInteger("arrow", id);
	}
	
	
	public void dorightclick(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        if (!worldIn.isRemote) {
        	grappleArrow entityarrow = getArrow(stack, worldIn);
        	
//        	System.out.println("right click");
        	
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
//				System.out.println("right click unattach");
//				System.out.println(entityarrow);
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
	
	public grappleArrow createarrow(ItemStack stack, World worldIn, EntityPlayer playerIn) {
//		System.out.println("Creating arrow!");
		return new grappleArrow(worldIn, playerIn, 0);
	}
	
	
//* // 1.7.10 Compatability
	@Override
//*/

    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityPlayer playerIn, int timeLeft)
    {
    }
    
//* // 1.7.10 Compatability
	@Override
//*/

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
//* // 1.7.10 Compatability
    @Override
//*/

	public EnumAction getItemUseAction(ItemStack par1ItemStack)
	{
/* // 1.8 Compatability
		return EnumAction.NONE;
	}
	
/*/ // 1.7.10 Compatability
		return EnumAction.none;
	}
	
    @Override
//*/

    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
    {
    	return true;
    }
   
    public boolean onEntitySwing(EntityLiving entityLiving, ItemStack stack)
    {
    	return true;
    }
   
/* // 1.8 Compatability
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos k, EntityPlayer player)
/*/ // 1.7.10 Compatability
    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player)
//*/

    {
      return true;
    }
   
    
/* // 1.8 Compatability
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
      return true;
    }
/*/ // 1.7.10 Compatability
//    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
//    {
//      return true;
//    }
//*/

    /*
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		EntityPlayer player = event.player;
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
    */
}

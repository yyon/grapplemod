package com.yyon.grapplinghook.items;

import java.util.HashMap;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.controllers.grappleController;
import com.yyon.grapplinghook.entities.grappleArrow;
import com.yyon.grapplinghook.network.GrappleClickMessage;


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
	public static HashMap<Entity, grappleArrow> grapplearrows = new HashMap<Entity, grappleArrow>();
	
	public grappleBow() {
		super();
		maxStackSize = 1;
		setFull3D();
		setUnlocalizedName("grapplinghook");
		
		this.setMaxDamage(500);
		
		setCreativeTab(CreativeTabs.TRANSPORTATION);
		
		MinecraftForge.EVENT_BUS.register(this);
	}

    @Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack)
	{
		return 72000;
	}
	
	public grappleArrow getArrow(Entity entity, World world) {
		if (grappleBow.grapplearrows.containsKey(entity)) {
			grappleArrow arrow = grappleBow.grapplearrows.get(entity);
			if (arrow != null && !arrow.isDead) {
				return arrow;
			}
		}
		return null;
	}
	
	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        ItemStack mat = new ItemStack(Items.LEATHER, 1);
        if (mat != null && net.minecraftforge.oredict.OreDictionary.itemMatches(mat, repair, false)) return true;
        return super.getIsRepairable(toRepair, repair);
	}

	public void setArrow(Entity entity, ItemStack stack, grappleArrow arrow) {
		grappleBow.grapplearrows.put(entity, arrow);
	}
	
	
	public void dorightclick(ItemStack stack, World worldIn, EntityLivingBase entityLiving, boolean righthand) {
        if (!worldIn.isRemote) {
        	grappleArrow entityarrow = getArrow(entityLiving, worldIn);
        	
        	if (entityarrow != null) {
        		int id = entityarrow.shootingEntityID;
        		if (!grapplemod.attached.contains(id)) {
        			setArrow(entityLiving, stack, null);
        			
        			if (!entityarrow.isDead) {
        				entityarrow.removeServer();
        				return;
        			}
        			
        			entityarrow = null;
        		}
        	}
        	
			float f = 2.0F;
			if (entityarrow == null) {
				entityarrow = this.createarrow(stack, worldIn, entityLiving, righthand);
	            entityarrow.setHeadingFromThrower(entityLiving, entityLiving.rotationPitch, entityLiving.rotationYaw, 0.0F, entityarrow.getVelocity(), 0.0F);
				setArrow(entityLiving, stack, entityarrow);
	
				stack.damageItem(1, entityLiving);
                worldIn.playSound((EntityPlayer)null, entityLiving.posX, entityLiving.posY, entityLiving.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
				
				worldIn.spawnEntityInWorld(entityarrow);
			} else {
				grapplemod.sendtocorrectclient(new GrappleClickMessage(entityarrow.shootingEntityID, false), entityarrow.shootingEntityID, entityarrow.worldObj);
				grapplemod.attached.remove(new Integer(entityarrow.shootingEntityID));
				this.setArrow(entityLiving, stack, null);
			}
    	}
	}
	
	public grappleArrow createarrow(ItemStack stack, World worldIn, EntityLivingBase entityLiving, boolean righthand) {
		return new grappleArrow(worldIn, entityLiving, righthand);
	}
    
    @Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World worldIn, EntityPlayer entityLiving, EnumHand hand)
    {
        if (!worldIn.isRemote) {
	        this.dorightclick(stack, worldIn, entityLiving, hand == EnumHand.MAIN_HAND);
        }
        entityLiving.setActiveHand(hand);
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }
	
	public static boolean isactive(ItemStack stack) {
		EntityPlayer p = Minecraft.getMinecraft().thePlayer;
		if (p.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND) == stack || p.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND) == stack) {
			int entityid = p.getEntityId();
			if (grapplemod.controllers.containsKey(entityid)) {
				Item item = stack.getItem();
				grappleController controller = grapplemod.controllers.get(entityid);
				if (item.getClass() == grappleBow.class && controller.controllerid == grapplemod.GRAPPLEID) {
					return true;
				} else if (item.getClass() == enderBow.class && controller.controllerid == grapplemod.ENDERID) {
					return true;
				} else if (item.getClass() == hookBow.class && controller.controllerid == grapplemod.HOOKID) {
					return true;
				} else if (item.getClass() == magnetBow.class && controller.controllerid == grapplemod.MAGNETID) {
					return true;
				} else if (item.getClass() == repeller.class && controller.controllerid == grapplemod.REPELID) {
					return true;
				} else if (item.getClass() == multiBow.class && controller.controllerid == grapplemod.MULTIID) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn,
			EntityLivingBase entityLiving, int timeLeft) {
		if (!worldIn.isRemote) {
//			stack.getSubCompound("grapplemod", true).setBoolean("extended", (this.getArrow(entityLiving, worldIn) != null));
		}
		super.onPlayerStoppedUsing(stack, worldIn, entityLiving, timeLeft);
	}

	/**
	 * returns the action that specifies what animation to play when the items is being used
	 */
    @Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack)
	{
		return EnumAction.NONE;
	}

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
    {
    	return true;
    }
   
    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker)
    {
    	return true;
    }
   
    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos k, EntityPlayer player)
    {
      return true;
    }
    
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean par4)
	{
		Minecraft minecraft = Minecraft.getMinecraft();
		list.add("A basic grappling hook for swinging");
		list.add("");
		list.add(grapplemod.getkeyname(minecraft.gameSettings.keyBindUseItem) + " - Throw grappling hook");
		list.add(grapplemod.getkeyname(minecraft.gameSettings.keyBindUseItem) + " again - Release");
		list.add("Double-" + grapplemod.getkeyname(minecraft.gameSettings.keyBindUseItem) + " - Release and throw again");
		list.add(grapplemod.getkeyname(minecraft.gameSettings.keyBindForward) + ", " +
				grapplemod.getkeyname(minecraft.gameSettings.keyBindLeft) + ", " +
				grapplemod.getkeyname(minecraft.gameSettings.keyBindBack) + ", " +
				grapplemod.getkeyname(minecraft.gameSettings.keyBindRight) +
				" - Swing");
		list.add(grapplemod.getkeyname(minecraft.gameSettings.keyBindJump) + " - Release and jump (while in midair)");
		list.add(grapplemod.getkeyname(minecraft.gameSettings.keyBindSneak) + " - Stop swinging");
		list.add(grapplemod.getkeyname(minecraft.gameSettings.keyBindSneak) + " + " +
				grapplemod.getkeyname(minecraft.gameSettings.keyBindForward) + 
				" - Climb up");
		list.add(grapplemod.getkeyname(minecraft.gameSettings.keyBindSneak) + " + " +
				grapplemod.getkeyname(minecraft.gameSettings.keyBindBack) + 
				" - Climb down");
	}
}

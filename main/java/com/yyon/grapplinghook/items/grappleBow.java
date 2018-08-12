package com.yyon.grapplinghook.items;

import java.util.HashMap;
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.yyon.grapplinghook.CommonProxyClass;
import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.vec;
import com.yyon.grapplinghook.entities.grappleArrow;
import com.yyon.grapplinghook.network.GrappleClickMessage;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


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
	public static HashMap<Entity, grappleArrow> grapplearrows1 = new HashMap<Entity, grappleArrow>();
	public static HashMap<Entity, grappleArrow> grapplearrows2 = new HashMap<Entity, grappleArrow>();
	
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
	
	public boolean hasArrow(Entity entity) {
		grappleArrow arrow1 = getArrow1(entity);
		grappleArrow arrow2 = getArrow2(entity);
		return (arrow1 != null) || (arrow2 != null);
	}
	
	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        ItemStack mat = new ItemStack(Items.LEATHER, 1);
        if (mat != null && net.minecraftforge.oredict.OreDictionary.itemMatches(mat, repair, false)) return true;
        return super.getIsRepairable(toRepair, repair);
	}

	public void setArrow1(Entity entity, grappleArrow arrow) {
		grappleBow.grapplearrows1.put(entity, arrow);
	}
	public void setArrow2(Entity entity, grappleArrow arrow) {
		grappleBow.grapplearrows2.put(entity, arrow);
	}
	public grappleArrow getArrow1(Entity entity) {
		if (grappleBow.grapplearrows1.containsKey(entity)) {
			grappleArrow arrow = grappleBow.grapplearrows1.get(entity);
			if (arrow != null && !arrow.isDead) {
				return arrow;
			}
		}
		return null;
	}
	public grappleArrow getArrow2(Entity entity) {
		if (grappleBow.grapplearrows2.containsKey(entity)) {
			grappleArrow arrow = grappleBow.grapplearrows2.get(entity);
			if (arrow != null && !arrow.isDead) {
				return arrow;
			}
		}
		return null;
	}	
	
	public void dorightclick(ItemStack stack, World worldIn, EntityLivingBase entityLiving, boolean righthand) {
        if (!worldIn.isRemote) {
        	boolean hasarrow = hasArrow(entityLiving);
        	
        	if (hasarrow) {
        		// if there's already an arrow, delete arrow
        		
    			grappleArrow arrow1 = getArrow1(entityLiving);
    			grappleArrow arrow2 = getArrow2(entityLiving);
    			
    			setArrow1(entityLiving, null);
    			setArrow2(entityLiving, null);

    			if (arrow1 != null) {
    				arrow1.removeServer();
    			}
    			if (arrow2 != null) {
    				arrow2.removeServer();
    			}
    			
        		int id = entityLiving.getEntityId();
        		if (grapplemod.attached.contains(id)) {
    				// remove controller if hook is attached
    				
    				grapplemod.sendtocorrectclient(new GrappleClickMessage(id, false), id, entityLiving.world);
    				grapplemod.attached.remove(new Integer(id));
    			}

    			if (arrow1 != null || arrow2 != null) {
    				return;
    			}
    		}
        	
        	GrappleCustomization custom = this.getCustomization(stack);
        	
        	if (!custom.doublehook) {
    			grappleArrow entityarrow = this.createarrow(stack, worldIn, entityLiving, righthand);
    	        float velx = -MathHelper.sin(entityLiving.rotationYaw * 0.017453292F) * MathHelper.cos(entityLiving.rotationPitch * 0.017453292F);
    	        float vely = -MathHelper.sin(entityLiving.rotationPitch * 0.017453292F);
    	        float velz = MathHelper.cos(entityLiving.rotationYaw * 0.017453292F) * MathHelper.cos(entityLiving.rotationPitch * 0.017453292F);
    	        entityarrow.shoot((double) velx, (double) vely, (double) velz, entityarrow.getVelocity(), 0.0F);
    			setArrow1(entityLiving, entityarrow);
    			worldIn.spawnEntity(entityarrow);
        	} else {
          		double angle = custom.angle;
          		if (entityLiving.isSneaking()) {
          			angle = custom.sneakingangle;
          		}
          		EntityLivingBase player = entityLiving;
          		
          		vec anglevec = new vec(0,0,1).rotate_yaw(Math.toRadians(-angle));
          		anglevec = anglevec.rotate_pitch(Math.toRadians(-player.rotationPitch));
          		anglevec = anglevec.rotate_yaw(Math.toRadians(player.rotationYaw));
    	        float velx = -MathHelper.sin((float) anglevec.getYaw() * 0.017453292F) * MathHelper.cos((float) anglevec.getPitch() * 0.017453292F);
    	        float vely = -MathHelper.sin((float) anglevec.getPitch() * 0.017453292F);
    	        float velz = MathHelper.cos((float) anglevec.getYaw() * 0.017453292F) * MathHelper.cos((float) anglevec.getPitch() * 0.017453292F);
    			grappleArrow entityarrow = this.createarrow(stack, worldIn, entityLiving, false);// new grappleArrow(worldIn, player, false);
//                entityarrow.shoot(player, (float) anglevec.getPitch(), (float)anglevec.getYaw(), 0.0F, entityarrow.getVelocity(), 0.0F);
    	        entityarrow.shoot((double) velx, (double) vely, (double) velz, entityarrow.getVelocity(), 0.0F);
                
    			worldIn.spawnEntity(entityarrow);
    			setArrow1(entityLiving, entityarrow);    			
    			
          		anglevec = new vec(0,0,1).rotate_yaw(Math.toRadians(angle));
          		anglevec = anglevec.rotate_pitch(Math.toRadians(-player.rotationPitch));
          		anglevec = anglevec.rotate_yaw(Math.toRadians(player.rotationYaw));
    	        velx = -MathHelper.sin((float) anglevec.getYaw() * 0.017453292F) * MathHelper.cos((float) anglevec.getPitch() * 0.017453292F);
    	        vely = -MathHelper.sin((float) anglevec.getPitch() * 0.017453292F);
    	        velz = MathHelper.cos((float) anglevec.getYaw() * 0.017453292F) * MathHelper.cos((float) anglevec.getPitch() * 0.017453292F);
    			entityarrow = this.createarrow(stack, worldIn, entityLiving, true);//new grappleArrow(worldIn, player, true);
//                entityarrow.shoot(player, (float) anglevec.getPitch(), (float)anglevec.getYaw(), 0.0F, entityarrow.getVelocity(), 0.0F);
    	        entityarrow.shoot((double) velx, (double) vely, (double) velz, entityarrow.getVelocity(), 0.0F);
                
    			worldIn.spawnEntity(entityarrow);
    			setArrow2(entityLiving, entityarrow);
        	}

			stack.damageItem(1, entityLiving);
            worldIn.playSound((EntityPlayer)null, entityLiving.posX, entityLiving.posY, entityLiving.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + 2.0F * 0.5F);
			
    	}
	}
	
    public double getAngle(EntityLivingBase entity, ItemStack stack) {
    	GrappleCustomization custom = this.getCustomization(stack);
    	if (entity.isSneaking()) {
    		return custom.sneakingangle;
    	} else {
    		return custom.angle;
    	}
    }
	
	public grappleArrow createarrow(ItemStack stack, World worldIn, EntityLivingBase entityLiving, boolean righthand) {
		grappleArrow arrow = new grappleArrow(worldIn, entityLiving, righthand, this.getCustomization(stack));
		grapplemod.addarrow(entityLiving.getEntityId(), arrow);
		return arrow;
	}
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer entityLiving, EnumHand hand)
    {
    	ItemStack stack = entityLiving.getHeldItem(hand);
        if (!worldIn.isRemote) {
	        this.dorightclick(stack, worldIn, entityLiving, hand == EnumHand.MAIN_HAND);
        }
        entityLiving.setActiveHand(hand);
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
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
	public void onLeftClick(ItemStack stack, EntityPlayer player) {
		if (player.world.isRemote) {
			if (this.getCustomization(stack).enderstaff) {
				grapplemod.proxy.launchplayer(player);
			}
		}
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
    
    public GrappleCustomization getCustomization(ItemStack itemstack) {
    	GrappleCustomization custom = new GrappleCustomization();
    	if (itemstack.hasTagCompound()) {
    		custom.loadNBT(itemstack.getTagCompound());
    	}
    	return custom;
    }
    
	@Override
    @SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag par4)
	{
		GrappleCustomization custom = getCustomization(stack);

		if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			if (custom.doublehook) {
				list.add("Double Hook");
			}
			if (custom.motor) {
				if (custom.smartmotor) {
					list.add("Smart Motor");
				} else {
					list.add("Motorized");
				}
			}
			if (custom.enderstaff) {
				list.add("Ender Staff");
			}
			if (custom.attract) {
				list.add("Magnetized");
			}
			if (custom.repel) {
				list.add("Forcefield");
			}
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			list.add("A basic grappling hook for swinging");
			list.add("");
			list.add(grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindUseItem) + " - Throw grappling hook");
			list.add(grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindUseItem) + " again - Release");
			list.add("Double-" + grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindUseItem) + " - Release and throw again");
			list.add(grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindForward) + ", " +
					grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindLeft) + ", " +
					grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindBack) + ", " +
					grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindRight) +
					" - Swing");
			list.add(grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindJump) + " - Release and jump (while in midair)");
			list.add(grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindSneak) + " - Stop swinging");
			list.add(grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindSneak) + " + " +
					grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindForward) + 
					" - Climb up");
			list.add(grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindSneak) + " + " +
					grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindBack) + 
					" - Climb down");
		} else {
			list.add("Hold " + grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindSneak) + " to see controls");
		}
	}

	@Override
	public void onLeftClickRelease(ItemStack stack, EntityPlayer player) {

	}
	
}

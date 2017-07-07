package com.yyon.grapplinghook.items;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.yyon.grapplinghook.CommonProxyClass;
import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.vec;
import com.yyon.grapplinghook.controllers.grappleController;
import com.yyon.grapplinghook.controllers.multihookController;
import com.yyon.grapplinghook.network.MultiHookMessage;
import com.yyon.grapplinghook.network.ToolConfigMessage;

public class multiBow extends Item implements clickitem {
	public multiBow() {
		super();
		maxStackSize = 1;
		setFull3D();
		setUnlocalizedName("multihook");
		
		this.setMaxDamage(500);
		
		setCreativeTab(CreativeTabs.TRANSPORTATION);
		
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack)
	{
		return 72000;
	}

	public void dorightclick(ItemStack stack, World worldIn, EntityPlayer player) {
		int playerid = player.getEntityId();
		if (worldIn.isRemote) {
			if (grapplemod.controllers.containsKey(playerid) && grapplemod.controllers.get(playerid).controllerid != grapplemod.AIRID) {
				grappleController controller = grapplemod.controllers.get(playerid);
				controller.unattach();
			} else {
				NBTTagCompound compound = grapplemod.getstackcompound(stack, "grapplemod");
				boolean slow = compound.getBoolean("slow");
				
				grappleController control = grapplemod.createControl(grapplemod.MULTIID, -1, playerid, worldIn, new vec(0,0,0), -1, null);
				if (control instanceof multihookController) {
					multihookController multicontrol = (multihookController) control;
					if (slow) {
						multicontrol.maxspeed = 2;
					} else {
						multicontrol.maxspeed = 4;
					}
				}
				grapplemod.network.sendToServer(new MultiHookMessage(playerid, player.isSneaking()));
			}
		}
	}

    @Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
    	ItemStack itemStackIn = playerIn.getHeldItem(hand);
        this.dorightclick(itemStackIn, worldIn, playerIn);
        
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack)
	{
		return EnumAction.NONE;
	}

	@Override
	public void onLeftClick(ItemStack stack, EntityPlayer player) {
		if (player.isSneaking()) {
			int playerid = player.getEntityId();
			grapplemod.network.sendToServer(new ToolConfigMessage(playerid));
		}
	}

	@Override
	public void onLeftClickRelease(ItemStack stack, EntityPlayer player) {
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
    
    public static float getAngle(EntityLivingBase entity) {
    	if (entity.isSneaking()) {
    		return 10F;//40F;
    	} else {
    		return 20F;
    	}
    }
    
	@Override
    @SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag par4)
	{
		list.add("Shoots two hooks and pulls player towards hooks");
		list.add("");
		list.add("Side crosshairs - Aim hooks");
		list.add("Center crosshairs - Direction of movement");
		list.add(grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindUseItem) + " - Throw grappling hooks");
		list.add(grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindUseItem) + " again - Release");
		list.add("Double-" + grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindUseItem) + " - Release and throw again");
		list.add(grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindJump) + " - Release and jump");
		list.add(grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindSneak) + " + " + 
				grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindUseItem) + " - Throw grappling hooks at a narrower angle");
		list.add(grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindSneak) + " + " + 
				grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindAttack) + " - Toggle speed");
	}
}

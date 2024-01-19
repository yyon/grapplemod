package com.yyon.grapplinghook.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
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

public class LongFallBoots extends ItemArmor {
	public LongFallBoots(ArmorMaterial material, int type) {
	    super(material, 0, type);
	    this.setUnlocalizedName("longfallboots");
	    MinecraftForge.EVENT_BUS.register(this);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister)
	{
		itemIcon = iconRegister.registerIcon("grapplemod:longfallboots");
	}

	@SubscribeEvent
	public void onLivingFallEvent(LivingFallEvent event)
	{
		if (event.entity != null && event.entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)event.entity;
			
			ItemStack armor = player.getCurrentArmor(0);
		    if (armor != null && armor.getItem() instanceof LongFallBoots)
		    {
				// this cancels the fall event so you take no damage
				event.setCanceled(true);
		    }
		}
	}
	
	/*
    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
    	if (event.source == DamageSource.inWall) {
    		if (event.entity != null && event.entity instanceof EntityPlayer)
    		{
    			EntityPlayer player = (EntityPlayer)event.entity;
    			
    			ItemStack armor = player.getCurrentArmor(0);
    		    if (armor != null && armor.getItem() instanceof LongFallBoots)
    		    {
    				event.setCanceled(true);
    		    }
    		}
    	}
    }
    */
    
    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event){
    	if (event.source == DamageSource.inWall) {
    		if (event.entity != null && event.entity instanceof EntityPlayer)
    		{
    			EntityPlayer player = (EntityPlayer)event.entity;
    			
    			ItemStack armor = player.getCurrentArmor(0);
    		    if (armor != null && armor.getItem() instanceof LongFallBoots)
    		    {
    				event.setCanceled(true);
    		    }
    		}
    	}
   	}
    
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
	{
		list.add("Cancels fall damage when worn");
	}
}

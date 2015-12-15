package com.yyon.grapplinghook;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
	
	@SubscribeEvent
	public void onLivingFallEvent(LivingFallEvent event)
	{
		if (event.entity != null && event.entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)event.entity;
			ItemStack armorFeet = player.getCurrentArmor(0);
			
		    if (armorFeet != null && armorFeet.getItem() instanceof LongFallBoots)
		    {
				// this cancels the fall event so you take no damage
				event.setCanceled(true);
		    }
		}
	}
}

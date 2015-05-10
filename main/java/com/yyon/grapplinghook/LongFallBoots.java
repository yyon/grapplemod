package com.yyon.grapplinghook;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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

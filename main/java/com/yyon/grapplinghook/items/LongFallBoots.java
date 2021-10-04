package com.yyon.grapplinghook.items;

import java.util.List;

import org.spongepowered.asm.mixin.MixinEnvironment.Side;

import com.yyon.grapplinghook.GrappleConfig;
import com.yyon.grapplinghook.grapplemod;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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

public class LongFallBoots extends ItemArmor {
	public LongFallBoots(ArmorMaterial material, int type) {
	    super(material, 0, EntityEquipmentSlot.FEET);
	    this.setUnlocalizedName("longfallboots");
	    MinecraftForge.EVENT_BUS.register(this);
		setCreativeTab(grapplemod.tabGrapplemod);
	}
	
	@SubscribeEvent
	public void onLivingHurtEvent(LivingHurtEvent event) {
		if (event.getEntity() != null && event.getEntity() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)event.getEntity();
			
			for (ItemStack armor : player.getArmorInventoryList()) {
			    if (armor != null && armor.getItem() instanceof LongFallBoots)
			    {
			    	if (event.getSource() == DamageSource.FLY_INTO_WALL) {
			    		System.out.println("Flew into wall");
						// this cancels the fall event so you take no damage
						event.setCanceled(true);
			    	}
			    }
			}
		}
	}
	
	@SubscribeEvent
	public void onLivingFallEvent(LivingFallEvent event)
	{
		if (event.getEntity() != null && event.getEntity() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)event.getEntity();
			
			for (ItemStack armor : player.getArmorInventoryList()) {
			    if (armor != null && armor.getItem() instanceof LongFallBoots)
			    {
					// this cancels the fall event so you take no damage
					event.setCanceled(true);
			    }
			}
		}
	}
	
	@Override
    @SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag par4)
	{
		if (!stack.isItemEnchanted()) {
			if (GrappleConfig.getconf().longfallbootsrecipe) {
				list.add("Right click a Grappling Hook Modifier block with Feather Falling IV Diamond Boots to obtain");
			}
		}
		list.add("Cancels fall damage when worn");
	}

	@Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (this.isInCreativeTab(tab))
        {
        	ItemStack stack = new ItemStack(this);
            items.add(stack);
            
        	stack = new ItemStack(this);
        	stack.addEnchantment(grapplemod.wallrunenchantment, 1);
        	stack.addEnchantment(grapplemod.doublejumpenchantment, 1);
        	stack.addEnchantment(grapplemod.slidingenchantment, 1);
            items.add(stack);
        }
    }
}

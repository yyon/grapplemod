package com.yyon.grapplinghook.items.upgrades;

import com.yyon.grapplinghook.common.CommonSetup;
import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.utils.GrappleCustomization;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class BaseUpgradeItem extends Item {
	public GrappleCustomization.upgradeCategories category = null;
	boolean craftingRemaining = false;

	public BaseUpgradeItem(int maxStackSize, GrappleCustomization.upgradeCategories theCategory) {
		super(new Item.Properties().stacksTo(maxStackSize).tab(CommonSetup.tabGrapplemod));
		
		this.category = theCategory;
		
		if (theCategory != null) {
			this.setCraftingRemainingItem();
		}
	}
	
	public void setCraftingRemainingItem() {
		craftingRemaining = true;
	}

	@Override
	public ItemStack getContainerItem(ItemStack itemStack)
    {
        if (!this.craftingRemaining)
        {
            return ItemStack.EMPTY;
        }
        return new ItemStack(this);
    }
	
	@Override
	public boolean hasCraftingRemainingItem() {
		return this.craftingRemaining;
	}


	public BaseUpgradeItem() {
		this(64, null);
	}
}

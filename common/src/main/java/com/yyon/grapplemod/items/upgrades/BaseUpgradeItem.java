package com.yyon.grapplemod.items.upgrades;

import com.yyon.grapplemod.mixin.ItemAccessor;
import com.yyon.grapplemod.utils.GrappleCustomization;
import net.minecraft.world.item.Item;

public class BaseUpgradeItem extends Item {
	public GrappleCustomization.upgradeCategories category = null;

	public BaseUpgradeItem(int maxStackSize, GrappleCustomization.upgradeCategories theCategory) {
		super(new Item.Properties().stacksTo(maxStackSize));
		
		this.category = theCategory;
		
		if (theCategory != null) {
			this.setCraftingRemainingItem();
		}
	}
	
	public void setCraftingRemainingItem() {
		((ItemAccessor) this).grapplemod$setCraftingRemainingItem(this);
	}

	public BaseUpgradeItem() {
		this(64, null);
	}
}

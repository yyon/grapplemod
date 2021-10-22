package com.yyon.grapplinghook.integrations;

import com.yyon.grapplinghook.common.CommonSetup;
import com.yyon.grapplinghook.utils.GrappleCustomization;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class JeiIntegrations implements IModPlugin {

	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation("grapplemod", "jeiintegration");
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration) {
		IModPlugin.super.registerItemSubtypes(registration);
		
		registration.registerSubtypeInterpreter(CommonSetup.grapplingHookItem, new IIngredientSubtypeInterpreter<ItemStack>() {
			public String optionalString(boolean value, String desc) {
				return value ? "+" : "!";
			}
			
			@Override
			public String apply(ItemStack ingredient, UidContext context) {
				GrappleCustomization custom = CommonSetup.grapplingHookItem.getCustomization(ingredient);
				return "" + 
				this.optionalString(custom.motor, "motor") +
				this.optionalString(custom.rocket, "rocket") +
				this.optionalString(custom.doublehook, "double") +
				this.optionalString(custom.smartmotor, "smart") +
				this.optionalString(custom.enderstaff, "enderstaff") +
				this.optionalString(custom.attract, "attract") +
				this.optionalString(custom.repel, "repel") +
				"";
			}
		});
	}

	
}

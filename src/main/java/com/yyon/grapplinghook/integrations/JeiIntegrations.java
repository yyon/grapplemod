/*
package com.yyon.grapplinghook.integrations;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yyon.grapplinghook.client.ClientProxyInterface;
import com.yyon.grapplinghook.common.CommonSetup;
import com.yyon.grapplinghook.config.GrappleConfig;
import com.yyon.grapplinghook.utils.GrappleCustomization;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.Arrays;

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
	
	class ModifierRecipes {
		ItemStack input1, input2, output;
		public ModifierRecipes(ItemStack input1, ItemStack output) {
			this.input1 = input1;
			this.input2 = null;
			this.output = output;
		}
		public ModifierRecipes(ItemStack input1, ItemStack input2, ItemStack output) {
			this.input1 = input1;
			this.input2 = input2;
			this.output = output;
		}
	};
	
	ResourceLocation modifierRecipesLoc = new ResourceLocation("grapplemod", "modifierrecipes");

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		IModPlugin.super.registerCategories(registration);
		
		registration.addRecipeCategories(new IRecipeCategory<ModifierRecipes>() {

			@Override
			public ResourceLocation getUid() {
				return modifierRecipesLoc;
			}

			@Override
			public Class<? extends ModifierRecipes> getRecipeClass() {
				return ModifierRecipes.class;
			}

			@Override
			public String getTitle() {
				return ClientProxyInterface.proxy.localize("block.grapplemod.block_grapple_modifier");
			}

			@Override
			public IDrawable getBackground() {
				return registration.getJeiHelpers().getGuiHelper().createDrawable(new ResourceLocation("grapplemod", "textures/gui/jei_modifier_bg.png"), 0, 0, 120, 60);
			}

			@Override
			public IDrawable getIcon() {
				return registration.getJeiHelpers().getGuiHelper().createDrawableIngredient(new ItemStack(CommonSetup.grappleModifierBlockItem));
			}

			@Override
			public void setIngredients(ModifierRecipes recipe, IIngredients ingredients) {
				if (recipe.input2 == null) {
					ingredients.setInput(VanillaTypes.ITEM, recipe.input1);
				} else {
					ingredients.setInputs(VanillaTypes.ITEM, Arrays.asList(recipe.input1, recipe.input2));
				}
				ingredients.setOutput(VanillaTypes.ITEM, recipe.output);
			}

			@Override
			public void setRecipe(IRecipeLayout recipeLayout, ModifierRecipes recipe, IIngredients ingredients) {
				IGuiItemStackGroup items = recipeLayout.getItemStacks();
				
				items.init(0, true, 12, 9);
				items.init(1, true, 34, 9);
				items.init(2, false, 85, 9);
				
				items.set(ingredients);
			}

			@Override
			public void draw(ModifierRecipes recipe, PoseStack matrixStack, double mouseX, double mouseY) {
				IRecipeCategory.super.draw(recipe, matrixStack, mouseX, mouseY);
				
				if (recipe.input2 != null) {
					String text = ClientProxyInterface.proxy.localize("grapplemod.jei_modifier_text");
					int linenum = 0;
					for (String line : text.split("\n")) {
						Minecraft.getInstance().font.draw(matrixStack, line, 3, 29+11*linenum, 0);
						linenum++;
					}
				}
			}
		});
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		IModPlugin.super.registerRecipeCatalysts(registration);
		
		registration.addRecipeCatalyst(new ItemStack(CommonSetup.grappleModifierBlockItem), modifierRecipesLoc);
	}
	
	ItemStack grappleWithCustom(String option) {
		GrappleCustomization custom = new GrappleCustomization();
		custom.setBoolean(option, true);
		ItemStack stack = new ItemStack(CommonSetup.grapplingHookItem);
		CommonSetup.grapplingHookItem.setCustomOnServer(stack, custom, null);
		return stack;
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		IModPlugin.super.registerRecipes(registration);
		
		ItemStack ff_diamond_boots = new ItemStack(Items.DIAMOND_BOOTS);
		ff_diamond_boots.enchant(Enchantments.FALL_PROTECTION, 4);
		registration.addRecipes(Arrays.asList(
				new ModifierRecipes(new ItemStack(CommonSetup.motorUpgradeItem), new ItemStack(CommonSetup.grapplingHookItem), this.grappleWithCustom("motor")), 
				new ModifierRecipes(new ItemStack(CommonSetup.doubleUpgradeItem), new ItemStack(CommonSetup.grapplingHookItem), this.grappleWithCustom("doublehook")), 
				new ModifierRecipes(new ItemStack(CommonSetup.staffUpgradeItem), new ItemStack(CommonSetup.grapplingHookItem), this.grappleWithCustom("enderstaff")), 
				new ModifierRecipes(new ItemStack(CommonSetup.forcefieldUpgradeItem), new ItemStack(CommonSetup.grapplingHookItem), this.grappleWithCustom("repel")), 
				new ModifierRecipes(new ItemStack(CommonSetup.magnetUpgradeItem), new ItemStack(CommonSetup.grapplingHookItem), this.grappleWithCustom("attract")), 
				new ModifierRecipes(new ItemStack(CommonSetup.rocketUpgradeItem), new ItemStack(CommonSetup.grapplingHookItem), this.grappleWithCustom("rocket"))
				), modifierRecipesLoc);
		
		

		if (GrappleConfig.getConf().longfallboots.longfallbootsrecipe) {
			registration.addRecipes(Arrays.asList(
					new ModifierRecipes(ff_diamond_boots, new ItemStack(CommonSetup.longFallBootsItem))
			), modifierRecipesLoc);
		}
	}

	
}
*/

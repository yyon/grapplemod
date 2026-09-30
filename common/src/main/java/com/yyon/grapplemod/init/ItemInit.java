package com.yyon.grapplemod.init;

import com.yyon.grapplemod.Constants;
import com.yyon.grapplemod.client.ClientProxyInterface;
import com.yyon.grapplemod.items.EnderStaffItem;
import com.yyon.grapplemod.items.ForcefieldItem;
import com.yyon.grapplemod.items.GrapplehookItem;
import com.yyon.grapplemod.items.LongFallBoots;
import com.yyon.grapplemod.items.upgrades.BaseUpgradeItem;
import com.yyon.grapplemod.items.upgrades.DoubleUpgradeItem;
import com.yyon.grapplemod.items.upgrades.ForcefieldUpgradeItem;
import com.yyon.grapplemod.items.upgrades.LimitsUpgradeItem;
import com.yyon.grapplemod.items.upgrades.MagnetUpgradeItem;
import com.yyon.grapplemod.items.upgrades.MotorUpgradeItem;
import com.yyon.grapplemod.items.upgrades.RocketUpgradeItem;
import com.yyon.grapplemod.items.upgrades.RopeUpgradeItem;
import com.yyon.grapplemod.items.upgrades.StaffUpgradeItem;
import com.yyon.grapplemod.items.upgrades.SwingUpgradeItem;
import com.yyon.grapplemod.items.upgrades.ThrowUpgradeItem;
import com.yyon.grapplemod.registration.RegistrationProvider;
import com.yyon.grapplemod.registration.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemInit {
	public static final RegistrationProvider<Item> ITEMS = RegistrationProvider.get(Registries.ITEM, Constants.MODID);
	public static final RegistrationProvider<CreativeModeTab> CREATIVE_MODE_TABS = RegistrationProvider.get(Registries.CREATIVE_MODE_TAB, Constants.MODID);

	public static final RegistryObject<Item, GrapplehookItem> GRAPPLING_HOOK = ITEMS.register("grapplinghook", GrapplehookItem::new);
	public static final RegistryObject<Item, EnderStaffItem> ENDER_STAFF = ITEMS.register("launcheritem", EnderStaffItem::new);
	public static final RegistryObject<Item, ForcefieldItem> FORCEFIELD = ITEMS.register("repeller", ForcefieldItem::new);

	public static final RegistryObject<Item, BaseUpgradeItem> BASE_UPGRADE = ITEMS.register("baseupgradeitem", BaseUpgradeItem::new);
	public static final RegistryObject<Item, DoubleUpgradeItem> DOUBLE_UPGRADE = ITEMS.register("doubleupgradeitem", DoubleUpgradeItem::new);
	public static final RegistryObject<Item, ForcefieldUpgradeItem> FORCEFIELD_UPGRADE = ITEMS.register("forcefieldupgradeitem", ForcefieldUpgradeItem::new);
	public static final RegistryObject<Item, MagnetUpgradeItem> MAGNET_UPGRADE = ITEMS.register("magnetupgradeitem", MagnetUpgradeItem::new);
	public static final RegistryObject<Item, MotorUpgradeItem> MOTOR_UPGRADE = ITEMS.register("motorupgradeitem", MotorUpgradeItem::new);
	public static final RegistryObject<Item, RopeUpgradeItem> ROPE_UPGRADE = ITEMS.register("ropeupgradeitem", RopeUpgradeItem::new);
	public static final RegistryObject<Item, StaffUpgradeItem> STAFF_UPGRADE = ITEMS.register("staffupgradeitem", StaffUpgradeItem::new);
	public static final RegistryObject<Item, SwingUpgradeItem> SWING_UPGRADE = ITEMS.register("swingupgradeitem", SwingUpgradeItem::new);
	public static final RegistryObject<Item, ThrowUpgradeItem> THROW_UPGRADE = ITEMS.register("throwupgradeitem", ThrowUpgradeItem::new);
	public static final RegistryObject<Item, LimitsUpgradeItem> LIMITS_UPGRADE = ITEMS.register("limitsupgradeitem", LimitsUpgradeItem::new);
	public static final RegistryObject<Item, RocketUpgradeItem> ROCKET_UPGRADE = ITEMS.register("rocketupgradeitem", RocketUpgradeItem::new);

	public static final RegistryObject<Item, LongFallBoots> LONG_FALL_BOOTS = ITEMS.register("longfallboots", () -> new LongFallBoots(ArmorMaterials.DIAMOND, ArmorItem.Type.BOOTS));

	public static final RegistryObject<CreativeModeTab, CreativeModeTab> TAB = CREATIVE_MODE_TABS.register(Constants.MODID, () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
			.icon(() -> new ItemStack(GRAPPLING_HOOK.get()))
			.title(Component.translatable("itemGroup.tabGrapplemod"))
			.displayItems((parameters, output) -> {
				ITEMS.getEntries().forEach(item -> output.accept(new ItemStack(item.get())));
				if (ClientProxyInterface.proxy != null) {
					ClientProxyInterface.proxy.fillGrappleVariants(output);
				}
			})
			.build());

	public static void loadClass() {
	}
}

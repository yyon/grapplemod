package com.yyon.grapplinghook.common;

import java.util.Optional;

import com.yyon.grapplinghook.blocks.modifierblock.BlockGrappleModifier;
import com.yyon.grapplinghook.blocks.modifierblock.TileEntityGrappleModifier;
import com.yyon.grapplinghook.enchantments.DoublejumpEnchantment;
import com.yyon.grapplinghook.enchantments.SlidingEnchantment;
import com.yyon.grapplinghook.enchantments.WallrunEnchantment;
import com.yyon.grapplinghook.entities.grapplearrow.GrapplehookEntity;
import com.yyon.grapplinghook.items.LongFallBoots;
import com.yyon.grapplinghook.items.GrapplehookItem;
import com.yyon.grapplinghook.items.EnderStaffItem;
import com.yyon.grapplinghook.items.ForcefieldItem;
import com.yyon.grapplinghook.items.upgrades.BaseUpgradeItem;
import com.yyon.grapplinghook.items.upgrades.DoubleUpgradeItem;
import com.yyon.grapplinghook.items.upgrades.ForcefieldUpgradeItem;
import com.yyon.grapplinghook.items.upgrades.LimitsUpgradeItem;
import com.yyon.grapplinghook.items.upgrades.MagnetUpgradeItem;
import com.yyon.grapplinghook.items.upgrades.MotorUpgradeItem;
import com.yyon.grapplinghook.items.upgrades.RocketUpgradeItem;
import com.yyon.grapplinghook.items.upgrades.RopeUpgradeItem;
import com.yyon.grapplinghook.items.upgrades.StaffUpgradeItem;
import com.yyon.grapplinghook.items.upgrades.SwingUpgradeItem;
import com.yyon.grapplinghook.items.upgrades.ThrowUpgradeItem;
import com.yyon.grapplinghook.network.DetachSingleHookMessage;
import com.yyon.grapplinghook.network.GrappleAttachMessage;
import com.yyon.grapplinghook.network.GrappleAttachPosMessage;
import com.yyon.grapplinghook.network.GrappleDetachMessage;
import com.yyon.grapplinghook.network.GrappleEndMessage;
import com.yyon.grapplinghook.network.GrappleModifierMessage;
import com.yyon.grapplinghook.network.KeypressMessage;
import com.yyon.grapplinghook.network.LoggedInMessage;
import com.yyon.grapplinghook.network.PlayerMovementMessage;
import com.yyon.grapplinghook.network.SegmentMessage;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class CommonSetup {
    public static GrapplehookItem grapplebowitem;
    public static EnderStaffItem launcheritem;
    public static ForcefieldItem repelleritem;

    public static BaseUpgradeItem baseupgradeitem;
    public static DoubleUpgradeItem doubleupgradeitem;
    public static ForcefieldUpgradeItem forcefieldupgradeitem;
    public static MagnetUpgradeItem magnetupgradeitem;
    public static MotorUpgradeItem motorupgradeitem;
    public static RopeUpgradeItem ropeupgradeitem;
    public static StaffUpgradeItem staffupgradeitem;
    public static SwingUpgradeItem swingupgradeitem;
    public static ThrowUpgradeItem throwupgradeitem;
    public static LimitsUpgradeItem limitsupgradeitem;
    public static RocketUpgradeItem rocketupgradeitem;

    public static Item longfallboots;
    
    public static WallrunEnchantment wallrunenchantment;
    public static DoublejumpEnchantment doublejumpenchantment;
    public static SlidingEnchantment slidingenchantment;

	public static SimpleChannel network;    // used to transmit your network messages
	public static final ResourceLocation simpleChannelRL = new ResourceLocation("grapplemod", "channel");

	public static Block blockGrappleModifier;
	public static BlockItem itemBlockGrappleModifier;
	
	public ResourceLocation resourceLocation;

	public static final ItemGroup tabGrapplemod = new ItemGroup("grapplemod") {
	      @OnlyIn(Dist.CLIENT)
	      public ItemStack makeIcon() {
	         return new ItemStack(grapplebowitem);
	      }
	};
	
	public static CommonEventHandlers eventHandlers = new CommonEventHandlers();;

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		network = NetworkRegistry.newSimpleChannel(simpleChannelRL, () -> "1.0",
	            version -> true,
	            version -> true);
		int id = 0;
		network.registerMessage(id++, PlayerMovementMessage.class, PlayerMovementMessage::encode, PlayerMovementMessage::new, PlayerMovementMessage::onMessageReceived, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		network.registerMessage(id++, GrappleEndMessage.class, GrappleEndMessage::encode, GrappleEndMessage::new, GrappleEndMessage::onMessageReceived, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		network.registerMessage(id++, GrappleModifierMessage.class, GrappleModifierMessage::encode, GrappleModifierMessage::new, GrappleModifierMessage::onMessageReceived, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		network.registerMessage(id++, KeypressMessage.class, KeypressMessage::encode, KeypressMessage::new, KeypressMessage::onMessageReceived, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		network.registerMessage(id++, GrappleAttachMessage.class, GrappleAttachMessage::encode, GrappleAttachMessage::new, GrappleAttachMessage::onMessageReceived, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		network.registerMessage(id++, GrappleDetachMessage.class, GrappleDetachMessage::encode, GrappleDetachMessage::new, GrappleDetachMessage::onMessageReceived, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		network.registerMessage(id++, DetachSingleHookMessage.class, DetachSingleHookMessage::encode, DetachSingleHookMessage::new, DetachSingleHookMessage::onMessageReceived, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		network.registerMessage(id++, GrappleAttachPosMessage.class, GrappleAttachPosMessage::encode, GrappleAttachPosMessage::new, GrappleAttachPosMessage::onMessageReceived, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		network.registerMessage(id++, SegmentMessage.class, SegmentMessage::encode, SegmentMessage::new, SegmentMessage::onMessageReceived, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		network.registerMessage(id++, LoggedInMessage.class, LoggedInMessage::encode, LoggedInMessage::new, LoggedInMessage::onMessageReceived, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
	}
	
	@SubscribeEvent
	public static void onBlocksRegistration(final RegistryEvent.Register<Block> blockRegisterEvent) {
		blockGrappleModifier = (BlockGrappleModifier)(new BlockGrappleModifier().setRegistryName("grapplemod", "block_grapple_modifier"));
		blockRegisterEvent.getRegistry().register(blockGrappleModifier);
	}

	public static TileEntityType<TileEntityGrappleModifier> tileEntityGrappleModifierType;
	
	@SubscribeEvent
	public static void onTileEntityTypeRegistration(final RegistryEvent.Register<TileEntityType<?>> event) {
		tileEntityGrappleModifierType =
				TileEntityType.Builder.of(TileEntityGrappleModifier::new, blockGrappleModifier).build(null);  // you probably don't need a datafixer --> null should be fine
		tileEntityGrappleModifierType.setRegistryName("grapplemod:block_grapple_modifier");
		event.getRegistry().register(tileEntityGrappleModifierType);
	}

	public static EntityType<GrapplehookEntity> grappleArrowType;
	
	@SubscribeEvent
	public static void onEntityTypeRegistration(RegistryEvent.Register<EntityType<?>> entityTypeRegisterEvent) {
		grappleArrowType = EntityType.Builder.<GrapplehookEntity>of(GrapplehookEntity::new, EntityClassification.MISC)
	            .sized(0.25F, 0.25F)
	            .build("grapplemod:grapplearrow");
		grappleArrowType.setRegistryName("grapplemod:grapplearrow");
	    entityTypeRegisterEvent.getRegistry().register(grappleArrowType);
	}
	
	public static Item[] getAllItems() {
		return new Item[] {
				grapplebowitem,
				launcheritem, 
				repelleritem, 
				longfallboots, 
				baseupgradeitem, 
				ropeupgradeitem, 
				throwupgradeitem, 
				motorupgradeitem, 
				swingupgradeitem, 
				staffupgradeitem, 
				forcefieldupgradeitem, 
				magnetupgradeitem, 
				doubleupgradeitem, 
				rocketupgradeitem, 
				limitsupgradeitem, 
				};
	}
	
	@SubscribeEvent
	public static void registerEnchantments(final RegistryEvent.Register<Enchantment> event) {
	    wallrunenchantment = new WallrunEnchantment();
	    wallrunenchantment.setRegistryName("wallrunenchantment");
	    doublejumpenchantment = new DoublejumpEnchantment();
	    doublejumpenchantment.setRegistryName("doublejumpenchantment");
	    slidingenchantment = new SlidingEnchantment();
	    slidingenchantment.setRegistryName("slidingenchantment");
	    
	    event.getRegistry().registerAll(wallrunenchantment, doublejumpenchantment, slidingenchantment);

	}

	public static void registerItem(Item item, String itemName, final RegistryEvent.Register<Item> itemRegisterEvent) {
		item.setRegistryName(itemName);
		itemRegisterEvent.getRegistry().register(item);
	}
	
	@SubscribeEvent
	public static void onItemsRegistration(final RegistryEvent.Register<Item> itemRegisterEvent) {
		grapplebowitem = new GrapplehookItem();
		registerItem(grapplebowitem, "grapplinghook", itemRegisterEvent);

		launcheritem = new EnderStaffItem();
		registerItem(launcheritem, "launcheritem", itemRegisterEvent);
		longfallboots = new LongFallBoots(ArmorMaterial.DIAMOND, 3);
		registerItem(longfallboots, "longfallboots", itemRegisterEvent);
		repelleritem = new ForcefieldItem();
		registerItem(repelleritem, "repeller", itemRegisterEvent);
	    baseupgradeitem = new BaseUpgradeItem();
		registerItem(baseupgradeitem, "baseupgradeitem", itemRegisterEvent);
	    doubleupgradeitem = new DoubleUpgradeItem();
		registerItem(doubleupgradeitem, "doubleupgradeitem", itemRegisterEvent);
	    forcefieldupgradeitem = new ForcefieldUpgradeItem();
		registerItem(forcefieldupgradeitem, "forcefieldupgradeitem", itemRegisterEvent);
	    magnetupgradeitem = new MagnetUpgradeItem();
		registerItem(magnetupgradeitem, "magnetupgradeitem", itemRegisterEvent);
	    motorupgradeitem = new MotorUpgradeItem();
		registerItem(motorupgradeitem, "motorupgradeitem", itemRegisterEvent);
	    ropeupgradeitem = new RopeUpgradeItem();
		registerItem(ropeupgradeitem, "ropeupgradeitem", itemRegisterEvent);
	    staffupgradeitem = new StaffUpgradeItem();
		registerItem(staffupgradeitem, "staffupgradeitem", itemRegisterEvent);
	    swingupgradeitem = new SwingUpgradeItem();
		registerItem(swingupgradeitem, "swingupgradeitem", itemRegisterEvent);
	    throwupgradeitem = new ThrowUpgradeItem();
		registerItem(throwupgradeitem, "throwupgradeitem", itemRegisterEvent);
	    limitsupgradeitem = new LimitsUpgradeItem();
		registerItem(limitsupgradeitem, "limitsupgradeitem", itemRegisterEvent);
	    rocketupgradeitem = new RocketUpgradeItem();
		registerItem(rocketupgradeitem, "rocketupgradeitem", itemRegisterEvent);

		// We need to create a BlockItem so the player can carry this block in their hand and it can appear in the inventory
		Item.Properties itemSimpleProperties = new Item.Properties()
				.stacksTo(64)
				.tab(tabGrapplemod);  // which inventory tab?
		itemBlockGrappleModifier = new BlockItem(blockGrappleModifier, itemSimpleProperties);
		itemBlockGrappleModifier.setRegistryName(blockGrappleModifier.getRegistryName());
		itemRegisterEvent.getRegistry().register(itemBlockGrappleModifier);
	}
}

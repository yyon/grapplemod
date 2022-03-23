package com.yyon.grapplinghook.common;

import com.yyon.grapplinghook.blocks.modifierblock.BlockGrappleModifier;
import com.yyon.grapplinghook.blocks.modifierblock.TileEntityGrappleModifier;
import com.yyon.grapplinghook.enchantments.DoublejumpEnchantment;
import com.yyon.grapplinghook.enchantments.SlidingEnchantment;
import com.yyon.grapplinghook.enchantments.WallrunEnchantment;
import com.yyon.grapplinghook.entities.grapplehook.GrapplehookEntity;
import com.yyon.grapplinghook.items.EnderStaffItem;
import com.yyon.grapplinghook.items.ForcefieldItem;
import com.yyon.grapplinghook.items.GrapplehookItem;
import com.yyon.grapplinghook.items.LongFallBoots;
import com.yyon.grapplinghook.items.upgrades.*;
import com.yyon.grapplinghook.network.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class CommonSetup {
    public static GrapplehookItem grapplingHookItem;
    public static EnderStaffItem enderStaffItem;
    public static ForcefieldItem forcefieldItem;

    public static BaseUpgradeItem baseUpgradeItem;
    public static DoubleUpgradeItem doubleUpgradeItem;
    public static ForcefieldUpgradeItem forcefieldUpgradeItem;
    public static MagnetUpgradeItem magnetUpgradeItem;
    public static MotorUpgradeItem motorUpgradeItem;
    public static RopeUpgradeItem ropeUpgradeItem;
    public static StaffUpgradeItem staffUpgradeItem;
    public static SwingUpgradeItem swingUpgradeItem;
    public static ThrowUpgradeItem throwUpgradeItem;
    public static LimitsUpgradeItem limitsUpgradeItem;
    public static RocketUpgradeItem rocketUpgradeItem;

    public static Item longFallBootsItem;
    
    public static WallrunEnchantment wallrunEnchantment;
    public static DoublejumpEnchantment doubleJumpEnchantment;
    public static SlidingEnchantment slidingEnchantment;

	public static SimpleChannel network;    // used to transmit your network messages
	public static final ResourceLocation simpleChannelRL = new ResourceLocation("grapplemod", "channel");

	public static Block grappleModifierBlock;
	public static BlockItem grappleModifierBlockItem;
	
	public static final CreativeModeTab tabGrapplemod = new CreativeModeTab("grapplemod") {
	      @OnlyIn(Dist.CLIENT)
	      public ItemStack makeIcon() {
	         return new ItemStack(grapplingHookItem);
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
		grappleModifierBlock = (BlockGrappleModifier)(new BlockGrappleModifier().setRegistryName("grapplemod", "block_grapple_modifier"));
		blockRegisterEvent.getRegistry().register(grappleModifierBlock);
	}

	public static BlockEntityType<TileEntityGrappleModifier> grappleModifierTileEntityType;
	
	@SubscribeEvent
	public static void onTileEntityTypeRegistration(final RegistryEvent.Register<BlockEntityType<?>> event) {
		grappleModifierTileEntityType =
				BlockEntityType.Builder.of(TileEntityGrappleModifier::new, grappleModifierBlock).build(null);  // you probably don't need a datafixer --> null should be fine
		grappleModifierTileEntityType.setRegistryName("grapplemod:block_grapple_modifier");
		event.getRegistry().register(grappleModifierTileEntityType);
	}

	public static EntityType<GrapplehookEntity> grapplehookEntityType;
	
	@SubscribeEvent
	public static void onEntityTypeRegistration(RegistryEvent.Register<EntityType<?>> entityTypeRegisterEvent) {
		grapplehookEntityType = EntityType.Builder.<GrapplehookEntity>of(GrapplehookEntity::new, MobCategory.MISC)
	            .sized(0.25F, 0.25F)
	            .build("grapplemod:grapplehook");
		grapplehookEntityType.setRegistryName("grapplemod:grapplehook");
	    entityTypeRegisterEvent.getRegistry().register(grapplehookEntityType);
	}
	
	public static Item[] getAllItems() {
		return new Item[] {
				grapplingHookItem,
				enderStaffItem, 
				forcefieldItem, 
				longFallBootsItem, 
				baseUpgradeItem, 
				ropeUpgradeItem, 
				throwUpgradeItem, 
				motorUpgradeItem, 
				swingUpgradeItem, 
				staffUpgradeItem, 
				forcefieldUpgradeItem, 
				magnetUpgradeItem, 
				doubleUpgradeItem, 
				rocketUpgradeItem, 
				limitsUpgradeItem, 
				};
	}
	
	@SubscribeEvent
	public static void registerEnchantments(final RegistryEvent.Register<Enchantment> event) {
	    wallrunEnchantment = new WallrunEnchantment();
	    wallrunEnchantment.setRegistryName("wallrunenchantment");
	    doubleJumpEnchantment = new DoublejumpEnchantment();
	    doubleJumpEnchantment.setRegistryName("doublejumpenchantment");
	    slidingEnchantment = new SlidingEnchantment();
	    slidingEnchantment.setRegistryName("slidingenchantment");
	    
	    event.getRegistry().registerAll(wallrunEnchantment, doubleJumpEnchantment, slidingEnchantment);

	}

	public static void registerItem(Item item, String itemName, final RegistryEvent.Register<Item> itemRegisterEvent) {
		item.setRegistryName(itemName);
		itemRegisterEvent.getRegistry().register(item);
	}
	
	@SubscribeEvent
	public static void onItemsRegistration(final RegistryEvent.Register<Item> itemRegisterEvent) {
		grapplingHookItem = new GrapplehookItem();
		registerItem(grapplingHookItem, "grapplinghook", itemRegisterEvent);

		enderStaffItem = new EnderStaffItem();
		registerItem(enderStaffItem, "launcheritem", itemRegisterEvent);
		longFallBootsItem = new LongFallBoots(ArmorMaterials.DIAMOND, 3);
		registerItem(longFallBootsItem, "longfallboots", itemRegisterEvent);
		forcefieldItem = new ForcefieldItem();
		registerItem(forcefieldItem, "repeller", itemRegisterEvent);
	    baseUpgradeItem = new BaseUpgradeItem();
		registerItem(baseUpgradeItem, "baseupgradeitem", itemRegisterEvent);
	    doubleUpgradeItem = new DoubleUpgradeItem();
		registerItem(doubleUpgradeItem, "doubleupgradeitem", itemRegisterEvent);
	    forcefieldUpgradeItem = new ForcefieldUpgradeItem();
		registerItem(forcefieldUpgradeItem, "forcefieldupgradeitem", itemRegisterEvent);
	    magnetUpgradeItem = new MagnetUpgradeItem();
		registerItem(magnetUpgradeItem, "magnetupgradeitem", itemRegisterEvent);
	    motorUpgradeItem = new MotorUpgradeItem();
		registerItem(motorUpgradeItem, "motorupgradeitem", itemRegisterEvent);
	    ropeUpgradeItem = new RopeUpgradeItem();
		registerItem(ropeUpgradeItem, "ropeupgradeitem", itemRegisterEvent);
	    staffUpgradeItem = new StaffUpgradeItem();
		registerItem(staffUpgradeItem, "staffupgradeitem", itemRegisterEvent);
	    swingUpgradeItem = new SwingUpgradeItem();
		registerItem(swingUpgradeItem, "swingupgradeitem", itemRegisterEvent);
	    throwUpgradeItem = new ThrowUpgradeItem();
		registerItem(throwUpgradeItem, "throwupgradeitem", itemRegisterEvent);
	    limitsUpgradeItem = new LimitsUpgradeItem();
		registerItem(limitsUpgradeItem, "limitsupgradeitem", itemRegisterEvent);
	    rocketUpgradeItem = new RocketUpgradeItem();
		registerItem(rocketUpgradeItem, "rocketupgradeitem", itemRegisterEvent);

		// We need to create a BlockItem so the player can carry this block in their hand and it can appear in the inventory
		Item.Properties itemSimpleProperties = new Item.Properties()
				.stacksTo(64)
				.tab(tabGrapplemod);  // which inventory tab?
		grappleModifierBlockItem = new BlockItem(grappleModifierBlock, itemSimpleProperties);
		grappleModifierBlockItem.setRegistryName(grappleModifierBlock.getRegistryName());
		itemRegisterEvent.getRegistry().register(grappleModifierBlockItem);
	}
}

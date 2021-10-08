package com.yyon.grapplinghook;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yyon.grapplinghook.blocks.BlockGrappleModifier;
import com.yyon.grapplinghook.blocks.TileEntityGrappleModifier;
import com.yyon.grapplinghook.entities.grappleArrow;
import com.yyon.grapplinghook.items.KeypressItem;
import com.yyon.grapplinghook.items.LongFallBoots;
import com.yyon.grapplinghook.items.grappleBow;
import com.yyon.grapplinghook.items.launcherItem;
import com.yyon.grapplinghook.items.repeller;
import com.yyon.grapplinghook.items.alternategrapple.DoubleMotorHook;
import com.yyon.grapplinghook.items.alternategrapple.EnderHook;
import com.yyon.grapplinghook.items.alternategrapple.MagnetHook;
import com.yyon.grapplinghook.items.alternategrapple.MotorHook;
import com.yyon.grapplinghook.items.alternategrapple.RocketDoubleMotorHook;
import com.yyon.grapplinghook.items.alternategrapple.RocketHook;
import com.yyon.grapplinghook.items.alternategrapple.SmartHook;
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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

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

//TODO
// Pull mobs
// Attach 2 things together
// wallrun on diagonal walls
// smart motor acts erratically when aiming above hook
// key events

@Mod(grapplemod.MODID)
@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class grapplemod {
    public static final String MODID = "grapplemod";
    
    public static final String VERSION = "1.16.5-v12";

    public static final Logger LOGGER = LogManager.getLogger();

    public static Item grapplebowitem;
    public static Item motorhookitem;
    public static Item smarthookitem;
    public static Item doublemotorhookitem;
    public static Item rocketdoublemotorhookitem;
    public static Item enderhookitem;
    public static Item magnethookitem;
    public static Item rockethookitem;
    public static Item launcheritem;
    public static Item repelleritem;

    public static Item baseupgradeitem;
    public static Item doubleupgradeitem;
    public static Item forcefieldupgradeitem;
    public static Item magnetupgradeitem;
    public static Item motorupgradeitem;
    public static Item ropeupgradeitem;
    public static Item staffupgradeitem;
    public static Item swingupgradeitem;
    public static Item throwupgradeitem;
    public static Item limitsupgradeitem;
    public static Item rocketupgradeitem;

    public static Item longfallboots;
    
//    public static final EnumEnchantmentType GRAPPLEENCHANTS_FEET = EnumHelper.addEnchantmentType("GRAPPLEENCHANTS_FEET", (item) -> item instanceof ItemArmor && ((ItemArmor)item).armorType == EntityEquipmentSlot.FEET);
//    
//    public static WallrunEnchantment wallrunenchantment;
//    public static DoublejumpEnchantment doublejumpenchantment;
//    public static SlidingEnchantment slidingenchantment;

//	public static Object instance;
	
	public static SimpleChannel network;    // used to transmit your network messages
	public static final ResourceLocation simpleChannelRL = new ResourceLocation("grapplemod", "channel");
//	public static SimpleNetworkWrapper network;
//	
//	public static HashMap<Integer, grappleController> controllers = new HashMap<Integer, grappleController>(); // client side
//	public static HashMap<BlockPos, grappleController> controllerpos = new HashMap<BlockPos, grappleController>();
	public static HashSet<Integer> attached = new HashSet<Integer>(); // server side	
	public static HashMap<Integer, HashSet<grappleArrow>> allarrows = new HashMap<Integer, HashSet<grappleArrow>>(); // server side
	
	private static int controllerid = 0;
	public static int GRAPPLEID = controllerid++;
	public static int REPELID = controllerid++;
	public static int AIRID = controllerid++;
		
	public static boolean anyblocks = true;
	public static HashSet<Block> grapplingblocks;
	public static boolean removeblocks = false;
	public static HashSet<Block> grapplingbreaksblocks;
	public static boolean anybreakblocks = false;
	public static HashSet<Block> grapplingignoresblocks;
	public static boolean anyignoresblocks = false;
	
	public static Block blockGrappleModifier;
	public static BlockItem itemBlockGrappleModifier;
	
	public ResourceLocation resourceLocation;
	
	public enum upgradeCategories {
		ROPE ("Rope"), 
		THROW ("Hook Thrower"), 
		MOTOR ("Motor"), 
		SWING ("Swing Speed"), 
		STAFF ("Ender Staff"), 
		FORCEFIELD ("Forcefield"), 
		MAGNET ("Hook Magnet"), 
		DOUBLE ("Double Hook"),
		LIMITS ("Limits"),
		ROCKET ("Rocket");
		
		public String description;
		private upgradeCategories(String desc) {
			this.description = desc;
		}
		
		public static upgradeCategories fromInt(int i) {
			return upgradeCategories.values()[i];
		}
		public int toInt() {
			for (int i = 0; i < size(); i++) {
				if (upgradeCategories.values()[i] == this) {
					return i;
				}
			}
			return -1;
		}
		public static int size() {
			return upgradeCategories.values().length;
		}
		public Item getItem() {
			if (this == upgradeCategories.ROPE) {
				return ropeupgradeitem;
			} else if (this == upgradeCategories.THROW) {
				return throwupgradeitem;
			} else if (this == upgradeCategories.MOTOR) {
				return motorupgradeitem;
			} else if (this == upgradeCategories.SWING) {
				return swingupgradeitem;
			} else if (this == upgradeCategories.STAFF) {
				return staffupgradeitem;
			} else if (this == upgradeCategories.FORCEFIELD) {
				return forcefieldupgradeitem;
			} else if (this == upgradeCategories.MAGNET) {
				return magnetupgradeitem;
			} else if (this == upgradeCategories.DOUBLE) {
				return doubleupgradeitem;
			} else if (this == upgradeCategories.LIMITS) {
				return limitsupgradeitem;
			} else if (this == upgradeCategories.ROCKET) {
				return rocketupgradeitem;
			}
			return null;
		}
	};
	
	public static final ItemGroup tabGrapplemod = new ItemGroup("grapplemod") {
	      @OnlyIn(Dist.CLIENT)
	      public ItemStack makeIcon() {
	         return new ItemStack(grapplebowitem);
	      }
	};

	public enum keys {
		keyBindUseItem,
		keyBindForward,
		keyBindLeft,
		keyBindBack,
		keyBindRight,
		keyBindJump,
		keyBindSneak,
		keyBindAttack
	}

	Method capturePosition = null;
	
	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		network = NetworkRegistry.newSimpleChannel(simpleChannelRL, () -> "1.0",
	            version -> true,
	            version -> true);
		int id = 0;
//		network.registerMessage(PlayerMovementMessage.Handler.class, PlayerMovementMessage.class, id++, Side.SERVER);
//		network.registerMessage(GrappleAttachMessage.Handler.class, GrappleAttachMessage.class, id++, Side.CLIENT);
//		network.registerMessage(GrappleEndMessage.Handler.class, GrappleEndMessage.class, id++, Side.SERVER);
//		network.registerMessage(GrappleDetachMessage.Handler.class, GrappleDetachMessage.class, id++, Side.CLIENT);
//		network.registerMessage(DetachSingleHookMessage.Handler.class, DetachSingleHookMessage.class, id++, Side.CLIENT);
//		network.registerMessage(GrappleAttachPosMessage.Handler.class, GrappleAttachPosMessage.class, id++, Side.CLIENT);
//		network.registerMessage(SegmentMessage.Handler.class, SegmentMessage.class, id++, Side.CLIENT);
//		network.registerMessage(GrappleModifierMessage.Handler.class, GrappleModifierMessage.class, id++, Side.SERVER);
//		network.registerMessage(LoggedInMessage.Handler.class, LoggedInMessage.class, id++, Side.CLIENT);
//		network.registerMessage(KeypressMessage.Handler.class, KeypressMessage.class, id++, Side.SERVER);
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

	public static CommonProxyClass proxy = DistExecutor.unsafeRunForDist(() -> ClientProxyClass::new, () -> () -> null);
	
	@SubscribeEvent
	public static void onTileEntityTypeRegistration(final RegistryEvent.Register<TileEntityType<?>> event) {
		tileEntityGrappleModifierType =
				TileEntityType.Builder.of(TileEntityGrappleModifier::new, blockGrappleModifier).build(null);  // you probably don't need a datafixer --> null should be fine
		tileEntityGrappleModifierType.setRegistryName("grapplemod:block_grapple_modifier");
		event.getRegistry().register(tileEntityGrappleModifierType);
	}

	public static EntityType<grappleArrow> grappleArrowType;
	
	@SubscribeEvent
	public static void onEntityTypeRegistration(RegistryEvent.Register<EntityType<?>> entityTypeRegisterEvent) {
		grappleArrowType = EntityType.Builder.<grappleArrow>of(grappleArrow::new, EntityClassification.MISC)
	            .sized(0.25F, 0.25F)
	            .build("grapplemod:grapplearrow");
		grappleArrowType.setRegistryName("grapplemod:grapplearrow");
	    entityTypeRegisterEvent.getRegistry().register(grappleArrowType);
	}

	/*
	public void preInit(FMLPreInitializationEvent event) {
	    MinecraftForge.EVENT_BUS.register(this);

		capturePosition = ObfuscationReflectionHelper.findMethod(NetHandlerPlayServer.class, "func_184342_d", Void.class);
    	if (capturePosition == null) {
    		System.out.println("Error: could not access capturePosition function");
    	}
}

	public void init(FMLInitializationEvent event, grapplemod grapplemod) {
		
	}
	
	public void postInit(FMLPostInitializationEvent event) {
	}
	
	public void sendplayermovementmessage(grappleArrow grappleArrow, int playerid, int arrowid) {
	}

	public void getplayermovement(grappleController control, int playerid) {
	}
	
//	@SubscribeEvent
//	public void onLivingFallEvent(LivingFallEvent event)
//	{
//		if (event.getEntity() != null && grapplemod.attached.contains(event.getEntity().getEntityId()))
//		{
//			event.setCanceled(true);
//		}
//	}
	
	
	public void resetlaunchertime(int playerid) {
	}

	public void launchplayer(EntityPlayer player) {
	}
	
	public boolean isSneaking(Entity entity) {
		return entity.isSneaking();
	}
	
    @SubscribeEvent
    public void onBlockBreak(BreakEvent event){
    	EntityPlayer player = event.getPlayer();
    	if (player != null) {
	    	ItemStack stack = player.getHeldItemMainhand();
	    	if (stack != null) {
	    		Item item = stack.getItem();
	    		if (item instanceof grappleBow) {
	    			event.setCanceled(true);
	    			return;
	    		}
	    	}
    	}
    	
    	this.blockbreak(event);
    }
    
    
    public void blockbreak(BreakEvent event) {
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
    	if (event.getSource() == DamageSource.IN_WALL) {
    		if (grapplemod.attached.contains(event.getEntity().getEntityId())) {
    			event.setCanceled(true);
    		}
    	}
    }
    
	public String getkeyname(CommonProxyClass.keys keyenum) {
		return null;
	}

	public void openModifierScreen(TileEntityGrappleModifier tileent) {
	}
	
	public String localize(String string) {
		return string;
	}

	public void startrocket(EntityPlayer player, GrappleCustomization custom) {
	}
	
	public void updateRocketRegen(double rocket_active_time, double rocket_refuel_ratio) {
	}

	public double getRocketFunctioning() {
		return 0;
	}

	public boolean iswallrunning(Entity entity, vec motion) {
		return false;
	}
	
	public boolean issliding(Entity entity, vec motion) {
		return false;
	}
	
	public Method getCapturePositionMethod() {
		return capturePosition;
	}
	
	public grappleController createControl(int id, int arrowid, int entityid, World world, vec pos, BlockPos blockpos, GrappleCustomization custom) {
		return null;
	}

	public void playSlideSound(Entity entity) {
	}
	
    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
		Entity entity = event.getEntity();
		int id = entity.getEntityId();
		boolean isconnected = grapplemod.allarrows.containsKey(id);
		if (isconnected) {
			HashSet<grappleArrow> arrows = grapplemod.allarrows.get(id);
			for (grappleArrow arrow: arrows) {
//				if (!arrow.isAddedToWorld()) {
//					System.out.println("arrow unloaded");
//					IChunkProvider chunkprovider = arrow.world.getChunkProvider();
//					if (chunkprovider instanceof ChunkProviderServer) {
//						ChunkProviderServer chunkproviderserver = (ChunkProviderServer) chunkprovider;
//						chunkproviderserver.loadChunk(arrow.chunkCoordX, arrow.chunkCoordZ, new Runnable() {
//							@Override
//							public void run() {
//								Entity newArrow = arrow.world.getEntityByID(arrow.getEntityId());
//								if (newArrow == null) {
//									System.out.println("Couldn't delete grappleArrow");
//									return;
//									}
//								newArrow.setDead();
//							}
//						});
//					}
//				}
				arrow.removeServer();
			}
			arrows.clear();

			grapplemod.attached.remove(id);
			
			if (grapplemod.controllers.containsKey(id)) {
				grapplemod.controllers.remove(id);
			}
			
			if (grappleBow.grapplearrows1.containsKey(entity)) {
				grappleBow.grapplearrows1.remove(entity);
			}
			if (grappleBow.grapplearrows2.containsKey(entity)) {
				grappleBow.grapplearrows2.remove(entity);
			}
			
			grapplemod.sendtocorrectclient(new GrappleDetachMessage(id), id, entity.world);
		}
	}

	public void playWallrunJumpSound(Entity entity) {
	}
	*/
	
	/*
	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
		
		if (GrappleConfig.getconf().override_allowflight) {
			FMLCommonHandler.instance().getMinecraftServerInstance().setAllowFlight(true);
		}
	}
	
	@SubscribeEvent
	public void onPlayerLoggedInEvent(PlayerLoggedInEvent e) {
		if (e.player instanceof EntityPlayerMP) {
			grapplemod.network.sendTo(new LoggedInMessage(GrappleConfig.options), (EntityPlayerMP) e.player);
		} else {
			System.out.println("Not an EntityPlayerMP");
		}
	}
	*/
	
//	@EventHandler
//	public void load(FMLInitializationEvent event){
//	}

	/*
	public static HashSet<Block> stringToBlocks(String s) {
		HashSet<Block> blocks = new HashSet<Block>();
		
		if (s.equals("") || s.equals("none") || s.equals("any")) {
			return blocks;
		}
		
		String[] blockstr = s.split(",");
		
	    for(String str:blockstr){
	    	str = str.trim();
	    	String modid;
	    	String name;
	    	if (str.contains(":")) {
	    		String[] splitstr = str.split(":");
	    		modid = splitstr[0];
	    		name = splitstr[1];
	    	} else {
	    		modid = "minecraft";
	    		name = str;
	    	}
	    	
	    	Block b = Block.REGISTRY.getObject(new ResourceLocation(modid, name));
	    	
	    	blocks.add(b);
	    }
	    
	    return blocks;
	}
	
	public static void updateGrapplingBlocks() {
		String s = GrappleConfig.getconf().grapplingBlocks;
		if (s.equals("any") || s.equals("")) {
			s = GrappleConfig.getconf().grapplingNonBlocks;
			if (s.equals("none") || s.equals("")) {
				anyblocks = true;
			} else {
				anyblocks = false;
				removeblocks = true;
			}
		} else {
			anyblocks = false;
			removeblocks = false;
		}
	
		if (!anyblocks) {
			grapplingblocks = stringToBlocks(s);
		}
		
		grapplingbreaksblocks = stringToBlocks(GrappleConfig.getconf().grappleBreakBlocks);
		anybreakblocks = grapplingbreaksblocks.size() != 0;
		
		grapplingignoresblocks = stringToBlocks(GrappleConfig.getconf().grappleIgnoreBlocks);
		anyignoresblocks = grapplingignoresblocks.size() != 0;
		
	}
	*/
	
	public static Item[] getAllItems() {
		return new Item[] {
				grapplebowitem, 
//				itemBlockGrappleModifier,
				enderhookitem, 
				magnethookitem,
				rockethookitem,
				motorhookitem, 
				smarthookitem, 
				doublemotorhookitem, 
				rocketdoublemotorhookitem, 
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
	
	/*
	@SubscribeEvent
	public void registerEnchantments(RegistryEvent.Register<Enchantment> event) {
	    event.getRegistry().registerAll(wallrunenchantment, doublejumpenchantment, slidingenchantment);

	}
	*/
	public static Item registerItem(Item item, String itemName, final RegistryEvent.Register<Item> itemRegisterEvent) {
		item.setRegistryName(itemName);
		itemRegisterEvent.getRegistry().register(item);
		return item;
	}
	
	@SubscribeEvent
	public static void onItemsRegistration(final RegistryEvent.Register<Item> itemRegisterEvent) {
		grapplebowitem = registerItem(new grappleBow(), "grapplinghook", itemRegisterEvent);
		motorhookitem = registerItem(new MotorHook(), "motorhook", itemRegisterEvent);
		smarthookitem = registerItem(new SmartHook(), "smarthook", itemRegisterEvent);
		doublemotorhookitem = registerItem(new DoubleMotorHook(), "doublemotorhook", itemRegisterEvent);
		rocketdoublemotorhookitem = registerItem(new RocketDoubleMotorHook(), "rocketdoublemotorhook", itemRegisterEvent);
		enderhookitem = registerItem(new EnderHook(), "enderhook", itemRegisterEvent);
		magnethookitem = registerItem(new MagnetHook(), "magnethook", itemRegisterEvent);
		rockethookitem = registerItem(new RocketHook(), "rockethook", itemRegisterEvent);
		launcheritem = registerItem(new launcherItem(), "launcheritem", itemRegisterEvent);
		longfallboots = registerItem(new LongFallBoots(ArmorMaterial.DIAMOND, 3), "longfallboots", itemRegisterEvent);
		repelleritem = registerItem(new repeller(), "repeller", itemRegisterEvent);
	    baseupgradeitem = registerItem(new BaseUpgradeItem(), "baseupgradeitem", itemRegisterEvent);
	    doubleupgradeitem = registerItem(new DoubleUpgradeItem(), "doubleupgradeitem", itemRegisterEvent);
	    forcefieldupgradeitem = registerItem(new ForcefieldUpgradeItem(), "forcefieldupgradeitem", itemRegisterEvent);
	    magnetupgradeitem = registerItem(new MagnetUpgradeItem(), "magnetupgradeitem", itemRegisterEvent);
	    motorupgradeitem = registerItem(new MotorUpgradeItem(), "motorupgradeitem", itemRegisterEvent);
	    ropeupgradeitem = registerItem(new RopeUpgradeItem(), "ropeupgradeitem", itemRegisterEvent);
	    staffupgradeitem = registerItem(new StaffUpgradeItem(), "staffupgradeitem", itemRegisterEvent);
	    swingupgradeitem = registerItem(new SwingUpgradeItem(), "swingupgradeitem", itemRegisterEvent);
	    throwupgradeitem = registerItem(new ThrowUpgradeItem(), "throwupgradeitem", itemRegisterEvent);
	    limitsupgradeitem = registerItem(new LimitsUpgradeItem(), "limitsupgradeitem", itemRegisterEvent);
	    rocketupgradeitem = registerItem(new RocketUpgradeItem(), "rocketupgradeitem", itemRegisterEvent);

		// We need to create a BlockItem so the player can carry this block in their hand and it can appear in the inventory
		Item.Properties itemSimpleProperties = new Item.Properties()
				.stacksTo(64)
				.tab(grapplemod.tabGrapplemod);  // which inventory tab?
		itemBlockGrappleModifier = new BlockItem(blockGrappleModifier, itemSimpleProperties);
		itemBlockGrappleModifier.setRegistryName(blockGrappleModifier.getRegistryName());
		itemRegisterEvent.getRegistry().register(itemBlockGrappleModifier);
	}
	
	public static void addarrow(int id, grappleArrow arrow) {
		if (!allarrows.containsKey(id)) {
			allarrows.put(id, new HashSet<grappleArrow>());
		}
		allarrows.get(id).add(arrow);
	}
	
	public static void removeallmultihookarrows(int id) {
		if (!allarrows.containsKey(id)) {
			allarrows.put(id, new HashSet<grappleArrow>());
		}
		for (grappleArrow arrow : allarrows.get(id)) {
			if (arrow != null && arrow.isAlive()) {
				arrow.removeServer();
			}
		}
		allarrows.put(id, new HashSet<grappleArrow>());
	}

	/*
	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		grapplebowitem = new grappleBow();
		grapplebowitem.setRegistryName("grapplinghook");
		motorhookitem = new MotorHook();
		motorhookitem.setRegistryName("motorhook");
		smarthookitem = new SmartHook();
		smarthookitem.setRegistryName("smarthook");
		doublemotorhookitem = new DoubleMotorHook();
		doublemotorhookitem.setRegistryName("doublemotorhook");
		rocketdoublemotorhookitem = new RocketDoubleMotorHook();
		rocketdoublemotorhookitem.setRegistryName("rocketdoublemotorhook");
		enderhookitem = new EnderHook();
		enderhookitem.setRegistryName("enderhook");
		magnethookitem = new MagnetHook();
		magnethookitem.setRegistryName("magnethook");
		rockethookitem = new RocketHook();
		rockethookitem.setRegistryName("rockethook");
		launcheritem = new launcherItem();
		launcheritem.setRegistryName("launcheritem");
		longfallboots = new LongFallBoots(ItemArmor.ArmorMaterial.DIAMOND, 3);
		longfallboots.setRegistryName("longfallboots");
		repelleritem = new repeller();
		repelleritem.setRegistryName("repeller");
	    baseupgradeitem = new BaseUpgradeItem();
	    baseupgradeitem.setRegistryName("baseupgradeitem");
	    doubleupgradeitem = new DoubleUpgradeItem();
	    doubleupgradeitem.setRegistryName("doubleupgradeitem");
	    doubleupgradeitem.setContainerItem(doubleupgradeitem);
	    forcefieldupgradeitem = new ForcefieldUpgradeItem();
	    forcefieldupgradeitem.setRegistryName("forcefieldupgradeitem");
	    forcefieldupgradeitem.setContainerItem(forcefieldupgradeitem);
	    magnetupgradeitem = new MagnetUpgradeItem();
	    magnetupgradeitem.setRegistryName("magnetupgradeitem");
	    magnetupgradeitem.setContainerItem(magnetupgradeitem);
	    motorupgradeitem = new MotorUpgradeItem();
	    motorupgradeitem.setRegistryName("motorupgradeitem");
	    motorupgradeitem.setContainerItem(motorupgradeitem);
	    ropeupgradeitem = new RopeUpgradeItem();
	    ropeupgradeitem.setRegistryName("ropeupgradeitem");
	    ropeupgradeitem.setContainerItem(ropeupgradeitem);
	    staffupgradeitem = new StaffUpgradeItem();
	    staffupgradeitem.setRegistryName("staffupgradeitem");
	    staffupgradeitem.setContainerItem(staffupgradeitem);
	    swingupgradeitem = new SwingUpgradeItem();
	    swingupgradeitem.setRegistryName("swingupgradeitem");
	    swingupgradeitem.setContainerItem(swingupgradeitem);
	    throwupgradeitem = new ThrowUpgradeItem();
	    throwupgradeitem.setRegistryName("throwupgradeitem");
	    throwupgradeitem.setContainerItem(throwupgradeitem);
	    limitsupgradeitem = new LimitsUpgradeItem();
	    limitsupgradeitem.setRegistryName("limitsupgradeitem");
	    limitsupgradeitem.setContainerItem(limitsupgradeitem);
	    rocketupgradeitem = new RocketUpgradeItem();
	    rocketupgradeitem.setRegistryName("rocketupgradeitem");
	    rocketupgradeitem.setContainerItem(rocketupgradeitem);
	    
	    wallrunenchantment = new WallrunEnchantment();
	    wallrunenchantment.setRegistryName("wallrunenchantment");
	    doublejumpenchantment = new DoublejumpEnchantment();
	    doublejumpenchantment.setRegistryName("doublejumpenchantment");
	    slidingenchantment = new SlidingEnchantment();
	    slidingenchantment.setRegistryName("slidingenchantment");
	    
//		System.out.println(grapplebowitem);
		
		resourceLocation = new ResourceLocation(grapplemod.MODID, "grapplemod");
		
		registerEntity(grappleArrow.class, "grappleArrow");
		
		network = NetworkRegistry.INSTANCE.newSimpleChannel("grapplemodchannel");
		byte id = 0;
		network.registerMessage(PlayerMovementMessage.Handler.class, PlayerMovementMessage.class, id++, Side.SERVER);
		network.registerMessage(GrappleAttachMessage.Handler.class, GrappleAttachMessage.class, id++, Side.CLIENT);
		network.registerMessage(GrappleEndMessage.Handler.class, GrappleEndMessage.class, id++, Side.SERVER);
		network.registerMessage(GrappleDetachMessage.Handler.class, GrappleDetachMessage.class, id++, Side.CLIENT);
		network.registerMessage(DetachSingleHookMessage.Handler.class, DetachSingleHookMessage.class, id++, Side.CLIENT);
		network.registerMessage(GrappleAttachPosMessage.Handler.class, GrappleAttachPosMessage.class, id++, Side.CLIENT);
		network.registerMessage(SegmentMessage.Handler.class, SegmentMessage.class, id++, Side.CLIENT);
		network.registerMessage(GrappleModifierMessage.Handler.class, GrappleModifierMessage.class, id++, Side.SERVER);
		network.registerMessage(LoggedInMessage.Handler.class, LoggedInMessage.class, id++, Side.CLIENT);
		network.registerMessage(KeypressMessage.Handler.class, KeypressMessage.class, id++, Side.SERVER);
		
		blockGrappleModifier = (BlockGrappleModifier)(new BlockGrappleModifier().setUnlocalizedName("block_grapple_modifier"));
		blockGrappleModifier.setHardness(10F);
		blockGrappleModifier.setRegistryName("block_grapple_modifier");
	    ForgeRegistries.BLOCKS.register(blockGrappleModifier);

	    itemBlockGrappleModifier = new ItemBlock(blockGrappleModifier);
	    itemBlockGrappleModifier.setRegistryName(blockGrappleModifier.getRegistryName());

	    // Each of your tile entities needs to be registered with a name that is unique to your mod.
		GameRegistry.registerTileEntity(TileEntityGrappleModifier.class, new ResourceLocation(grapplemod.MODID, "tile_entity_grapple_modifier"));
	
	    MinecraftForge.EVENT_BUS.register(this);
	    
		proxy.preInit(event);
		
		tabGrapplemod.setRelevantEnchantmentTypes(GRAPPLEENCHANTS_FEET);
	}
	*/
	
	/*
	@EventHandler
	public void Init(FMLInitializationEvent event) {
		proxy.init(event, this);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
		
		grapplemod.updateGrapplingBlocks();
	}
	*/
	
	/*
	int entityID = 0;
	public void registerEntity(Class<? extends Entity> entityClass, String name)
	{
		EntityRegistry.registerModEntity(resourceLocation, entityClass, name, entityID++, this, 900, 1, true);
	}
	*/
	
	/*
	public static void registerController(int entityId, grappleController controller) {
		if (controllers.containsKey(entityId)) {
			controllers.get(entityId).unattach();
		}
		
		controllers.put(entityId, controller);
	}
	
	public static void unregisterController(int entityId) {
		controllers.remove(entityId);
	}
	*/

	/*
	public static void receiveGrappleDetach(int id) {
		grappleController controller = controllers.get(id);
		if (controller != null) {
			controller.receiveGrappleDetach();
		} else {
			System.out.println("Couldn't find controller");
		}
	}
	
	public static void receiveGrappleDetachHook(int id, int hookid) {
		grappleController controller = controllers.get(id);
		if (controller != null) {
			controller.receiveGrappleDetachHook(hookid);
		} else {
			System.out.println("Couldn't find controller");
		}
	}

	public static void receiveEnderLaunch(int id, double x, double y, double z) {
		grappleController controller = controllers.get(id);
		if (controller != null) {
			controller.receiveEnderLaunch(x, y, z);
		} else {
			System.out.println("Couldn't find controller");
		}
	}
	*/
	
	public static void sendtocorrectclient(Object message, int playerid, World w) {
		Entity entity = w.getEntity(playerid);
		if (entity instanceof ServerPlayerEntity) {
			grapplemod.network.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) entity), message);
		} else {
			System.out.println("ERROR! couldn't find player");
		}
	}
	
	/*
	public static void removesubarrow(int id) {
		HashSet<Integer> arrowIds = new HashSet<Integer>();
		arrowIds.add(id);
		grapplemod.network.sendToServer(new GrappleEndMessage(-1, arrowIds));
	}

	public static void receiveGrappleEnd(int id, World world, HashSet<Integer> arrowIds) {
		if (grapplemod.attached.contains(id)) {
			grapplemod.attached.remove(new Integer
					(id));
		} else {
		}
		
		for (int arrowid : arrowIds) {
	      	Entity grapple = world.getEntityByID(arrowid);
	  		if (grapple instanceof grappleArrow) {
	  			((grappleArrow) grapple).removeServer();
	  		} else {
	
	  		}
		}
  		
  		Entity entity = world.getEntityByID(id);
  		if (entity != null) {
      		entity.fallDistance = 0;
  		}
  		
  		grapplemod.removeallmultihookarrows(id);
	}
	

	public static NBTTagCompound getstackcompound(ItemStack stack, String key) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		NBTTagCompound basecompound = stack.getTagCompound();
        if (basecompound.hasKey(key, 10))
        {
            return basecompound.getCompoundTag(key);
        }
        else
        {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            stack.setTagInfo(key, nbttagcompound);
            return nbttagcompound;
        }
	}
	

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
	    if(eventArgs.getModID().equals("grapplemod")){
			System.out.println("grapplemod config updated");
			ConfigManager.sync("grapplemod", INSTANCE);
			
			grapplemod.updateGrapplingBlocks();
		}
	}
	*/

	public static void receiveKeypress(PlayerEntity player, KeypressItem.Keys key, boolean isDown) {
		if (player != null) {
			ItemStack stack = player.getItemInHand(Hand.MAIN_HAND);
			if (stack != null) {
				Item item = stack.getItem();
				if (item instanceof KeypressItem) {
					if (isDown) {
						((KeypressItem)item).onCustomKeyDown(stack, player, key, true);
					} else {
						((KeypressItem)item).onCustomKeyUp(stack, player, key, true);
					}
					return;
				}
			}

			stack = player.getItemInHand(Hand.OFF_HAND);
			if (stack != null) {
				Item item = stack.getItem();
				if (item instanceof KeypressItem) {
					if (isDown) {
						((KeypressItem)item).onCustomKeyDown(stack, player, key, false);
					} else {
						((KeypressItem)item).onCustomKeyUp(stack, player, key, false);
					}
					return;
				}
			}
		}
	}

	/*
	public static Rarity getRarityFromInt(int rarity_int) {
		Rarity[] rarities = (new Rarity[] {Rarity.VERY_RARE, Rarity.RARE, Rarity.UNCOMMON, Rarity.COMMON});
		if (rarity_int < 0) {rarity_int = 0;}
		if (rarity_int >= rarities.length) {rarity_int = rarities.length-1;}
		return rarities[rarity_int];
	}
	*/
	
	public static BlockRayTraceResult rayTraceBlocks(World world, vec from, vec to) {
		RayTraceResult result = world.clip(new RayTraceContext(from.toVec3d(), to.toVec3d(), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, null));
		if (result != null && result instanceof BlockRayTraceResult) {
			return (BlockRayTraceResult) result;
		}
		return null;
	}
}

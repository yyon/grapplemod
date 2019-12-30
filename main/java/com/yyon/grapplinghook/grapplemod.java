package com.yyon.grapplinghook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.yyon.grapplinghook.blocks.BlockGrappleModifier;
import com.yyon.grapplinghook.blocks.TileEntityGrappleModifier;
import com.yyon.grapplinghook.controllers.airfrictionController;
import com.yyon.grapplinghook.controllers.grappleController;
import com.yyon.grapplinghook.controllers.repelController;
import com.yyon.grapplinghook.enchantments.DoublejumpEnchantment;
import com.yyon.grapplinghook.enchantments.SlidingEnchantment;
import com.yyon.grapplinghook.enchantments.WallrunEnchantment;
import com.yyon.grapplinghook.entities.grappleArrow;
import com.yyon.grapplinghook.items.KeypressItem;
import com.yyon.grapplinghook.items.KeypressItem.Keys;
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
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;

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

@Mod(value="grapplemod")
public class grapplemod {

	public grapplemod(){}

    public static final String MODID = "grapplemod";
    
    public static final String VERSION = "1.14.4-v12";

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
    
    public static final EnchantmentType GRAPPLEENCHANTS_FEET = EnchantmentType.create("GRAPPLEENCHANTS_FEET", (item) -> item instanceof ArmorItem && ((ArmorItem)item).getEquipmentSlot() == EquipmentSlotType.FEET);
    
    public static WallrunEnchantment wallrunenchantment;
    public static DoublejumpEnchantment doublejumpenchantment;
    public static SlidingEnchantment slidingenchantment;

	public static Object instance;
	
	public static SimpleChannel network = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(MODID, "main_channel"))
			.clientAcceptedVersions(VERSION::equals)
			.serverAcceptedVersions(VERSION::equals)
			.networkProtocolVersion(() -> VERSION)
			.simpleChannel();
	
	public static HashMap<Integer, grappleController> controllers = new HashMap<Integer, grappleController>(); // client side
	public static HashMap<BlockPos, grappleController> controllerpos = new HashMap<BlockPos, grappleController>();
	public static HashSet<Integer> attached = new HashSet<Integer>(); // server side
	
	public static HashMap<Integer, HashSet<grappleArrow>> allarrows = new HashMap<Integer, HashSet<grappleArrow>>(); // server side
	
	private static int controllerid = 0;
	public static int GRAPPLEID = controllerid++;
	public static int REPELID = controllerid++;
	public static int AIRID = controllerid++;
		
	public static boolean anyblocks = true;
	public static ArrayList<Block> grapplingblocks;
	public static boolean removeblocks = false;
	
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
	
	public static final ItemGroup tabGrapplemod = (new ItemGroup("tabGrapplemod") {
		
		@Override
		public void fill(NonNullList<ItemStack> items) {
			// sort items
			super.fill(items);
			Item[] allitems = getAllItems();
			Collections.sort(items, new Comparator<ItemStack>() {
				@Override
				public int compare(ItemStack arg0, ItemStack arg1) {
					return getIndex(arg0.getItem()) - getIndex(arg1.getItem());
				}
				public int getIndex(Item item) {
					int i = 0;
					for (Item item2 : allitems) {
						if (item == item2) {
							return i;
						}
						i++;
					}
					return i;
				}
			});
		}

		@Override
		public ItemStack createIcon() {
			return new ItemStack(grapplebowitem);
		}
	});
	
//	@SidedProxy(clientSide="com.yyon.grapplinghook.ClientProxyClass", serverSide="com.yyon.grapplinghook.ServerProxyClass")
//	public static CommonProxyClass proxy;
	public static CommonProxyClass proxy = DistExecutor.runForDist(() -> ClientProxyClass::new, () -> ServerProxyClass::new);
	
	public void registerRenderers(){
	}
	public void generateNether(World world, Random random, int chunkX, int chunkZ){}
	public void generateSurface(World world, Random random, int chunkX, int chunkZ){}
	public int addFuel(ItemStack fuel){
		return 0;
	}

	public void serverLoad(FMLServerStartingEvent event){
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
			String[] blockstr = s.split(",");
			
			grapplingblocks = new ArrayList<Block>();
			
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
		    	
		        grapplingblocks.add(b);
		    }
		}
	}
	
	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event) {
//		System.out.println("REGISTERING ITEMS");
//		System.out.println(grapplebowitem);
	    event.getRegistry().registerAll(getAllItems());

	}
	
	public static Item[] getAllItems() {
		return new Item[] {
				grapplebowitem, 
				itemBlockGrappleModifier,
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
	
	@SubscribeEvent
	public void registerEnchantments(RegistryEvent.Register<Enchantment> event) {
	    event.getRegistry().registerAll(wallrunenchantment, doublejumpenchantment, slidingenchantment);

	}
	
	@SubscribeEvent
	public void preInit(FMLCommonSetupEvent event){
//		System.out.println("PREINIT!!!");
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
		longfallboots = new LongFallBoots(ArmorMaterial.DIAMOND, 3);
		longfallboots.setRegistryName("longfallboots");
		repelleritem = new repeller();
		repelleritem.setRegistryName("repeller");
	    baseupgradeitem = new BaseUpgradeItem(BaseUpgradeItem.getproperties(true));
	    baseupgradeitem.setRegistryName("baseupgradeitem");
	    doubleupgradeitem = new DoubleUpgradeItem();
	    doubleupgradeitem.setRegistryName("doubleupgradeitem");
	    forcefieldupgradeitem = new ForcefieldUpgradeItem();
	    forcefieldupgradeitem.setRegistryName("forcefieldupgradeitem");
	    magnetupgradeitem = new MagnetUpgradeItem();
	    magnetupgradeitem.setRegistryName("magnetupgradeitem");
	    motorupgradeitem = new MotorUpgradeItem();
	    motorupgradeitem.setRegistryName("motorupgradeitem");
	    ropeupgradeitem = new RopeUpgradeItem();
	    ropeupgradeitem.setRegistryName("ropeupgradeitem");
	    staffupgradeitem = new StaffUpgradeItem();
	    staffupgradeitem.setRegistryName("staffupgradeitem");
	    swingupgradeitem = new SwingUpgradeItem();
	    swingupgradeitem.setRegistryName("swingupgradeitem");
	    throwupgradeitem = new ThrowUpgradeItem();
	    throwupgradeitem.setRegistryName("throwupgradeitem");
	    limitsupgradeitem = new LimitsUpgradeItem();
	    limitsupgradeitem.setRegistryName("limitsupgradeitem");
	    rocketupgradeitem = new RocketUpgradeItem();
	    rocketupgradeitem.setRegistryName("rocketupgradeitem");
	    
	    wallrunenchantment = new WallrunEnchantment();
	    wallrunenchantment.setRegistryName("wallrunenchantment");
	    doublejumpenchantment = new DoublejumpEnchantment();
	    doublejumpenchantment.setRegistryName("doublejumpenchantment");
	    slidingenchantment = new SlidingEnchantment();
	    slidingenchantment.setRegistryName("slidingenchantment");
	    
//		System.out.println(grapplebowitem);
		
		resourceLocation = new ResourceLocation(grapplemod.MODID, "grapplemod");
		
		registerEntity(grappleArrow.class, "grappleArrow");
		
		registerMessage(PlayerMovementMessage.class, PlayerMovementMessage::toBytes, PlayerMovementMessage::fromBytes, PlayerMovementMessage::handle);
		registerMessage(GrappleAttachMessage.class, GrappleAttachMessage::toBytes, GrappleAttachMessage::fromBytes, GrappleAttachMessage::handle);
		registerMessage(GrappleEndMessage.class, GrappleEndMessage::toBytes, GrappleEndMessage::fromBytes, GrappleEndMessage::handle);
		registerMessage(GrappleDetachMessage.class, GrappleDetachMessage::toBytes, GrappleDetachMessage::fromBytes, GrappleDetachMessage::handle);
		registerMessage(DetachSingleHookMessage.class, DetachSingleHookMessage::toBytes, DetachSingleHookMessage::fromBytes, DetachSingleHookMessage::handle);
		registerMessage(GrappleAttachPosMessage.class, GrappleAttachPosMessage::toBytes, GrappleAttachPosMessage::fromBytes, GrappleAttachPosMessage::handle);
		registerMessage(SegmentMessage.class, SegmentMessage::toBytes, SegmentMessage::fromBytes, SegmentMessage::handle);
		registerMessage(GrappleModifierMessage.class, GrappleModifierMessage::toBytes, GrappleModifierMessage::fromBytes, GrappleModifierMessage::handle);
		registerMessage(LoggedInMessage.class, LoggedInMessage::toBytes, LoggedInMessage::fromBytes, LoggedInMessage::handle);
		registerMessage(KeypressMessage.class, KeypressMessage::toBytes, KeypressMessage::fromBytes, KeypressMessage::handle);
		
		blockGrappleModifier = (BlockGrappleModifier)(new BlockGrappleModifier(BlockGrappleModifier.getproperties()));
		blockGrappleModifier.setRegistryName("block_grapple_modifier");
	    ForgeRegistries.BLOCKS.register(blockGrappleModifier);

		Item.Properties properties = new Properties();
		properties.group(tabGrapplemod);
	    itemBlockGrappleModifier = new BlockItem(blockGrappleModifier, properties);
	    itemBlockGrappleModifier.setRegistryName(blockGrappleModifier.getRegistryName());

	    // Each of your tile entities needs to be registered with a name that is unique to your mod.
		GameRegistry.registerTileEntity(TileEntityGrappleModifier.class, "tile_entity_grapple_modifier");
	
	    MinecraftForge.EVENT_BUS.register(this);
	    
		proxy.preInit(event);
		
		tabGrapplemod.setRelevantEnchantmentTypes(GRAPPLEENCHANTS_FEET);

		grapplemod.updateGrapplingBlocks();
	}
	

	static byte message_id = 0;
	private static <MSG> void registerMessage(Class<MSG> type, BiConsumer<MSG, PacketBuffer> encoder, Function<PacketBuffer, MSG> decoder,
			BiConsumer<MSG, Supplier<Context>> consumer) {
		network.registerMessage(message_id++, type, encoder, decoder, consumer);
	}
	
	int entityID = 0;
	public void registerEntity(Class<? extends Entity> entityClass, String name)
	{
		EntityRegistry.registerModEntity(resourceLocation, entityClass, name, entityID++, this, 900, 1, true);
	}
	
	public static void registerController(int entityId, grappleController controller) {
		if (controllers.containsKey(entityId)) {
			controllers.get(entityId).unattach();
		}
		
		controllers.put(entityId, controller);
	}
	
	public static void unregisterController(int entityId) {
		controllers.remove(entityId);
	}

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
	
	public static <MSG> void sendtocorrectclient(MSG message, int playerid, World w) {
		Entity entity = w.getEntityByID(playerid);
		if (entity instanceof ServerPlayerEntity) {
			grapplemod.network.sendTo(message, (ServerPlayerEntity) entity);
		} else {
			System.out.println("ERROR! couldn't find player");
		}
	}
	
	public static grappleController createControl(int id, int arrowid, int entityid, World world, vec pos, BlockPos blockpos, GrappleCustomization custom) {

		grappleArrow arrow = null;
		Entity arrowentity = world.getEntityByID(arrowid);
		if (arrowentity != null && arrowentity instanceof grappleArrow) {
			arrow = (grappleArrow) arrowentity;
		}
		
		boolean multi = (custom != null) && (custom.doublehook);
		
		grappleController currentcontroller = controllers.get(entityid);
		if (currentcontroller != null && !(multi && currentcontroller.custom != null && currentcontroller.custom.doublehook)) {
			currentcontroller.unattach();
		}
		
//		System.out.println(blockpos);
		
		grappleController control = null;
		if (id == GRAPPLEID) {
			if (!multi) {
				control = new grappleController(arrowid, entityid, world, pos, id, custom);
			} else {
				control = grapplemod.controllers.get(entityid);
				boolean created = false;
				if (control != null && control.getClass().equals(grappleController.class)) {
					grappleController c = (grappleController) control;
					if (control.custom.doublehook) {
						if (arrow != null && arrow instanceof grappleArrow) {
							grappleArrow multiarrow = (grappleArrow) arrowentity;
							created = true;
							c.addArrow(multiarrow);
						}
					}
				}
				if (!created) {
/*					System.out.println("Couldn't create");
					grapplemod.removesubarrow(arrowid);*/
					control = new grappleController(arrowid, entityid, world, pos, id, custom);
				}
			}
		} else if (id == REPELID) {
			control = new repelController(arrowid, entityid, world, pos, id);
		} else if (id == AIRID) {
			control = new airfrictionController(arrowid, entityid, world, pos, id, custom);
		}
		if (blockpos != null && control != null) {
			grapplemod.controllerpos.put(blockpos, control);
		}
		
		return control;
	}
	
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
	

	public static CompoundNBT getstackcompound(ItemStack stack, String key) {
		if (!stack.hasTag()) {
			stack.setTag(new CompoundNBT());
		}
		CompoundNBT basecompound = stack.getTag();
        if (basecompound.contains(key, 10))
        {
            return basecompound.getCompound(key);
        }
        else
        {
            CompoundNBT nbttagcompound = new CompoundNBT();
            stack.setTagInfo(key, nbttagcompound);
            return nbttagcompound;
        }
	}
	

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
	    if(eventArgs.getModID().equals("grapplemod")){
			System.out.println("grapplemod config updated");
			ConfigManager.sync("grapplemod", Type.INSTANCE);
			
			grapplemod.updateGrapplingBlocks();
		}
	}

	public static void receiveKeypress(PlayerEntity player, Keys key, boolean isDown) {
		if (player != null) {
			ItemStack stack = player.getHeldItemMainhand();
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

			stack = player.getHeldItemOffhand();
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

}

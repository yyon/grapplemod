package com.yyon.grapplinghook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import com.yyon.grapplinghook.controllers.enderController;
import com.yyon.grapplinghook.controllers.grappleController;
import com.yyon.grapplinghook.controllers.hookControl;
import com.yyon.grapplinghook.entities.enderArrow;
import com.yyon.grapplinghook.entities.grappleArrow;
import com.yyon.grapplinghook.entities.hookArrow;
import com.yyon.grapplinghook.items.LongFallBoots;
import com.yyon.grapplinghook.items.enderBow;
import com.yyon.grapplinghook.items.grappleBow;
import com.yyon.grapplinghook.items.hookBow;
import com.yyon.grapplinghook.items.launcherItem;
import com.yyon.grapplinghook.network.GrappleAttachMessage;
import com.yyon.grapplinghook.network.GrappleAttachPosMessage;
import com.yyon.grapplinghook.network.GrappleClickMessage;
import com.yyon.grapplinghook.network.GrappleEndMessage;
import com.yyon.grapplinghook.network.PlayerMovementMessage;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

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
// upgrade to 1.8.8

@Mod(modid = grapplemod.MODID, version = grapplemod.VERSION)
public class grapplemod {

	public grapplemod(){}

    public static final String MODID = "grapplemod";
    public static final String VERSION = "1.7.10-v4";
    
    public static Item grapplebowitem;
    public static Item hookshotitem;
    public static Item enderhookitem;
    public static Item launcheritem;
    public static Item longfallboots;
    
	public static Object instance;
	
	public static SimpleNetworkWrapper network;
	
	public static HashMap<Integer, grappleController> controllers = new HashMap<Integer, grappleController>(); // client side
	public static HashMap<BlockPos, grappleController> controllerpos = new HashMap<BlockPos, grappleController>();
	public static ArrayList<Integer> attached = new ArrayList<Integer>(); // server side
	
	private static int controllerid;
	public static int GRAPPLEID = controllerid++;
	public static int ENDERID = controllerid++;
	public static int HOOKID = controllerid++;
	
	public static int grapplingLength = 0;
	public static boolean anyblocks = true;
	public static ArrayList<Block> grapplingblocks;
	
	@SidedProxy(clientSide="com.yyon.grapplinghook.ClientProxyClass", serverSide="com.yyon.grapplinghook.ServerProxyClass")
	public static CommonProxyClass proxy;
	
	@EventHandler
	public void load(FMLInitializationEvent event){
		GameRegistry.addRecipe(new ItemStack(grapplebowitem, 1), new Object[]{
			"X2", 
			"4X", Character.valueOf('2'), new ItemStack(Items.iron_pickaxe, 1), Character.valueOf('4'), new ItemStack(Items.lead, 1), 
		});
		GameRegistry.addRecipe(new ItemStack(hookshotitem, 1), new Object[]{
			"X2", 
			"4X", Character.valueOf('2'), new ItemStack(grapplebowitem, 1), Character.valueOf('4'), new ItemStack(Blocks.piston, 1), 
		});
		GameRegistry.addRecipe(new ItemStack(launcheritem, 1), new Object[]{
			"X2", 
			"4X", Character.valueOf('2'), new ItemStack(Items.ender_pearl, 1), Character.valueOf('4'), new ItemStack(Blocks.piston, 1), 
		});
		GameRegistry.addRecipe(new ItemStack(enderhookitem, 1), new Object[]{
			"X2", 
			"4X", Character.valueOf('2'), new ItemStack(grapplebowitem, 1), Character.valueOf('4'), new ItemStack(launcheritem, 1), 
		});
		GameRegistry.addRecipe(new ItemStack(longfallboots, 1), new Object[]{
			"2", 
			"4", Character.valueOf('2'), new ItemStack(Items.diamond_boots, 1), Character.valueOf('4'), new ItemStack(Blocks.wool, 1), 
		});
	}

	public void registerRenderers(){
	}
	public void generateNether(World world, Random random, int chunkX, int chunkZ){}
	public void generateSurface(World world, Random random, int chunkX, int chunkZ){}
	public int addFuel(ItemStack fuel){
		return 0;
	}
	
	@EventHandler
	public void serverLoad(FMLServerStartingEvent event){
		MinecraftServer.getServer().worldServerForDimension(0).getGameRules().addGameRule("grapplingLength", "0");
		MinecraftServer.getServer().worldServerForDimension(0).getGameRules().addGameRule("grapplingBlocks", "any");
	}
	
	public static void updateMaxLen() {
		String s = MinecraftServer.getServer().worldServerForDimension(0).getGameRules().getGameRuleStringValue("grapplingLength");
		grapplemod.grapplingLength = Integer.parseInt(s);
	}
	
	public static void updateGrapplingBlocks() {
		String s = MinecraftServer.getServer().worldServerForDimension(0).getGameRules().getGameRuleStringValue("grapplingBlocks");
		if (s.equals("any") || s.equals("")) {
			anyblocks = true;
		} else {
			anyblocks = false;
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
		    	
		    	Block b = GameRegistry.findBlock(modid, name);
		    	
		        grapplingblocks.add(b);
		    }
		}
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		grapplebowitem = new grappleBow();
		hookshotitem = new hookBow();
		launcheritem = new launcherItem();
		longfallboots = new LongFallBoots(ItemArmor.ArmorMaterial.DIAMOND, 3);
		enderhookitem = new enderBow();
		GameRegistry.registerItem(grapplebowitem, "grapplinghook"); // addObject
		GameRegistry.registerItem(hookshotitem, "hookshot"); // addObject
		GameRegistry.registerItem(launcheritem, "launcheritem"); // addObject
		GameRegistry.registerItem(longfallboots, "longfallboots");
		GameRegistry.registerItem(enderhookitem, "enderhook"); // addObject
		registerEntity(grappleArrow.class, "grappleArrow");
		registerEntity(enderArrow.class, "enderArrow");
		registerEntity(hookArrow.class, "hookArrow");
		proxy.preInit(event);
		network = NetworkRegistry.INSTANCE.newSimpleChannel("grapplemodchannel");
		byte id = 0;
//		network.registerMessage(PlayerPosMessage.Handler.class, PlayerPosMessage.class, id++, Side.CLIENT);
//		network.registerMessage(DummyMessage.Handler.class, DummyMessage.class, id++, Side.SERVER);
		network.registerMessage(PlayerMovementMessage.Handler.class, PlayerMovementMessage.class, id++, Side.SERVER);
		network.registerMessage(GrappleAttachMessage.Handler.class, GrappleAttachMessage.class, id++, Side.CLIENT);
		network.registerMessage(GrappleEndMessage.Handler.class, GrappleEndMessage.class, id++, Side.SERVER);
		network.registerMessage(GrappleClickMessage.Handler.class, GrappleClickMessage.class, id++, Side.CLIENT);
		network.registerMessage(GrappleAttachPosMessage.Handler.class, GrappleAttachPosMessage.class, id++, Side.CLIENT);
	}
	
	@EventHandler
	public void Init(FMLInitializationEvent event) {
		proxy.init(event, this);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		System.out.println("post init");
		System.out.println(proxy);
		proxy.postInit(event);
	}

	public void registerEntity(Class<? extends Entity> entityClass, String name)
	{
		int entityID = EntityRegistry.findGlobalUniqueEntityId();
//		long seed = name.hashCode();
//		Random rand = new Random(seed);
//		int primaryColor = rand.nextInt() * 16777215;
//		int secondaryColor = rand.nextInt() * 16777215;
		
		EntityRegistry.registerGlobalEntityID(entityClass, name, entityID);
		EntityRegistry.registerModEntity(entityClass, name, entityID, this, 64, 1, true);
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

	public static void receiveGrappleClick(int id,
			boolean leftclick) {
		grappleController controller = controllers.get(id);
		if (controller != null) {
			controller.receiveGrappleClick(leftclick);
		} else {
			System.out.println("Couldn't find controller");
		}
	}

	public static void receiveEnderLaunch(int id, double x, double y, double z) {
		System.out.println("Received EnderGrappleLaunchMessage");
		grappleController controller = controllers.get(id);
		if (controller != null) {
			controller.receiveEnderLaunch(x, y, z);
		} else {
			System.out.println("Couldn't find controller");
		}
	}
	
	public static void sendtocorrectclient(IMessage message, int playerid, World w) {
		Entity entity = w.getEntityByID(playerid);
		if (entity instanceof EntityPlayerMP) {
			grapplemod.network.sendTo(message, (EntityPlayerMP) entity);
		} else {
			System.out.println("ERROR! couldn't find player");
			System.out.println(playerid);
			System.out.println(entity);
		}
	}
	
	public static grappleController createControl(int id, int arrowid, int entityid, World world, vec pos, int maxlen, BlockPos blockpos) {
		/*
		Class<? extends grappleController> theclass = grapplecontrolsclasses.get(id);
		Constructor<? extends grappleController> ctor;
		grappleController control = null;
		try {
			ctor = theclass.getConstructor(Integer.class, Integer.class, World.class, Vec3.class);
			control = ctor.newInstance(arrowid, entityid, world, pos);
		*/
		grappleController control = null;
		if (id == GRAPPLEID) {
			control = new grappleController(arrowid, entityid, world, pos, maxlen);
		} else if (id == ENDERID) {
			control = new enderController(arrowid, entityid, world, pos, maxlen);
		} else if (id == HOOKID) {
			control = new hookControl(arrowid, entityid, world, pos, maxlen);
		}
		grapplemod.controllerpos.put(blockpos, control);
		System.out.println("create control");
		System.out.println(blockpos);
		
		return control;
	}
	
	public static NBTTagCompound getCompound(ItemStack stack) {
		NBTTagCompound compound = stack.getTagCompound();
		if (compound == null) {
			compound = new NBTTagCompound();
			stack.setTagCompound(compound);
		}
		return compound;
	}
}
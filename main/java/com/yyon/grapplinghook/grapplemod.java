package com.yyon.grapplinghook;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import com.yyon.grapplinghook.common.CommonProxyClass;

@Mod(modid = grapplemod.MODID, version = grapplemod.VERSION)
public class grapplemod {

	public grapplemod(){}

    public static final String MODID = "grapplemod";
    public static final String VERSION = "1.0";
    
    public static Item grapplebowitem;
    public static Item hookshotitem;
    public static Item launcheritem;
    public static Item longfallboots;
    
	public static Object instance;
	
	public static SimpleNetworkWrapper network;
	
	@SidedProxy(clientSide="com.yyon.grapplinghook.client.ClientProxyClass", serverSide="com.yyon.grapplinghook.common.CommonProxyClass")
	public static CommonProxyClass proxy;
	
	@EventHandler
	public void load(FMLInitializationEvent event){
		GameRegistry.addRecipe(new ItemStack(grapplebowitem, 1), new Object[]{
			"XX2", 
			"X4X", 
			"XXX", Character.valueOf('2'), new ItemStack(Items.iron_pickaxe, 1), Character.valueOf('4'), new ItemStack(Items.lead, 1), 
		});
		GameRegistry.addRecipe(new ItemStack(hookshotitem, 1), new Object[]{
			"XX2", 
			"X4X", 
			"XXX", Character.valueOf('2'), new ItemStack(grapplebowitem, 1), Character.valueOf('4'), new ItemStack(Blocks.piston, 1), 
		});
		GameRegistry.addRecipe(new ItemStack(launcheritem, 1), new Object[]{
			"XX2", 
			"X4X", 
			"XXX", Character.valueOf('2'), new ItemStack(Items.ender_pearl, 1), Character.valueOf('4'), new ItemStack(Blocks.piston, 1), 
		});
		GameRegistry.addRecipe(new ItemStack(longfallboots, 1), new Object[]{
			"X2X", 
			"X4X", 
			"XXX", Character.valueOf('2'), new ItemStack(Items.diamond_boots, 1), Character.valueOf('4'), new ItemStack(Blocks.wool, 1), 
		});
	}

	public void registerRenderers(){
	}
	public void generateNether(World world, Random random, int chunkX, int chunkZ){}
	public void generateSurface(World world, Random random, int chunkX, int chunkZ){}
	public int addFuel(ItemStack fuel){
		return 0;
	}
	public void serverLoad(FMLServerStartingEvent event){}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		grapplebowitem = (new grappleBow());
		hookshotitem = (new hookBow());
		launcheritem = (new launcherItem());
		longfallboots = new LongFallBoots(ItemArmor.ArmorMaterial.DIAMOND, 3);
		GameRegistry.registerItem(grapplebowitem, "grapplinghook"); // addObject		
		GameRegistry.registerItem(hookshotitem, "hookshot"); // addObject
		GameRegistry.registerItem(launcheritem, "launcheritem"); // addObject
		GameRegistry.registerItem(longfallboots, "longfallboots");
		registerEntity(grappleArrow.class, "grappleArrow");
		registerEntity(hookArrow.class, "hookArrow");
		proxy.preInit(event);
		
		network = NetworkRegistry.INSTANCE.newSimpleChannel("MyChannel");
		network.registerMessage(PlayerMovementMessage.Handler.class, PlayerMovementMessage.class, 0, Side.SERVER);
		network.registerMessage(PlayerPosMessage.Handler.class, PlayerPosMessage.class, 1, Side.CLIENT);
	}
	
	@EventHandler
	public void Init(FMLInitializationEvent event) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(grapplebowitem, 0, new ModelResourceLocation("grapplemod:grapplinghook", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(hookshotitem, 0, new ModelResourceLocation("grapplemod:hookshot", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(launcheritem, 0, new ModelResourceLocation("grapplemod:launcheritem", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(longfallboots, 0, new ModelResourceLocation("grapplemod:longfallboots", "inventory"));
		proxy.init(event);
	}
	
	public void registerEntity(Class entityClass, String name)
	{
		int entityID = EntityRegistry.findGlobalUniqueEntityId();
		long seed = name.hashCode();
		Random rand = new Random(seed);
		int primaryColor = rand.nextInt() * 16777215;
		int secondaryColor = rand.nextInt() * 16777215;
		
		EntityRegistry.registerGlobalEntityID(entityClass, name, entityID);
		EntityRegistry.registerModEntity(entityClass, name, entityID, this, 64, 1, true);
	}
}
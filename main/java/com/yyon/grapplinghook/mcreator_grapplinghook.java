package com.yyon.grapplinghook;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.yyon.grapplinghook.common.CommonProxyClass;

@Mod(modid = mcreator_grapplinghook.MODID, version = mcreator_grapplinghook.VERSION)
public class mcreator_grapplinghook {

	public mcreator_grapplinghook(){}

    public static final String MODID = "grapplemod";
    public static final String VERSION = "1.0";
    
    public static Item block;
	public static Object instance;

	@SidedProxy(clientSide="com.yyon.grapplinghook.client.ClientProxyClass", serverSide="com.yyon.grapplinghook.common.CommonProxyClass")
	public static CommonProxyClass proxy;
	
	@EventHandler
	public void load(FMLInitializationEvent event){
		GameRegistry.addRecipe(new ItemStack(block, 1), new Object[]{
			"XX2", "X4X", "XXX", Character.valueOf('2'), new ItemStack(Items.iron_ingot, 1), Character.valueOf('4'), new ItemStack(Items.lead, 1), 
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
		block = (new grappleBow());
		GameRegistry.registerItem(block, "grapplinghook"); // addObject		
		registerEntity(grappleArrow.class, "grappleArrow");
		proxy.preInit(event);
	}
	
	@EventHandler
	public void Init(FMLInitializationEvent event) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(block, 0, new ModelResourceLocation("grapplemod:grapplinghook", "inventory"));
		proxy.init(event);
	}
	
	public static void registerEntity(Class entityClass, String name)
	{
		int entityID = EntityRegistry.findGlobalUniqueEntityId();
		long seed = name.hashCode();
		Random rand = new Random(seed);
		int primaryColor = rand.nextInt() * 16777215;
		int secondaryColor = rand.nextInt() * 16777215;
		
		EntityRegistry.registerGlobalEntityID(entityClass, name, entityID);
//		EntityRegistry.registerModEntity(entityClass, name, entityID, instance, 64, 1, true);
	}
}
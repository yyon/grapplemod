package com.yyon.grapplinghook;

import java.util.Random;

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

@Mod(modid = grapplemod.MODID, version = grapplemod.VERSION)
public class grapplemod {

	public grapplemod(){}

    public static final String MODID = "grapplemod";
    public static final String VERSION = "1.0";
    
    public static Item grapplebowitem;
    public static Item hookshotitem;
    public static Item enderhookitem;
    public static Item launcheritem;
    public static Item longfallboots;
    
	public static Object instance;
	
	public static SimpleNetworkWrapper network;
	
	@SidedProxy(clientSide="com.yyon.grapplinghook.client.ClientProxyClass", serverSide="com.yyon.grapplinghook.common.CommonProxyClass")
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
	public void serverLoad(FMLServerStartingEvent event){}
	
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
		registerEntity(hookArrow.class, "hookArrow");
		proxy.preInit(event);
		network = NetworkRegistry.INSTANCE.newSimpleChannel("grapplemodchannel");
		network.registerMessage(PlayerPosMessage.Handler.class, PlayerPosMessage.class, 0, Side.CLIENT);
		network.registerMessage(PlayerMovementMessage.Handler.class, PlayerMovementMessage.class, 1, Side.SERVER);
		network.registerMessage(GrappleAttachMessage.Handler.class, GrappleAttachMessage.class, 2, Side.CLIENT);
		network.registerMessage(GrappleEndMessage.Handler.class, GrappleEndMessage.class, 3, Side.CLIENT);
	}
	
	@EventHandler
	public void Init(FMLInitializationEvent event) {
		proxy.init(event, this);
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
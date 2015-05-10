package com.yyon.grapplinghook.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.init.Items;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import com.yyon.grapplinghook.RenderGrappleArrow;
import com.yyon.grapplinghook.grappleArrow;
import com.yyon.grapplinghook.common.CommonProxyClass;

public class ClientProxyClass extends CommonProxyClass {
	public void preInit(FMLPreInitializationEvent event) {
		
	}

	public void init(FMLInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(grappleArrow.class, 
				new RenderGrappleArrow(Minecraft.getMinecraft().getRenderManager(), Items.iron_pickaxe, Minecraft.getMinecraft().getRenderItem()));
		
	}
	
	public static void postInit(FMLPostInitializationEvent event) {
		
	}
}

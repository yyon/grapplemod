package com.yyon.grapplinghook.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import com.yyon.grapplinghook.PlayerMovementMessage;
import com.yyon.grapplinghook.RenderGrappleArrow;
import com.yyon.grapplinghook.grappleArrow;
import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.common.CommonProxyClass;

public class ClientProxyClass extends CommonProxyClass {
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		
	}
	
	@Override
	public void init(FMLInitializationEvent event, grapplemod grapplemod) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(grapplemod.grapplebowitem, 0, new ModelResourceLocation("grapplemod:grapplinghook", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(grapplemod.hookshotitem, 0, new ModelResourceLocation("grapplemod:hookshot", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(grapplemod.launcheritem, 0, new ModelResourceLocation("grapplemod:launcheritem", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(grapplemod.longfallboots, 0, new ModelResourceLocation("grapplemod:longfallboots", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(grapplemod.enderhookitem, 0, new ModelResourceLocation("grapplemod:enderhook", "inventory"));
		RenderingRegistry.registerEntityRenderingHandler(grappleArrow.class, 
				new RenderGrappleArrow(Minecraft.getMinecraft().getRenderManager(), Items.iron_pickaxe, Minecraft.getMinecraft().getRenderItem()));
		
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event) {
		
	}
	
	@Override
	public void sendplayermovementmessage(grappleArrow grappleArrow, int playerid, int arrowid) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if (player.getEntityId() == playerid) {
			grapplemod.network.sendToServer(new PlayerMovementMessage(arrowid, player.moveStrafing, player.moveForward, ((EntityPlayerSP) player).movementInput.jump));
			grappleArrow.receivePlayerMovementMessage(player.moveStrafing, player.moveForward, ((EntityPlayerSP) player).movementInput.jump);
		}
	}
}

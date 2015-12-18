package com.yyon.grapplinghook;

import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class GrappleAttachMessageHandler implements IMessageHandler<GrappleAttachMessage, IMessage> {
    
    @Override
    public IMessage onMessage(GrappleAttachMessage message, MessageContext ctx) {
//        System.out.println(String.format("Received %s from %s", message.text, ctx.getServerHandler().playerEntity.getDisplayName()));
    	World world = grapplemod.proxy.getClientWorld();//Minecraft.getMinecraft().theWorld;
    	Entity grapple = world.getEntityByID(message.id);
    	if (grapple instanceof grappleArrow) {
        	((grappleArrow) grapple).clientAttach(message.x, message.y, message.z);
    	} else {
    		System.out.println("Couldn't find grappleArrow");
    		System.out.println(message.id);
    	}
    	
    	grapplemod.createControl(message.controlid, message.id, message.entityid, world, Vec3.createVectorHelper(message.x, message.y, message.z));
        
    	//            Entity arrowentity = world.getEntityByID(message.arrowId);
//        if (arrowentity instanceof grappleArrow) {
//        	((grappleArrow) arrowentity).receivePlayerMovementMessage(message.strafe, message.forward);
//        }
        return null; // no response in this case
    }
}

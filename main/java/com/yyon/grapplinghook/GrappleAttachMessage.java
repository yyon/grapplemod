package com.yyon.grapplinghook;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

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

public class GrappleAttachMessage implements IMessage {
   
	public int id;
//	public double r;
	public double x;
	public double y;
	public double z;
	public int controlid;
	public int entityid;
//	public double mx;
//	public double my;
//	public double mz;

    public GrappleAttachMessage() { }

    public GrappleAttachMessage(int id, double x, double y, double z, int controlid, int entityid) {
    	this.id = id;
//    	this.r = r;
        this.x = x;
        this.y = y;
        this.z = z;
        this.controlid = controlid;
        this.entityid = entityid;
//        this.mx = mx;
//        this.my = my;
//        this.mz = mz;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    	this.id = buf.readInt();
//    	this.r = buf.readDouble();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.controlid = buf.readInt();
        this.entityid = buf.readInt();
//        this.mx = buf.readDouble();
//        this.my = buf.readDouble();
//        this.mz = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	buf.writeInt(this.id);
//    	buf.writeDouble(this.r);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeInt(this.controlid);
        buf.writeInt(this.entityid);
//        buf.writeDouble(this.mx);
//        buf.writeDouble(this.my);
//        buf.writeDouble(this.mz);
    }

    public static class Handler implements IMessageHandler<GrappleAttachMessage, IMessage> {
    	public class runner implements Runnable {
    		GrappleAttachMessage message;
    		MessageContext ctx;
    		public runner(GrappleAttachMessage message, MessageContext ctx) {
    			super();
    			this.message = message;
    			this.ctx = ctx;
    		}
    		
            @Override
            public void run() {
            	World world = Minecraft.getMinecraft().theWorld;
            	Entity grapple = world.getEntityByID(message.id);
            	if (grapple instanceof grappleArrow) {
	            	((grappleArrow) grapple).clientAttach(message.x, message.y, message.z);
            	} else {
            		System.out.println("Couldn't find grappleArrow");
            		System.out.println(message.id);
            	}
            	
            	grapplemod.createControl(message.controlid, message.id, message.entityid, world, new Vec3(message.x, message.y, message.z));
            }
    	}
    	
       
        @Override
        public IMessage onMessage(GrappleAttachMessage message, MessageContext ctx) {
//            System.out.println(String.format("Received %s from %s", message.text, ctx.getServerHandler().playerEntity.getDisplayName()));
        	IThreadListener mainThread = Minecraft.getMinecraft(); // or Minecraft.getMinecraft() on the client
            mainThread.addScheduledTask(new runner(message, ctx));
            
        	//            Entity arrowentity = world.getEntityByID(message.arrowId);
//            if (arrowentity instanceof grappleArrow) {
//            	((grappleArrow) arrowentity).receivePlayerMovementMessage(message.strafe, message.forward);
//            }
            return null; // no response in this case
        }
    }
}
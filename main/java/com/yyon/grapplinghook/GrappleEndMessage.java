package com.yyon.grapplinghook;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
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

public class GrappleEndMessage implements IMessage {
   
	public int entityid;
	public int arrowid;

    public GrappleEndMessage() { }

    public GrappleEndMessage(int entityid, int arrowid) {
    	this.entityid = entityid;
    	this.arrowid = arrowid;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    	this.entityid = buf.readInt();
    	this.arrowid = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	buf.writeInt(this.entityid);
    	buf.writeInt(this.arrowid);
    }

    public static class Handler implements IMessageHandler<GrappleEndMessage, IMessage> {
    	public class runner implements Runnable {
    		GrappleEndMessage message;
    		MessageContext ctx;
    		public runner(GrappleEndMessage message, MessageContext ctx) {
    			super();
    			this.message = message;
    			this.ctx = ctx;
    		}
    		
            @Override
            public void run() {
            	System.out.println("received grapple end message");
            	
				int id = message.entityid;
				System.out.print("Going to remove attached: ");
				System.out.println(id);
				if (grapplemod.attached.contains(id)) {
					grapplemod.attached.remove(new Integer(id));
				} else {
					System.out.println("Tried to disattach but couldn't");
					System.out.println(grapplemod.attached);
				}
				
				World world = ctx.getServerHandler().playerEntity.worldObj;
              	Entity grapple = world.getEntityByID(message.arrowid);
          		if (grapple instanceof grappleArrow) {
          			((grappleArrow) grapple).removeServer();
          		}
            }
    	}
    	
       
        @Override
        public IMessage onMessage(GrappleEndMessage message, MessageContext ctx) {
//            System.out.println(String.format("Received %s from %s", message.text, ctx.getServerHandler().playerEntity.getDisplayName()));
        	IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj; // or Minecraft.getMinecraft() on the client
            mainThread.addScheduledTask(new runner(message, ctx));
            
        	//            Entity arrowentity = world.getEntityByID(message.arrowId);
//            if (arrowentity instanceof grappleArrow) {
//            	((grappleArrow) arrowentity).receivePlayerMovementMessage(message.strafe, message.forward);
//            }
            return null; // no response in this case
        }
    }
}
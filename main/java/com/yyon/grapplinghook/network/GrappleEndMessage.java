package com.yyon.grapplinghook.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.entities.grappleArrow;

import com.yyon.grapplinghook.network.GrappleAttachMessage.Handler.runner;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

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
            	
				int id = message.entityid;

				if (grapplemod.attached.contains(id)) {
					grapplemod.attached.remove(new Integer(id));
				}
				
				World world = ctx.getServerHandler().playerEntity.worldObj;
              	Entity grapple = world.getEntityByID(message.arrowid);
          		if (grapple instanceof grappleArrow) {
          			((grappleArrow) grapple).removeServer();

          		}
          		
          		Entity entity = world.getEntityByID(id);
          		entity.fallDistance = 0;

            }
    	}
    	
       
        @Override
        public IMessage onMessage(GrappleEndMessage message, MessageContext ctx) {
        	new runner(message, ctx).run();
            return null;
        }
    }
}

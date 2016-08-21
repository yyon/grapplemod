package com.yyon.grapplinghook.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import com.yyon.grapplinghook.entities.grappleArrow;

import com.yyon.grapplinghook.network.PlayerMovementMessage.Handler.runner;

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

public class GrappleAttachPosMessage implements IMessage {
   
	public int id;
	public double x;
	public double y;
	public double z;

    public GrappleAttachPosMessage() { }

    public GrappleAttachPosMessage(int id, double x, double y, double z) {
    	this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    	this.id = buf.readInt();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	buf.writeInt(this.id);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
    }

    public static class Handler implements IMessageHandler<GrappleAttachPosMessage, IMessage> {
    	public class runner implements Runnable {
    		GrappleAttachPosMessage message;
    		MessageContext ctx;
    		public runner(GrappleAttachPosMessage message, MessageContext ctx) {
    			super();
    			this.message = message;
    			this.ctx = ctx;
    		}
    		
            @Override
            public void run() {
            	World world = Minecraft.getMinecraft().theWorld;
            	Entity grapple = world.getEntityByID(message.id);
            	if (grapple instanceof grappleArrow) {
	            	((grappleArrow) grapple).setAttachPos(message.x, message.y, message.z);
            	}
            }
    	}
    	
       
        @Override
        public IMessage onMessage(GrappleAttachPosMessage message, MessageContext ctx) {
        	new runner(message, ctx).run();
            return null;
        }
    }
}

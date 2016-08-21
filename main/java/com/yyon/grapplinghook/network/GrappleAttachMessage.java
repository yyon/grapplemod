package com.yyon.grapplinghook.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.vec;
import com.yyon.grapplinghook.entities.grappleArrow;

import com.yyon.grapplinghook.BlockPos;
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

public class GrappleAttachMessage implements IMessage {
   
	public int id;
	public double x;
	public double y;
	public double z;
	public int controlid;
	public int entityid;
	public int maxlen;
	public BlockPos blockpos;

    public GrappleAttachMessage() { }

    public GrappleAttachMessage(int id, double x, double y, double z, int controlid, int entityid, int maxlen, BlockPos blockpos) {
    	this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.controlid = controlid;
        this.entityid = entityid;
        this.maxlen = maxlen;
        this.blockpos = blockpos;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    	this.id = buf.readInt();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.controlid = buf.readInt();
        this.entityid = buf.readInt();
        this.maxlen = buf.readInt();
        int blockx = buf.readInt();
        int blocky = buf.readInt();
        int blockz = buf.readInt();
        this.blockpos = new BlockPos(blockx, blocky, blockz);
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	buf.writeInt(this.id);
    	buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeInt(this.controlid);
        buf.writeInt(this.entityid);
        buf.writeInt(this.maxlen);
        buf.writeInt(this.blockpos.getX());
        buf.writeInt(this.blockpos.getY());
        buf.writeInt(this.blockpos.getZ());
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
            	}
            	
            	grapplemod.createControl(message.controlid, message.id, message.entityid, world, new vec(message.x, message.y, message.z), message.maxlen, message.blockpos);
            }
    	}
    	
       
        @Override
        public IMessage onMessage(GrappleAttachMessage message, MessageContext ctx) {
        	new runner(message, ctx).run();

            return null;
        }
    }
}

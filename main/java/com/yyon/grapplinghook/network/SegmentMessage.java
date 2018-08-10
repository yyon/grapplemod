package com.yyon.grapplinghook.network;

import com.yyon.grapplinghook.vec;
import com.yyon.grapplinghook.controllers.SegmentHandler;
import com.yyon.grapplinghook.entities.grappleArrow;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
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

public class SegmentMessage implements IMessage {
   
	public int id;
	public boolean add;
	public int index;
	public vec pos;
	public EnumFacing topfacing;
	public EnumFacing bottomfacing;


    public SegmentMessage() { }

    public SegmentMessage(int id, boolean add, int index, vec pos, EnumFacing topfacing, EnumFacing bottomfacing) {
    	this.id = id;
    	this.add = add;
    	this.index = index;
    	this.pos = pos;
    	this.topfacing = topfacing;
    	this.bottomfacing = bottomfacing;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    	this.id = buf.readInt();
    	this.add = buf.readBoolean();
    	this.index = buf.readInt();
    	this.pos = new vec(buf.readDouble(), buf.readDouble(), buf.readDouble());
    	this.topfacing = EnumFacing.getFront(buf.readInt());
    	this.bottomfacing = EnumFacing.getFront(buf.readInt());
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	buf.writeInt(this.id);
    	buf.writeBoolean(this.add);
    	buf.writeInt(this.index);
    	buf.writeDouble(pos.x);
    	buf.writeDouble(pos.y);
    	buf.writeDouble(pos.z);
    	buf.writeInt(this.topfacing.getIndex());
    	buf.writeInt(this.bottomfacing.getIndex());
    }

    public static class Handler implements IMessageHandler<SegmentMessage, IMessage> {
    	public class runner implements Runnable {
    		SegmentMessage message;
    		MessageContext ctx;
    		public runner(SegmentMessage message, MessageContext ctx) {
    			super();
    			this.message = message;
    			this.ctx = ctx;
    		}
    		
            @Override
            public void run() {
            	World world = Minecraft.getMinecraft().world;
            	Entity grapple = world.getEntityByID(message.id);
            	if (grapple == null) {
            		return;
            	}
            	
            	if (grapple instanceof grappleArrow) {
            		SegmentHandler segmenthandler = ((grappleArrow) grapple).segmenthandler;
            		if (message.add) {
            			segmenthandler.actuallyaddsegment(message.index, message.pos, message.bottomfacing, message.topfacing);
            		} else {
            			segmenthandler.removesegment(message.index);
            		}
            	} else {
            	}
            }
    	}
    	
       
        @Override
        public IMessage onMessage(SegmentMessage message, MessageContext ctx) {

        	IThreadListener mainThread = Minecraft.getMinecraft();
            mainThread.addScheduledTask(new runner(message, ctx));

            return null; 
        }
    }
}

package com.yyon.grapplinghook.network;

import java.util.LinkedList;

import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.vec;
import com.yyon.grapplinghook.controllers.SegmentHandler;
import com.yyon.grapplinghook.entities.grappleArrow;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
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
	public double x;
	public double y;
	public double z;
	public int controlid;
	public int entityid;
	public BlockPos blockpos;
	public LinkedList<vec> segments;
	public LinkedList<EnumFacing> segmenttopsides;
	public LinkedList<EnumFacing> segmentbottomsides;
	public GrappleCustomization custom;

    public GrappleAttachMessage() { }

    public GrappleAttachMessage(int id, double x, double y, double z, int controlid, int entityid, BlockPos blockpos, LinkedList<vec> segments, LinkedList<EnumFacing> segmenttopsides, LinkedList<EnumFacing> segmentbottomsides, GrappleCustomization custom) {
    	this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.controlid = controlid;
        this.entityid = entityid;
        this.blockpos = blockpos;
        this.segments = segments;
        this.segmenttopsides = segmenttopsides;
        this.segmentbottomsides = segmentbottomsides;
        this.custom = custom;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    	this.id = buf.readInt();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.controlid = buf.readInt();
        this.entityid = buf.readInt();
        int blockx = buf.readInt();
        int blocky = buf.readInt();
        int blockz = buf.readInt();
        this.blockpos = new BlockPos(blockx, blocky, blockz);
        
        this.custom = new GrappleCustomization();
        this.custom.readFromBuf(buf);
        
        int size = buf.readInt();
        this.segments = new LinkedList<vec>();
        this.segmentbottomsides = new LinkedList<EnumFacing>();
        this.segmenttopsides = new LinkedList<EnumFacing>();

		segments.add(new vec(0, 0, 0));
		segmentbottomsides.add(null);
		segmenttopsides.add(null);
		
		for (int i = 1; i < size-1; i++) {
        	this.segments.add(new vec(buf.readDouble(), buf.readDouble(), buf.readDouble()));
        	this.segmentbottomsides.add(EnumFacing.getFront(buf.readInt()));
        	this.segmenttopsides.add(EnumFacing.getFront(buf.readInt()));
        }
		
		segments.add(new vec(0, 0, 0));
		segmentbottomsides.add(null);
		segmenttopsides.add(null);
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	buf.writeInt(this.id);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeInt(this.controlid);
        buf.writeInt(this.entityid);
        buf.writeInt(this.blockpos.getX());
        buf.writeInt(this.blockpos.getY());
        buf.writeInt(this.blockpos.getZ());
        
        this.custom.writeToBuf(buf);
        
        buf.writeInt(this.segments.size());
        for (int i = 1; i < this.segments.size()-1; i++) {
        	buf.writeDouble(this.segments.get(i).x);
        	buf.writeDouble(this.segments.get(i).y);
        	buf.writeDouble(this.segments.get(i).z);
        	buf.writeInt(this.segmentbottomsides.get(i).getIndex());
        	buf.writeInt(this.segmenttopsides.get(i).getIndex());
        }
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
            	World world = Minecraft.getMinecraft().world;
            	Entity grapple = world.getEntityByID(message.id);
            	if (grapple instanceof grappleArrow) {
	            	((grappleArrow) grapple).clientAttach(message.x, message.y, message.z);
	            	SegmentHandler segmenthandler = ((grappleArrow) grapple).segmenthandler;
	            	segmenthandler.segments = message.segments;
	            	segmenthandler.segmentbottomsides = message.segmentbottomsides;
	            	segmenthandler.segmenttopsides = message.segmenttopsides;
	            	
	            	Entity player = world.getEntityByID(message.entityid);
	            	segmenthandler.forceSetPos(new vec(message.x, message.y, message.z), vec.positionvec(player));
            	} else {
            	}
            	            	
            	grapplemod.proxy.createControl(message.controlid, message.id, message.entityid, world, new vec(message.x, message.y, message.z), message.blockpos, message.custom);
            }
    	}
    	
       
        @Override
        public IMessage onMessage(GrappleAttachMessage message, MessageContext ctx) {

        	IThreadListener mainThread = Minecraft.getMinecraft();
            mainThread.addScheduledTask(new runner(message, ctx));

            return null; 
        }
    }
}

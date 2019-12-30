package com.yyon.grapplinghook.network;

import java.util.LinkedList;
import java.util.function.Supplier;

import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.vec;
import com.yyon.grapplinghook.controllers.SegmentHandler;
import com.yyon.grapplinghook.entities.grappleArrow;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

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

public class GrappleAttachMessage {
   
	public int id;
	public double x;
	public double y;
	public double z;
	public int controlid;
	public int entityid;
	public BlockPos blockpos;
	public LinkedList<vec> segments;
	public LinkedList<Direction> segmenttopsides;
	public LinkedList<Direction> segmentbottomsides;
	public GrappleCustomization custom;

    public GrappleAttachMessage() {}

    public GrappleAttachMessage(int id, double x, double y, double z, int controlid, int entityid, BlockPos blockpos, LinkedList<vec> segments, LinkedList<Direction> segmenttopsides, LinkedList<Direction> segmentbottomsides, GrappleCustomization custom) {
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

    public static GrappleAttachMessage fromBytes(PacketBuffer buf) {
    	GrappleAttachMessage msg = new GrappleAttachMessage();
    	msg.id = buf.readInt();
        msg.x = buf.readDouble();
        msg.y = buf.readDouble();
        msg.z = buf.readDouble();
        msg.controlid = buf.readInt();
        msg.entityid = buf.readInt();
        int blockx = buf.readInt();
        int blocky = buf.readInt();
        int blockz = buf.readInt();
        msg.blockpos = new BlockPos(blockx, blocky, blockz);
        
        msg.custom = new GrappleCustomization();
        msg.custom.readFromBuf(buf);
        
        int size = buf.readInt();
        msg.segments = new LinkedList<vec>();
        msg.segmentbottomsides = new LinkedList<Direction>();
        msg.segmenttopsides = new LinkedList<Direction>();

		msg.segments.add(new vec(0, 0, 0));
		msg.segmentbottomsides.add(null);
		msg.segmenttopsides.add(null);
		
		for (int i = 1; i < size-1; i++) {
        	msg.segments.add(new vec(buf.readDouble(), buf.readDouble(), buf.readDouble()));
        	msg.segmentbottomsides.add(Direction.byIndex(buf.readInt()));
        	msg.segmenttopsides.add(Direction.byIndex(buf.readInt()));
        }
		
		msg.segments.add(new vec(0, 0, 0));
		msg.segmentbottomsides.add(null);
		msg.segmenttopsides.add(null);
		
		return msg;
    }

    public static void toBytes(GrappleAttachMessage pkt, PacketBuffer buf) {
    	buf.writeInt(pkt.id);
        buf.writeDouble(pkt.x);
        buf.writeDouble(pkt.y);
        buf.writeDouble(pkt.z);
        buf.writeInt(pkt.controlid);
        buf.writeInt(pkt.entityid);
        buf.writeInt(pkt.blockpos.getX());
        buf.writeInt(pkt.blockpos.getY());
        buf.writeInt(pkt.blockpos.getZ());
        
        pkt.custom.writeToBuf(buf);
        
        buf.writeInt(pkt.segments.size());
        for (int i = 1; i < pkt.segments.size()-1; i++) {
        	buf.writeDouble(pkt.segments.get(i).x);
        	buf.writeDouble(pkt.segments.get(i).y);
        	buf.writeDouble(pkt.segments.get(i).z);
        	buf.writeInt(pkt.segmentbottomsides.get(i).getIndex());
        	buf.writeInt(pkt.segmenttopsides.get(i).getIndex());
        }
    }

    public static void handle(final GrappleAttachMessage message, Supplier<NetworkEvent.Context> ctx) {
    	ServerPlayerEntity pl = ctx.get().getSender();
        World world = pl.world;
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
		            	
		grapplemod.createControl(message.controlid, message.id, message.entityid, world, new vec(message.x, message.y, message.z), message.blockpos, message.custom);
	}
}

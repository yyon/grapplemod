package com.yyon.grapplinghook.network;

import java.util.function.Supplier;

import com.yyon.grapplinghook.vec;
import com.yyon.grapplinghook.controllers.SegmentHandler;
import com.yyon.grapplinghook.entities.grappleArrow;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
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

public class SegmentMessage {
   
	public int id;
	public boolean add;
	public int index;
	public vec pos;
	public Direction topfacing;
	public Direction bottomfacing;


    public SegmentMessage() { }

    public SegmentMessage(int id, boolean add, int index, vec pos, Direction topfacing, Direction bottomfacing) {
    	this.id = id;
    	this.add = add;
    	this.index = index;
    	this.pos = pos;
    	this.topfacing = topfacing;
    	this.bottomfacing = bottomfacing;
    }

    public static SegmentMessage fromBytes(PacketBuffer buf) {
    	SegmentMessage pkt = new SegmentMessage();
    	pkt.id = buf.readInt();
    	pkt.add = buf.readBoolean();
    	pkt.index = buf.readInt();
    	pkt.pos = new vec(buf.readDouble(), buf.readDouble(), buf.readDouble());
    	pkt.topfacing = Direction.byIndex(buf.readInt());
    	pkt.bottomfacing = Direction.byIndex(buf.readInt());
    	return pkt;
    }

    public static void toBytes(SegmentMessage pkt, PacketBuffer buf) {
    	buf.writeInt(pkt.id);
    	buf.writeBoolean(pkt.add);
    	buf.writeInt(pkt.index);
    	buf.writeDouble(pkt.pos.x);
    	buf.writeDouble(pkt.pos.y);
    	buf.writeDouble(pkt.pos.z);
    	buf.writeInt(pkt.topfacing.getIndex());
    	buf.writeInt(pkt.bottomfacing.getIndex());
    }

    public static void handle(final SegmentMessage message, Supplier<NetworkEvent.Context> ctx) {
    	ServerPlayerEntity pl = ctx.get().getSender();
        World world = pl.world;
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

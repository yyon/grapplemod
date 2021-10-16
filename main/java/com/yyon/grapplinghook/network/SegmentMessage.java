package com.yyon.grapplinghook.network;

import com.yyon.grapplinghook.entities.grapplearrow.GrapplehookEntity;
import com.yyon.grapplinghook.entities.grapplearrow.SegmentHandler;
import com.yyon.grapplinghook.utils.Vec;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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

public class SegmentMessage extends BaseMessageClient {
   
	public int id;
	public boolean add;
	public int index;
	public Vec pos;
	public Direction topfacing;
	public Direction bottomfacing;

    public SegmentMessage(PacketBuffer buf) {
    	super(buf);
    }

    public SegmentMessage(int id, boolean add, int index, Vec pos, Direction topfacing, Direction bottomfacing) {
    	this.id = id;
    	this.add = add;
    	this.index = index;
    	this.pos = pos;
    	this.topfacing = topfacing;
    	this.bottomfacing = bottomfacing;
    }

    public void decode(PacketBuffer buf) {
    	this.id = buf.readInt();
    	this.add = buf.readBoolean();
    	this.index = buf.readInt();
    	this.pos = new Vec(buf.readDouble(), buf.readDouble(), buf.readDouble());
    	this.topfacing = buf.readEnum(Direction.class);
    	this.bottomfacing = buf.readEnum(Direction.class);
    }

    public void encode(PacketBuffer buf) {
    	buf.writeInt(this.id);
    	buf.writeBoolean(this.add);
    	buf.writeInt(this.index);
    	buf.writeDouble(pos.x);
    	buf.writeDouble(pos.y);
    	buf.writeDouble(pos.z);
    	buf.writeEnum(this.topfacing);
    	buf.writeEnum(this.bottomfacing);
    }

    @OnlyIn(Dist.CLIENT)
    public void processMessage(NetworkEvent.Context ctx) {
    	World world = Minecraft.getInstance().level;
    	Entity grapple = world.getEntity(this.id);
    	if (grapple == null) {
    		return;
    	}
    	
    	if (grapple instanceof GrapplehookEntity) {
    		SegmentHandler segmenthandler = ((GrapplehookEntity) grapple).segmenthandler;
    		if (this.add) {
    			segmenthandler.actuallyaddsegment(this.index, this.pos, this.bottomfacing, this.topfacing);
    		} else {
    			segmenthandler.removesegment(this.index);
    		}
    	} else {
    	}
    }
}

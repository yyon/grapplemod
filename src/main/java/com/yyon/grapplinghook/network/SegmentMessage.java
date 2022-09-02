package com.yyon.grapplinghook.network;

import com.yyon.grapplinghook.entities.grapplehook.GrapplehookEntity;
import com.yyon.grapplinghook.entities.grapplehook.SegmentHandler;
import com.yyon.grapplinghook.utils.Vec;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

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
	public Direction topFacing;
	public Direction bottomFacing;

    public SegmentMessage(FriendlyByteBuf buf) {
    	super(buf);
    }

    public SegmentMessage(int id, boolean add, int index, Vec pos, Direction topfacing, Direction bottomfacing) {
    	this.id = id;
    	this.add = add;
    	this.index = index;
    	this.pos = pos;
    	this.topFacing = topfacing;
    	this.bottomFacing = bottomfacing;
    }

    public void decode(FriendlyByteBuf buf) {
    	this.id = buf.readInt();
    	this.add = buf.readBoolean();
    	this.index = buf.readInt();
    	this.pos = new Vec(buf.readDouble(), buf.readDouble(), buf.readDouble());
    	this.topFacing = buf.readEnum(Direction.class);
    	this.bottomFacing = buf.readEnum(Direction.class);
    }

    public void encode(FriendlyByteBuf buf) {
    	buf.writeInt(this.id);
    	buf.writeBoolean(this.add);
    	buf.writeInt(this.index);
    	buf.writeDouble(pos.x);
    	buf.writeDouble(pos.y);
    	buf.writeDouble(pos.z);
    	buf.writeEnum(this.topFacing);
    	buf.writeEnum(this.bottomFacing);
    }

    @OnlyIn(Dist.CLIENT)
    public void processMessage(NetworkEvent.Context ctx) {
    	Level world = Minecraft.getInstance().level;
    	Entity grapple = world.getEntity(this.id);
    	if (grapple == null) {
    		return;
    	}
    	
    	if (grapple instanceof GrapplehookEntity) {
    		SegmentHandler segmenthandler = ((GrapplehookEntity) grapple).segmentHandler;
    		if (this.add) {
    			segmenthandler.actuallyAddSegment(this.index, this.pos, this.bottomFacing, this.topFacing);
    		} else {
    			segmenthandler.removeSegment(this.index);
    		}
    	} else {
    	}
    }
}

package com.yyon.grapplinghook.network;

import com.yyon.grapplinghook.client.ClientProxyInterface;
import com.yyon.grapplinghook.entities.grapplehook.GrapplehookEntity;
import com.yyon.grapplinghook.entities.grapplehook.SegmentHandler;
import com.yyon.grapplinghook.utils.GrappleCustomization;
import com.yyon.grapplinghook.utils.Vec;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.LinkedList;

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

public class GrappleAttachMessage extends BaseMessageClient {
   
	public int id;
	public double x;
	public double y;
	public double z;
	public int controlId;
	public int entityId;
	public BlockPos blockPos;
	public LinkedList<Vec> segments;
	public LinkedList<Direction> segmentTopSides;
	public LinkedList<Direction> segmentBottomSides;
	public GrappleCustomization custom;

    public GrappleAttachMessage(FriendlyByteBuf buf) {
    	super(buf);
    }

    public GrappleAttachMessage(int id, double x, double y, double z, int controlid, int entityid, BlockPos blockpos, LinkedList<Vec> segments, LinkedList<Direction> segmenttopsides, LinkedList<Direction> segmentbottomsides, GrappleCustomization custom) {
    	this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.controlId = controlid;
        this.entityId = entityid;
        this.blockPos = blockpos;
        this.segments = segments;
        this.segmentTopSides = segmenttopsides;
        this.segmentBottomSides = segmentbottomsides;
        this.custom = custom;
    }

    public void decode(FriendlyByteBuf buf) {
    	this.id = buf.readInt();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.controlId = buf.readInt();
        this.entityId = buf.readInt();
        int blockx = buf.readInt();
        int blocky = buf.readInt();
        int blockz = buf.readInt();
        this.blockPos = new BlockPos(blockx, blocky, blockz);
        
        this.custom = new GrappleCustomization();
        this.custom.readFromBuf(buf);
        
        int size = buf.readInt();
        this.segments = new LinkedList<Vec>();
        this.segmentBottomSides = new LinkedList<Direction>();
        this.segmentTopSides = new LinkedList<Direction>();

		segments.add(new Vec(0, 0, 0));
		segmentBottomSides.add(null);
		segmentTopSides.add(null);
		
		for (int i = 1; i < size-1; i++) {
        	this.segments.add(new Vec(buf.readDouble(), buf.readDouble(), buf.readDouble()));
        	this.segmentBottomSides.add(buf.readEnum(Direction.class));
        	this.segmentTopSides.add(buf.readEnum(Direction.class));
        }
		
		segments.add(new Vec(0, 0, 0));
		segmentBottomSides.add(null);
		segmentTopSides.add(null);
    }

    public void encode(FriendlyByteBuf buf) {
    	buf.writeInt(this.id);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeInt(this.controlId);
        buf.writeInt(this.entityId);
        buf.writeInt(this.blockPos.getX());
        buf.writeInt(this.blockPos.getY());
        buf.writeInt(this.blockPos.getZ());
        
        this.custom.writeToBuf(buf);
        
        buf.writeInt(this.segments.size());
        for (int i = 1; i < this.segments.size()-1; i++) {
        	buf.writeDouble(this.segments.get(i).x);
        	buf.writeDouble(this.segments.get(i).y);
        	buf.writeDouble(this.segments.get(i).z);
        	buf.writeEnum(this.segmentBottomSides.get(i));
        	buf.writeEnum(this.segmentTopSides.get(i));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void processMessage(NetworkEvent.Context ctx) {
		Level world = Minecraft.getInstance().level;
    	Entity grapple = world.getEntity(this.id);
    	if (grapple instanceof GrapplehookEntity) {
        	((GrapplehookEntity) grapple).clientAttach(this.x, this.y, this.z);
        	SegmentHandler segmenthandler = ((GrapplehookEntity) grapple).segmentHandler;
        	segmenthandler.segments = this.segments;
        	segmenthandler.segmentBottomSides = this.segmentBottomSides;
        	segmenthandler.segmentTopSides = this.segmentTopSides;
        	
        	Entity player = world.getEntity(this.entityId);
        	segmenthandler.forceSetPos(new Vec(this.x, this.y, this.z), Vec.positionVec(player));
    	} else {
    	}
    	            	
    	ClientProxyInterface.proxy.createControl(this.controlId, this.id, this.entityId, world, new Vec(this.x, this.y, this.z), this.blockPos, this.custom);
    }
}

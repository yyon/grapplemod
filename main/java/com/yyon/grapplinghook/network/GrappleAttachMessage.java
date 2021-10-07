package com.yyon.grapplinghook.network;

import java.util.LinkedList;

import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.vec;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
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

public class GrappleAttachMessage extends BaseMessageClient {
   
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

    public GrappleAttachMessage(PacketBuffer buf) {
    	super(buf);
    }

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

    public void decode(PacketBuffer buf) {
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
        this.segmentbottomsides = new LinkedList<Direction>();
        this.segmenttopsides = new LinkedList<Direction>();

		segments.add(new vec(0, 0, 0));
		segmentbottomsides.add(null);
		segmenttopsides.add(null);
		
		for (int i = 1; i < size-1; i++) {
        	this.segments.add(new vec(buf.readDouble(), buf.readDouble(), buf.readDouble()));
        	this.segmentbottomsides.add(Direction.from2DDataValue(buf.readInt()));
        	this.segmenttopsides.add(Direction.from2DDataValue(buf.readInt()));
        }
		
		segments.add(new vec(0, 0, 0));
		segmentbottomsides.add(null);
		segmenttopsides.add(null);
    }

    public void encode(PacketBuffer buf) {
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
        	buf.writeInt(this.segmentbottomsides.get(i).get2DDataValue());
        	buf.writeInt(this.segmenttopsides.get(i).get2DDataValue());
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void processMessage(NetworkEvent.Context ctx) {
    	/*
    	World world = Minecraft.getInstance().level;
    	Entity grapple = world.getEntity(this.id);
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
    	*/
    }
}

package com.yyon.grapplinghook.network;

import java.util.LinkedList;

import com.yyon.grapplinghook.client.ClientProxyInterface;
import com.yyon.grapplinghook.entities.grapplearrow.SegmentHandler;
import com.yyon.grapplinghook.entities.grapplearrow.GrapplehookEntity;
import com.yyon.grapplinghook.utils.GrappleCustomization;
import com.yyon.grapplinghook.utils.Vec;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
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

public class GrappleAttachMessage extends BaseMessageClient {
   
	public int id;
	public double x;
	public double y;
	public double z;
	public int controlid;
	public int entityid;
	public BlockPos blockpos;
	public LinkedList<Vec> segments;
	public LinkedList<Direction> segmenttopsides;
	public LinkedList<Direction> segmentbottomsides;
	public GrappleCustomization custom;

    public GrappleAttachMessage(PacketBuffer buf) {
    	super(buf);
    }

    public GrappleAttachMessage(int id, double x, double y, double z, int controlid, int entityid, BlockPos blockpos, LinkedList<Vec> segments, LinkedList<Direction> segmenttopsides, LinkedList<Direction> segmentbottomsides, GrappleCustomization custom) {
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
        this.segments = new LinkedList<Vec>();
        this.segmentbottomsides = new LinkedList<Direction>();
        this.segmenttopsides = new LinkedList<Direction>();

		segments.add(new Vec(0, 0, 0));
		segmentbottomsides.add(null);
		segmenttopsides.add(null);
		
		for (int i = 1; i < size-1; i++) {
        	this.segments.add(new Vec(buf.readDouble(), buf.readDouble(), buf.readDouble()));
        	this.segmentbottomsides.add(buf.readEnum(Direction.class));
        	this.segmenttopsides.add(buf.readEnum(Direction.class));
        }
		
		segments.add(new Vec(0, 0, 0));
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
        	buf.writeEnum(this.segmentbottomsides.get(i));
        	buf.writeEnum(this.segmenttopsides.get(i));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void processMessage(NetworkEvent.Context ctx) {
		World world = Minecraft.getInstance().level;
    	Entity grapple = world.getEntity(this.id);
    	if (grapple instanceof GrapplehookEntity) {
        	((GrapplehookEntity) grapple).clientAttach(this.x, this.y, this.z);
        	SegmentHandler segmenthandler = ((GrapplehookEntity) grapple).segmenthandler;
        	segmenthandler.segments = this.segments;
        	segmenthandler.segmentbottomsides = this.segmentbottomsides;
        	segmenthandler.segmenttopsides = this.segmenttopsides;
        	
        	Entity player = world.getEntity(this.entityid);
        	segmenthandler.forceSetPos(new Vec(this.x, this.y, this.z), Vec.positionvec(player));
    	} else {
    	}
    	            	
    	ClientProxyInterface.proxy.createControl(this.controlid, this.id, this.entityid, world, new Vec(this.x, this.y, this.z), this.blockpos, this.custom);
    }
}

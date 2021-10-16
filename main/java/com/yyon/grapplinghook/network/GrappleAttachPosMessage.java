package com.yyon.grapplinghook.network;

import com.yyon.grapplinghook.entities.grapplehook.GrapplehookEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
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

public class GrappleAttachPosMessage extends BaseMessageClient {
   
	public int id;
	public double x;
	public double y;
	public double z;

    public GrappleAttachPosMessage(PacketBuffer buf) {
    	super(buf);
    }

    public GrappleAttachPosMessage(int id, double x, double y, double z) {
    	this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void decode(PacketBuffer buf) {
    	this.id = buf.readInt();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
    }

    public void encode(PacketBuffer buf) {
    	buf.writeInt(this.id);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
    }

    @OnlyIn(Dist.CLIENT)
    public void processMessage(NetworkEvent.Context ctx) {
    	World world = Minecraft.getInstance().level;
    	Entity grapple = world.getEntity(this.id);
    	if (grapple instanceof GrapplehookEntity) {
        	((GrapplehookEntity) grapple).setAttachPos(this.x, this.y, this.z);
    	}
    }
}

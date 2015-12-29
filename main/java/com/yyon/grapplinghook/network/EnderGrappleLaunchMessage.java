package com.yyon.grapplinghook.network;

import com.yyon.grapplinghook.grapplemod;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

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

public class EnderGrappleLaunchMessage implements IMessage {
   
	public int id;
	public boolean leftclick;
//	public double r;
	public double x;
	public double y;
	public double z;
//	public double mx;
//	public double my;
//	public double mz;

    public EnderGrappleLaunchMessage() { }

    public EnderGrappleLaunchMessage(int id, double x, double y, double z) {
    	this.id = id;
//    	this.r = r;
        this.x = x;
        this.y = y;
        this.z = z;
//        this.mx = mx;
//        this.my = my;
//        this.mz = mz;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    	this.id = buf.readInt();
    	this.leftclick = buf.readBoolean();
//    	this.r = buf.readDouble();
       this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
//        this.mx = buf.readDouble();
//        this.my = buf.readDouble();
//        this.mz = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	buf.writeInt(this.id);
    	buf.writeBoolean(this.leftclick);
//    	buf.writeDouble(this.r);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
//        buf.writeDouble(this.mx);
//        buf.writeDouble(this.my);
//        buf.writeDouble(this.mz);
    }

    public static class Handler implements IMessageHandler<EnderGrappleLaunchMessage, IMessage> {
        @Override
        public IMessage onMessage(EnderGrappleLaunchMessage message, MessageContext ctx) {
//            System.out.println(String.format("Received %s from %s", message.text, ctx.getServerHandler().playerEntity.getDisplayName()));
        	grapplemod.receiveEnderLaunch(message.id, message.x, message.y, message.z);
            
        	//            Entity arrowentity = world.getEntityByID(message.arrowId);
//            if (arrowentity instanceof grappleArrow) {
//            	((grappleArrow) arrowentity).receivePlayerMovementMessage(message.strafe, message.forward);
//            }
            return null; // no response in this case
        }
    }
}
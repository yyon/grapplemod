package com.yyon.grapplinghook.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

import com.yyon.grapplinghook.grapplemod;

import net.minecraft.util.IThreadListener;
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

public class GrappleClickMessage implements IMessage {
   
	public int id;
	public boolean leftclick;

    public GrappleClickMessage() { }

    public GrappleClickMessage(int id, boolean leftclick) {
    	this.id = id;
    	this.leftclick = leftclick;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    	this.id = buf.readInt();
    	this.leftclick = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	buf.writeInt(this.id);
    	buf.writeBoolean(this.leftclick);
    }

    public static class Handler implements IMessageHandler<GrappleClickMessage, IMessage> {
    	public class runner implements Runnable {
    		GrappleClickMessage message;
    		MessageContext ctx;
    		public runner(GrappleClickMessage message, MessageContext ctx) {
    			super();
    			this.message = message;
    			this.ctx = ctx;
    		}
    		
            @Override
            public void run() {
            	grapplemod.receiveGrappleClick(message.id, message.leftclick);
            }
    	}
    	
       
        @Override
        public IMessage onMessage(GrappleClickMessage message, MessageContext ctx) {

        	IThreadListener mainThread = Minecraft.getMinecraft();
            mainThread.addScheduledTask(new runner(message, ctx));
            
            return null;
        }
    }
}

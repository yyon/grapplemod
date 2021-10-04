package com.yyon.grapplinghook.network;

import com.yyon.grapplinghook.grapplemod;
//* // 1.8 Compatability
import com.yyon.grapplinghook.items.KeypressItem;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
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

public class KeypressMessage implements IMessage {
	
	KeypressItem.Keys key;
	boolean isDown;

    public KeypressMessage() { }

    public KeypressMessage(KeypressItem.Keys thekey, boolean isDown) {
    	this.key = thekey;
    	this.isDown = isDown;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    	this.key = KeypressItem.Keys.values()[buf.readInt()];
    	this.isDown = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	buf.writeInt(this.key.ordinal());
    	buf.writeBoolean(this.isDown);
    }

    public static class Handler implements IMessageHandler<KeypressMessage, IMessage> {
    	public class runner implements Runnable {
    		KeypressMessage message;
    		MessageContext ctx;
    		public runner(KeypressMessage message, MessageContext ctx) {
    			super();
    			this.message = message;
    			this.ctx = ctx;
    		}
    		
            @Override
            public void run() {
				World w = ctx.getServerHandler().player.world;
				
				grapplemod.receiveKeypress(ctx.getServerHandler().player, this.message.key, this.message.isDown);
            }
    	}
    	
        @Override
        public IMessage onMessage(KeypressMessage message, MessageContext ctx) {

        	IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world; // or Minecraft.getMinecraft() on the client
            mainThread.addScheduledTask(new runner(message, ctx));

            return null; // no response in this case
        }
    }
}

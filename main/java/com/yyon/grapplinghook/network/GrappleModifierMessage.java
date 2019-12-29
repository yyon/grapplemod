package com.yyon.grapplinghook.network;

import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.blocks.TileEntityGrappleModifier;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.ServerWorld;
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

public class GrappleModifierMessage implements IMessage {
   
	public BlockPos pos;
	public GrappleCustomization custom;

    public GrappleModifierMessage() { }

    public GrappleModifierMessage(BlockPos pos, GrappleCustomization custom) {
    	this.pos = pos;
    	this.custom = custom;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    	this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
    	this.custom = new GrappleCustomization();
    	this.custom.readFromBuf(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	buf.writeInt(this.pos.getX());
    	buf.writeInt(this.pos.getY());
    	buf.writeInt(this.pos.getZ());
    	this.custom.writeToBuf(buf);
    }

    public static class Handler implements IMessageHandler<GrappleModifierMessage, IMessage> {
    	public class runner implements Runnable {
    		GrappleModifierMessage message;
    		MessageContext ctx;
    		public runner(GrappleModifierMessage message, MessageContext ctx) {
    			super();
    			this.message = message;
    			this.ctx = ctx;
    		}
    		
            @Override
            public void run() {
				World w = ctx.getServerHandler().player.world;
				
				TileEntity ent = w.getTileEntity(message.pos);
				
				if (ent instanceof TileEntityGrappleModifier) {
					((TileEntityGrappleModifier) ent).setCustomizationServer(message.custom);
				}
            }
    	}
    	
       
        @Override
        public IMessage onMessage(GrappleModifierMessage message, MessageContext ctx) {

        	IThreadListener mainThread = (ServerWorld) ctx.getServerHandler().player.world; // or Minecraft.getMinecraft() on the client
            mainThread.addScheduledTask(new runner(message, ctx));

            return null; // no response in this case
        }
    }
}

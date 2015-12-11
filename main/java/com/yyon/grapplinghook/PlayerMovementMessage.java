package com.yyon.grapplinghook;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;


public class PlayerMovementMessage implements IMessage {
   
	public int entityId;
	public double x;
	public double y;
	public double z;
	public double mx;
	public double my;
	public double mz;
	
	public PlayerMovementMessage() {
	}
	
    public PlayerMovementMessage(int entityId, double x, double y, double z, double mx, double my, double mz) {
    	this.entityId = entityId;
    	this.x = x;
    	this.y = y;
    	this.z = z;
    	this.mx = mx;
    	this.my = my;
    	this.mz = mz;
    	
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    	this.entityId = buf.readInt();
    	this.x = buf.readDouble();
    	this.y = buf.readDouble();
    	this.z = buf.readDouble();
    	this.mx = buf.readDouble();
    	this.my = buf.readDouble();
    	this.mz = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeDouble(mx);
        buf.writeDouble(my);
        buf.writeDouble(mz);
    }

    public static class Handler implements IMessageHandler<PlayerMovementMessage, IMessage> {
       
    	public class runner implements Runnable {
    		PlayerMovementMessage message;
    		MessageContext ctx;
    		public runner(PlayerMovementMessage message, MessageContext ctx) {
    			super();
    			this.message = message;
    			this.ctx = ctx;
    		}
    		
            @Override
            public void run() {
                World world = ctx.getServerHandler().playerEntity.worldObj;
                Entity entity = world.getEntityByID(message.entityId);
//                entity.setPositionAndUpdate(message.x, message.y, message.z);
                entity.posX = message.x;
                entity.posY = message.y;
                entity.posZ = message.z;
                entity.motionX = message.mx;
                entity.motionY = message.my;
                entity.motionZ = message.mz;
            }
    	}
    	
        @Override
        public IMessage onMessage(PlayerMovementMessage message, MessageContext ctx) {
//            System.out.println(String.format("Received %s from %s", message.text, ctx.getServerHandler().playerEntity.getDisplayName()));
        	IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj; // or Minecraft.getMinecraft() on the client
            mainThread.addScheduledTask(new runner(message, ctx));
            return null; // no response in this case
        }
    }
}
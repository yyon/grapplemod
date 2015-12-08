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
   
    private int arrowId;
    private double strafe;
    private double forward;
    private boolean jump;

    public PlayerMovementMessage() { }

    public PlayerMovementMessage(int arrowId, double strafe, double forward, boolean jump) {
        this.arrowId = arrowId;
        this.strafe = strafe;
        this.forward = forward;
        this.jump = jump;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        arrowId = buf.readInt();
        strafe = buf.readDouble();
        forward = buf.readDouble();
        jump = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(arrowId);
        buf.writeDouble(strafe);
        buf.writeDouble(forward);
        buf.writeBoolean(jump);
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
                Entity arrowentity = world.getEntityByID(message.arrowId);
                if (arrowentity instanceof grappleArrow) {
                	((grappleArrow) arrowentity).receivePlayerMovementMessage(message.strafe, message.forward, message.jump);
                }
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
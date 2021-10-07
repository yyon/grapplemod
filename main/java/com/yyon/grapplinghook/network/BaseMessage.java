package com.yyon.grapplinghook.network;

import java.util.function.Supplier;

import com.yyon.grapplinghook.grapplemod;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

public abstract class BaseMessage {
	public BaseMessage(PacketBuffer buf) {
		this.decode(buf);
	}
	
	public BaseMessage() {
	}
	
	public abstract void decode(PacketBuffer buf);
	
	public abstract void encode(PacketBuffer buf);

    public abstract void processMessage(NetworkEvent.Context ctx);
    
    public void onMessageReceivedServer(Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
        if (sideReceived != LogicalSide.SERVER) {
			grapplemod.LOGGER.warn("message received on wrong side:" + ctx.getDirection().getReceptionSide());
			return;
        }
        
        ctx.setPacketHandled(true);
        
        final ServerPlayerEntity sendingPlayer = ctx.getSender();
        if (sendingPlayer == null) {
        	grapplemod.LOGGER.warn("EntityPlayerMP was null when message was received");
        }

        ctx.enqueueWork(() -> processMessage(ctx));
    }

    public void onMessageReceivedClient(Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
        if (sideReceived != LogicalSide.CLIENT) {
			grapplemod.LOGGER.warn("message received on wrong side:" + ctx.getDirection().getReceptionSide());
			return;
        }
        
        ctx.setPacketHandled(true);
        
        ctx.enqueueWork(() -> 
        	DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> processMessage(ctx))
        );
    }
}

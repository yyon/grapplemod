package com.yyon.grapplinghook.network;

import java.util.function.Supplier;

import com.yyon.grapplinghook.grapplemod;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

public abstract class BaseMessageServer {
	public BaseMessageServer(PacketBuffer buf) {
		this.decode(buf);
	}
	
	public BaseMessageServer() {
	}
	
	public abstract void decode(PacketBuffer buf);
	
	public abstract void encode(PacketBuffer buf);

    public abstract void processMessage(NetworkEvent.Context ctx);
    
    public void onMessageReceived(Supplier<NetworkEvent.Context> ctxSupplier) {
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
}

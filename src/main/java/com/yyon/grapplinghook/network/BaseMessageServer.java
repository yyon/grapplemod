package com.yyon.grapplinghook.network;

import com.yyon.grapplinghook.GrappleMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class BaseMessageServer {
	public BaseMessageServer(FriendlyByteBuf buf) {
		this.decode(buf);
	}
	
	public BaseMessageServer() {
	}
	
	public abstract void decode(FriendlyByteBuf buf);
	
	public abstract void encode(FriendlyByteBuf buf);

    public abstract void processMessage(NetworkEvent.Context ctx);
    
    public void onMessageReceived(Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
        if (sideReceived != LogicalSide.SERVER) {
			GrappleMod.LOGGER.warn("message received on wrong side:" + ctx.getDirection().getReceptionSide());
			return;
        }
        
        ctx.setPacketHandled(true);
        
        final ServerPlayer sendingPlayer = ctx.getSender();
        if (sendingPlayer == null) {
        	GrappleMod.LOGGER.warn("EntityPlayerMP was null when message was received");
        }

        ctx.enqueueWork(() -> processMessage(ctx));
    }
}

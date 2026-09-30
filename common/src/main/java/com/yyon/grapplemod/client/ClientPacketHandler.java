package com.yyon.grapplemod.client;

import com.yyon.grapplemod.entities.grapplehook.GrapplehookEntity;
import com.yyon.grapplemod.entities.grapplehook.SegmentHandler;
import com.yyon.grapplemod.network.DetachSingleHookMessage;
import com.yyon.grapplemod.network.GrappleAttachMessage;
import com.yyon.grapplemod.network.GrappleAttachPosMessage;
import com.yyon.grapplemod.network.GrappleDetachMessage;
import com.yyon.grapplemod.network.SegmentMessage;
import com.yyon.grapplemod.utils.Vec;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ClientPacketHandler {
	public static void handleGrappleAttach(GrappleAttachMessage msg) {
		Level world = Minecraft.getInstance().level;
		if (world == null) {
			return;
		}
    	Entity grapple = world.getEntity(msg.id);
    	if (grapple instanceof GrapplehookEntity) {
        	((GrapplehookEntity) grapple).clientAttach(msg.x, msg.y, msg.z);
        	SegmentHandler segmenthandler = ((GrapplehookEntity) grapple).segmentHandler;
        	segmenthandler.segments = msg.segments;
        	segmenthandler.segmentBottomSides = msg.segmentBottomSides;
        	segmenthandler.segmentTopSides = msg.segmentTopSides;

        	Entity player = world.getEntity(msg.entityId);
        	segmenthandler.forceSetPos(new Vec(msg.x, msg.y, msg.z), Vec.positionVec(player));
    	}

    	ClientProxyInterface.proxy.createControl(msg.controlId, msg.id, msg.entityId, world, new Vec(msg.x, msg.y, msg.z), msg.blockPos, msg.custom);
	}

	public static void handleGrappleAttachPos(GrappleAttachPosMessage msg) {
    	Level world = Minecraft.getInstance().level;
		if (world == null) {
			return;
		}
    	Entity grapple = world.getEntity(msg.id);
    	if (grapple instanceof GrapplehookEntity) {
        	((GrapplehookEntity) grapple).setAttachPos(msg.x, msg.y, msg.z);
    	}
	}

	public static void handleGrappleDetach(GrappleDetachMessage msg) {
    	ClientControllerManager.receiveGrappleDetach(msg.id);
	}

	public static void handleDetachSingleHook(DetachSingleHookMessage msg) {
    	ClientControllerManager.receiveGrappleDetachHook(msg.id, msg.hookid);
	}

	public static void handleSegment(SegmentMessage msg) {
    	Level world = Minecraft.getInstance().level;
		if (world == null) {
			return;
		}
    	Entity grapple = world.getEntity(msg.id);
    	if (grapple instanceof GrapplehookEntity) {
    		SegmentHandler segmenthandler = ((GrapplehookEntity) grapple).segmentHandler;
    		if (msg.add) {
    			segmenthandler.actuallyAddSegment(msg.index, msg.pos, msg.bottomFacing, msg.topFacing);
    		} else {
    			segmenthandler.removeSegment(msg.index);
    		}
    	}
	}
}

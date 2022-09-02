package com.yyon.grapplinghook.server;

import com.yyon.grapplinghook.entities.grapplehook.GrapplehookEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.HashSet;

public class ServerControllerManager {
	public static HashSet<Integer> attached = new HashSet<Integer>(); // server side
	public static HashMap<Integer, HashSet<GrapplehookEntity>> allGrapplehookEntities = new HashMap<Integer, HashSet<GrapplehookEntity>>(); // server side

	public static void addGrapplehookEntity(int id, GrapplehookEntity hookEntity) {
		if (!allGrapplehookEntities.containsKey(id)) {
			allGrapplehookEntities.put(id, new HashSet<GrapplehookEntity>());
		}
		allGrapplehookEntities.get(id).add(hookEntity);
	}
	
	public static void removeAllMultiHookGrapplehookEntities(int id) {
		if (!allGrapplehookEntities.containsKey(id)) {
			allGrapplehookEntities.put(id, new HashSet<GrapplehookEntity>());
		}
		for (GrapplehookEntity hookEntity : allGrapplehookEntities.get(id)) {
			if (hookEntity != null && hookEntity.isAlive()) {
				hookEntity.removeServer();
			}
		}
		allGrapplehookEntities.put(id, new HashSet<GrapplehookEntity>());
	}
	
	public static void receiveGrappleEnd(int id, Level world, HashSet<Integer> hookEntityIds) {
		if (attached.contains(id)) {
			attached.remove(id);
		} else {
		}
		
		for (int hookEntityId : hookEntityIds) {
	      	Entity grapple = world.getEntity(hookEntityId);
	  		if (grapple instanceof GrapplehookEntity) {
	  			((GrapplehookEntity) grapple).removeServer();
	  		} else {
	
	  		}
		}
  		
  		Entity entity = world.getEntity(id);
  		if (entity != null) {
      		entity.fallDistance = 0;
  		}
  		
  		removeAllMultiHookGrapplehookEntities(id);
	}
}

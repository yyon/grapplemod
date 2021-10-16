package com.yyon.grapplinghook.server;

import java.util.HashMap;
import java.util.HashSet;

import com.yyon.grapplinghook.entities.grapplearrow.GrapplehookEntity;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class ServerControllerManager {
	public static HashSet<Integer> attached = new HashSet<Integer>(); // server side
	public static HashMap<Integer, HashSet<GrapplehookEntity>> allarrows = new HashMap<Integer, HashSet<GrapplehookEntity>>(); // server side

	public static void addarrow(int id, GrapplehookEntity arrow) {
		if (!allarrows.containsKey(id)) {
			allarrows.put(id, new HashSet<GrapplehookEntity>());
		}
		allarrows.get(id).add(arrow);
	}
	
	public static void removeallmultihookarrows(int id) {
		if (!allarrows.containsKey(id)) {
			allarrows.put(id, new HashSet<GrapplehookEntity>());
		}
		for (GrapplehookEntity arrow : allarrows.get(id)) {
			if (arrow != null && arrow.isAlive()) {
				arrow.removeServer();
			}
		}
		allarrows.put(id, new HashSet<GrapplehookEntity>());
	}
	
	public static void receiveGrappleEnd(int id, World world, HashSet<Integer> arrowIds) {
		if (attached.contains(id)) {
			attached.remove(id);
		} else {
		}
		
		for (int arrowid : arrowIds) {
	      	Entity grapple = world.getEntity(arrowid);
	  		if (grapple instanceof GrapplehookEntity) {
	  			((GrapplehookEntity) grapple).removeServer();
	  		} else {
	
	  		}
		}
  		
  		Entity entity = world.getEntity(id);
  		if (entity != null) {
      		entity.fallDistance = 0;
  		}
  		
  		removeallmultihookarrows(id);
	}
}

package com.yyon.grapplemod.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.HashSet;

public class GrappleConfigUtils {
	private static boolean anyBlocks = true;
	private static HashSet<Block> grapplingBlocks;
	private static boolean removeBlocks = false;
	private static HashSet<Block> grapplingBreaksBlocks;
	private static boolean anyBreakBlocks = false;

	public static HashSet<Block> stringToBlocks(String s) {
		HashSet<Block> blocks = new HashSet<Block>();

		if (s.equals("") || s.equals("none") || s.equals("any")) {
			return blocks;
		}

		String[] blockstr = s.split(",");

	    for(String str:blockstr){
	    	str = str.trim();
	    	String modid;
	    	String name;
	    	if (str.contains(":")) {
	    		String[] splitstr = str.split(":");
	    		modid = splitstr[0];
	    		name = splitstr[1];
	    	} else {
	    		modid = "minecraft";
	    		name = str;
	    	}

	    	ResourceLocation loc = ResourceLocation.tryBuild(modid, name);
	    	if (loc != null && BuiltInRegistries.BLOCK.containsKey(loc)) {
	    		blocks.add(BuiltInRegistries.BLOCK.get(loc));
	    	}
	    }

	    return blocks;
	}

	public static void updateGrapplingBlocks() {
		GrappleConfig.Config.GrapplingHook.Blocks config = GrappleConfig.getConf().grapplinghook.blocks;
		prevGrapplingBlocks = config.grapplingBlocks;
		prevGrapplingNonBlocks = config.grapplingNonBlocks;
		prevGrapplingBreakBlocks = config.grappleBreakBlocks;

		String s = config.grapplingBlocks;
		if (s.equals("any") || s.equals("")) {
			s = config.grapplingNonBlocks;
			if (s.equals("none") || s.equals("")) {
				anyBlocks = true;
			} else {
				anyBlocks = false;
				removeBlocks = true;
			}
		} else {
			anyBlocks = false;
			removeBlocks = false;
		}

		if (!anyBlocks) {
			grapplingBlocks = stringToBlocks(s);
		}

		grapplingBreaksBlocks = stringToBlocks(config.grappleBreakBlocks);
		anyBreakBlocks = grapplingBreaksBlocks.size() != 0;
	}

	private static String prevGrapplingBlocks = null;
	private static String prevGrapplingNonBlocks = null;
	public static boolean attachesBlock(Block block) {
		if (!GrappleConfig.getConf().grapplinghook.blocks.grapplingBlocks.equals(prevGrapplingBlocks) || !GrappleConfig.getConf().grapplinghook.blocks.grapplingNonBlocks.equals(prevGrapplingNonBlocks)) {
			updateGrapplingBlocks();
		}

		if (anyBlocks) {
			return true;
		}

		boolean inlist = grapplingBlocks.contains(block);

		if (removeBlocks) {
			return !inlist;
		} else {
			return inlist;
		}
	}

	private static String prevGrapplingBreakBlocks = null;
	public static boolean breaksBlock(Block block) {
		if (!GrappleConfig.getConf().grapplinghook.blocks.grappleBreakBlocks.equals(prevGrapplingBreakBlocks)) {
			updateGrapplingBlocks();
		}

		if (!anyBreakBlocks) {
			return false;
		}

		return grapplingBreaksBlocks.contains(block);
	}
}

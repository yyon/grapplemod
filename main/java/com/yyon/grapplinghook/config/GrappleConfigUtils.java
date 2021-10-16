package com.yyon.grapplinghook.config;

import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class GrappleConfigUtils {
	private static boolean anyblocks = true;
	private static HashSet<Block> grapplingblocks;
	private static boolean removeblocks = false;
	private static HashSet<Block> grapplingbreaksblocks;
	private static boolean anybreakblocks = false;

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
	    	
	    	Block b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(modid, name));
	    	
	    	blocks.add(b);
	    }
	    
	    return blocks;
	}
	
	public static void updateGrapplingBlocks() {
		String s = GrappleConfig.getconf().grapplinghook.blocks.grapplingBlocks;
		if (s.equals("any") || s.equals("")) {
			s = GrappleConfig.getconf().grapplinghook.blocks.grapplingNonBlocks;
			if (s.equals("none") || s.equals("")) {
				anyblocks = true;
			} else {
				anyblocks = false;
				removeblocks = true;
			}
		} else {
			anyblocks = false;
			removeblocks = false;
		}
	
		if (!anyblocks) {
			grapplingblocks = stringToBlocks(s);
		}
		
		grapplingbreaksblocks = stringToBlocks(GrappleConfig.getconf().grapplinghook.blocks.grappleBreakBlocks);
		anybreakblocks = grapplingbreaksblocks.size() != 0;
		
	}

	private static String prevGrapplingBlocks = null;
	private static String prevGrapplingNonBlocks = null;
	public static boolean attachesblock(Block block) {
		if (!GrappleConfig.getconf().grapplinghook.blocks.grapplingBlocks.equals(prevGrapplingBlocks) || !GrappleConfig.getconf().grapplinghook.blocks.grapplingNonBlocks.equals(prevGrapplingNonBlocks)) {
			updateGrapplingBlocks();
		}
		
		if (anyblocks) {
			return true;
		}
		
		boolean inlist = grapplingblocks.contains(block);
		
		if (removeblocks) {
			return !inlist;
		} else {
			return inlist;
		}
	}

	private static String prevGrapplingBreakBlocks = null;
	public static boolean breaksblock(Block block) {
		if (!GrappleConfig.getconf().grapplinghook.blocks.grappleBreakBlocks.equals(prevGrapplingBreakBlocks)) {
			updateGrapplingBlocks();
		}
		
		if (!anybreakblocks) {
			return false;
		}
		
		return grapplingbreaksblocks.contains(block);
	}

	public static Rarity getRarityFromInt(int rarity_int) {
		Rarity[] rarities = (new Rarity[] {Rarity.VERY_RARE, Rarity.RARE, Rarity.UNCOMMON, Rarity.COMMON});
		if (rarity_int < 0) {rarity_int = 0;}
		if (rarity_int >= rarities.length) {rarity_int = rarities.length-1;}
		return rarities[rarity_int];
	}
}

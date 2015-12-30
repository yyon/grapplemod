package com.yyon.grapplinghook;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class BlockPos {
	public int x;
	public int y;
	public int z;
	
	public BlockPos(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public boolean equals(Object obj) {
	    if (obj == null) {
	        return false;
	    }
	    if (getClass() != obj.getClass()) {
	        return false;
	    }
	    final BlockPos other = (BlockPos) obj;
	    if (this.x != other.x) {return false;}
	    if (this.y != other.y) {return false;}
	    if (this.z != other.z) {return false;}
	    return true;
	}
	
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getZ() {
		return z;
	}
	
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
            // if deriving: appendSuper(super.hashCode()).
            append(this.x).
            append(this.y).
            append(this.z).
            toHashCode();
    }
}

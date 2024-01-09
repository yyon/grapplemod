package com.yyon.grapplinghook.entities;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import com.yyon.grapplinghook.grapplemod;

/*
 * This file is part of GrappleMod.

    GrappleMod is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GrappleMod is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GrappleMod.  If not, see <http://www.gnu.org/licenses/>.
 */

public class smartHookArrow extends grappleArrow
{
	public boolean slow = false;
	
	public smartHookArrow(World worldIn) {
		super(worldIn);
	}
	
	public smartHookArrow(World worldIn, EntityLivingBase shooter,
			boolean righthand, boolean slow) {
		super(worldIn, shooter, righthand);
		this.slow = slow;
	}
	
	@Override
    public void writeSpawnData(ByteBuf data)
    {
		super.writeSpawnData(data);
		data.writeBoolean(this.slow);
    }
	
	@Override
    public void readSpawnData(ByteBuf data)
    {
		super.readSpawnData(data);
		this.slow = data.readBoolean();
    }

	@Override
    public float func_70182_d()
    {
        return 20F;
    }
    
    @Override
	public int getControlId() {
		return grapplemod.SMARTHOOKID;
	}
}

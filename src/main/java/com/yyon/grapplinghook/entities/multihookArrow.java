package com.yyon.grapplinghook.entities;

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

public class multihookArrow extends magnetArrow
{
	public multihookArrow(World worldIn) {
		super(worldIn);
	}
	
	public multihookArrow(World worldIn, EntityLivingBase shooter,
			boolean righthand) {
		super(worldIn, shooter, righthand);
	}
	
    @Override
	public int getControlId() {
		return grapplemod.MULTISUBID;
	}
    
	@Override
    public float func_70182_d()
    {
        return 20F;
    }
}

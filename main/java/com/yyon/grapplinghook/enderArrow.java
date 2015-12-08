package com.yyon.grapplinghook;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class enderArrow extends grappleArrow
{
	public enderArrow(World worldIn) {
		super(worldIn);
	}
	
	public enderArrow(World worldIn, EntityLivingBase shooter,
			float p_i1756_3_) {
		super(worldIn, shooter, p_i1756_3_);
	}
	
    protected float getVelocity()
    {
        return 20F;
    }
}

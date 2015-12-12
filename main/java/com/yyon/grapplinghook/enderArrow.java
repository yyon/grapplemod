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
    
	
    @Override
	public int getControlId() {
		return grapplemod.ENDERID;
	}
    /*
    @Override
	public void createControl() {
		System.out.println("Creating ender controller");
		this.control = new enderController(this.getEntityId(), this.shootingEntity.getEntityId(), this.worldObj, new Vec3(this.posX, this.posY, this.posZ));
	}
	*/
}

package com.yyon.grapplinghook;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class hookArrow extends grappleArrow
{
	public hookArrow(World worldIn) {
		super(worldIn);
	}
	
	public hookArrow(World worldIn, EntityLivingBase shooter,
			float p_i1756_3_) {
		super(worldIn, shooter, p_i1756_3_);
	}
	
    protected float getVelocity()
    {
        return 20F;
    }
    
    @Override
	public int getControlId() {
		return grapplemod.HOOKID;
	}
    /*
    @Override
	public void createControl() {
		this.control = new hookControl(this.getEntityId(), this.shootingEntity.getEntityId(), this.worldObj, new Vec3(this.posX, this.posY, this.posZ));
	}
	*/
}

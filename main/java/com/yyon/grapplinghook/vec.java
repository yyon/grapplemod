package com.yyon.grapplinghook;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;

public class vec {
	public double x;
	public double y;
	public double z;
	
	public vec(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		
		this.checkNaN();
	}
	
	public void checkNaN() {
		if (Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z)) {
			grapplemod.LOGGER.error("Error: vector contains NaN");
			this.x = 0; this.y = 0; this.z = 0;
//			throw new RuntimeException("hello");
		}
	}
	
	public vec(Vector3d vec3d) {
		this.x = vec3d.x;
		this.y = vec3d.y;
		this.z = vec3d.z;
		
		this.checkNaN();
	}
	
	public vec(vec vec) {
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
	}

	public Vector3d toVec3d() {
		return new Vector3d(this.x, this.y, this.z);
	}
	
	public static vec positionvec(Entity e) {
		return new vec(e.position());
	}
	
	public static vec partialpositionvec(Entity e, double partialTicks) {
		return new vec(lerp(partialTicks, e.xo, e.getX()), lerp(partialTicks, e.yo, e.getY()), lerp(partialTicks, e.zo, e.getZ()));
	}
	
	public static double lerp(double frac, double from, double to) {
		return (from * (1-frac)) + (to * frac);
	}
	
	public static vec motionvec(Entity e) {
		return new vec(e.getDeltaMovement());
	}
	
	public vec add(vec v2) {
		return new vec(this.x + v2.x, this.y + v2.y, this.z + v2.z);
	}
	
	public void add_ip(double x, double y, double z) {
		this.x += x;
		this.y += y;
		this.z += z;
	}
	
	public void add_ip(vec v2) {
		this.x += v2.x;
		this.y += v2.y;
		this.z += v2.z;
	}
	
	public vec sub(vec v2) {
		return new vec(this.x - v2.x, this.y - v2.y, this.z - v2.z);
	}

	public void sub_ip(vec v2) {
		this.x -= v2.x;
		this.y -= v2.y;
		this.z -= v2.z;
	}
	
	public vec rotate_yaw(double a) {
		return new vec(this.x * Math.cos(a) - this.z * Math.sin(a), this.y, this.x * Math.sin(a) + this.z * Math.cos(a));
	}
	
    public vec rotate_pitch(double pitch) {
        return new vec(this.x, this.y * Math.cos(pitch) + this.z * Math.sin(pitch), this.z * Math.cos(pitch) - this.y * Math.sin(pitch));
    }
    
    public static vec fromAngles(double yaw, double pitch) {
    	return new vec(Math.tan(-yaw), Math.tan(pitch), 1).normalize();
    }
	
	public vec mult(double changefactor) {
		return new vec(this.x * changefactor, this.y * changefactor, this.z * changefactor);
	}
	
	public void mult_ip(double changefactor) {
		this.x *= changefactor;
		this.y *= changefactor;
		this.z *= changefactor;
	}
	
	public double length() {
		return Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2) + Math.pow(this.z, 2));
	}
	
	public vec normalize() {
		if (this.length() == 0) {
			grapplemod.LOGGER.warn("normalizing vector with no length");
			return new vec(this);
		}
		return this.mult(1.0 / this.length());
	}
	
	public void normalize_ip() {
		this.mult_ip(1.0 / this.length());
	}
	
	public double dot(vec v2) {
		return this.x*v2.x + this.y*v2.y + this.z*v2.z;
	}
	
	public vec changelen(double l) {
		double oldl = this.length();
		if (oldl != 0) {
			double changefactor = l / oldl;
			return this.mult(changefactor);
		} else {
			return this;
		}
	}
	
	public void changelen_ip(double l) {
		double oldl = this.length();
		if (oldl != 0) {
			double changefactor = l / oldl;
			this.mult_ip(changefactor);
		}
	}
	
	public vec proj(vec v2) {
		vec v3 = v2.normalize();
		double dot = this.dot(v3);
		return v3.changelen(dot);
	}
	
	public double dist_along(vec v2) {
		vec v3 = v2.normalize();
		return this.dot(v3);
	}
	
	public vec removealong(vec v2) {
		return this.sub(this.proj(v2));
	}
	
	public void print(){
		System.out.println(this.toString());
	}
	
	public String toString() {
		return "<" + Double.toString(this.x) + "," + Double.toString(this.y) + "," + Double.toString(this.z) + ">";
	}

	public vec add(double x, double y, double z) {
		return new vec(this.x + x, this.y + y, this.z + z);
	}
	
	public double getYaw() {
		vec norm = this.normalize();
		return Math.toDegrees(-Math.atan2(norm.x, norm.z));
	}
	
	public double getPitch() {
		vec norm = this.normalize();
		return Math.toDegrees(-Math.asin(norm.y));
	}
	
	public vec cross(vec b) {
		return new vec(this.y * b.z - this.z * b.y, this.z * b.x - this.x * b.z, this.x * b.y - this.y * b.x);
	}
	
	public double angle(vec b) {
		double la = this.length();
		double lb = b.length();
		if (la == 0 || lb == 0) { return 0; }
		return Math.acos(this.dot(b) / (la*lb));
	}
	
	public void setpos(Entity e) {
		this.checkNaN();

		e.setPos(this.x, this.y, this.z);
	}
	
	public void setmotion(Entity e) {
		this.checkNaN();
		
		e.setDeltaMovement(this.toVec3d());
	}

	public static vec lookvec(Entity entity) {
		return new vec(entity.getLookAngle());
	}
}

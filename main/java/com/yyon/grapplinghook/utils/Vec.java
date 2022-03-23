package com.yyon.grapplinghook.utils;

import com.yyon.grapplinghook.grapplemod;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class Vec {
	public double x;
	public double y;
	public double z;
	
	public Vec(double x, double y, double z) {
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
	
	public Vec(Vec3 vec3d) {
		this.x = vec3d.x;
		this.y = vec3d.y;
		this.z = vec3d.z;
		
		this.checkNaN();
	}
	
	public Vec(Vec vec) {
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
	}

	public Vec3 toVec3d() {
		return new Vec3(this.x, this.y, this.z);
	}
	
	public static Vec positionVec(Entity e) {
		return new Vec(e.position());
	}
	
	public static Vec partialPositionVec(Entity e, double partialTicks) {
		return new Vec(lerp(partialTicks, e.xo, e.getX()), lerp(partialTicks, e.yo, e.getY()), lerp(partialTicks, e.zo, e.getZ()));
	}
	
	public static double lerp(double frac, double from, double to) {
		return (from * (1-frac)) + (to * frac);
	}
	
	public static Vec motionVec(Entity e) {
		return new Vec(e.getDeltaMovement());
	}
	
	public Vec add(Vec v2) {
		return new Vec(this.x + v2.x, this.y + v2.y, this.z + v2.z);
	}
	
	public void add_ip(double x, double y, double z) {
		this.x += x;
		this.y += y;
		this.z += z;
	}
	
	public void add_ip(Vec v2) {
		this.x += v2.x;
		this.y += v2.y;
		this.z += v2.z;
	}
	
	public Vec sub(Vec v2) {
		return new Vec(this.x - v2.x, this.y - v2.y, this.z - v2.z);
	}

	public void sub_ip(Vec v2) {
		this.x -= v2.x;
		this.y -= v2.y;
		this.z -= v2.z;
	}
	
	public Vec rotateYaw(double a) {
		return new Vec(this.x * Math.cos(a) - this.z * Math.sin(a), this.y, this.x * Math.sin(a) + this.z * Math.cos(a));
	}
	
    public Vec rotatePitch(double pitch) {
        return new Vec(this.x, this.y * Math.cos(pitch) + this.z * Math.sin(pitch), this.z * Math.cos(pitch) - this.y * Math.sin(pitch));
    }
    
    public static Vec fromAngles(double yaw, double pitch) {
    	return new Vec(Math.tan(-yaw), Math.tan(pitch), 1).normalize();
    }
	
	public Vec mult(double changefactor) {
		return new Vec(this.x * changefactor, this.y * changefactor, this.z * changefactor);
	}
	
	public void mult_ip(double changefactor) {
		this.x *= changefactor;
		this.y *= changefactor;
		this.z *= changefactor;
	}
	
	public double length() {
		return Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2) + Math.pow(this.z, 2));
	}
	
	public Vec normalize() {
		if (this.length() == 0) {
			grapplemod.LOGGER.warn("normalizing vector with no length");
			return new Vec(this);
		}
		return this.mult(1.0 / this.length());
	}
	
	public void normalize_ip() {
		this.mult_ip(1.0 / this.length());
	}
	
	public double dot(Vec v2) {
		return this.x*v2.x + this.y*v2.y + this.z*v2.z;
	}
	
	public Vec changeLen(double l) {
		double oldl = this.length();
		if (oldl != 0) {
			double changefactor = l / oldl;
			return this.mult(changefactor);
		} else {
			return this;
		}
	}
	
	public void changeLen_ip(double l) {
		double oldl = this.length();
		if (oldl != 0) {
			double changefactor = l / oldl;
			this.mult_ip(changefactor);
		}
	}
	
	public Vec proj(Vec v2) {
		Vec v3 = v2.normalize();
		double dot = this.dot(v3);
		return v3.changeLen(dot);
	}
	
	public double distAlong(Vec v2) {
		Vec v3 = v2.normalize();
		return this.dot(v3);
	}
	
	public Vec removeAlong(Vec v2) {
		return this.sub(this.proj(v2));
	}
	
	public void print(){
		System.out.println(this.toString());
	}
	
	public String toString() {
		return "<" + Double.toString(this.x) + "," + Double.toString(this.y) + "," + Double.toString(this.z) + ">";
	}

	public Vec add(double x, double y, double z) {
		return new Vec(this.x + x, this.y + y, this.z + z);
	}
	
	public double getYaw() {
		Vec norm = this.normalize();
		return Math.toDegrees(-Math.atan2(norm.x, norm.z));
	}
	
	public double getPitch() {
		Vec norm = this.normalize();
		return Math.toDegrees(-Math.asin(norm.y));
	}
	
	public Vec cross(Vec b) {
		return new Vec(this.y * b.z - this.z * b.y, this.z * b.x - this.x * b.z, this.x * b.y - this.y * b.x);
	}
	
	public double angle(Vec b) {
		double la = this.length();
		double lb = b.length();
		if (la == 0 || lb == 0) { return 0; }
		return Math.acos(this.dot(b) / (la*lb));
	}
	
	public void setPos(Entity e) {
		this.checkNaN();

		e.setPos(this.x, this.y, this.z);
	}
	
	public void setMotion(Entity e) {
		this.checkNaN();
		
		e.setDeltaMovement(this.toVec3d());
	}

	public static Vec lookVec(Entity entity) {
		return new Vec(entity.getLookAngle());
	}
}

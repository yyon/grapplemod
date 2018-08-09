package com.yyon.grapplinghook;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class vec {
	public double x;
	public double y;
	public double z;
	
	public vec(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public vec(Vec3d vec3d) {
		this.x = vec3d.x;
		this.y = vec3d.y;
		this.z = vec3d.z;
	}
	
	public Vec3d toVec3d() {
		return new Vec3d(this.x, this.y, this.z);
	}
	
	public static vec positionvec(Entity e) {
		return new vec(e.posX, e.posY, e.posZ);
	}
	
	public static vec motionvec(Entity e) {
		return new vec(e.motionX, e.motionY, e.motionZ);
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
	
	public vec proj (vec v2) {
		vec v3 = v2.normalize();
		double dot = this.dot(v3);
		return v3.changelen(dot);
	}
	
	public vec removealong(vec v2) {
		return this.sub(this.proj(v2));
	}
	
	public void print(){
		System.out.print("<");
		System.out.print(this.x);
		System.out.print(",");
		System.out.print(this.y);
		System.out.print(",");
		System.out.print(this.z);
		System.out.print(">\n");
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
}

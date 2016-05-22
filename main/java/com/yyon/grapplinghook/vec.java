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
		this.x = vec3d.xCoord;
		this.y = vec3d.yCoord;
		this.z = vec3d.zCoord;
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
		v2.normalize_ip();
		double dot = this.dot(v2);
		return v2.changelen(dot);
	}
	
	public vec removealong(vec v2) {
		return this.sub(this.proj(v2));
	}
	
}

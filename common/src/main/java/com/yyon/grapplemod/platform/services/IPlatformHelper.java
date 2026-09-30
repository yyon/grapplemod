package com.yyon.grapplemod.platform.services;

public interface IPlatformHelper {
	String getPlatformName();

	boolean isModLoaded(String modId);

	boolean isDevelopmentEnvironment();
}

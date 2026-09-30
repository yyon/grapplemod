package com.yyon.grapplemod.init;

import com.yyon.grapplemod.Constants;
import com.yyon.grapplemod.registration.RegistrationProvider;
import com.yyon.grapplemod.registration.RegistryObject;
import com.yyon.grapplemod.utils.GrappleCustomization;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;

public class ComponentInit {
	public static final RegistrationProvider<DataComponentType<?>> DATA_COMPONENTS = RegistrationProvider.get(Registries.DATA_COMPONENT_TYPE, Constants.MODID);

	public static final RegistryObject<DataComponentType<?>, DataComponentType<GrappleCustomization>> CUSTOMIZATION = DATA_COMPONENTS.register("custom", () -> DataComponentType.<GrappleCustomization>builder()
			.persistent(GrappleCustomization.CODEC)
			.networkSynchronized(GrappleCustomization.STREAM_CODEC)
			.build());

	public static void loadClass() {
	}
}

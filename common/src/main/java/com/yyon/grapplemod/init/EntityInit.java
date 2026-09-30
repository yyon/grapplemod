package com.yyon.grapplemod.init;

import com.yyon.grapplemod.Constants;
import com.yyon.grapplemod.entities.grapplehook.GrapplehookEntity;
import com.yyon.grapplemod.registration.RegistrationProvider;
import com.yyon.grapplemod.registration.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class EntityInit {
	public static final RegistrationProvider<EntityType<?>> ENTITIES = RegistrationProvider.get(Registries.ENTITY_TYPE, Constants.MODID);

	public static final RegistryObject<EntityType<?>, EntityType<GrapplehookEntity>> GRAPPLEHOOK = ENTITIES.register("grapplehook", () -> EntityType.Builder.<GrapplehookEntity>of(GrapplehookEntity::new, MobCategory.MISC)
			.sized(0.25F, 0.25F)
			.build(Constants.MODID + ":grapplehook"));

	public static void loadClass() {
	}
}

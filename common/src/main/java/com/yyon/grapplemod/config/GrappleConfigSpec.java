package com.yyon.grapplemod.config;

import com.yyon.grapplemod.Constants;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class GrappleConfigSpec {
	private static final double MAX_DOUBLE = 1.0E9;

	public enum Mode {
		ALL, SERVER, STARTUP
	}

	private final Object root;
	private final Mode mode;
	private final ModConfigSpec spec;
	private final List<Entry> entries = new ArrayList<>();

	public GrappleConfigSpec(Object root, String name, Mode mode) {
		this.root = root;
		this.mode = mode;
		ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
		this.define(builder, root, new ArrayList<>(), "text.autoconfig." + Constants.MODID + ".option." + name);
		this.spec = builder.build();
	}

	public ModConfigSpec getSpec() {
		return this.spec;
	}

	public void onLoad() {
		for (Entry entry : this.entries) {
			entry.apply(this.root, entry.value.get());
		}
	}

	public void onUnload() {
		for (Entry entry : this.entries) {
			entry.apply(this.root, entry.value.getDefault());
		}
	}

	private void define(ModConfigSpec.Builder builder, Object obj, List<Field> path, String key) {
		for (Field field : obj.getClass().getFields()) {
			if (Modifier.isStatic(field.getModifiers()) || !this.includes(field)) {
				continue;
			}

			String name = field.getName();
			String fieldKey = key + "." + name;
			List<Field> fieldPath = new ArrayList<>(path);
			fieldPath.add(field);

			Comment comment = field.getAnnotation(Comment.class);
			if (comment != null) {
				builder.comment(comment.value());
			}
			builder.translation(fieldKey);

			Object value = get(field, obj);
			Class<?> type = field.getType();
			if (type == boolean.class) {
				this.entries.add(new Entry(fieldPath, builder.define(name, (boolean) value)));
			} else if (type == int.class) {
				Range range = field.getAnnotation(Range.class);
				int min = range == null ? 0 : range.min();
				int max = range == null ? Integer.MAX_VALUE : range.max();
				this.entries.add(new Entry(fieldPath, builder.defineInRange(name, (int) value, min, max)));
			} else if (type == double.class || type == float.class) {
				this.entries.add(new Entry(fieldPath, builder.defineInRange(name, ((Number) value).doubleValue(), -MAX_DOUBLE, MAX_DOUBLE)));
			} else if (type == String.class) {
				this.entries.add(new Entry(fieldPath, builder.define(name, (String) value)));
			} else {
				builder.push(name);
				this.define(builder, value, fieldPath, fieldKey);
				builder.pop();
			}
		}
	}

	private boolean includes(Field field) {
		Class<?> type = field.getType();
		if (type.isPrimitive() || type == String.class) {
			boolean startup = field.isAnnotationPresent(Startup.class);
			return switch (this.mode) {
				case ALL -> true;
				case SERVER -> !startup;
				case STARTUP -> startup;
			};
		}
		for (Field child : type.getFields()) {
			if (!Modifier.isStatic(child.getModifiers()) && this.includes(child)) {
				return true;
			}
		}
		return false;
	}

	private static Object get(Field field, Object obj) {
		try {
			return field.get(obj);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}

	private record Entry(List<Field> path, ModConfigSpec.ConfigValue<?> value) {
		void apply(Object root, Object newValue) {
			Object obj = root;
			for (int i = 0; i < this.path.size() - 1; i++) {
				obj = get(this.path.get(i), obj);
			}
			Field field = this.path.get(this.path.size() - 1);
			try {
				if (field.getType() == float.class) {
					field.setFloat(obj, ((Number) newValue).floatValue());
				} else if (field.getType() == double.class) {
					field.setDouble(obj, ((Number) newValue).doubleValue());
				} else if (field.getType() == int.class) {
					field.setInt(obj, ((Number) newValue).intValue());
				} else {
					field.set(obj, newValue);
				}
			} catch (IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
		}
	}
}

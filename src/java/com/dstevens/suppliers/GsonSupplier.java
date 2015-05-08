package com.dstevens.suppliers;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import static com.dstevens.collections.Lists.list;

@Service
public class GsonSupplier implements Supplier<Gson> {

	private final GsonBuilder builder = new GsonBuilder();

	public GsonSupplier() {
		builder.registerTypeHierarchyAdapter(Enum.class, new EnumSerializer());
	}
	
	@Override
	public Gson get() {
		return builder.create();
	}
	
	private static interface FieldSerializer {
		public boolean canHandle(Field f);
		public void handle(Field f, Object o, JsonObject jsonObject);
	}
	
	private static class CharacterFieldSerializer implements FieldSerializer {
		@Override
		public boolean canHandle(Field f) {
			return Character.class.isAssignableFrom(f.getType());
		}

		@Override
		public void handle(Field f, Object o, JsonObject jsonObject) {
			jsonObject.addProperty(f.getName(), value(f, o));
		}
		
		private Character value(Field f, Object o) {
			try {
				return f.getChar(o);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new IllegalStateException("Failed to get value from " + f + " for " + o, e);
			}
		}
	}
	
	private static class StringFieldSerializer implements FieldSerializer {
		@Override
		public boolean canHandle(Field f) {
			return String.class.isAssignableFrom(f.getType());
		}
		
		@Override
		public void handle(Field f, Object o, JsonObject jsonObject) {
			jsonObject.addProperty(f.getName(), value(f, o));
		}
		
		private String value(Field f, Object o) {
			try {
				return (String) f.get(o);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new IllegalStateException("Failed to get value from " + f + " for " + o, e);
			}
		}
	}
	
	private static class NumberFieldSerializer implements FieldSerializer {
		@Override
		public boolean canHandle(Field f) {
			return Number.class.isAssignableFrom(f.getType());
		}
		
		@Override
		public void handle(Field f, Object o, JsonObject jsonObject) {
			jsonObject.addProperty(f.getName(), value(f, o));
		}
		
		private Number value(Field f, Object o) {
			try {
				return (Number) f.get(o);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new IllegalStateException("Failed to get value from " + f + " for " + o, e);
			}
		}
	}
	
	private static class ObjectFieldSerializer implements FieldSerializer {
		@Override
		public boolean canHandle(Field f) {
			return true;
		}
		
		@Override
		public void handle(Field f, Object o, JsonObject jsonObject) {
			jsonObject.add(f.getName(), value(f, o));
		}
		
		private JsonElement value(Field f, Object o) {
			try {
				return new Gson().toJsonTree(f.get(o));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new IllegalStateException("Failed to get value from " + f + " for " + o, e);
			}
		}
	}
	
	private static class EnumSerializer implements JsonSerializer<Enum<?>> {
		private final List<FieldSerializer> serializers = list();
		private EnumSerializer() {
			serializers.add(new StringFieldSerializer());
			serializers.add(new CharacterFieldSerializer());
			serializers.add(new NumberFieldSerializer());
			serializers.add(new ObjectFieldSerializer());
		}
		
		public JsonElement serialize(Enum<?> p, Type arg1, JsonSerializationContext arg2) {
			JsonObject jsonObject = new JsonObject();
			
			for (Field field : instanceFieldsInEnum(p)) {
				field.setAccessible(true);
				for (FieldSerializer serializer : serializers) {
					if(serializer.canHandle(field)) {
						serializer.handle(field, p, jsonObject);
						break;
					}
				}
			}
			return jsonObject;
		}

		@SuppressWarnings("unchecked")
		private List<Field> instanceFieldsInEnum(Enum<?> p) {
			List<Field> fields = list(p.getClass().getDeclaredFields());
			fields.removeAll(list(((Class<? extends Enum<?>>) p.getClass()).
				   getDeclaredFields()).
				   stream().
				   filter((Field f) -> Modifier.isStatic(f.getModifiers())).
				   collect(Collectors.toList()));
			return fields;
		}
	}

}

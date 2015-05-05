package com.dstevens.suppliers;

import java.lang.reflect.Type;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

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
	
	private static class EnumSerializer implements JsonSerializer<Enum<?>> {
		@Override
		public JsonElement serialize(Enum<?> p, Type arg1, JsonSerializationContext arg2) {
			return new JsonPrimitive(p.ordinal());
		}
	}

}

package com.dstevens.suppliers;

import java.util.List;

import org.junit.Test;

import com.google.gson.Gson;

import static com.dstevens.collections.Lists.list;

public class GsonSupplierTest {

	public static enum TestableEnum {
		A("A", 2, new Object()),
		B("bb", 3, new Object()),
		C("CcC", 5, new Object()),
		;
		
		private final String stringVal;
		private final int intVal;
		private final Object object;

		public String getStringVal() {
			return stringVal;
		}

		public int getIntVal() {
			return intVal;
		}

		public Object getObject() {
			return object;
		}

		private TestableEnum(String stringVal, int intVal, Object object) {
			this.stringVal = stringVal;
			this.intVal = intVal;
			this.object = object;
		}
	}
	
	@Test
	public void testJsonizeEnum() {
		Gson gson = new GsonSupplier().get();
		List<TestableEnum> list = list(TestableEnum.values());
		System.out.println(gson.toJson(list));
	}
	
}

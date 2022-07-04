package com.markozajc.akiwrapper.core.utils;

import static com.markozajc.akiwrapper.core.utils.JSONUtils.*;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JSONUtilsTest {

	// @formatter:off
	public static final String TEST_GET_INT_JSON =
		"{										"+
		"	\"test1\": \"1\",					"+
		"	\"test2\": 2,						"+
		"	\"test3\": \"not an integer\",		"+
		"	\"test4\": {}						"+
		"}										";
	public static final String TEST_GET_STRING_JSON =
		"{										"+
		"	\"test1\": \"string\",				"+
		"	\"test2\": 's',						"+
		"	\"test3\": 1						"+
		"}										";
	public static final String TEST_GET_DOUBLE_JSON =
		"{										"+
		"	\"test1\": \"1\",					"+
		"	\"test2\": \"0.5\",					"+
		"	\"test3\": 2,						"+
		"	\"test4\": 1.5,						"+
		"	\"test5\": \"not a double\",		"+
		"	\"test6\": {}						"+
		"}										";
	// @formatter:on

	@Test
	void testGetInt() {
		var json = new JSONObject(TEST_GET_INT_JSON);
		assertEquals(1, getInteger(json, "test1").orElse(-1));
		assertEquals(2, getInteger(json, "test2").orElse(-1));
		assertThrows(NumberFormatException.class, () -> getInteger(json, "test3"));
		assertThrows(NumberFormatException.class, () -> getInteger(json, "test4"));
		assertEquals(-1, getInteger(json, "test5").orElse(-1));
	}

	@Test
	void testGetString() {
		var json = new JSONObject(TEST_GET_STRING_JSON);
		assertEquals("string", getString(json, "test1").orElseThrow());
		assertEquals("s", getString(json, "test2").orElseThrow());
		assertEquals("1", getString(json, "test3").orElseThrow());
		assertNull(getString(json, "test4").orElse(null));
	}

	@Test
	void testGetDouble() {
		var json = new JSONObject(TEST_GET_DOUBLE_JSON);
		assertEquals(1d, getDouble(json, "test1").orElseThrow());
		assertEquals(0.5d, getDouble(json, "test2").orElseThrow());
		assertEquals(2d, getDouble(json, "test3").orElseThrow());
		assertEquals(1.5d, getDouble(json, "test4").orElseThrow());
		assertThrows(NumberFormatException.class, () -> getDouble(json, "test5"));
		assertThrows(NumberFormatException.class, () -> getDouble(json, "test6"));
		assertEquals(-1d, getDouble(json, "test7").orElse(-1d));
	}

}

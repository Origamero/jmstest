package com.waua.jmstest;

import java.lang.reflect.Type;

import com.google.gson.Gson;

public class ConvertUtils {
	public static <T> T jsonToType(String jsonQueue, Class<T> type)   {
		Gson gson = new Gson();
		return gson.fromJson(jsonQueue, type);
	}
	
	   public static <T> T jsonToType(String jsonQueue, Type type)  {
	        Gson gson = new Gson();
	        return gson.fromJson(jsonQueue, type);
	    }
}

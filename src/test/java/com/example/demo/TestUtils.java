package com.example.demo;

import java.lang.reflect.Field;

public class TestUtils {

	/**
	 * This method to inject the field with fieldName in the target Object with toInject Object.
	 * @param target
	 * @param fieldName
	 * @param toInject
	 */
	public static void injectObjects(Object target, String fieldName, Object toInject) {
		
		boolean wasPrivate = false;
		
		try {
			// get the "fieldName" field in target object
			Field f = target.getClass().getDeclaredField(fieldName);
		
			// If the field is not accessible, set it to accessible
			if (!f.isAccessible()) {
				f.setAccessible(true);
				wasPrivate = true;
			}
			
			// set the value of the field to toInject object
			f.set(target, toInject);
			
			// if the field was not accessible, set it to non-acccessible
			if (wasPrivate) {
				f.setAccessible(false);
			}
			
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

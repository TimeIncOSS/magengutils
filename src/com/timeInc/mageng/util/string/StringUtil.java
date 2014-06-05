/*******************************************************************************
 * Copyright 2014 Time Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.timeInc.mageng.util.string;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for {@link java.lang.String}
 */
public class StringUtil {
	
	private StringUtil() {}
	
	
	/**
	 * Split comma separated primitive values
	 * of autoboxed type T into a List.
	 *
	 * @param <T> the autoboxed primitive type
	 * @param clazz the class of the autoboxed primitive type
	 * @param input the input
	 * @return the list
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Number> List<T> splitCommaSepIntoPrimitive(Class<T> clazz, String input)  {
		String splitStr[] = input.split(",");
		try {
			List<T> res = new ArrayList<T>(splitStr.length);
			Method m = clazz.getMethod("valueOf", String.class);
			
			for(String str : splitStr) {
				res.add((T)m.invoke(null, str));
			}
			
			return res;
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	/**
	 * Strip trailing slash.
	 *
	 * @param input the input
	 * @return the string
	 */
	public static String stripTrailingSlash(String input) {
		return input.charAt(input.length()-1) == '\\' || input.charAt(input.length()-1) == '/' ? input.substring(0,input.length()-1) : input;
	}
	
	/**
	 * Strip leading slash.
	 *
	 * @param input the input
	 * @return the string
	 */
	public static String stripLeadingSlash(String input) {
		return input.charAt(0) == '\\' || input.charAt(0) == '/' ? input.substring(1,input.length()) : input;		
	}
	
	/**
	 * Checks if is empty.
	 *
	 * @param string the string
	 * @return true, if is empty
	 */
	public static boolean isEmpty(String string) {
		return string == null || string.isEmpty();  
	}
	
	/**
	 * Create a string from a list of strings
	 * by using the seperator between each element
	 *
	 * @param seperator the seperator
	 * @param strList the str list
	 * @return the string 
	 */
	public static String mkString(String seperator, List<String> strList) {
		if(strList.size() == 1)
			return strList.get(0);
		
		
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < strList.size() - 1; i++) {
			sb.append(strList.get(i) + seperator);
		}
		
		sb.append(strList.get(strList.size()-1));
		
		return sb.toString();
	}
}

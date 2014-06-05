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
package com.timeInc.mageng.util.misc;

import java.io.File;

/**
 * A utility class that does parameter validations 
 * 
 */
public class Precondition {
	private Precondition() {
		throw new AssertionError(); // safeguard to prevent object construction
	}
	
	/**
	 * Determines whether an object is null.
	 * @param o the object to check for nullability
	 * @param paramName the name of the parameter that corresponds to the object
	 * @throws IllegalArgumentException if the object is null 
	 */
	public static void checkNull(Object o,String paramName) {
	if(o==null)
			throw new IllegalArgumentException("The parameter " + paramName + " can not be null!");
	}	
	
	/**
	 * Determines whether a string is empty. A string is empty if it is 
	 * null or the length excluding whitespaces is equal to 0
	 * @param s the string to check for emptiness
	 * @param paramName the name of the parameter that corresponds to this string
	 * @throws IllegalArgumentException if the string is null or has length 0. 
	 */	
	public static void checkStringEmpty(String s, String paramName) {
		checkNull(s,paramName);
		if(s.trim().length()==0)
			throw new IllegalArgumentException("The parameter " + paramName + " can not be of length 0!");
	}
	
	/**
	 * Determines whether a file exists.
	 * @param fileName the absolute path to the file 
	 * @param paramName the name of the parameter that corresponds to this string
	 * @throws IllegalArgumentException if the file does not exist.
	 */	
	public static File checkFileExists(File file, String paramName) {
		if(!file.exists()) 
			throw new IllegalArgumentException("The file " + file + " of " + paramName + " does not exist!");
		
		return file;
	}
	
	/**
	 * Determines whether a file exists.
	 * @param fileName the absolute path to the file 
	 * @param paramName the name of the parameter that corresponds to this string
	 * @throws IllegalArgumentException if the file does not exist.
	 */	
	public static File checkFileExists(File file) {
		return checkFileExists(file, "");
	}
	
}

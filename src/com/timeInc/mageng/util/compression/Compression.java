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
package com.timeInc.mageng.util.compression;

import java.io.File;

/**
 * Interface for (de)compressing a file.
 */
public interface Compression {
	
	/**
	 * Get the default extension for this compression type
	 * @return the extension
	 */
	String getExtension();
	
	/**
	 * Determined whether the file is a valid format
	 * @param compressedFile the compressed file
	 * @return true if it is a valid format; false otherwise
	 */
	boolean isValid(File compressedFile);
	
	
	/**
	 * Extract the compressed file to the destination directory
	 * @param compressedFile the compressed file
	 * @param outputDirectory the destination directory
	 */
	void extractAll(File compressedFile, File outputDirectory);
	
	
	/**
	 * Add a file to a compressed file
	 * @param compFile the compressed file to add to
	 * @param fileToAdd the file to add
	 * @param relativePath the relative path in the compressed file
	 */
	void add(File compFile, File fileToAdd, String relativePath);
	
	/**
	 * Extract a certain file in the compressed file and put it in
	 * the destination
	 * @param compressedFile the compressed file
	 * @param relativeFile the path in the compressed file
	 * @param destination the destination to put the extract file
	 */
	void extract(File compressedFile, String relativeFile, File destination);
	
	
	/**
	 * Remove a file / directory within the compressed file
	 * @param compressedFile 
	 * @param fileToRemove the file to remove
	 * @param isDir if the specified file is a directory
	 */
	void remove(File compressedFile, String fileToRemove, boolean isDir);
}

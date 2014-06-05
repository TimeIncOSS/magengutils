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
import java.util.ArrayList;
import java.util.List;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import org.apache.log4j.Logger;

/**
 * Zip / Unzip
 */
public class Zip implements Compression {
	private static final Logger log = Logger.getLogger(Zip.class);
	
	/* (non-Javadoc)
	 * @see com.timeInc.util.compression.Compression#isValid(java.io.File)
	 */
	@Override
	public boolean isValid(File compressedFile) {
		try {
			ZipFile zipFile = new ZipFile(compressedFile);
			if(zipFile.isValidZipFile())
				return true;
			else 
				return false;
		} catch (ZipException e) {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see com.timeInc.util.compression.Compression#extractAll(java.io.File, java.io.File)
	 */
	@Override
	public void extractAll(File compressedFile, File outputDirectory) {
		try {
			ZipFile zipFile = new ZipFile(compressedFile);
			zipFile.extractAll(outputDirectory.getAbsolutePath());
		} catch (ZipException e) {
			throw new RuntimeException("Error unzipping", e);
		}
	}

	/* (non-Javadoc)
	 * @see com.timeInc.util.compression.Compression#add(java.io.File, java.io.File, java.lang.String)
	 */
	@Override
	public void add(File zipFile, File fileToAdd, String relativePath) {
		try {
			ZipFile zip = new ZipFile(zipFile);
			
			ZipParameters parameters = new ZipParameters();
			parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE); // set compression method to deflate compression
			
			parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
			
			parameters.setRootFolderInZip(relativePath);
			
			if(fileToAdd.isFile())
				zip.addFile(fileToAdd, parameters);
			else
				zip.addFolder(fileToAdd, parameters);
			
		} catch (ZipException e) {
			throw new RuntimeException("Error adding file to zip", e);
		}		
	}

	/* (non-Javadoc)
	 * @see com.timeInc.util.compression.Compression#extract(java.io.File, java.lang.String, java.io.File)
	 */
	@Override
	public void extract(File zipFile, String relativeFile, File destination) {
		try {
			ZipFile zip = new ZipFile(zipFile);
			zip.extractFile(relativeFile, destination.getAbsolutePath());
		} catch (ZipException e) {
			throw new RuntimeException("Error extracting", e);
		}
	}

	/* (non-Javadoc)
	 * @see com.timeInc.util.compression.Compression#remove(java.io.File, java.lang.String, boolean)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void remove(File zipFile, String fileToRemove, boolean isDir) {
		try {
			ZipFile zip = new ZipFile(zipFile);
			
			if(zip.getFileHeaders() != null && isDir) {
				List<FileHeader> headersToRemove = new ArrayList<FileHeader>();
				
				for(FileHeader header : (List<FileHeader>) zip.getFileHeaders()) {
					if(header.getFileName().startsWith(fileToRemove))
						headersToRemove.add(header);
				}
				
				for(FileHeader toRemove : headersToRemove) { 
					log.debug("Removing file in zip " + toRemove.getFileName());
					zip.removeFile(toRemove);
				}
				
					
			} else
				zip.removeFile(fileToRemove);
		} catch (ZipException e) {
			throw new RuntimeException("Error removing " + fileToRemove + " from " + zipFile, e);
		}
	}

	/* (non-Javadoc)
	 * @see com.timeInc.util.compression.Compression#getExtension()
	 */
	@Override
	public String getExtension() {
		return "zip";
	}
}

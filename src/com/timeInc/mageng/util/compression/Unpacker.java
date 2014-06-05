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

import org.apache.log4j.Logger;

import com.timeInc.mageng.util.exceptions.PrettyException;
import com.timeInc.mageng.util.file.FileUtil;

/**
 * Unpacks a compressed file depending on the {@code Unpacker.Method provided}
 */
public class Unpacker {
	private static final Logger log = Logger.getLogger(Unpacker.class);

	public enum Method { DECOMPRESS, DECOMPRESS_IF_POSSIBLE, NONE } 
	
	private final File file;
	private final File outputDir;
	private final Compression comp;

	private File unzippedDir;

	/**
	 * Instantiates a new unpacker
	 * using the specified input file
	 *
	 * @param file the file
	 * @param outputDir the output dir
	 */
	public Unpacker(File file, File outputDir) {
		this(new Zip(), file, outputDir);
	}

	/**
	 * Instantiates a new unpacker.
	 *
	 * @param comp 
	 * @param uploadedFile the uploaded file
	 * @param outputDir the output dir
	 */
	public Unpacker(Compression comp, File uploadedFile, File outputDir) {
		this.comp = comp;
		this.file = uploadedFile;
		this.outputDir = outputDir;
	}


	private File unpackIfPossible() {
		if(isUnpackable()) {
			synchronized(this) {
				if(unzippedDir == null) {
					log.debug("Trying to unpack " + file);

					unzippedDir = FileUtil.createTempDirectory(outputDir);
					comp.extractAll(file, unzippedDir);
				} 
				return unzippedDir;
			}
		} else
			return null;
	}
	
	/**
	 * Checks if is unpackable.
	 *
	 * @return true, if is unpackable
	 */
	public boolean isUnpackable() {
		return comp.isValid(file);
	}
	
	/**
	 * Unpack a file using the specified Method.
	 * If DECOMPRESS is used 
	 *
	 * @param unpackMethod the unpack method
	 * @return the file
	 */
	public File unpack(Method unpackMethod) {
		switch(unpackMethod) {
			case DECOMPRESS:
				File decompressedDir = unpackIfPossible();
	
				if(decompressedDir == null)
					throw new PrettyException("Unable to decompress file " + file.getName());
				else
					return decompressedDir;
	
			case DECOMPRESS_IF_POSSIBLE:
				File decompressDir = unpackIfPossible();
	
				if(decompressDir != null)
					return decompressDir;
	
			default: 
				return file;	
		}
	}
}

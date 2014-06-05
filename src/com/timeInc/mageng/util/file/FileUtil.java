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
package com.timeInc.mageng.util.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.log4j.Logger;

/**
 *
 * Utility class that manipulates the File API
 *
 */

public class FileUtil extends org.apache.commons.io.FileUtils {
	static Logger log = Logger.getLogger(FileUtil.class);

	/**
	 * Creates the temp directory under the specified folder
	 *
	 * @param rootFolder the root folder
	 * @return the temp directory
	 */
	public static File createTempDirectory(File rootFolder) {
		File tempDir = new File(rootFolder,Math.random() + "");
		tempDir.mkdirs();
		return tempDir;
	}


	/**
	 * Creates the directory if not exist.
	 * @param file the directory to create; needs to be a file
	 * @throws RuntimeException if the directory creation failed
	 */
	public static void createDirectoryIfNotExist(File file) {
		boolean status = true;
		if(!file.exists())
			status = file.mkdirs();

		if(!status)
			throw new RuntimeException("Failed to create directory " + file);
	}

	/**
	 * Gets the path without file.
	 *
	 * @param pathToFile the path to file
	 * @return the path without file
	 */
	public static String getPathWithoutFile(File pathToFile) {
		return pathToFile.getPath().substring(0,pathToFile.getPath().lastIndexOf(File.separator));
	}

	/**
	 * Gets the path hierarchy in an array
	 *
	 * @param path the entire path
	 * @return an array of the file hierarchy where 0 represents the topmost path
	 */
	public static String[] getPath(String path) {
		String normalizedPath =  path.replace("\\","/");

		if(normalizedPath.charAt(0) == '/') 
			normalizedPath = normalizedPath.substring(1);

		if(normalizedPath.isEmpty())
			return new String[0];
		else  {
			String paths[] = normalizedPath.split("/");
			return paths;
		}
	}

	/**
	 * Copy to temp dir.
	 *
	 * @param source the source
	 * @param baseDir the base dir
	 * @return the file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static File copyToTempDir(File source, File baseDir) throws IOException {
		File outputDir = FileUtil.createTempDirectory(baseDir);
		if(source.isDirectory()) {
			copyDirectory(source,outputDir);
			return outputDir;
		} else {
			File copyOfFile = new File(outputDir, source.getName());
			copyFile(source, copyOfFile);
			return copyOfFile;
		}
	}

	/**
	 * Zip the contents of a folder. The root directory
	 * of the zip will be the directory contents of inFolder
	 * @param inFolder the folder to zip
	 * @param outZipFile the path to place the zip file
	 * @throws IOException if there was a problem zipping
	 */
	public static void zip(String inFolder, File outZipFile) throws IOException {
		ZipOutputStream out = null;
		try {
			out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outZipFile)));
			zip(new File(inFolder), inFolder , out);
		} finally {
			if (out != null) {
				out.close();
				out = null;
			}
		}
	}

	private static void zip(File rootFolder, String inFolder, ZipOutputStream out) throws IOException {
		/*create a new File object based on the directory we have to zip File    
         get a listing of the directory content */ 
		String[] dirList = new File(inFolder).list(); 
		byte[] readBuffer = new byte[2156]; 
		int bytesIn = 0; 
		//loop through dirList, and zip the files 
		for(int i=0; i<dirList.length; i++)  { 
			File f = new File(inFolder, dirList[i]); 
			if(f.isDirectory()) { 
				//if the File object is a directory, call this 
				//function again to add its content recursively 
				zip(rootFolder, f.getPath(), out); 
				continue; 
			} 

			FileInputStream fis = new FileInputStream(f); 
			// let's make file path relative
			ZipEntry anEntry = new ZipEntry(rootFolder.toURI().relativize(f.toURI()).getPath()); 
			//place the zip entry in the ZipOutputStream object 
			out.putNextEntry(anEntry); 
			//now write the content of the file to the ZipOutputStream 
			while((bytesIn = fis.read(readBuffer)) != -1) { 
				out.write(readBuffer, 0, bytesIn); 
			} 
			fis.close(); 
		}
	}



	/**
	 * Unzip the file and places it into
	 * the specified directory
	 *
	 * @param sourcef the path to zip file
	 * @param destination the destination to unpack to
	 * @param deleteOriginal delete the original zip file
	 * @throws IOException if the file can not be read
	 */
	public static void unzip(File sourcef, String destination, boolean deleteOriginal) throws IOException  {
		if (sourcef == null || !sourcef.exists() || !sourcef.canRead()) {
			throw new IOException("Cannot read file.");
		}

		new File(destination).mkdir();
		final int buffer = 2048;
		BufferedOutputStream dest = null;
		BufferedInputStream is = null;
		ZipEntry entry;
		ZipFile zipfile = new ZipFile(sourcef);
		Enumeration e = zipfile.entries();
		while(e.hasMoreElements()) {

			entry = (ZipEntry) e.nextElement();
			//           log.debug("Extracting: " +entry);
			is = new BufferedInputStream(zipfile.getInputStream(entry));
			if (entry.isDirectory()) {
				File dir = new File(destination + "/" + entry.getName());
				dir.mkdir();
				continue;
			}
			File f = new File(destination + "/" + entry.getName());

			f.getParentFile().mkdirs();
			FileOutputStream fos = new FileOutputStream(f);
			dest = new BufferedOutputStream(fos, buffer);
			byte buf[] = new byte[1024 * 5];
			int len = 0;
			while ((len = is.read(buf)) != -1) {
				dest.write(buf, 0, len);
			}
			dest.flush();
			dest.close();
			is.close();
			fos.close();
		}
		zipfile.close();

		if (deleteOriginal) {
			log.info("Deleting file: " + sourcef.getAbsolutePath());
			sourcef.deleteOnExit();
		}
	}

	/**
	 * Gets the files that meet the FileNameFilter recursing
	 * if specified.
	 *
	 * @param directory the directory to start scanning
	 * @param filter the filter
	 * @param recurse recursively search the specified directory
	 * @return File[] the files that match the FilenameFilter
	 */
	public static File[] getFiles(File directory, FilenameFilter filter, boolean recurse){
		Collection<File> files = listFiles(directory, filter, recurse);
		File[] arr = new File[files.size()];
		return files.toArray(arr);
	}

	private static Collection<File> listFiles(File directory, FilenameFilter filter, boolean recurse) {
		Vector<File> files = new Vector<File>();
		File[] entries = directory.listFiles();

		for (File entry : entries) {
			if (filter == null || filter.accept(directory, entry.getName())) {
				files.add(entry);
			}
			if (recurse && entry.isDirectory()) {
				files.addAll(listFiles(entry, filter, recurse));
			}
		}
		return files;		
	}

	/**
	 * List files.
	 *
	 * @param directory the directory
	 * @param filter the filter
	 * @param recurse the recurse
	 * @return the collection
	 */
	public static Collection<File> listFiles(File directory, FileFilter filter, boolean recurse) {
		Vector<File> files = new Vector<File>();
		File[] entries = directory.listFiles();

		for (File entry : entries) {
			if (filter == null || filter.accept(entry)) {
				files.add(entry);
			}
			if (recurse && entry.isDirectory()) {
				files.addAll(listFiles(entry, filter, recurse));
			}
		}
		return files;		
	}


	/**
	 * Retrieves the extension of a certain file by simply
	 * getting all the characters that appear after the last '.' of the file.
	 * Files such as xxx.tar.gz will only return .gz
	 * @param file the file to get the extension for
	 * @return return the characters after the last '.' otherwise an empty string
	 */
	public static String getFileExtension(File file) {
		String ext = "";
		ext = file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length());
		return ext;
	}

	/**
	 * Retrieves the filename without the extenstion of a certain file by simply
	 * getting all the characters that appear before the last '.' of the file.
	 * Files such as xxx.tar.gz will only return xxx.tar
	 * @param file the file to get the filename without extension for
	 * @return return the filename without the extension otheriwse an empty string
	 */
	public static String getFileNameNoExtension(File file) {
		String fileName = "";
		fileName = file.getName().substring(0,file.getName().lastIndexOf("."));
		return fileName;
	}


	/**
	 * Tars a directory and puts it in the specified
	 * file path. Both windows and *nix variants can read
	 * the tar file.
	 * @param tarFile the absolute path containing the file name of where to create the tar
	 * @param sourceDirectory the directory to tar
	 * @throws IOException if there was a problem reading the sourceDirectory and outputting to the path tarFile
	 */
	public static void tar(File tarFile, File sourceDirectory) throws IOException {
		FileOutputStream fOut = null;
		BufferedOutputStream bOut = null;
		GzipCompressorOutputStream gzOut = null;
		TarArchiveOutputStream tOut = null;

		try {
			fOut = new FileOutputStream(tarFile);
			bOut = new BufferedOutputStream(fOut);
			gzOut = new GzipCompressorOutputStream(bOut);
			tOut = new TarArchiveOutputStream(gzOut);

			addFileToTarGz(tOut,sourceDirectory,"",true);
		} finally {
			if(tOut != null) {
				tOut.finish();
				tOut.close();
			}

			if(gzOut != null) gzOut.close();
			if(bOut != null) bOut.close();
			if(fOut != null) fOut.close();
		}
	}


	/**
	 * Tars a directory and puts it in the specified
	 * file path. Both windows and *nix variants can read
	 * the tar file.
	 * @param tarFile the absolute path containing the file name of where to create the tar
	 * @param sourceDirectory the directory to tar
	 * @throws IOException if there was a problem reading the sourceDirectory and outputting to the path tarFile
	 */
	public static void tar(String tarFile, String sourceDirectory) throws IOException {
		tar(new File(tarFile),new File(sourceDirectory));
	}

	/**
	 * Move files.
	 *
	 * @param directory the directory
	 * @param files the files
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void moveFiles(File directory, File... files) throws IOException {
		if(!directory.isDirectory())
			throw new IllegalArgumentException("Directory parameter specified is not a directory");
		for(File f : files) {
			FileUtil.moveFileToDirectory(f,directory,true);
		}
	}


	private static void addFileToTarGz(TarArchiveOutputStream tOut, File file, String base, boolean initial) throws IOException { 
		// setting initial on the first call to false will include the root folder
		String entryName = base + file.getName();


		if(file.isFile()) {
			TarArchiveEntry tarEntry = new TarArchiveEntry(file, entryName);
			tOut.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
			tOut.putArchiveEntry(tarEntry);
			FileInputStream fis = new FileInputStream(file);
			try {			
				IOUtils.copy(fis, tOut);
			} finally {
				fis.close(); // make sure to close the stream; otherwise other write operations can not be performed
			}
			tOut.closeArchiveEntry();
		} else {	

			if(!initial) { // initial indicates that it is a root directory
				TarArchiveEntry tarEntry = new TarArchiveEntry(file, entryName);
				tOut.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
				tOut.putArchiveEntry(tarEntry);	
				tOut.closeArchiveEntry();
			}

			File[] children = file.listFiles(); // Recursively 'depth-first' add to tar

			if (children != null) {
				for (File child : children) {
					if(initial)
						addFileToTarGz(tOut, child,"", false);
					else
						addFileToTarGz(tOut, child, entryName + "/", false);					
				}
			}
		}
	}


	/** 
	 * Delete all files and directories in directory but do not delete the
	 * directory itself.
	 * 
	 * @param fDir - directory to delete
	 * @return boolean - success flag
	 */
	public static boolean deleteDirectoryContent(File fDir) {
		boolean bRetval = false;
		if (fDir != null && fDir.isDirectory()) {
			File[] files = fDir.listFiles();
			if (files != null) {
				bRetval = true;
				boolean dirDeleted;
				for (File file : files) {
					if (file.isDirectory()) {
						dirDeleted = deleteDirectoryContent(file);
						if (dirDeleted) {
							bRetval = bRetval && file.delete();
						} else {
							bRetval = false;
						}
					} else {
						bRetval = bRetval && file.delete();
					}
				}
			}
		}
		return bRetval;
	}


	/**
	 * Deletes a given directory.
	 *
	 * @param filePath the file path
	 * @return boolean
	 */
	public static boolean deleteDirectoryContent(String filePath) {
		return deleteDirectoryContent(new File(filePath));
	}


	/**
	 * Deletes contents of directory and also the directory.
	 *
	 * @param file the file
	 */
	public static void deleteDirectoryAndContent(File file) {
		FileUtil.deleteDirectoryContent(file);
		file.delete();
	}


	/**
	 * Delete all the files/directories that
	 * are siblings to the excluded file.
	 * @param excludedFile the file to exclude from deletion
	 */
	public static void deleteAll(File excludedFile) {
		File parent = excludedFile.getParentFile();

		for(File file : parent.listFiles()) {
			if(!file.equals(excludedFile)) {
				if(file.isFile()) 
					file.delete(); 
				else 
					deleteDirectoryAndContent(file);
			}
		}
	}



	/**
	 * Replaces all occurrence of keys of map in a file with the values of that key.
	 *
	 * @param m the m
	 * @param fileNamePath the file name path
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void replace(Map<String, String> m, String fileNamePath) throws IOException {
		File f = new File(fileNamePath);
		if (f.exists() && f.isFile() && f.canWrite()) {
			StringBuffer buff = new StringBuffer();
			FileInputStream fstream = new FileInputStream(f);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {
				// Print the content on the console
				buff.append(strLine + "\n");
			}
			//Close the input stream
			in.close();

			String txt = buff.toString();

			/* replace key with values from the buffer */
			for(Map.Entry<String, String> e : m.entrySet()) {
				txt = txt.replaceAll(e.getKey(), e.getValue());
			}

			FileWriter tfw = new FileWriter(fileNamePath);
			tfw.write(txt);
			tfw.close();
		}
	}

	/**
	 * List all files.
	 *
	 * @param directory the directory
	 * @param recurse true to recursively go down; false otherwise
	 * @return the files in the directory
	 */
	public static File[] listFilesAsArray(File directory, FilenameFilter filter, boolean recurse) {
		Collection<File> files = listFiles(directory, filter, recurse);
		File[] arr = new File[files.size()];
		return files.toArray(arr);
	}



	/**
	 * Moves the given list of files to the destination directory creating directories as needed.
	 *
	 * @param files the files
	 * @param destinationDirectory the destination directory
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void moveFilesToFolder(File files[], File destinationDirectory) throws IOException {
		for (File f : files) {
			moveFileToDirectory(f, destinationDirectory, true);
		}
	}

	/**
	 * List all files.
	 *
	 * @param directory the directory
	 * @param recurse true to recursively go down; false otherwise
	 * @return the files in the directory
	 */
	public static Collection<File> listAllFiles(File directory, boolean recurse){
		return org.apache.commons.io.FileUtils.listFiles(directory, null, true);
	}
}

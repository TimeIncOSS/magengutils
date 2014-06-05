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
package com.timeInc.mageng.util.sftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.timeInc.mageng.util.exceptions.PrettyException;
import com.timeInc.mageng.util.file.FileUtil;


/**
 * Sftp utility methods to upload to an sftp server.
 */
public class SftpServer {
	private static final  Logger log = Logger.getLogger(SftpServer.class);
	

	/**
	 * Upload using an InputStream to the specified location using the 
	 * provided nameofFile
	 *
	 * @param cred the credentials to the sftp server
	 * @param location the location relative to the user's home directory
	 * @param inputStream the input stream to upload
	 * @param nameOfFile the name of file to use for the upload
	 */
	public void upload(ScpCredentials cred, String location, InputStream inputStream, String nameOfFile) {
		log.debug("Uploading to " + cred.getHost());

		JSch jsch = new JSch();
		Session session = null;
		
		ChannelSftp channel = null;

		try {
			session = jsch.getSession(cred.getUsername(), cred.getHost(), cred.getPort());
			session.setPassword(cred.getPassword());
			session.setConfig("StrictHostKeyChecking", "no");
			session.setConfig("PreferredAuthentications", "password,keyboard-interactive");

			session.connect();
			
			channel = (ChannelSftp) session.openChannel("sftp");
			channel.connect();
			
			
			log.debug("Begin uploading file " + nameOfFile + " to remote directory " + location);
			

			createDirectoryOnServer(channel.getHome(), location, channel);

			channel.cd(channel.getHome() + location);

			channel.put(inputStream, nameOfFile);

		} catch (Exception e) {
			throw new PrettyException("Error uploading to: " + cred.getHost(), e);
		} finally {
			if (channel != null)
				channel.disconnect();
			if (session != null)
				session.disconnect();
			IOUtils.closeQuietly(inputStream);
		}
	}

	/**
	 * Upload using the provided file to the specified location.
	 *
	 * @param cred the credentials to the sftp server
	 * @param location the location relative to the user's home directory
	 * @param file the file to upload
	 */
	public void upload(ScpCredentials cred, String location, File file) {
		try {
			upload(cred, location, new FileInputStream(file), file.getName());
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static void createDirectoryOnServer(String homeDir, String remotePath, ChannelSftp channel) throws JSchException, SftpException {
		String path[] = FileUtil.getPath(remotePath);

		String currentPath = homeDir;

		for (String folder : path) {
			try {
				currentPath += "/" + folder;

				channel.stat(currentPath);
			} catch (SftpException e) {
				if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
					channel.mkdir(currentPath);
				} else
					throw new JSchException("Failed to access remote directory " + remotePath, e);
			}
		}
	}
}

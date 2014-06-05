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

import java.io.File;

import com.timeInc.mageng.util.misc.Status;

/**
 * Determines whether the specified file exists.
 */
public class FileExistValidator implements FileValidator {
	
	/* (non-Javadoc)
	 * @see com.timeInc.util.file.FileValidator#validate(java.io.File)
	 */
	@Override
	public Status validate(File file) {
		if(!file.exists() && !file.isFile())
			return Status.getFailure("File " + file.getName() + " does not exist or is not a file");
		else
			return Status.getSuccess();
	}
}

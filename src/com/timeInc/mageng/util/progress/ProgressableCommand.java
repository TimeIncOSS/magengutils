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
package com.timeInc.mageng.util.progress;


/**
 * A ProgressableCommand is an command that is able to inform 
 * a DetailedProgressListener of its progress while executing.
 *
 */
public interface ProgressableCommand {
	
	/**
	 * Executes this command informing the DetailedProgressListener of 
	 * the progress
	 * @param listener the DetailedProgressListener to inform
	 * @throws CommandExecutionException if there was an error executing this command
	 */
	void execute(DetailedProgressListener listener);
	
}

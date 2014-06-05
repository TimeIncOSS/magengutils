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
package com.timeInc.mageng.util.progress.concurrent;

import java.util.List;

import com.timeInc.mageng.util.progress.ProgressableCommand;

/**
 * The Interface ProgressExecutor.
 *
 * @param <T> custom metadata to return for each ProgressableCommand
 */
public interface ProgressExecutor<T> {
	
	/**
	 * Submit progressable commands so that they can be executed.
	 * Running the postProcess command after all commands have finished executing.
	 *
	 * @param commands the commands
	 * @param postProcess the post process to run afterwards
	 * @return the list of T in the order of the submitted commands.
	 */
	List<T> startCommand(List<ProgressableCommand> commands, Runnable postProcess);
	
	/**
	 * Terminate. Cleaning up if necessary.
	 */
	void terminate();
}

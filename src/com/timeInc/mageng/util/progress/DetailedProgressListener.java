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
 * A progress listener that can keep track of a group of progresses which
 * is collectively seen as one process. A DetailedProgressListener provides
 * a description of the current ongoing progress and the percent it takes up from
 * the group of processes.
 * 
 *
 */
public interface DetailedProgressListener extends ProgressListener {
	
	/**
	 * Informs the listener that a new progress has started which takes 
	 * up a certain percent of the TOTAL process
	 * @param description a brief description of the new progress
	 * @param percent the percent it takes up. The sum of percents for all progresses
	 * should not be greater than 100
	 */
	void progressStarted(String description, int percent);
	
	/**
	 * Informs the listener that the TOTAL process has ended
	 * @param success true if the process completed successfully; false otherwise
	 * @param message the message to include when the TOTAL process is complete
	 */
	void ended(boolean success, String message);
}

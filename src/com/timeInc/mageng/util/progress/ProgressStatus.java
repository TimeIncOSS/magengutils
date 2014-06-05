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

import com.timeInc.mageng.util.misc.Status;


/**
 * A thread-safe value class that contains percentage completion 
 * related to a Progress.
 * 
 */

public class ProgressStatus extends Status {
	private boolean isDone = false;
	private int percent = 0;

	/**
	 * Constructs an instance initializing its state to a new
	 * progress.
	 */
	public ProgressStatus()  {
		super("Starting", false);
	}

	/**	
	 * Constructs an instance setting this object to the state that is specified 
	 * by the parameter. 
	 * @param description the current progress description.
	 * @param isDone true if the progress is done false otherwise
	 * @param isError true if there is an error false otherwise
	 * @param percent the current percent complete
	 */
	ProgressStatus(String description, boolean isDone, boolean isError, int percent) {
		super(description,isError);
		this.isDone = isDone;
		this.percent = percent;
	}
	
	
	/**
	 * Determines if the process is done.
	 * @return true if is is; false otherwise
	 */
	public synchronized boolean isDone() {
		return isDone;
	}
	

	/**
	 *  
	 * Gets how many percent the progress has completed.
	 *
	 * @return the percent
	 */
	public synchronized int getPercent() {
		return percent;
	}

	/**
	 * Atomically sets the progress's percent and the current progress description.
	 * If the progress is done; invocation of this method is like a noop.
	 * @param percentComplete the percent complete
	 * @param description a message describing the current progress
	 */
	public synchronized void setPercentComplete(int percentComplete, String description) {	
		if(!isDone) {
			this.description = description;
			percent = percentComplete;
		}
	}

	/**
	 * Sets the progress to successfully completed with a brief 
	 * message.
	 * If the progress is done; invocation of this method is like a noop.
	 *
	 * @param description the message describing the completion. Eg. "Success"
	 */
	public synchronized void setDone(String description) {
		if(!isDone) {
			this.description = description;
			this.isDone = true;
			this.percent = 100;
		}
	}	

	/**
	 * Sets the progress completion status to done but
	 * with an error describing the cause of failure.
	 * If the progress is done; invocation of this method is like a noop.
	 *
	 * @param description the message describing the cause of failure
	 */
	public synchronized void setError(String description) {
		this.isError = true;
		this.isDone = true;
		this.description = description;
	}
}

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
package com.timeInc.mageng.util.misc;

/**
 * A thread-safe value class that represents the result
 * of some process. Whether it is successful or not and any associated 
 * emails.
 * 
 * Thread-safe
 */
public class Status {
	protected String description;
	protected boolean isError;

	/**
	 * Instantiates a new status.
	 *
	 * @param description the description
	 * @param isError the is error
	 */
	public Status(String description, boolean isError) {
		this.isError = isError;
		this.description = description;
	}


	/**
	 * Gets the success.
	 *
	 * @return the success
	 */
	public static Status getSuccess() {
		return new Status("Success", false);
	}

	/**
	 * Gets the failure.
	 *
	 * @param reason the reason
	 * @return the failure
	 */
	public static Status getFailure(String reason) {
		return new Status("Failure: " + reason, true);
	}


	/**
	 * Gets the description of this progress.
	 *
	 * @return the description
	 */
	public synchronized String getDescription() {
		return description;
	}

	/**
	 * Determines if an error occurred in the progress.
	 * If this method returns true then {@link #ProgressStatus.isDone()} will return true
	 * @return true if there is an error; false otherwise
	 */
	public synchronized boolean isError() {
		return isError;
	}
}

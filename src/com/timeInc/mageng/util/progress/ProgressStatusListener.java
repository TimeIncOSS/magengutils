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
 * Simple implementation of DetailedProgressListener that is to be used in conjunction
 * with ProgressStatus. It manages the ProgressStatus by setting it to the appropriate
 * state when certain progress events happen.
 * 
 * <br>
 * 
 * This class calculates and ensures that the ProgressStatus percent never exceeds 100.
 * When an end event fired, it sets the ProgressStatus to done or error depending on the 
 * success flag.
 * 
 * 
 * {@link ProgressStatusListener#progressStarted(String, int)} must be called for every new progress before invoking
 * {@link ProgressStatusListener#inProgress(long, long)} since a DetailedProgressListener represents the sum
 * of progresses.
 * 
 * 
 * 
 * This class is not thread-safe.
 * 
 * @author tchamnongvongse1271
 *
 */

public class ProgressStatusListener implements DetailedProgressListener {
	private static final int MINIMUM_PROGRESS = 0;
	private static final int MAX_PERCENT = 100;
	
	private int totalTaskPercent; // the percentage this task takes up
	private final ProgressStatus status;
	private boolean initial;
	private String currentDescription = null;
	private int remainingPercent; // the remaining percent left for subsequent tasks
	private int startingPercent;

	
	
	/**
	 * Constructs an instance of this with the specified ProgressStatus to 
	 * act upon when certain events occur.
	 * @param status the ProgressStatus to act upon for this listener
	 */
	public ProgressStatusListener(ProgressStatus status) {
		this.status = status;
		initial = true;
		remainingPercent = MAX_PERCENT;
		startingPercent = 0;
	}
	
	
	/**
	 * Sets the ProgressStatus current description to the description parameter and the percent to the
	 * percent parameter. 
	 *
	 * @param description the description
	 * @param percent the percent
	 * @see also #DetailedProgressListener{@link #progressStarted(String, int)}
	 */
	@Override
	public void progressStarted(String description, int percent) {
		
		if((remainingPercent-percent) < 0)
			throw new IllegalArgumentException("Sum of progresses is > " + MAX_PERCENT);
				
		if(initial) {
			status.setPercentComplete(0,description);
			initial = false;
		}
		else {
			startingPercent += totalTaskPercent; // the starting percent is the previous totalTaskPercent
			status.setPercentComplete(startingPercent,description); // a new progress has started that occurred after previous progresses set the percent to match it.
		}	

		this.totalTaskPercent = percent;
		this.remainingPercent -= percent;
		this.currentDescription = description;
		
	}
	

	/**
	 * Sets the ProgressStatus's percent, which is calculated based on how many percent the current progress takes
	 * and the currentProgress/totalProgress. The current progress is the most recent
	 * invocation of {@link #progressStarted(String, int)} prior to invoking this method. If the currentProgress is greater than the 
	 * totalProgress then the progress will take up all of its allocated percentage.
	 *
	 * @param totalProgress the total progress
	 * @param currentProgress the current progress
	 */
	@Override
	public void inProgress(long totalProgress, long currentProgress) {
		if(currentProgress < MINIMUM_PROGRESS) {
			throw new IllegalArgumentException("currentProgress is less than 0 - currentProgress:" + currentProgress + " totalProgress:" + totalProgress);
		}
		
		if(this.currentDescription == null)
			throw new IllegalArgumentException("progressStarted must be called atleast once before invoking this method");
		
		double subPercent;
		if(currentProgress > totalProgress) { // sometimes we may have a buffered progress anything that is greater will automatically be the max subpercent
			subPercent = totalTaskPercent;
		} else {
			subPercent = Math.ceil(totalTaskPercent * (currentProgress * 1.0 / totalProgress)); // round up to the nearest percentage
		}
		
		int progress = startingPercent + (int)subPercent;
		status.setPercentComplete(progress,currentDescription);
	}
	
	/**
	 * Informs the ProgressStatus that the total progress process has completed
	 * successfully or with a failure.
	 *
	 * @param success the success
	 * @param message the message
	 */
	@Override
	public void ended(boolean success, String message) {
		if(success)
			status.setDone(message);
		else 
			status.setError(message);	
	}
}

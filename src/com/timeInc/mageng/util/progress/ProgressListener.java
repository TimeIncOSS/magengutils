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
 * A progress listener that is used to inform the current progress
 * out of a estimated total progress.
 *
 */
public interface ProgressListener {
	/**
	 * Informs forms this listener the progress.
	 * @param total estimated total size of the progress
	 * @param currentProgress the current progress. This should change
	 * if a progression has occurred and should never decrease.
	 */
	public void inProgress(long total, long currentProgress);
}

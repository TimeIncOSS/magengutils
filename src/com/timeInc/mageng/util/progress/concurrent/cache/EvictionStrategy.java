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
package com.timeInc.mageng.util.progress.concurrent.cache;

import java.util.Date;

import com.timeInc.mageng.util.progress.ProgressStatus;
/**
 * Class that determines when a ProgressStatus should be
 * evicted from the ProgressableCache.
 * 
 */
public interface EvictionStrategy {
	
	/**
	 * Determines whether to remove the ProgressStatus
	 * from cache or not
	 * @param date the date the ProgressStatus was put into the cache
	 * @param status the ProgressStatus that is in the cache
	 * @return true to remove the it from the cache; false otherwise
	 */
	boolean evict(Date date, ProgressStatus status);
}

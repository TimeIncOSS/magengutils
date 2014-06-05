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

import static java.util.concurrent.TimeUnit.*;

/**
 * Simple implementation of an eviction strategy where
 * ProgressStatus that are done and has been in the cache for more
 * than 20 minutes are evicted.
 * 
 * Clocks must be synchronized
 * 
 */

public class SimpleEvictionStrategy implements EvictionStrategy {
	private static int MINUTES_BEFORE_EVICT = 20;
	private static long MS_BEFORE_EVICT = MILLISECONDS.convert(MINUTES_BEFORE_EVICT, MINUTES);
	
	/**
	 * @see EvictionStrategy#evict(Date, ProgressStatus)
	 */
	@Override
	public boolean evict(Date date, ProgressStatus status) {
		long duration = System.currentTimeMillis() - date.getTime();
		if(duration >= MS_BEFORE_EVICT) 
			return true;
		
		return false;
	}
	
}

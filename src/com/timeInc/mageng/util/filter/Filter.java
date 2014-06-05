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
package com.timeInc.mageng.util.filter;


/**
 * 
 * Represents a factory for the binary filter operation.
 *
 * @param <T> the filter type
 */
public interface Filter<T> {
	public enum Operation {
		LESS, GREATER, EQ
	}

	/**
	 * Retrieve filter.
	 *
	 * @return the t
	 */
	public T retrieveFilter();
}

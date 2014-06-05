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
package com.timeInc.mageng.util.exceptions;

import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Thrown when an exception of some sort occurred and a user friendly message
 * is to be used.
 */
public class PrettyException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private final String friendlyMsg;
	
	/**
	 * Instantiates a new pretty exception.
	 *
	 * @param friendlyMsg the friendly msg
	 */
	public PrettyException(String friendlyMsg) {
		this(friendlyMsg, null);
	}

	/**
	 * Instantiates a new pretty exception.
	 *
	 * @param friendlyMsg the friendly msg
	 * @param cause the cause
	 */
	public PrettyException(String friendlyMsg, Throwable cause) {
		super(friendlyMsg, cause);
		this.friendlyMsg = friendlyMsg;
	}
	
	/**
	 * Gets the detailed msg.
	 *
	 * @return the detailed msg
	 */
	public String getDetailedMsg() {
		if(super.getCause() == null)
			return friendlyMsg;
		else
			return ExceptionUtils.getStackTrace(super.getCause());
	}
	
	/**
	 * Gets the friendly msg.
	 *
	 * @return the friendly msg
	 */
	public String getFriendlyMsg() {
		return friendlyMsg; 
	}
}

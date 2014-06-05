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
package com.timeInc.mageng.util.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Date utility methods
 *
 */
public class DateUtil {

	/**
	 * Adjusts the current date backwards by a certain amount; depending on the
	 * dateField. @see java.util.Calendar for dateField modifiers.
	 *
	 * @param dateField the date field modifier
	 * @param amount the amount to adjust it by.
	 * @return the adjusted date
	 */
	public static Date getBefore(int dateField, int amount) {
		return getAfter(Calendar.getInstance().getTime(), dateField, -amount);
	}
	
	/**
	 * Adjusts the original date backwards by a certain amount; depending on the
	 * dateField. @see java.util.Calendar for dateField modifiers.
	 *
	 * @param original the original date
	 * @param dateField the date field modifier
	 * @param amount the amount to adjust it by.
	 * @return the adjusted date
	 */
	public static Date getBefore(Date original, int dateField, int amount) {
		return getAfter(original, dateField, -amount);
	}
	
	/**
	 * Adjusts the original date forward by a certain amount; depending on the
	 * dateField. @see java.util.Calendar for dateField modifiers.
	 *
	 * @param original the original date
	 * @param dateField the date field modifier
	 * @param amount the amount to adjust it by.
	 * @return the adjusted date
	 */
	public static Date getAfter(Date original, int dateField, int amount) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(original);
		cal.add(dateField, amount);
		
		return cal.getTime();
	}
	
	/**
	 * Gets the present date at midnight
	 * @return the present date at midnight
	 */
	public static Date presentDay() {
		Calendar now = Calendar.getInstance();
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		return now.getTime();
	}
	
	/**
	 * Converts a date to a string representation
	 * @see java.text.SimpleDateFormat.SimpleDateFormat for valid formats
	 * @param date the date
	 * @param format the format
	 * @return the date
	 */
	public static String getDate(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String val = sdf.format(date);
		return val;
	}
	
	/**
	 * Gets the date from string representation.
	 * @see java.text.SimpleDateFormat.SimpleDateFormat for valid formats
	 *
	 * @param value the date string
	 * @param format the format
	 * @return the date 
	 * @throws ParseException if the format was invalid
	 */
	public static Date getDateFromString(String value, String format) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.parse(value);
	}
}

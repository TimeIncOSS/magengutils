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

import java.text.ParseException;

import com.timeInc.mageng.util.date.DateUtil;
import com.timeInc.mageng.util.filter.Filter.Operation;
import com.timeInc.mageng.util.misc.Precondition;

/**
 * Represents an a filter for EXTJS 4
 */
public class ExtJsFilterAttribute implements FilterAttribute<Object> {
	
	private static final String COMPARISON_EQUALS = "eq";
	private static final String COMPARISON_GREATER = "gt";
	private static final String COMPARISON_LESS = "lt";
	private static final String DATE_FORMAT = "MM/dd/yyyy";
	
	private static final String DATE_TYPE = "date";

	private final String value;
	private final String field;
	private final String type;
	private final String comparison;
	
	

	/**
	 * Instantiates a new ext js filter attribute.
	 *
	 * @param value the value
	 * @param field the field
	 * @param type the type
	 * @param comparison the comparison
	 */
	public ExtJsFilterAttribute(String value, String field, String type, String comparison) {
		Precondition.checkStringEmpty(value,"value");
		Precondition.checkStringEmpty(field,"field");
		Precondition.checkStringEmpty(type,"type");
		Precondition.checkStringEmpty(comparison,"comparison");
		
		
		if(!comparison.equals(COMPARISON_EQUALS) || !comparison.equals(COMPARISON_GREATER)
				|| !comparison.equals(COMPARISON_LESS))
			throw new IllegalArgumentException("Valid operations are " + COMPARISON_EQUALS + " " + COMPARISON_GREATER + " " + COMPARISON_LESS);

		if(!type.equals(DATE_TYPE))
			throw new IllegalArgumentException("Currently the only supported type is " + DATE_TYPE);
		
		this.value = value;
		this.field = field;
		this.type = type;
		this.comparison = comparison;
		
	}
	

	/* (non-Javadoc)
	 * @see com.timeInc.util.filter.FilterAttribute#getOperation()
	 */
	@Override
	public Operation getOperation() {
		Operation op;
		if(comparison.equals(COMPARISON_EQUALS))
			op = Operation.EQ;
		else if(comparison.equals(COMPARISON_GREATER))
			op = Operation.GREATER;
		else if(comparison.equals(COMPARISON_LESS))
			op = Operation.LESS;
		else
			throw new AssertionError("This shouldn't have happened as it was checked in constructor");
		
		return op;
	}



	/* (non-Javadoc)
	 * @see com.timeInc.util.filter.FilterAttribute#getValue()
	 */
	@Override
	public Object getValue() {
		if(type.equals(DATE_TYPE)) {
			try {
				return DateUtil.getDateFromString(value, DATE_FORMAT);
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		}
		else
			throw new AssertionError("This shouldn't have happened as it was checked in constructor");
	}


	/* (non-Javadoc)
	 * @see com.timeInc.util.filter.FilterAttribute#getField()
	 */
	@Override
	public String getField() {
		return field;
	}
}

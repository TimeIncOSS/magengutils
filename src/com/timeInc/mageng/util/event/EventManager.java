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
package com.timeInc.mageng.util.event;

import java.util.List;

/**
 * Implements the observer pattern where
 * {@link EventListener} can (de)register for a certain event.
 *
 * @param <T> event type
 * @param <V> the metadata associated with the event
 */
public interface EventManager<T,V> {
	
	
	/**
	 * Notify the registered listeners that an event with 
	 * the associated metadata has occurred
	 * @param event the event
	 * @param config the metadata associated with the event
	 */
	<K extends T> void notify(K event, V config);
	
	/**
	 * Notify that an event has occurred with different metadata 
	 * @param event the event that occurred
	 * @param configs the metadatas
	 */
	void batchNotify(T event, List<V> configs);
	
	/**
	 * Deregister the EventListener from this event
	 * @param event the event to deregister the listener from
	 * @param listener the listener to register
	 * @return true
	 */
	boolean remove(T event, EventListener<T,V> listener);
	<K extends T> void register(K event, EventListener<T,? extends V> listener);
	<K extends T> void register(EventListener<T,? extends V> listener, K... events);
	
}

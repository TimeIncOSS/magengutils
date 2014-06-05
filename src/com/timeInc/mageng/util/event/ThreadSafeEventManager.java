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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.log4j.Logger;


/**
 * Thread-safe implementation of {@link EventManager}
 */
public class ThreadSafeEventManager<T,V> implements EventManager<T,V> {
	private static Logger log = Logger.getLogger(ThreadSafeEventManager.class);
	
	private ConcurrentHashMap<T,CopyOnWriteArraySet<EventListener<T,V>>> listenerMap = new ConcurrentHashMap<T,CopyOnWriteArraySet<EventListener<T,V>>>();

	/* (non-Javadoc)
	 * @see com.timeInc.util.event.EventManager#notify(java.lang.Object, java.lang.Object)
	 */
	@Override
	public <K extends T> void notify(K event, V config) {	
		Set<EventListener<T,V>> listeners = listenerMap.get(event); 
		
		if(listeners != null) {
			for(EventListener<T,V> listener : listeners) {
				listener.handleEvent(event,config);
			}
		} else
			log.warn("Ignoring event:" + event + " since it is not registered");

	}
	

	/* (non-Javadoc)
	 * @see com.timeInc.util.event.EventManager#remove(java.lang.Object, com.timeInc.util.event.EventListener)
	 */
	@Override
	public boolean remove(T event, EventListener<T,V> listener) {
		Set<EventListener<T,V>> listeners = listenerMap.get(event);
		synchronized(listeners) {  // need to synchronize since there is a check-then-act scenario
			if(listeners!=null) {  
				boolean removed = listeners.remove(listener); 
				if(listeners.isEmpty())
					listenerMap.remove(event);
				return removed;		
			}
		}
		return false;			
	}


	/* (non-Javadoc)
	 * @see com.timeInc.util.event.EventManager#register(com.timeInc.util.event.EventListener, java.lang.Object[])
	 */
	@Override
	public <K extends T> void register(EventListener<T,? extends V> listener, K... events) {
		for(T event : events) {
			register(event,listener);
		}
	}

	/* (non-Javadoc)
	 * @see com.timeInc.util.event.EventManager#register(java.lang.Object, com.timeInc.util.event.EventListener)
	 */
	@Override
	public <K extends T> void register(K event, EventListener<T, ? extends V> listener) {	
		do {
			CopyOnWriteArraySet<EventListener<T,V>> eventSet = listenerMap.get(event);
			if(eventSet == null) {
				CopyOnWriteArraySet<EventListener<T,V>> es = new CopyOnWriteArraySet<EventListener<T,V>>();

				eventSet = listenerMap.putIfAbsent(event,es);

				if(eventSet == null) {
					eventSet = es;
				}								
			}
			synchronized(eventSet) {
				eventSet.add((EventListener<T,V>)listener);
			}

		} while(listenerMap.get(event) == null); // loop because the remove method might have removed the CopyOnWriteArraySet if there was only one element left...
	}


	/* (non-Javadoc)
	 * @see com.timeInc.util.event.EventManager#batchNotify(java.lang.Object, java.util.List)
	 */
	@Override
	public void batchNotify(T event, List<V> configs) {
		for(V config : configs) {
			notify(event,config);
		}
	}
}

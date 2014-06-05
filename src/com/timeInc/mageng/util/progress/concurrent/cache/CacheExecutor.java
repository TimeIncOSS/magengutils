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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.timeInc.mageng.util.progress.ProgressStatus;
import com.timeInc.mageng.util.progress.ProgressableCommand;
import com.timeInc.mageng.util.progress.concurrent.CallableFactory;
import com.timeInc.mageng.util.progress.concurrent.CallableFactory.CallableProgress;
import com.timeInc.mageng.util.progress.concurrent.CallableFactory.GroupedCallables;
import com.timeInc.mageng.util.progress.concurrent.ProgressExecutor;

/**
 * 
 * A thread-safe cache that is used to keep track of ProgressStatuses for ProgressableCommands.
 * 
 * A ProgressStatus gets stored in the cache when a ProgressableCommand is scheduled to be executed by invoking
 * {@link CacheExecutor#startCommand(List)} or {@link CacheExecutor#startCommand(List, Command)}. The
 * identifier {@link ProgressableCommand#getId()} is used as the identifier for the ProgressStatuses that reside in the 
 * cache.
 * 
 *
 */

public class CacheExecutor implements ProgressExecutor<String> {
	private static final Logger log = Logger.getLogger(CacheExecutor.class);
	private static final int DEFAULT_CLEAN_CACHE_THRESHOLD = 200;

	private final ConcurrentHashMap<String, ProgressStatusDate> progressMap = new ConcurrentHashMap<String,ProgressStatusDate>(); // the cache to hold progresses

	private final ExecutorService executor;
	private final int cacheSize;  // the number of complete progresses to maintain before evicting it
	private final EvictionStrategy evictStrategy;
	private final IdGenerator idGen;
	private final CallableFactory factory;


	/**
	 * Constructs an instance using a default threshold with {@link SimpleEvictionStrategy} as
	 * the eviction strategy. ProgressableCommands are executed 
	 * using a {@link Executors#newCachedThreadPool()}
	 */
	public CacheExecutor() {
		this(Executors.newCachedThreadPool(), DEFAULT_CLEAN_CACHE_THRESHOLD, new SimpleEvictionStrategy(), new UniqueIdGenerator(), new CallableFactory());
	}

	public CacheExecutor(ExecutorService executor) {
		this(executor, DEFAULT_CLEAN_CACHE_THRESHOLD, new SimpleEvictionStrategy(), new UniqueIdGenerator(), new CallableFactory());
	}

	/**
	 * Constructs an instance using the provided executor for executing the ProgressableCommand. The specified
	 * EvictionStrategy will be used when the cache reaches cacheSize.
	 * @param executor the ExecutorService to use when executing the p
	 * @param cacheSize the maximum number of ProgressStatues to maintain in the cache
	 * @param evictStrategy the eviction strategy to use when the cache is full
	 */
	public CacheExecutor(ExecutorService executor, int cacheSize, 
			EvictionStrategy evictStrategy, IdGenerator idGen,
			CallableFactory factory) {

		this.executor = executor;
		this.cacheSize = cacheSize;
		this.evictStrategy = evictStrategy;
		this.idGen = idGen;
		this.factory = factory;
	}

	/**
	 * Returns an instance of the ProgressStatus for a certain ProgressableCommand
	 * using its identifier. 
	 * @param id the id of the ProgressableCommand
	 * @return the ProgressStatus for that ProgressableCommand or null if no such id exists in the cache
	 */
	public ProgressStatus getStatus(String id) {
		return progressMap.get(id).status;
	}


	/**
	 * Submits the list of ProgressableCommands to be executed by the ExecutorService
	 * and adds a corresponding ProgressStatus to the cache that can be retrieved using
	 * {@link CacheExecutor#getStatus(String)}
	 * @param commands the list of commands to execute and add a ProgressStatus to the cache
	 */
	public List<String> startCommand(List<ProgressableCommand> commands) {
		return startCommand(commands, null);
	}


	/**
	 * Submits the list of ProgressableCommands to be executed by the ExecutorService and provides a Command 
	 * callback that get executed after all ProgressableCommands have finished executing
	 * Adds a corresponding ProgressStatus to the cache that can be retrieved using
	 * {@link CacheExecutor#getStatus(String)}.
	 * @param commands the list of commands to execute and add a ProgressStatus to the cache
	 * @param postProcess the postprocess Command to execute after all ProgressableCommands have finished executing
	 */
	public List<String> startCommand(List<ProgressableCommand> commands, final Runnable postProcess) {	
		clearCache();

		List<String> idList = new ArrayList<String>(); 
		GroupedCallables progressCommand = factory.produce(commands,  postProcess);

		for(CallableProgress callable : progressCommand.commands) {
			while(true) {
				String id = idGen.getId();
				ProgressStatusDate progressStatus = progressMap.putIfAbsent(id, new ProgressStatusDate(callable.getStatus()));
				
				if(progressStatus == null) {
					executor.submit(callable);
					idList.add(id);
					break;
				}
			}
		}

		executor.submit(progressCommand.post); 

		return Collections.unmodifiableList(idList);
	}


	private void clearCache() { // weak guarantees but most of the time will allow a clearCache
		log.info("Clearing progress cache");

		if(progressMap.size() > cacheSize) { // TODO use done list to prevent traversal of entire map
			for(Map.Entry<String,ProgressStatusDate> entry : progressMap.entrySet()) {
				ProgressStatusDate pd = entry.getValue();
				Date date = entry.getValue().date;
				if(pd.status.isDone() && evictStrategy.evict(date, pd.status)) {
					log.info("Removing progress status from cache " + entry.getKey());
					progressMap.remove(entry.getKey());
				}
			}
		}
	}
	

	private static class ProgressStatusDate {
		final Date date;
		final ProgressStatus status;

		public ProgressStatusDate(ProgressStatus status) {
			this.date = new Date();
			this.status = status;
		}
	}

	/* (non-Javadoc)
	 * @see com.timeInc.util.progress.concurrent.ProgressExecutor#terminate()
	 */
	public void terminate() {
		executor.shutdown();
		try {
			if(!executor.awaitTermination(10,TimeUnit.SECONDS)) {
				executor.shutdownNow();
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}	
	}

	private static class UniqueIdGenerator implements IdGenerator {
		@Override
		public String getId() {
			return UUID.randomUUID().toString();
		}
	}
}

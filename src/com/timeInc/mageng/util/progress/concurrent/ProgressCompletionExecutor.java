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
package com.timeInc.mageng.util.progress.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.timeInc.mageng.util.misc.Status;
import com.timeInc.mageng.util.progress.ProgressStatus;
import com.timeInc.mageng.util.progress.ProgressableCommand;
import com.timeInc.mageng.util.progress.concurrent.CallableFactory.CallableProgress;
import com.timeInc.mageng.util.progress.concurrent.CallableFactory.GroupedCallables;

/**
 * Executes ProgressableCommand by blocking until all tasks have finished completing.
 */
public class ProgressCompletionExecutor implements ProgressExecutor<Status> {
	private static final int DEFAULT_THREAD = Runtime.getRuntime().availableProcessors();
	private static final Logger log = Logger.getLogger(ProgressCompletionExecutor.class);
	
	private final CallableFactory factory;
	private final ExecutorService executor;
	
	/**
	 * Instantiates a new progress completion executor.
	 */
	public ProgressCompletionExecutor() {
		this(new CallableFactory(), new ThreadPoolExecutor(DEFAULT_THREAD, DEFAULT_THREAD, 0, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new ThreadPoolExecutor.CallerRunsPolicy()));
	}
	
	/**
	 * Instantiates a new progress completion executor.
	 *
	 * @param factory the factory
	 * @param executor the executor
	 */
	public ProgressCompletionExecutor(CallableFactory factory, ExecutorService executor) {
		this.factory = factory;
		this.executor = executor;
	}
	
	/**
	 * Instantiates a new progress completion executor.
	 *
	 * @param executor the executor
	 */
	public ProgressCompletionExecutor(ExecutorService executor) {
		this(new CallableFactory(), executor);
	}
	
	/* (non-Javadoc)
	 * @see com.timeInc.util.progress.concurrent.ProgressExecutor#startCommand(java.util.List, java.lang.Runnable)
	 */
	public List<Status> startCommand(List<ProgressableCommand> commands, final Runnable postProcess) {
		CompletionService<ProgressStatus> ecs = new ExecutorCompletionService<ProgressStatus>(executor);
		
		GroupedCallables progressCommand = factory.produce(commands,  postProcess);
		
		
		for(CallableProgress callable : progressCommand.commands) 
				ecs.submit(callable);
		
		executor.submit(progressCommand.post);
		
		List<Status> completedProgress = new ArrayList<Status>();

		try {
			for(@SuppressWarnings("unused") ProgressableCommand command : commands) {
				completedProgress.add(ecs.take().get());
			}
			
		} catch(InterruptedException ex) {
			Thread.currentThread().interrupt();
		} catch(ExecutionException ex) {
			log.error("This shouldn't have happened", ex.getCause());
		}
		
		return completedProgress;
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
}

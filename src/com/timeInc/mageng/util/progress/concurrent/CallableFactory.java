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
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import com.timeInc.mageng.util.progress.DetailedProgressListener;
import com.timeInc.mageng.util.progress.ProgressStatus;
import com.timeInc.mageng.util.progress.ProgressStatusListener;
import com.timeInc.mageng.util.progress.ProgressableCommand;

/**
 * A factory for creating Callable<ProgressStatus>.
 */
public class CallableFactory {
	
	/**
	 * Produce the CallableProgress for each command and
	 * the postprocess command to execute after all commands
	 * are finished
	 *
	 * @param commands the commands
	 * @param postProcess the post process command
	 * @return GroupedCallables
	 */
	public GroupedCallables produce(List<ProgressableCommand> commands, final Runnable postProcess) {
		final CountDownLatch latch = new CountDownLatch(commands.size());
		
		List<CallableProgress> list = new ArrayList<CallableProgress>();
		
		for(ProgressableCommand command: commands) {  // throw an exception if there is an ongoing progress with the same id
			ProgressStatus progress = new ProgressStatus();
			DetailedProgressListener listener = new ProgressStatusListener(progress); 			
			list.add(new CallableProgress(latch, command, listener, progress));
		} 
		
		return new GroupedCallables(list, new Runnable() {
			@Override
			public void run() {
				try {
					latch.await();
					postProcess.run();
				} catch(InterruptedException ie) {
					Thread.currentThread().interrupt();
				}
			}
		});
	}
	
	public static class GroupedCallables {
		public final List<CallableProgress> commands;
		public final Runnable post;
		
		public GroupedCallables(List<CallableProgress> commands, Runnable post) {
			this.commands = commands;
			this.post = post;
		}
	}

	public static class CallableProgress implements Callable<ProgressStatus> {
		private final ProgressableCommand command;
		private final DetailedProgressListener listener;
		private final ProgressStatus status;
		private final CountDownLatch latch;

		/**
		 * Instantiates a new CallableProgress
		 *
		 * @param latch the latch
		 * @param command the command
		 * @param listener the listener
		 * @param status the status
		 */
		public CallableProgress(CountDownLatch latch, ProgressableCommand command, DetailedProgressListener listener, ProgressStatus status) {
			this.command = command;
			this.listener = listener;
			this.status = status;
			this.latch = latch;
		}

		/**
		 *  Executes the ProgressableCommand.
		 *  If an exception is thrown, the status is set to an error
		 *  with a message of Internal Error
		 */
		@Override
		public ProgressStatus call()  {
			try {
				command.execute(listener);
			} catch(Exception e) {
				status.setError("Internal Error");
			} finally {
				latch.countDown();
			}
			
			return status;
		}	
		
		/**
		 * Gets the status.
		 *
		 * @return the status
		 */
		public ProgressStatus getStatus() {
			return status;
		}
	}

//	/** 
//	 * Wrapper class for ProgressStatus that adds a date state to identify
//	 * when ProgressStatus was put in the cache. 
//	 */
//	public static class ProgressStatusDate extends ProgressStatus {
//		private final Date date;
//
//		public ProgressStatusDate(Date date) {
//			this.date = date;
//		}
//
//		public Date getDate() {
//			return date;
//		}
//	}

}

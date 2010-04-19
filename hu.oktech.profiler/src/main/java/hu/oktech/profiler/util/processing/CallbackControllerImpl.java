/* 
 * Copyright (c) 2009-2010, OKTECH-Info Kft. - All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Please check attribution requirements at
 * 
 * http://code.google.com/p/oktech-profiler/wiki/License
 * 
 */
package hu.oktech.profiler.util.processing;

/**
 * The default {@link CallbackController} implementation. It is basic, doesn't
 * contains limits or timeouts.
 * 
 * @author Istvan Soos
 * 
 */
public class CallbackControllerImpl implements CallbackController {

	protected volatile boolean running = true;
	protected volatile long counter = 0;

	public long getCounter() {
		return counter;
	}

	public void stop() {
		running = false;
	}

	public boolean isRunning() {
		return running;
	}

	public void increment() {
		counter++;
	}

}

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
package hu.oktech.profiler.runtime.controller;

import java.lang.instrument.Instrumentation;

/**
 * Multiple controller can be initialized and used in a runtime. This interface
 * enables easy access to them.
 * 
 * @author Istvan Soos
 */
public interface ControllerService {

	public void setInstrumentation(Instrumentation instrumentation);

	public void register(String name, String type, String config);

	public void start(String name);

	public boolean isRunning(String name);

	public void stop(String name);

	public void remove(String name);

	public String execute(String name, String command);

	public Controller get(String name);

}

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
package hu.oktech.profiler.runtime.io;

import hu.oktech.profiler.util.processing.CallbackController;
import hu.oktech.profiler.util.processing.CallbackControllerImpl;
import hu.oktech.profiler.util.processing.ObjectCallback;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Queue;

/**
 * Helper class that reads a dump file written by {@link DumpSingleFileTimer}.
 * 
 * @author Istvan Soos
 */
public class DumpFileReader {

	protected File[] files;

	public DumpFileReader(File... files) {
		super();
		this.files = files;
	}

	public long read(final Queue<Object> queue) {
		return read(new ObjectCallback() {

			public void process(Object object, CallbackController controller) {
				queue.add(object);
			}
		});
	}

	public long read(ObjectCallback callback) {
		CallbackControllerImpl controller = new CallbackControllerImpl();
		if (files != null) {
			for (File file : files) {
				if (!controller.isRunning())
					break;

				try {
					BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file), 655360);

					for (;;) {
						if (controller.isRunning()) {
							try {
								ObjectInputStream ois = new ObjectInputStream(bis);

								while (ois.readBoolean() && controller.isRunning()) {
									callback.process(ois.readObject(), controller);
									controller.increment();
								}
							} catch (EOFException ex) {
								break;
							}

							continue;
						} else {
							break;
						}
					}

					bis.close();
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				} catch (ClassNotFoundException ex) {
					throw new RuntimeException(ex);
				}
			}
		}
		return controller.getCounter();
	}

	public File[] getFiles() {
		return files;
	}

	public void setFiles(File[] files) {
		this.files = files;
	}

}

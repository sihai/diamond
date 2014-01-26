/**
 * Copyright 2013 openteach
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * 
 */
package com.openteach.diamond.repository.client;

/**
 * 
 * @author sihai
 *
 */
public class DataEvent {

	/**
	 * Event type
	 */
	private EventType type;

	/**
	 * old data
	 */
	private Data oldOne;
	
	/**
	 * new data
	 */
	private Data newOne;
	
	public EventType getType() {
		return type;
	}

	public Data getOldOne() {
		return oldOne;
	}

	public Data getNewOne() {
		return newOne;
	}

	//=================================================================
	//					Event type
	//=================================================================
	public static enum EventType {
		NEW,
		MODIFIED,
		DELETED;
	}
	
	//=================================================================
	//					Event builder
	//=================================================================
	public static interface DataEventBuilder {
		/**
		 * 
		 * @return
		 */
		DataEvent build();
	}
	
	public static class NewDataEventBuilder implements DataEventBuilder {
		
		/**
		 * new data
		 */
		private Data newOne;
		
		public NewDataEventBuilder withNewData(Data newData) {
			this.newOne = newData;
			return this;
		}

		@Override
		public DataEvent build() {
			DataEvent event = new DataEvent();
			event.type = EventType.NEW;
			event.newOne = this.newOne;
			return event;
		}
	}
	
	public static class ModifiedDataEventBuilder implements DataEventBuilder {
		
		/**
		 * old data
		 */
		private Data oldOne;
		
		/**
		 * new data
		 */
		private Data newOne;
		
		public ModifiedDataEventBuilder withOldData(Data oldData) {
			this.oldOne = oldData;
			return this;
		}
		
		public ModifiedDataEventBuilder withNewData(Data newData) {
			this.newOne = newData;
			return this;
		}

		@Override
		public DataEvent build() {
			DataEvent event = new DataEvent();
			event.type = EventType.MODIFIED;
			event.oldOne = this.oldOne;
			event.newOne = this.newOne;
			return event;
		}
	}
	
	public static class DeletedDataEventBuilder implements DataEventBuilder {
		
		/**
		 * old data
		 */
		private Data oldOne;
		
		public DeletedDataEventBuilder withOldData(Data oldData) {
			this.oldOne = oldData;
			return this;
		}

		@Override
		public DataEvent build() {
			DataEvent event = new DataEvent();
			event.type = EventType.DELETED;
			event.oldOne = this.oldOne;
			return event;
		}
	}
}

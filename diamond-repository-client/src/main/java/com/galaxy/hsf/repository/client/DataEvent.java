/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.repository.client;

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

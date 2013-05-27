/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service;

import com.galaxy.hsf.service.metadata.Consumer;
import com.galaxy.hsf.service.metadata.MetadataService;
import com.galaxy.hsf.service.metadata.Publisher;


/**
 * 
 * @author sihai
 *
 */
public class HSFServiceHolderComponent {

	private static Publisher publisher;
	private static Consumer consumer;
    private static MetadataService metadataService;
    
    public void setPublisher(Publisher publisher) {
        HSFServiceHolderComponent.publisher = publisher;
    }
    public void unsetPublisher(Publisher publisher) {
        if (HSFServiceHolderComponent.publisher == publisher) {
            HSFServiceHolderComponent.publisher = null;
        }
    }
    
    public void setConsumer(Consumer consumer) {
        HSFServiceHolderComponent.consumer = consumer;
    }
    public void unsetConsumer(Consumer consumer) {
        if (HSFServiceHolderComponent.consumer == consumer) {
            HSFServiceHolderComponent.consumer = null;
        }
    }

    public void setMetadataService(MetadataService metadataService) {
        HSFServiceHolderComponent.metadataService = metadataService;
    }
    public void unsetMetadataService(MetadataService metadataService) {
        if (HSFServiceHolderComponent.metadataService == metadataService) {
            HSFServiceHolderComponent.metadataService = null;
        }
    }

    public static Publisher getPublisher() {
        return HSFServiceHolderComponent.publisher;
    }
    
    public static Consumer getConsumer() {
        return HSFServiceHolderComponent.consumer;
    }
    
    static public MetadataService getMetadataService() {
        return HSFServiceHolderComponent.metadataService;
    }
}

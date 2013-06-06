/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.diamond.metadata.impl;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.galaxy.diamond.metadata.MetadataWriteService;
import com.galaxy.diamond.metadata.PublishHook;
import com.galaxy.diamond.metadata.ServiceMetadata;
import com.galaxy.diamond.repository.client.Data;
import com.galaxy.diamond.repository.client.Key;
import com.galaxy.diamond.repository.client.RepositoryClient;
import com.galaxy.diamond.repository.client.exception.SequenceNotMatchException;

/**
 * 
 * @author sihai
 *
 */
public class DefaultMetadataWriteService extends AbstractMetadataService implements MetadataWriteService {

	private Log logger = LogFactory.getLog(DefaultMetadataWriteService.class);
	
	/**
	 * 
	 * @param repositoryClient
	 */
	public DefaultMetadataWriteService(RepositoryClient repositoryClient) {
		super(repositoryClient);
	}

	@Override
	public void register(ServiceMetadata metadata) {
		for(int i = 0;; i++) {
			try {
				_put_(metadata);
				break;
			} catch (SequenceNotMatchException e) {
				logger.warn(String.format("Put metadata failed, sequence not match, so try do it again, tryed %d times", i), e);
			}
		}
	}

	@Override
	public void unregister(ServiceMetadata metadata) {
		for(int i = 0;; i++) {
			try {
				_delete_(metadata);
				break;
			} catch (SequenceNotMatchException e) {
				logger.warn(String.format("Put metadata failed, sequence not match, so try do it again, tryed %d times", i), e);
			}
		}
	}

	@Override
	public void addPublishHook(PublishHook hook) {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * @param metadata
	 * @throws SequenceNotMatchException 
	 */
	private void _put_(ServiceMetadata metadata) throws SequenceNotMatchException {
		String name = metadata.getUniqueName();
		Key key = repositoryClient.newKey(name);
		Data data = repositoryClient.get(key);
		if(null != data) {
			String str = (String)data.getValue();
			JSONObject json = JSONObject.fromObject(str);
			ServiceMetadata old = (ServiceMetadata)JSONObject.toBean(json, ServiceMetadata.class);
			for(Map.Entry<String, List<String>> e : old.getAddressMap().entrySet()) {
				metadata.addAddresses(e.getKey(), e.getValue());
			}
		} else {
			data = repositoryClient.newData(key, JSONObject.fromObject(metadata).toString(), -1);
		}
		repositoryClient.put(data);
	}
	
	/**
	 * 
	 * @param metadata
	 * @throws SequenceNotMatchException 
	 */
	private void _delete_(ServiceMetadata metadata) throws SequenceNotMatchException {
		String name = metadata.getUniqueName();
		Key key = repositoryClient.newKey(name);
		Data data = repositoryClient.get(key);
		if(null != data) {
			String str = (String)data.getValue();
			JSONObject json = JSONObject.fromObject(str);
			ServiceMetadata old = (ServiceMetadata)JSONObject.toBean(json, ServiceMetadata.class);
			for(Map.Entry<String, List<String>> e : old.getAddressMap().entrySet()) {
				metadata.removeAddresses(e.getKey(), e.getValue());
			}
			if(old.isUnregisterable()) {
				repositoryClient.delete(key);
			} else {
				repositoryClient.put(data);
			}
		}
	}
}

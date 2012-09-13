package com.kdcloud.client.gwt.rest;

import java.util.ArrayList;

import org.restlet.client.resource.ClientProxy;
import org.restlet.client.resource.Get;
import org.restlet.client.resource.Put;
import org.restlet.client.resource.Result;

import com.kdcloud.server.domain.Dataset;

public interface UserDataResourceProxy extends ClientProxy {
	
	@Get
	public void list(Result<ArrayList<Dataset>> callback);
	
	@Put
	public void createDataset(Dataset dto, Result<Long> callback);

}

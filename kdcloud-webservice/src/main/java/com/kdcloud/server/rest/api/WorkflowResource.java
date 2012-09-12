package com.kdcloud.server.rest.api;

import org.restlet.representation.Representation;
import org.restlet.data.Form;
import org.restlet.resource.Post;

import com.kdcloud.server.domain.ServerParameter;

public interface WorkflowResource {
	
	public static final String URI = "/workflow/" + ServerParameter.WORKFLOW_ID;
	
	@Post
	public Representation execute(Form form);

}

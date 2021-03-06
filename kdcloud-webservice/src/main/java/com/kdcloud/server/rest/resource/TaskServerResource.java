package com.kdcloud.server.rest.resource;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import weka.core.Instances;

import com.kdcloud.lib.rest.api.TaskResource;
import com.kdcloud.lib.rest.ext.InstancesRepresentation;
import com.kdcloud.server.entity.Task;

public class TaskServerResource extends KDServerResource implements TaskResource {

	@Override
	public Representation retriveOutput() {
		Task t = (Task) getEntityMapper().findByUUID(getResourceIdentifier());
		if (!t.isCompleted())
			throw new ResourceException(Status.CLIENT_ERROR_LOCKED);
		Instances result = getInstancesMapper().load(t.getResult());
		return new InstancesRepresentation(MediaType.TEXT_CSV, result);
	}

}

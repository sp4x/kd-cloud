package com.kdcloud.server.rest.resource;

import java.util.logging.Level;

import org.restlet.Application;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;

import com.kdcloud.server.domain.Report;
import com.kdcloud.server.domain.ServerParameter;
import com.kdcloud.server.domain.datastore.Task;
import com.kdcloud.server.domain.datastore.Workflow;
import com.kdcloud.server.rest.api.WorkflowResource;
import com.kdcloud.server.rest.ext.InstancesRepresentation;

public class WorkflowServerResource extends WorkerServerResource implements
		WorkflowResource {

	private Workflow workflow;

	public WorkflowServerResource() {
	}

	public WorkflowServerResource(Application application, Workflow workflow) {
		super(application);
		this.workflow = workflow;
	}
	
	@Override
	public Representation handle() {
		String workflowId = getParameter(ServerParameter.WORKFLOW_ID);
		workflow = getPersistenceContext().getWorkflowDao().findById(new Long(workflowId));
		if (workflow == null)
			return notFound();
		return super.handle();
	}

	@Override
	@Post
	public Representation execute(Form form) {
		Task task = new Task();
		task.setApplicant(user);
		task.setWorkflow(workflow);
		Report report = execute(task, form);
		if (report.getDom() != null)
			return new DomRepresentation(MediaType.APPLICATION_XML, report.getDom());
		if (report.getData() != null)
			return new InstancesRepresentation(MediaType.TEXT_CSV, report.getData());
		return null;
	}

}

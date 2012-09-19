package com.kdcloud.lib.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.restlet.data.Form;
import org.restlet.data.Parameter;
import org.restlet.representation.Representation;

import weka.core.Instances;

import com.kdcloud.lib.rest.ext.InstancesRepresentation;

@XmlAccessorType(XmlAccessType.NONE)
public class ServerAction implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@XmlElement
	String uri;
	
	@XmlElement
	ServerMethod method;
	
	@XmlElement(name="postParameter")
	Set<ServerParameter> postParams;
	
	ArrayList<Parameter> postForm;
	
	boolean repeat;
	
	long sleepTime;
	
	public ServerAction() {
		this.postParams = new HashSet<ServerParameter>();
		this.postForm = new ArrayList<Parameter>();
	}


	public ServerAction(String uri, ServerMethod method,
			boolean repeat, long sleepTime) {
		super();
		this.uri = uri;
		this.method = method;
		this.repeat = repeat;
		this.sleepTime = sleepTime;
		this.postParams = new HashSet<ServerParameter>();
		this.postForm = new ArrayList<Parameter>();
	}


	public ServerAction(ServerAction serverAction) {
		this.method = serverAction.method;
		this.repeat = serverAction.repeat;
		this.sleepTime = serverAction.sleepTime;
		this.uri = serverAction.uri;
		this.postParams = serverAction.postParams;
		this.postForm = serverAction.postForm;
	}


	public ServerMethod getMethod() {
		return method;
	}

	public void setMethod(ServerMethod method) {
		this.method = method;
	}


	public String getUri() {
		return uri;
	}

	public boolean isRepeat() {
		return repeat;
	}

	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}

	public long getSleepTime() {
		return sleepTime;
	}
	
	public long getSleepTimeInMillis() {
		return sleepTime * 1000;
	}

	public void setSleepTime(long sleepTime) {
		this.sleepTime = sleepTime;
	}
	
	public List<ServerParameter> getParams() {
		List<ServerParameter> params = 
				new LinkedList<ServerParameter>();
		params.addAll(ServerParameter.getParamsFromUri(uri));
		for (ServerParameter p : postParams) {
			if (!p.hasValue())
				params.add(p);
		}
		return params;
	}
	
	public boolean hasParameters() {
		return !getParams().isEmpty();
	}
	
	public boolean addParameter(ServerParameter param) {
		return postParams.add(param);
	}

	public void setParameter(ServerParameter param, String value) {
		if (ServerParameter.getParamsFromUri(uri).contains(param)) {
			uri = uri.replaceAll(param.getPattern(), value);
		} else if (postParams.remove(param)) {
			postForm.add(new Parameter(param.getName(), value));
		}
	}
	
	public Representation getPostRepresentation() {
		for (ServerParameter p : postParams) {
			Parameter restletParameter = p.toRestletParameter();
			if (!postForm.contains(restletParameter) && p.hasValue())
				postForm.add(restletParameter);
		}
		return new Form(postForm).getWebRepresentation();
	}
	
	public Representation getPutRepresentation(Instances instances) {
		return new InstancesRepresentation(instances);
	}
	
	@Override
	public String toString() {
		String postString = " ( ";
		for (ServerParameter p : postParams) {
			postString = postString + p.getName() + " ";
		}
		postString = postString + ")";
		return method.toString() + ": " + uri + postString;
	}
	
}

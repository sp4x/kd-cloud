package com.kdcloud.server.rest.application;

import java.io.Serializable;
import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.routing.Router;
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.security.MapVerifier;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.kdcloud.server.engine.QRS;
import com.kdcloud.server.rest.resource.UserDataServerResource;
import com.kdcloud.weka.core.Attribute;
import com.kdcloud.weka.core.DenseInstance;
import com.kdcloud.weka.core.Instances;

public class RestletTestCase {

	static final String HOST = "http://localhost";
	static final int PORT = 8887;
	static final String BASE_URI = HOST + ":" + PORT;

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig());
	
	Context context = new GAEContext();

	Application testApp = new Application() {

		@Override
		public Restlet createInboundRoot() {
			Router router = new Router(getContext());
			helper.setUp();
			router.attachDefault(new KDApplication(context));
			helper.tearDown();
			return router;
		}

		@Override
		public void handle(Request request, Response response) {
			helper.setUp();
			super.handle(request, response);
			helper.tearDown();
		}

	};

	Component component;

	@Before
	public void setUp() {
		component = new Component();
		component.getServers().add(Protocol.HTTP, PORT);

		ChallengeAuthenticator guard = new ChallengeAuthenticator(null,
				ChallengeScheme.HTTP_BASIC, "testRealm");
		MapVerifier mapVerifier = new MapVerifier();
		mapVerifier.getLocalSecrets().put("login", "secret".toCharArray());
		guard.setVerifier(mapVerifier);
		guard.setNext(testApp);

		component.getDefaultHost().attachDefault(guard);
		try {
			component.start();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@After
	public void tearDown() {
		try {
			component.stop();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void test() {
		ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
		ChallengeResponse authentication = new ChallengeResponse(scheme,
				"login", "secret");

		ClientResource data = new ClientResource(BASE_URI
				+ UserDataServerResource.URI);
		data.setChallengeResponse(authentication);
		Instances instances = new Instances("test", QRS.getWorkflow().getInputSpec(), 0);
		instances.add(new DenseInstance(0, new double[]{1, 2}));
		ObjectRepresentation<Serializable> instancesRep = new ObjectRepresentation<Serializable>(instances);
		try {
			Representation response = data
					.put(instancesRep, MediaType.APPLICATION_JAVA_OBJECT);
			ObjectRepresentation<Serializable> id = new ObjectRepresentation<Serializable>(
					response);
			Assert.assertTrue(id.getObject() instanceof Long);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}

	}

}

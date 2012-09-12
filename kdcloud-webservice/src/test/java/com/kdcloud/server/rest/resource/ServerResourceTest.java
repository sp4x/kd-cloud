package com.kdcloud.server.rest.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Application;
import org.restlet.Context;
import org.restlet.data.Form;

import weka.core.DenseInstance;
import weka.core.Instances;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.kdcloud.server.domain.Report;
import com.kdcloud.server.domain.ServerParameter;
import com.kdcloud.server.domain.datastore.DataTable;
import com.kdcloud.server.domain.datastore.User;
import com.kdcloud.server.domain.datastore.Workflow;
import com.kdcloud.server.engine.embedded.EmbeddedEngine;

public class ServerResourceTest {

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig()
	/* .setDefaultHighRepJobPolicyUnappliedJobPercentage(100) */);

	public static final String USER_ID = TestContext.USER_ID;
	
	private Context context = new TestContext();

	private Application application = new Application(context);
	
	UserDataServerResource userDataResource = new UserDataServerResource(application);

	@Before
	public void setUp() throws Exception {
		helper.setUp();
	}

	@After
	public void tearDown() throws Exception {
		helper.tearDown();
	}

	@Test
	public void testUserData() {
		Long id = userDataResource.createDataset();
		assertNotNull(id);

		User u = userDataResource.userDao.findById(USER_ID);
		assertNotNull(u);
		
		assertNotNull(u.getTable());
		
		DataTable dataset = userDataResource.dataTableDao.findById(id);
		assertNotNull(dataset.getId());
	}

	@Test
	public void testDataset() {
		Long id = userDataResource.createDataset();
		DataTable dataset = userDataResource.dataTableDao.findById(id);

		DatasetServerResource datasetResource = new DatasetServerResource(application, dataset);
		double[] cells = { 1, 2 };
		Instances data = new Instances(dataset.getInstances());
		data.add(new DenseInstance(0, cells));
		datasetResource.uploadData(data);
		
		assertEquals(1, datasetResource.getData().size());

//		datasetResource.deleteDataset();
//		assertNull(datasetResource.dataTableDao.findById(id));
	}

	@Test
	public void testModalities() {
//		Utils.initDatabase(context);
//		
//		ModalitiesServerResource modalitiesResource = new ModalitiesServerResource(application);
//		List<ModEntity> list = modalitiesResource.listModalities().asList();
//		assertEquals(3, list.size());
//
//		ModEntity modality = list.get(0);
//		modality.setName("test");
//		ChoosenModalityServerResource choosenModalityResource = new ChoosenModalityServerResource(application, modality);
//		choosenModalityResource.editModality(modality);
//		modality = choosenModalityResource.modalityDao.findById(modality.getId());
//		assertEquals("test", modality.getName());
//		
//		choosenModalityResource.deleteModality();
//		modality = choosenModalityResource.modalityDao.findById(modality.getId());
//		assertNull(modality);
	}

	@Test
	public void testWorkflow() {
		Workflow workflow = EmbeddedEngine.getQRSWorkflow();
		Instances instances = new Instances("test", workflow.getInputSpec(), 0);
		userDataResource.createDataset(instances);
		WorkflowServerResource workflowResource = new WorkflowServerResource(application, workflow);
		Form form = new Form();
		form.add(ServerParameter.USER_ID.getName(), USER_ID);
		Report r = workflowResource.execute(form);
		assertNotNull(r);
	}

	@Test
	public void testGlobalData() {
		Workflow workflow = EmbeddedEngine.getQRSWorkflow();
		Instances instances = new Instances("test", workflow.getInputSpec(), 0);
		String[] ids = { "a", "b", "c" };
		for (String s : ids) {
			User user = new User();
			user.setId(s);
			userDataResource.user = user;
			userDataResource.createDataset(instances);
		}
		GlobalDataServerResource globalDataResource = new GlobalDataServerResource(application);
		assertTrue(globalDataResource.getAllUsersWithData().contains(ids[0]));
		assertEquals(ids.length, globalDataResource.getAllUsersWithData()
				.size());

		GlobalAnalysisServerResource globalAnalysisResource = new GlobalAnalysisServerResource(application, workflow);
		Form form = new Form();
		assertEquals(ids.length, globalAnalysisResource.execute(form).size());
	}

	@Test
	public void testDevices() {
		DeviceServerResource deviceResource = new DeviceServerResource(application);
		deviceResource.register("test");
		deviceResource.unregister("test");
	}

//	@Test
//	public void testScheduling() {
//		userDataResource.createDataset();
//		userDataResource.getPersistenceContext().close();
//		scheduler.requestProcess();
//		scheduler.getPersistenceContext().close();
//		worker.pullTask(null);
//	}

}

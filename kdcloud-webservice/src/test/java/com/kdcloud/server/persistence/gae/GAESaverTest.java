package com.kdcloud.server.persistence.gae;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import weka.core.Instances;
import weka.core.converters.CSVLoader;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.kdcloud.server.entity.DataTable;
import com.kdcloud.server.persistence.DataMapperFactory;

public class GAESaverTest {
	
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig()
	/* .setDefaultHighRepJobPolicyUnappliedJobPercentage(100) */);
	
	DataMapperFactory factory = new DataMapperFactoryImpl();

	@Before
	public void setUp() throws Exception {
		helper.setUp();
	}
	
	@After
	public void tearDown() {
		helper.tearDown();
	}

	@Test
	public void test() throws IOException {
		CSVLoader loader = new CSVLoader();
		loader.setSource(getClass().getClassLoader().getResourceAsStream("ecg_small.csv"));
		Instances input = loader.getDataSet();
		InstancesMapperImpl saver = new InstancesMapperImpl();
		DataTable t = new DataTable();
		t.setName("test");
		factory.getEntityMapper().save(t);
		saver.save(input, t);
		assertEquals(input.size(), saver.load(t).size());
		saver.clear(t);
	}

}

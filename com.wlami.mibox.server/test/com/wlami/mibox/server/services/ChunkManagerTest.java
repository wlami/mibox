package com.wlami.mibox.server.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Arrays;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.test.framework.JerseyTest;
import com.wlami.mibox.server.data.User;

public class ChunkManagerTest extends JerseyTest {

	Connection connection;
	EntityManagerFactory emf;
	EntityManager em;

	public ChunkManagerTest() throws Exception {
		super("com.wlami.mibox.server.services");
		Class.forName("org.h2.Driver");
		connection = DriverManager.getConnection(
				"jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "", "");
		emf = Persistence.createEntityManagerFactory("test");
		em = emf.createEntityManager();
		em.setFlushMode(FlushModeType.AUTO);
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@Test(expected = UniformInterfaceException.class)
	public void testLoadChunkUnauthorized() {
		WebResource res = resource().path("chunkmanager").path("hashhashhash");
		res.addFilter(new HTTPBasicAuthFilter("user", "user"));
		byte[] result = res.get((byte[].class));
		System.out.println(Arrays.toString(result));
	}

	@Test(expected = UniformInterfaceException.class)
	public void testLoadChunkNotFound() {
		addUser("user", "password");
		WebResource res = resource().path("chunkmanager").path("hashhashhash");
		res.addFilter(new HTTPBasicAuthFilter("user", "password"));
		byte[] result = res.get((byte[].class));
		System.out.println(Arrays.toString(result));
	}

	@Test
	public void testLoadChunk() {
		addUser("user1", "password");
		WebResource res = resource().path("chunkmanager").path("hashhashhash");
		res.addFilter(new HTTPBasicAuthFilter("user1", "password"));
		byte[] result = res.get((byte[].class));
		System.out.println(Arrays.toString(result));
	}

	/**
	 * Add a user to the database
	 * 
	 * @param username
	 * @param password
	 */
	protected void addUser(String username, String password) {
		User user = new User(username, "test@localhost", password);
		em.getTransaction().begin();
		em.persist(user);
		em.getTransaction().commit();
	}

	@Test
	public void testSaveChunk() {

	}

	@After
	public void tearDown() throws Exception {
		em.getTransaction().begin();
		em.createQuery("DELETE FROM User u").executeUpdate();
		em.getTransaction().commit();
		super.tearDown();
	}

}

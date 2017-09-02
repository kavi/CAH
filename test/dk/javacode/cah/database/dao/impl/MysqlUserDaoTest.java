package dk.javacode.cah.database.dao.impl;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import dk.javacode.cah.database.dao.UserDao;
import dk.javacode.cah.database.dao.impl.MySqlUserDao;
import dk.javacode.cah.model.User;
import dk.javacode.cah.testutil.DbTestEnvironment;
import dk.javacode.proxy.InterceptorProxy;

public class MysqlUserDaoTest {
	
	private UserDao dao;
	private DbTestEnvironment env;

	@BeforeClass
	public static void setupClass() {
	}
	
	@Before
	public void setUp() throws Exception {
		env = new DbTestEnvironment("mydata.xml");
		env.setUp();
		dao = InterceptorProxy.buildProxy(UserDao.class, new MySqlUserDao());
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreate() {
		User user = new User();
		user.setEmail("email@gmail.com");
		user.setPassword("s3cret");
		user.setName("min-hest");

		dao.createUser(user);
		assertNotNull(user.getId());
		System.out.println(user);
	}
	
	@Test
	public void testDelete() {
		User user = dao.findUser("Admin");
		assertNotNull("pre-condition", user);

		assertFalse(dao.isUsernameAvailable("Admin"));
		dao.deleteUser("Admin");
		
		user = dao.findUser("Admin");
		assertNull(user);
	}
	

}

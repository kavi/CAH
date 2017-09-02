package dk.javacode.cah.database.dao.impl;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dk.javacode.cah.database.dao.UserDao;
import dk.javacode.cah.model.User;
import dk.javacode.cah.testutil.DbTestEnvironment;
import dk.javacode.proxy.InterceptorProxy;

public class MySqlGameDaoTest {
//	private GameDao dao;
	private UserDao userdao;
	private DbTestEnvironment env;

	public MySqlGameDaoTest() {
		super();
	}

	@Before
	public void setUp() throws Exception {
		env = new DbTestEnvironment("mydata.xml");
		env.setUp();
//		dao = InterceptorProxy.buildProxy(GameDao.class, new MySqlGameDao());
		userdao = InterceptorProxy.buildProxy(UserDao.class, new MySqlUserDao());
	}

	@After
	public void tearDown() throws Exception {
		env.tearDown();
	}

	@Test //TODO test GameService
	public void testCreateGame() {
		User user = userdao.findUser("Admin");
		assertNotNull("Precondition. User must be found", user);
//		Game game = new Game();
//		game.setOwner(user);
//		game.setName("1st Game");
//		game.setPassword("123");
//		dao.createGame(game, false);
//
//		List<Game> allgames = dao.findAll();
//		assertEquals(1, allgames.size());
//		System.out.println(allgames);
	}

}

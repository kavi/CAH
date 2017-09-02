package dk.javacode.cah.database.dao.impl;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dk.javacode.cah.database.dao.WhiteCardDao;
import dk.javacode.cah.database.dao.impl.MySqlWhiteCardDao;
import dk.javacode.cah.model.WhiteCard;
import dk.javacode.cah.testutil.DbTestEnvironment;
import dk.javacode.proxy.InterceptorProxy;

public class MysqlWhiteCardDaoTest {

	private WhiteCardDao dao;
	private DbTestEnvironment env;

	@Before
	public void setUp() throws Exception {
		env = new DbTestEnvironment("mydata.xml");
		env.setUp();
		dao = InterceptorProxy.buildProxy(WhiteCardDao.class, new MySqlWhiteCardDao());
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		WhiteCard card = new WhiteCard();
		card.setText("Dette er et sjovt kort. Haha!");
		dao.createCard(card);
		assertNotNull(card.getId());
		System.out.println(card);

		List<WhiteCard> cards = dao.findAll(false);
		assertEquals(455, cards.size());

		dao.hardDeleteCard(card.getId());

		cards = dao.findAll(false);
		assertEquals(454, cards.size());
	}

}

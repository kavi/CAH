package dk.javacode.cah.model;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dk.javacode.notjsr.NotJSRValidator;
import dk.javacode.notjsr.ValidationError;

public class GameTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testNameValidation() {
		Game game = new Game("Simple Name");
		List<ValidationError<Game>> errors = NotJSRValidator.validate(game);
		assertEquals(0, errors.size());
		
		game = new Game("Simple+Name");
		errors = NotJSRValidator.validate(game);
		assertEquals(0, errors.size());

		game = new Game("Simple & Name");
		errors = NotJSRValidator.validate(game);
		assertEquals(0, errors.size());

		game = new Game("Simple & Name 123");
		errors = NotJSRValidator.validate(game);
		assertEquals(0, errors.size());

		game = new Game("'Simple Name 123'");
		errors = NotJSRValidator.validate(game);
		assertEquals(0, errors.size());

		game = new Game("'Simple/Name 123'");
		errors = NotJSRValidator.validate(game);
		assertEquals(0, errors.size());

		game = new Game("Simple Name.");
		errors = NotJSRValidator.validate(game);
		assertEquals(0, errors.size());

		game = new Game("Simple, Name");
		errors = NotJSRValidator.validate(game);
		assertEquals(0, errors.size());

		game = new Game("'Simple > Name 123'");
		errors = NotJSRValidator.validate(game);
		assertEquals(1, errors.size());

		game = new Game("'Simple < Name 123'");
		errors = NotJSRValidator.validate(game);
		assertEquals(1, errors.size());
		
		game = new Game("a");
		errors = NotJSRValidator.validate(game);
		assertEquals(1, errors.size());
		
		String longname;
		for (longname = "a";longname.length() < 61; longname += "a");
		
		game = new Game(longname);
		errors = NotJSRValidator.validate(game);
		assertEquals(1, errors.size());

	}

}

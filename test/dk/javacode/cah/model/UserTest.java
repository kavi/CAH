package dk.javacode.cah.model;

import java.util.List;
import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dk.javacode.notjsr.NotJSRValidator;
import dk.javacode.notjsr.ValidationError;

public class UserTest {

	private User u;
	
	@Before
	public void setUp() throws Exception {
		u = new User();
		u.setPassword("1234");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testValidationNameNull() {
		List<ValidationError<User>> result = NotJSRValidator.validate(u);
		System.out.println(result);
		assertEquals(1, result.size());
	}

	@Test
	public void testValidationNameShort() {
		u.setName("To");
		List<ValidationError<User>> result = NotJSRValidator.validate(u);
		System.out.println(result);
		assertEquals(1, result.size());
	}

	@Test
	public void testValidationNameOk() {
		u.setName("ThisIsAGood1");
		List<ValidationError<User>> result = NotJSRValidator.validate(u);
		System.out.println(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testValidationNameInvalidChar() {
		u.setName("InvalidChar:");
		List<ValidationError<User>> result = NotJSRValidator.validate(u);
		System.out.println(result);
		assertEquals(1, result.size());
	}
	
	@Test
	public void testValidationNameInvalidContainsSpace() {
		u.setName("InvalidChar i");
		List<ValidationError<User>> result = NotJSRValidator.validate(u);
		System.out.println(result);
		assertEquals(1, result.size());
	}
	
	@Test
	public void testValidationNameInvalidContainsDanishLetter() {
		u.setName("InvalidCharø");
		List<ValidationError<User>> result = NotJSRValidator.validate(u);
		System.out.println(result);
		assertEquals(1, result.size());
	}

	
	@Test
	public void testValidationPasswordNull() {
		u.setName("ThisIsAGood1");
		u.setPassword(null);
		List<ValidationError<User>> result = NotJSRValidator.validate(u);
		System.out.println(result);
		assertEquals(1, result.size());
	}

	@Test
	public void testValidationPasswordShort() {
		u.setName("ThisIsAGood1");
		u.setPassword("123");
		List<ValidationError<User>> result = NotJSRValidator.validate(u);
		System.out.println(result);
		assertEquals(1, result.size());
	}

}

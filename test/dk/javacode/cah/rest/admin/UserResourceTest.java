package dk.javacode.cah.rest.admin;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UserResourceTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testHidePassword() {
		UserResource r = new UserResource();
		String plain = "{\"name\":\"Admin\",\"email\":\"\",\"password\":\"nissefar\",\"password2\":\"\",\"familyFilter\":false,\"editPassword\":false,\"id\":1}";
		String hidden = r.hidePassword(plain);
		assertFalse(hidden.contains("nissefar"));
		
		plain = "{\"name\":\"Admin\",\"email\":\"\",\"password\":\"nissefar\",\"familyFilter\":false,\"editPassword\":false,\"id\":1}";
		hidden = r.hidePassword(plain);
		assertFalse(hidden.contains("nissefar"));
		
		plain = "{\"name\":\"Admin\",\"email\":\"\",\"password\":\"nissefar\",\"password2\":\"nissefar\",\"familyFilter\":false,\"editPassword\":false,\"id\":1}";
		hidden = r.hidePassword(plain);
		assertFalse(hidden.contains("nissefar"));
	}

}

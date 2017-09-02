package dk.javacode.crypt;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CryptHelperTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test 
	public void testEncodePassword() {
		String plain = "nissefar";
		String encoded = CryptHelper.encodePassword(plain);
		System.out.println(encoded);
		assertEquals("58fcc29a15b4c15a2399a8d81cbba2e4", encoded);
	}

}

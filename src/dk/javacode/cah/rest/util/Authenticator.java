package dk.javacode.cah.rest.util;

import java.util.concurrent.ConcurrentMap;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.restlet.Request;
import org.restlet.util.Series;

import dk.javacode.cah.database.dao.UserDao;
import dk.javacode.cah.database.dao.impl.MySqlUserDao;
import dk.javacode.cah.model.User;
import dk.javacode.proxy.InterceptorProxy;

public class Authenticator {

	private UserDao userDao;
	private User user;
	private static Logger log = Logger.getLogger(Authenticator.class); 

	public Authenticator() {
		userDao = InterceptorProxy.buildProxy(UserDao.class, new MySqlUserDao());
	}

	public User authenticate(Request request) {
		if (request == null) {
			return null;
		}
		String userpw = null;
		try {
			ConcurrentMap<String, Object> attributes = request.getAttributes();
			if (attributes == null) {
				log.debug("No attributes in request");
				return null;
			}
			Series<?> headers = (Series<?>) attributes.get("org.restlet.http.headers");
			if (headers == null) {
				log.debug("No headers in request");
				return null;
			}
			String[] authValues = headers.getValuesArray("Authorization");
			System.out.println(headers);
			if (authValues == null || authValues.length < 1) {
				log.debug("No Authorization values in header");
				return null;				
			}
			String[] tokens = authValues[0].split(" ");
			if (tokens.length != 2) {
				log.debug("Unexpected number of tokens in Authorization value: " + authValues[0]);
				return null;								
			}
			String type = tokens[0];
			if (!type.equalsIgnoreCase("Basic")) {
				log.debug("Only basic authentication type supported. Got: " + type);
				return null;												
			}
			String userpwenc = tokens[1];
			userpw = new String(Base64.decodeBase64(userpwenc));
		} catch (RuntimeException e) {
			log.info("Unable to parse headers for authentication", e);
			return null;
		}
		int firstColon = userpw.indexOf(':');
		if (firstColon < 1) {
			log.info("No colon in userpw: " + userpw);
			return null;
		}
		String username = userpw.substring(0,firstColon);
		String pw = userpw.substring(firstColon + 1);
		user = userDao.findUser(username, pw);
		return user;
	}

	public User getUser() {
		return user;
	}	
}

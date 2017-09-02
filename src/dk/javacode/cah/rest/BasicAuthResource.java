package dk.javacode.cah.rest;

import java.sql.SQLException;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.restlet.Request;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;

import dk.javacode.cah.database.dao.UserDao;
import dk.javacode.cah.database.dao.impl.MySqlUserDao;
import dk.javacode.cah.model.User;
import dk.javacode.proxy.InterceptorProxy;
import dk.javacode.srsm.exceptions.MappingException;

public class BasicAuthResource extends ServerResource {
	
	private static final Logger log = Logger.getLogger(BasicAuthResource.class);

	public UserDao userDao;

	protected User user;
	
	public String entityText;
	
	public BasicAuthResource() {
		super();
		userDao = InterceptorProxy.buildProxy(UserDao.class, new MySqlUserDao());
	}

	public User authenticate(Request request) throws SQLException, MappingException {
		if (request == null) {
			return null;
		}
		String userpw = null;
		try {
			ConcurrentMap<String, Object> attributes = request.getAttributes();
			if (attributes == null) {
				BasicAuthResource.log.debug("No attributes in request");
				return null;
			}
			Series<?> headers = (Series<?>) attributes.get("org.restlet.http.headers");
			if (headers == null) {
				BasicAuthResource.log.debug("No headers in request");
				return null;
			}
			String[] authValues = headers.getValuesArray("Authorization");
			System.out.println(headers);
			if (authValues == null || authValues.length < 1) {
				BasicAuthResource.log.debug("No Authorization values in header");
				return null;				
			}
			String[] tokens = authValues[0].split(" ");
			if (tokens.length != 2) {
				BasicAuthResource.log.debug("Unexpected number of tokens in Authorization value: " + authValues[0]);
				return null;								
			}
			String type = tokens[0];
			if (!type.equalsIgnoreCase("Basic")) {
				BasicAuthResource.log.debug("Only basic authentication type supported. Got: " + type);
				return null;												
			}
			String userpwenc = tokens[1];
			userpw = new String(Base64.decodeBase64(userpwenc));
		} catch (RuntimeException e) {
			BasicAuthResource.log.info("Unable to parse headers for authentication", e);
			return null;
		}
		int firstColon = userpw.indexOf(':');
		if (firstColon < 1) {
			return null;
		}
		String user = userpw.substring(0,firstColon);
		String pw = userpw.substring(firstColon + 1);
		BasicAuthResource.log.debug("Looking up user: " + user + ":<password-hidden>");
		this.entityText = request.getEntityAsText();
		return this.userDao.findUser(user, pw);
	}

	@Override
	protected Representation doHandle() throws ResourceException {
		try {
			user = authenticate(getRequest());

		} catch (Exception e) {
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return new StringRepresentation("Unauthorized");
		}	
		if (user == null) {
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return new StringRepresentation("Unauthorized");
		}
		return super.doHandle();
	}	

	@Override
	protected Representation doHandle(Variant variant) throws ResourceException {
		try {
			user = authenticate(getRequest());

		} catch (Exception e) {
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return new StringRepresentation("Unauthorized");
		}	
		if (user == null) {
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return new StringRepresentation("Unauthorized");
		}
		return super.doHandle(variant);
	}

	@Override
	protected Representation doNegotiatedHandle() throws ResourceException {
		try {
			user = authenticate(getRequest());

		} catch (Exception e) {
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return new StringRepresentation("Unauthorized");
		}	
		if (user == null) {
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return new StringRepresentation("Unauthorized");
		}
		return super.doNegotiatedHandle();
	}

	
	public StringRepresentation getStringRepresentation(String message) {
		log.trace(message);
		return new StringRepresentation(message);
	}

	public StringRepresentation getStringRepresentation(String message, Exception e) {
		log.warn(message, e);
		return new StringRepresentation(message + "\n" + e.getMessage());
	}

	
}

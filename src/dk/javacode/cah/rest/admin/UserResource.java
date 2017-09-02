package dk.javacode.cah.rest.admin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import dk.javacode.cah.database.dao.UserDao;
import dk.javacode.cah.database.dao.impl.MySqlUserDao;
import dk.javacode.cah.model.User;
import dk.javacode.cah.rest.util.Authenticator;
import dk.javacode.cah.util.JsonHelper;
import dk.javacode.notjsr.NotJSRValidator;
import dk.javacode.notjsr.ValidationError;
import dk.javacode.proxy.InterceptorProxy;

public class UserResource extends ServerResource {
	private static final Logger log = Logger.getLogger(UserResource.class);

	private Authenticator authenticator;
	private UserDao userDao;

	public UserResource() {
		super();
		userDao = InterceptorProxy.buildProxy(UserDao.class, new MySqlUserDao());
		authenticator = new Authenticator();
	}
	
	@Get("json")
	public Representation login(JsonRepresentation r) throws IOException, JSONException, SQLException {
		log.debug("login (getUser)"); 
		authenticator.authenticate(getRequest());
		if (authenticator.getUser() == null) {
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return new StringRepresentation("Unauthorized");
		}
		setStatus(Status.SUCCESS_OK);
		return new JsonRepresentation(authenticator.getUser());
	}

	@Post("json")
	public Representation createUser(JsonRepresentation r) throws IOException, JSONException, SQLException {
		String entityText = getRequest().getEntityAsText();
		if (entityText == null) {
			JSONArray errormsg = new JSONArray();
			JSONObject jerror = new JSONObject();
			jerror.put("invalidValue", "null");
			jerror.put("message", "No data received"); 
			errormsg.put(jerror);				
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return new JsonRepresentation(errormsg);	
		}
		log.debug("createUser: " + hidePassword(entityText));

		JSONObject json = null;
		User user = new User();
		try {
			json = new JSONObject(entityText);
			String name = json.getString("name");
			String password = json.getString("password");
			if (name == null || password == null) {
				JSONArray errormsg = new JSONArray();
				JSONObject jerror = new JSONObject();
				jerror.put("invalidValue", "null");
				jerror.put("message", "username and password must be set"); 
				errormsg.put(jerror);				
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return new JsonRepresentation(errormsg);				
			}
			String email = "";
			if (json.has("email")) {
				email = json.getString("email");
			}
			boolean familyFilter = true;
			if (json.has("familyFilter")) {
				familyFilter = json.getBoolean("familyFilter");
			}
			user.setEmail(email);
			user.setName(name);
			user.setPassword(password);
			user.setFamilyFilter(familyFilter);
			
			List<ValidationError<User>> errors = NotJSRValidator.validate(user);
			
			if (errors.size() > 0) {
				log.info("(createUser) Validation errors: " + errors);
				JSONArray errormsg = new JSONArray();
				for (ValidationError<User> error : errors) {
					JSONObject jerror = new JSONObject();
					jerror.put("invalidValue", error.getInvalidValue());
					jerror.put("message", error.getMessage());
					errormsg.put(jerror);
				}
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return new JsonRepresentation(errormsg);
			}
		} catch (Exception e) {
			log.info("Unable to handle request: " + entityText, e);
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return new StringRepresentation("Bad request. " + e.getMessage());			
		}
		
		try {
			User olduser = userDao.findUser(user.getName());
			if (olduser != null) {
				log.info("Duplicate user name");
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return new StringRepresentation("User name already used. Login or choose a different user name.");				
			}
			userDao.createUser(user);
		} catch (Exception e) {
			log.info("Unable to store user: " + user, e);
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation("Unable to store user.");
		}
		
		setStatus(Status.SUCCESS_CREATED);
		return new StringRepresentation(user.getId() + "");
	}
	
	

	public String hidePassword(String input) {
		input = input.replaceAll("\\\"password\\\":\".*?\"", "\"password\":\"****\"");
		return input.replaceAll("\\\"password2\\\":\".*?\"", "\"password2\":\"****\"");
	}	
	
	@Put("json")
	public Representation editUser(JsonRepresentation r) throws IOException, JSONException, SQLException {
		User user = authenticator.authenticate(getRequest());
		if (authenticator.getUser() == null) {
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return new StringRepresentation("Unauthorized");
		}
		String entityText = getRequest().getEntityAsText();
		log.debug("editUser: " + entityText);

		JSONObject json = null;
		try {
			json = new JSONObject(entityText);
			String name = json.getString("name");
			String password = json.getString("password");
			String email = json.getString("email");
			boolean familyFilter = json.has("familyFilter") ? json.getBoolean("familyFilter") : user.isFamilyFilter();

			
			if (!name.equals(user.getName())) {
				log.info("Can only update the logged in user.");
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return new StringRepresentation("Bad request. Can only update the logged in user.");							
			}
			user.setFamilyFilter(familyFilter);
			user.setEmail(email);
			user.setPassword(password);			
			List<ValidationError<User>> errors = NotJSRValidator.validate(user);
			if (errors.size() > 0) {
				JSONArray errormsg = JsonHelper.toArray(errors);
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return new JsonRepresentation(errormsg);
			}
		} catch (Exception e) {
			log.info("Unable to handle request: " + entityText, e);
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return new StringRepresentation("Unable to handle request");			
		}
		
		try {
			userDao.updateUser(user);
		} catch (Exception e) {
			log.info("Unable to store user: " + user, e);
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return new StringRepresentation("Bad request");
		}
		
		setStatus(Status.SUCCESS_CREATED);
		return new StringRepresentation(user.getId() + "");
	}
}

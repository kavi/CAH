package dk.javacode.cah.rest;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import dk.javacode.cah.database.dao.DaoException;
import dk.javacode.cah.database.service.DeckService;
import dk.javacode.cah.database.service.interfaces.IDeckService;
import dk.javacode.cah.model.DeckLanguage;
import dk.javacode.cah.util.JsonHelper;
import dk.javacode.proxy.InterceptorProxy;
import dk.javacode.srsm.exceptions.MappingException;

public class LanguageResource extends ServerResource {
	private static final Logger log = Logger.getLogger(LanguageResource.class);

	private IDeckService deckService;

	public LanguageResource() {
		super();
		deckService = InterceptorProxy.buildProxy(IDeckService.class, new DeckService());
	}

	@Get
	public Representation getLanguages() throws SQLException, MappingException {
		log.trace("getLanguages");

		List<DeckLanguage> languages = null;
		try {
			languages = deckService.findLanguages();
		} catch (DaoException e) {
			log.warn("Error loading language.", e);
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation("Internal error loading languages.");
		}

		setStatus(Status.SUCCESS_OK);
		JSONArray jsonArray = JsonHelper.toArray(languages);
		Representation r = new JsonRepresentation(jsonArray);
		return r;
	}

}

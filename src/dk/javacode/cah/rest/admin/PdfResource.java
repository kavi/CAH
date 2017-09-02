package dk.javacode.cah.rest.admin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.json.JSONException;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import dk.javacode.cah.database.service.BlackCardService;
import dk.javacode.cah.database.service.DeckService;
import dk.javacode.cah.database.service.WhiteCardService;
import dk.javacode.cah.database.service.interfaces.IBlackCardService;
import dk.javacode.cah.database.service.interfaces.IDeckService;
import dk.javacode.cah.database.service.interfaces.IWhiteCardService;
import dk.javacode.cah.model.Deck;
import dk.javacode.proxy.InterceptorProxy;

public class PdfResource extends ServerResource {
	private static final Logger log = Logger.getLogger(PdfResource.class);

	private IDeckService deckService;
	private IWhiteCardService whiteCardService;
	private IBlackCardService blackCardService;

	public PdfResource() {
		super();
		deckService = InterceptorProxy.buildProxy(IDeckService.class, new DeckService());
		blackCardService = InterceptorProxy.buildProxy(IBlackCardService.class, new BlackCardService());
		whiteCardService = InterceptorProxy.buildProxy(IWhiteCardService.class, new WhiteCardService());
	}

	@Get("pdf")
	public Representation getPdf(JsonRepresentation r) throws IOException, JSONException, SQLException,
			COSVisitorException {
		log.debug("getPdf");
		
		int rows = 6;
		List<Integer> deckIds = new ArrayList<Integer>();
		Boolean familyFilter = null;
		
		String familyFilterParam = getQueryValue("familyFilter");
		String rowsParam = getQueryValue("rows");
		if (rowsParam != null) {
			try {
				rows = Integer.parseInt(rowsParam);
			} catch (NumberFormatException e) {
				log.warn("Unable to parse rows: " + rowsParam);
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return new StringRepresentation("rows must be a number");
			}
		}
		String decksParam = getQueryValue("decks");
		if (decksParam == null) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return new StringRepresentation("No decks specified");			
		}
		String[] decksStr = decksParam.split(",");
		for (String deckstr : decksStr) {
			try {
				int deckId = Integer.parseInt(deckstr);
				Deck deck = deckService.findById(deckId);
				if (deck == null) {
					setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
					return new StringRepresentation("Invalid deck specified (not found): " + deckstr);					
				}
				deckIds.add(deckId);
			} catch (NumberFormatException e) {
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return new StringRepresentation("Invalid deck specified: " + deckstr);
			}
		}

		
		try {
			familyFilter = Boolean.parseBoolean(familyFilterParam);
			log.debug("FamilyFilter is: " + familyFilter);
		} catch (RuntimeException e) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return new StringRepresentation("Unable to download pdf. FamilyFilter attribute not set correctly.");
		}

		return new PdfStreamRepresentation(whiteCardService.findAllAvailable(familyFilter, deckIds), blackCardService.findAllAvailable(familyFilter, deckIds), rows);
	}	
}

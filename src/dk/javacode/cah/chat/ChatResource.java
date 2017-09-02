package dk.javacode.cah.chat;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import dk.javacode.cah.database.service.GameActionService;
import dk.javacode.cah.database.service.interfaces.IGameActionService;
import dk.javacode.cah.model.Game;
import dk.javacode.cah.rest.BasicAuthResource;
import dk.javacode.cah.util.JsonHelper;
import dk.javacode.proxy.InterceptorProxy;

public class ChatResource extends BasicAuthResource {
	private static final Logger log = Logger.getLogger(ChatResource.class);

	private IGameActionService gameService;
	private ChatService chatService;

	public ChatResource() {
		super();
		gameService = InterceptorProxy.buildProxy(IGameActionService.class, new GameActionService());
		chatService = new ChatService();
	}

	@Post("json")
	public Representation createMessage() {
		log.info("createMessage");
		try {
			// input = this.getRequest().getEntityAsText();
			log.info(entityText);
		} catch (Exception e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return getStringRepresentation("Unable to get input", e);
		}

		String gameidstr = getAttribute("game_id");
		if (gameidstr == null) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return getStringRepresentation("Bad routing. No game id");
		}
		int gameId;
		try {
			gameId = Integer.parseInt(gameidstr);
		} catch (NumberFormatException e) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return getStringRepresentation("Bad request - unparseable game_id: " + gameidstr, e);
		}

		Game game = gameService.findGame(gameId);
		if (game == null) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return getStringRepresentation("Bad request. Game not found: " + gameId);
		}
		ChatRoomDTO room = chatService.getRoomDTO(game);
		if (room  == null) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return getStringRepresentation("Unable to load room for game: " + gameId);			
		}

		String messageText;
		try {			
			JSONObject json = new JSONObject(entityText);
			messageText = json.getString("message");
		} catch (Exception e) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			log.warn("Unparseable json: " + entityText);
			return getStringRepresentation("Bad request. Unparsable json.");
		}
		
		ChatMessage msg = new ChatMessage(user, messageText);
		try {
			chatService.addMessage(game, msg);
		} catch (RuntimeException e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return getStringRepresentation("Database error: " + e.getMessage(), e);
		}

		setStatus(Status.SUCCESS_CREATED);
		log.info("[" + user + "] added message: " + msg);
		JSONObject jsonout = new JSONObject(msg);
		return new JsonRepresentation(jsonout);
	}

	@Get
	public Representation getChatRoom() {
		log.trace("getChatRoom");
		String gameidstr = getAttribute("game_id");
		if (gameidstr == null) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return getStringRepresentation("Bad routing. No language id");
		}
		int gameId;
		try {
			gameId = Integer.parseInt(gameidstr);
		} catch (NumberFormatException e) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return getStringRepresentation("Bad request, unable to parse game id: " + gameidstr, e);
		}

		Game game;
		try {
			game = gameService.findGame(gameId);
		} catch (RuntimeException e) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return getStringRepresentation("Error loading game for chat room", e);			
		}
		if (game == null) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return getStringRepresentation("Bad request, game not found. id=" + gameId);			
		}
		try {
			ChatRoomDTO room = chatService.getRoomDTO(game);
			List<ChatMessage> messages = new ArrayList<ChatMessage>(room.getMessages());
			setStatus(Status.SUCCESS_OK);
			JSONObject jsonout = new JSONObject();
			jsonout.put("gameId", room.getGameId());
			jsonout.put("messages", JsonHelper.toArray(messages));
			Representation r = new JsonRepresentation(jsonout);
			return r;
		} catch (Exception e) {
			log.error("Unexpected error", e);
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation("Unexpected error");
		}
	}

}

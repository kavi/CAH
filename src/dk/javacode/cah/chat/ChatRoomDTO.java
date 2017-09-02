package dk.javacode.cah.chat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import dk.javacode.cah.model.Game;

public class ChatRoomDTO {

	private List<ChatMessage> messages = new LinkedList<ChatMessage>();
	private Game game;

	public ChatRoomDTO() {
		super();
	}

	public Integer getGameId() {
		return game != null ? game.getId() : null;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public List<ChatMessage> getMessages() {
		return Collections.unmodifiableList(messages);
	}

	public void setMessages(List<ChatMessage> messages) {
		this.messages = new ArrayList<ChatMessage>();
		this.messages.addAll(messages);
	}
}

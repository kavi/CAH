package dk.javacode.cah.chat;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import dk.javacode.cah.model.Game;

public class ChatRoom implements Serializable {

	private static final long serialVersionUID = 1L;

	private int messageLimit = 20;

	private List<ChatMessage> messages = new LinkedList<ChatMessage>();
	private transient Game game;

	public ChatRoom() {
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

	public int getMessageLimit() {
		return messageLimit;
	}

	public void setMessageLimit(int messageLimit) {
		this.messageLimit = messageLimit;
	}

	public List<ChatMessage> getMessages() {
		return Collections.unmodifiableList(messages);
	}

	public void setMessages(List<ChatMessage> messages) {
		this.messages = messages;
	}

	public void addMessage(ChatMessage msg) {
		messages.add(msg);
		if (messages.size() > messageLimit) {
			messages.remove(0);
		}
	}

	@Override
	public String toString() {
		return "ChatRoom [messageLimit=" + messageLimit + ", messages=" + messages + ", gameId=" + getGameId() + "]";
	}

}

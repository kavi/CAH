package dk.javacode.cah.chat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import dk.javacode.cah.chat.ChatService.CachedRoom;
import dk.javacode.cah.model.Game;
import dk.javacode.cah.model.User;

public class ChatFileStore {

	public static String ROOT_FOLDER = ".";
	private static final Logger log = Logger.getLogger(ChatFileStore.class);

	public ChatFileStore() {
	}
	
	private String getRoomFileName(Game game) {
		String name = ROOT_FOLDER + "/room_" + game.getId() + ".data";
		return name;
	}
	
	public void write(CachedRoom croom) {
		File originalfile = new File(getRoomFileName(croom.getRoom().getGame()));
		File bakfile = new File(getRoomFileName(croom.getRoom().getGame()) + ".bak");
		File tmpfile = new File(getRoomFileName(croom.getRoom().getGame()) + ".tmp");
		try {
			write(new FileWriter(tmpfile), croom);
			if (originalfile.exists()) {
				if (bakfile.exists()) {
					bakfile.delete();
				}
				FileUtils.moveFile(originalfile, bakfile);
			}
			FileUtils.moveFile(tmpfile, originalfile);
		} catch (IOException e) {
			throw new RuntimeException("Error writing chatroom", e);
		}
	}

	public void write(Writer w, CachedRoom croom) {
		BufferedWriter writer = new BufferedWriter(w);
		try {
			ChatRoom room = croom.getRoom();
			List<ChatMessage> messages = room.getMessages();
			for (ChatMessage msg : messages) {
				Integer senderId = msg.getSender().getId();
				String senderName = msg.getSender().getName();
				String text = msg.getText().trim();
				long tstamp = msg.getTime();
				writer.write(senderId + ":" + senderName + "\n");
				writer.write(text + "\n");
				writer.write(tstamp + "\n");
			}
			writer.flush();
		} catch (IOException e) {
			throw new RuntimeException("Error writing chatroom");
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}
	
	public CachedRoom load(Game game) {
		File file = new File(getRoomFileName(game));
		if (!file.exists()) {
			log.info("No file found for game: " + game.getId());
			return null;
		}
		log.info("Attempting to load file (" + file.getAbsolutePath() + ") for game: " + game.getId());
		try {
			return load(new FileReader(file), game);
		} catch (FileNotFoundException e) {
			log.warn("FileNotFound for game: " + game.getId());
			return null;
		}		
	}

	public CachedRoom load(Reader r, Game game) {
		BufferedReader reader = new BufferedReader(r);
		try {
			ChatRoom room = new ChatRoom();
			room.setGame(game);
			List<ChatMessage> messages = new LinkedList<ChatMessage>();

			int i = 0;
			ChatMessage msg = new ChatMessage();
			while (reader.ready()) {
				String l = reader.readLine();
				if (i % 3 == 0) {
					User sender = new User();
					int idx = l.indexOf(':');
					String id = l.substring(0, idx);
					String name = l.substring(idx + 1);
					sender.setName(name);
					sender.setId(Integer.parseInt(id));
					msg.setSender(sender);
				} else if (i % 3 == 1) {
					msg.setText(l);
				} else if (i % 3 == 2) {
					long tstamp = Long.parseLong(l);
					msg.setTimestamp(new Date(tstamp));
					messages.add(msg);
					msg = new ChatMessage();
				}
				i++;
			}
			room.setMessages(messages);
			CachedRoom croom = new CachedRoom(room);
			return croom;
		} catch (IOException e) {
			throw new RuntimeException("IOError reading chatroom", e);
		} catch (RuntimeException e) {
			throw new RuntimeException("Error reading chatroom", e);
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}
}

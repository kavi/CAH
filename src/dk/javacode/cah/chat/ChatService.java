package dk.javacode.cah.chat;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import dk.javacode.cah.model.Game;

public class ChatService {

	private static int roomLimit = 20;
	private static Logger log = Logger.getLogger(ChatService.class);

	private static Map<Integer, CachedRoom> roomCache = new HashMap<Integer, CachedRoom>();
	private static ReentrantLock lock = new ReentrantLock();
	private ChatFileStore filestore = new ChatFileStore();

	public ChatService() {
	}

	public ChatRoomDTO getRoomDTO(Game game) {
		CachedRoom croom = roomCache.get(game.getId());
		if (croom == null) {
			croom = createNewRoom(game);
		}
		try {
			croom.lock();
			croom.setLastUsed(new Date());
			ChatRoom room = croom.getRoom();
			ChatRoomDTO res = new ChatRoomDTO();
			res.setGame(room.getGame());
			res.setMessages(room.getMessages());
			return res;
		} finally {
			croom.unlock();
		}
	}

	private CachedRoom createNewRoom(Game game) {
		lock.lock();
		if (roomCache.containsKey(game.getId())) {
			return roomCache.get(game.getId());
		}
		if (roomCache.size() >= roomLimit) {
			SortedSet<CachedRoom> sorted = new TreeSet<CachedRoom>(roomCache.values());
			CachedRoom oldestRoom = sorted.first();
			log.info("Removing old room: " + oldestRoom + " (newest: " + sorted.last() + ")");
			roomCache.remove(oldestRoom.getRoom().getGameId());
//			filevstore.write(oldestRoom);
		} else {
			log.info("Create new room. size is not an issue (" + roomCache.size() + "/" + roomLimit + ")");
		}
		try {
			CachedRoom croom = filestore.load(game);
			if (croom != null) {
				roomCache.put(game.getId(), croom);
				return croom;
			}
			ChatRoom newroom = new ChatRoom();
			newroom.setGame(game);
			croom = new CachedRoom(newroom);
			croom.setLastUsed(new Date());
			croom.setRoom(newroom);
			roomCache.put(game.getId(), croom);
			return croom;
		} finally {
			lock.unlock();
		}
	}

	public void addMessage(Game game, ChatMessage msg) {
		if (!roomCache.containsKey(game.getId())) {
			createNewRoom(game);
		}
		CachedRoom croom = roomCache.get(game.getId());
		croom.lock();
		try {
			croom.setLastUsed(new Date());
			croom.getRoom().addMessage(msg);
			filestore.write(croom);
		} finally {
			croom.unlock();
		}
	}

	public static class CachedRoom implements Comparable<CachedRoom>, Serializable {
		private static final long serialVersionUID = 3L;
		private ChatRoom room;
		private Date lastUsed = new Date();
		private ReentrantLock lock = new ReentrantLock();

		public CachedRoom(ChatRoom room) {
			super();
			this.room = room;
		}

		public ChatRoom getRoom() {
			return room;
		}

		public void setRoom(ChatRoom room) {
			this.room = room;
		}

		public Date getLastUsed() {
			return lastUsed;
		}

		public void setLastUsed(Date lastUsed) {
			this.lastUsed = lastUsed;
		}

		public  void lock() {
			lock.lock();
		}

		public void unlock() {
			lock.unlock();
		}

		@Override
		public int compareTo(CachedRoom o) {
			return lastUsed.compareTo(o.getLastUsed());
		}

		@Override
		public String toString() {
			return "CachedRoom [room=" + room + ", lastUsed=" + lastUsed + "]";
		}
	}
}

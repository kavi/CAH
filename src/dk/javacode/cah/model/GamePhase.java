package dk.javacode.cah.model;

public enum GamePhase {
	DRAW_QUESTION(1, "Draw Question"),
	DRAW_HAND(2, "Draw Hand"),
	PLAY_WHITES(3, "Play Whites"),
	VOTE(4, "Vote"),
	FINISH_ROUND(5, "Finish Round");

	private int id;
	private String name;

	private GamePhase(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static GamePhase get(int id) {
		for (GamePhase p : values()) {
			if (p.id == id) {
				return p;
			}
		}
		return null;
	}
}

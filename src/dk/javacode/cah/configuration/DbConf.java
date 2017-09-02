package dk.javacode.cah.configuration;


public class DbConf {

	private String user = "kavi";
	private String pass = "test1234";
	private String dbname = "cah";
	private String hostname = "127.0.0.1";
	private String protocol = "mysql";
	private int port = 10000;

	public DbConf() {

	}

	public DbConf(String hostname, int port, String user, String pass, String dbname) {
		super();
		this.user = user;
		this.pass = pass;
		this.dbname = dbname;
		this.hostname = hostname;
		this.port = port;
	}
	
	public static DbConf load(ConfigValues config) {
		try {
			DbConf res = new DbConf();
			res.setDbname(config.getString("database.db-name"));
			res.setHostname(config.getString("database.hostname"));
			res.setUser(config.getString("database.username"));
			res.setPass(config.getString("database.password"));
			res.setPort(config.getInteger("database.port", 3306));
			return res;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public String toString() {
		return "DbConf [user=" + user + ", pass=" + ((pass != null) ? "****" : "null") + ", dbname=" + dbname + ", hostname=" + hostname + ", port="
				+ port + "]";
	}
}

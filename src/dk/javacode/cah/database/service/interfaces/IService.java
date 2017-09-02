package dk.javacode.cah.database.service.interfaces;

import java.sql.Connection;

public interface IService {
	public Connection getConnection();
	public void setConnection(Connection c);
}

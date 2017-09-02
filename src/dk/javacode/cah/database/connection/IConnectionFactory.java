package dk.javacode.cah.database.connection;

import java.sql.Connection;
import java.sql.SQLException;

public interface IConnectionFactory {

	public abstract Connection getConnection() throws SQLException;
	public int getNumberOfBusyConnections() throws SQLException;
	public int getNumberOfConnections() throws SQLException;

}
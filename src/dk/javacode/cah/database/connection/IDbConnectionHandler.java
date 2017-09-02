package dk.javacode.cah.database.connection;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import com.mchange.v2.c3p0.PooledDataSource;

public interface IDbConnectionHandler {

	public abstract Connection getConnection() throws SQLException, PropertyVetoException;

	public abstract PooledDataSource getDataSource() throws PropertyVetoException;

}
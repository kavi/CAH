package dk.javacode.cah.database.connection;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import com.mchange.v2.c3p0.PooledDataSource;

public class ConnectionFactory implements IConnectionFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see dk.javacode.tao.db.factory.IDaoFactory#getConnection()
	 */
	@Override
	public Connection getConnection() throws SQLException {
		try {
			return DbConnectionHandler.instance().getConnection();
		} catch (PropertyVetoException e) {
			throw new RuntimeException(e);
		}
	}

	public int getNumberOfConnections() throws SQLException {
		try {
			PooledDataSource ds = DbConnectionHandler.instance().getDataSource();
			if (ds == null) {
				return 1;
			}
			return ds.getNumConnectionsDefaultUser();
		} catch (PropertyVetoException e) {
			throw new RuntimeException(e);
		}
	}

	public int getNumberOfBusyConnections() throws SQLException {
		try {
			PooledDataSource ds = DbConnectionHandler.instance().getDataSource();
			if (ds == null) {
				return 0;
			}
			return ds.getNumBusyConnectionsDefaultUser();
		} catch (PropertyVetoException e) {
			throw new RuntimeException(e);
		}
	}
}

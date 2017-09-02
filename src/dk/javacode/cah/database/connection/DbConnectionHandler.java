package dk.javacode.cah.database.connection;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.PooledDataSource;

import dk.javacode.cah.configuration.DbConf;

public class DbConnectionHandler implements IDbConnectionHandler {
		
	public static DbConf conf;
	private PooledDataSource datasource = null;
	
	public static IDbConnectionHandler instance = new DbConnectionHandler();
	
	private DbConnectionHandler() {
	}

	public static IDbConnectionHandler instance() {
		return instance;
	}
	
	/* (non-Javadoc)
	 * @see dk.javacode.cah.db.connection.IDbConnectionHandler#getConnection()
	 */
	@Override
	public Connection getConnection() throws SQLException, PropertyVetoException {
		PooledDataSource ds = getDataSource();		
		return ds.getConnection();
	}
	
	/* (non-Javadoc)
	 * @see dk.javacode.cah.db.connection.IDbConnectionHandler#getDataSource()
	 */
	@Override
	public PooledDataSource getDataSource() throws PropertyVetoException {
		if (datasource == null) {
			datasource = createDataSource();
		}
		return datasource;
	}

	private PooledDataSource createDataSource() throws PropertyVetoException {
		ComboPooledDataSource cpds = new ComboPooledDataSource();
		cpds.setDriverClass("com.mysql.jdbc.Driver");
		cpds.setJdbcUrl("jdbc:" + conf.getProtocol() + "://" + conf.getHostname()+ ":" + conf.getPort() + "/" + conf.getDbname() + "?zeroDateTimeBehavior=convertToNull");
		cpds.setUser(conf.getUser());
		cpds.setPassword(conf.getPass());
		cpds.setMinPoolSize(5);
		cpds.setAcquireIncrement(5);
		cpds.setMaxPoolSize(20);
		cpds.setMaxIdleTime(10);
		cpds.setNumHelperThreads(10);
		return cpds;
	}	
}

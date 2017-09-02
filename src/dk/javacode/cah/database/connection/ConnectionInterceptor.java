package dk.javacode.cah.database.connection;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import dk.javacode.cah.database.service.interfaces.IService;
import dk.javacode.proxy.InterceptionHandler;

public class ConnectionInterceptor implements InterceptionHandler<IService> {

	private static Logger log = Logger.getLogger(ConnectionInterceptor.class);

	private ConnectionFactory connectionFactory = new ConnectionFactory();
	
	@Override
	public Object before(IService dao, Object[] args) {
		try {
			log.trace("Connection availability. total: " + connectionFactory.getNumberOfConnections() + ", busy: "
					+ connectionFactory.getNumberOfBusyConnections());
			Connection con = connectionFactory.getConnection();
			con.setAutoCommit(false);
			dao.setConnection(con);			
			return null;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void after(IService dao, Object[] args, Object chainedResult) {
		try {			
			if (!dao.getConnection().isClosed()) {
				dao.getConnection().commit();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			closeConnection(dao.getConnection());
		}
	}

	
	@Override
	public void onerror(IService dao, Throwable error) {
		try {
			log.info("Rolling back");
			dao.getConnection().rollback();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			closeConnection(dao.getConnection());
		}
	}
	
	private void closeConnection(Connection con) {
		try {
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Class<IService> accepts() {
		return IService.class;
	}
}

package dk.javacode.cah.database.dao.impl;

import java.sql.Connection;

import dk.javacode.cah.database.connection.IDao;

public abstract class AbstractDao implements IDao {
	
	protected Connection connection;

	@Override
	public Connection getConnection() {
		return this.connection;				
	}

	@Override
	public void setConnection(Connection c) {
		this.connection = c;
	}
	

}

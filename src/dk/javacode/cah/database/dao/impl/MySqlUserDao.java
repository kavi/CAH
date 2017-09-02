package dk.javacode.cah.database.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import dk.javacode.cah.database.connection.ConnectionInterceptor;
import dk.javacode.cah.database.dao.DaoException;
import dk.javacode.cah.database.dao.UserDao;
import dk.javacode.cah.model.User;
import dk.javacode.crypt.CryptHelper;
import dk.javacode.proxy.SimpleInterceptor;
import dk.javacode.srsm.SqlSelectColumn;
import dk.javacode.srsm.exceptions.MappingException;
import dk.javacode.srsm.helpers.SqlInsertHelper;
import dk.javacode.srsm.helpers.SqlQueryHelper;

public class MySqlUserDao extends AbstractDao implements UserDao {
	private SqlQueryHelper sqlBuilder = new SqlQueryHelper();
	private SqlInsertHelper sqlInsert = new SqlInsertHelper();

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public User findById(int id) {
		try {
			return sqlBuilder.getOne(connection, User.class, new SqlSelectColumn("id", id));
		} catch (SQLException e) {
			throw new DaoException(e);
		} catch (MappingException e) {
			throw new DaoException(e);
		}
	}

	@Override
	@SimpleInterceptor(ConnectionInterceptor.class)
	public void deleteUser(String name) {
		try {
			PreparedStatement stmt = connection.prepareStatement("delete from user where name = ?");
			stmt.setString(1, name);
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new DaoException(e);
		}
	}

	@SimpleInterceptor(ConnectionInterceptor.class)
	public User findUser(String name, String plainpassword) {
		try {
			String encodedPass = CryptHelper.encodePassword(plainpassword);
			return sqlBuilder.getOne(connection, User.class, new SqlSelectColumn("name", name), new SqlSelectColumn("password", encodedPass));			
		} catch (SQLException e) {
			throw new DaoException(e);
		} catch (MappingException e) {
			throw new DaoException(e);
		}
	}
	
	@SimpleInterceptor(ConnectionInterceptor.class)
	public User findUser(String name) {
		try {
			return sqlBuilder.getOne(connection, User.class, new SqlSelectColumn("name", name));			
		} catch (SQLException e) {
			throw new DaoException(e);
		} catch (MappingException e) {
			throw new DaoException(e);
		}
	}
	
	@SimpleInterceptor(ConnectionInterceptor.class)
	public void createUser(User user) {
		try {
			String plainpassword = user.getPassword();
			String md5password = CryptHelper.encodePassword(plainpassword);
			user.setPassword(md5password);
			sqlInsert.insert(connection, user, "user");
		} catch (Exception e) {
			throw new DaoException(e);
		} 
	}
	
	

	@SimpleInterceptor(ConnectionInterceptor.class)
	public void updateUser(User user) {
		try {
			PreparedStatement stmt = connection.prepareStatement("update user set email = ?, password = ?, family_filter = ?  where id = ?");
			stmt.setString(1, user.getEmail());
			stmt.setString(2, CryptHelper.encodePassword(user.getPassword()));
			stmt.setBoolean(3, user.isFamilyFilter());
			stmt.setInt(4, user.getId());
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new DaoException(e);
		}
	}

	@SimpleInterceptor(ConnectionInterceptor.class)
	public boolean isUsernameAvailable(String username) {
		try {
			return sqlBuilder.getOne(connection, User.class, new SqlSelectColumn("name", username)) == null;
		} catch (MappingException e) {
			throw new DaoException(e);
		} catch (SQLException e) {
			throw new DaoException(e);
		}					
	}
}

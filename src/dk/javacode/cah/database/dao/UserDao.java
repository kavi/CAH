package dk.javacode.cah.database.dao;

import dk.javacode.cah.database.connection.IDao;
import dk.javacode.cah.model.User;

public interface UserDao extends IDao {

	public void deleteUser(String name);
	
	public User findById(int id);
	public User findUser(String name);
	public User findUser(String name, String password);
	
	public void createUser(User user);
	
	public void updateUser(User user);
	
	public boolean isUsernameAvailable(String username);
}

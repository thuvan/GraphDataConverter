package emaildata.repository;

import emaildata.User;

public interface IUserRepository {
	User getUserByID(int userID);
	User getUserByEmail(String email);
	User addOrUpdate(String from, String username);
	User addIfNotExist(String to);	
	User addUser(String emailAddress,String username);
	int size();
}

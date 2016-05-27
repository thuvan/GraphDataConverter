package emaildata.repository;

import java.util.Hashtable;
import java.util.Map;

import common.AbstractRepository;
import emaildata.User;
public class UserRepository extends AbstractRepository implements IUserRepository {
	private final Map<Integer, User> userByID = new Hashtable<Integer,User>();
	private final Map<String, User> userByEmailAddress = new Hashtable<String,User>();
	@Override
	public User getUserByID(int userID) {
		return userByID.get(userID);
	}

	@Override
	public User getUserByEmail(String email) {
		return userByEmailAddress.get(email);
	}

	@Override
	public User addOrUpdate(String emailAddress, String username) {
		User user = getUserByEmail(emailAddress);		
		if (user == null){
			user = addUser(emailAddress, username);
		}else
			user.setUsername(username);
		return user;
	}

	@Override
	public User addIfNotExist(String emailAddress) {
		User user = getUserByEmail(emailAddress);		
		if (user == null){
			user = addUser(emailAddress, "unknow");
		}
		return user;
	}

	@Override
	public User addUser(String emailAddress,String username) {		
		User user = new User();
		user.setEmailAddress(emailAddress);
		user.setUsername(username);
		user.setId(this.getAndIncrementNextId());
		userByEmailAddress.put(user.getEmailAddress(), user);
		userByID.put(user.getId(), user);		
		return user;
	}

	@Override
	public int size() {
		return this.userByEmailAddress.size();
	}
	
	

}

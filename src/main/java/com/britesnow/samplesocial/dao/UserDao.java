package com.britesnow.samplesocial.dao;


import java.util.Optional;

import org.j8ql.query.Query;

import com.britesnow.samplesocial.entity.User;
import com.google.inject.Singleton;

@Singleton
public class UserDao extends BaseDao<User,Long> {

	public Optional<User> get(Long id) {
		return super.get(null, id);
	}

	public Optional<User> getByUsername(String username){
		return daoHelper.first(Query.select(entityClass).where("username", username));
	}

	// --------- create user --------- //
	/**
	 * Create a new user and return the complete user object.
	 */
	public User createUser(String username, String password){
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		return createUser(user);
	}

	/**
	 * Create a demo user. Same as createUser, but add the setDemo("enron")
	 */
	public User createDemoUser(String username, String password){
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		// for User, we can create new ones without an existing User
		return createUser(user);
	}

	/**
	 * Low level method that create the user, the "UserFeederInfo" entity, and return the User object.
	 *
	 * TODO: probably need to the UserFeedInfo object create in the override of the dao.create
	 *
	 * @param user
	 * @return
	 */
	private User createUser(User user){
		Long id = create(null, user);
		user = get(null,id).get();
		return user;
	}

}

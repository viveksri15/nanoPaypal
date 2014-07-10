package com.nanoPaypal.user.impl;

import com.nanoPaypal.user.specs.IUser;

public class UserFactory {
	/**
	 * Get a user
	 * @param userName
	 * @param password
	 * @return user if exists
	 * @throws Exception
	 */
	public static IUser getUser(String userName, String password)
			throws Exception {
		return new DBUser(userName, password);
	}
	/**
	 * Create a new user
	 * @param userName
	 * @param password
	 * @param email
	 * @return a new user if created
	 * @throws Exception
	 */
	public static IUser createUser(String userName, String password,
			String email) throws Exception {
		DBUser dbUser = new DBUser(userName, password, email);
		return dbUser;
	}

	/**
	 * Get user based on his uid
	 * @param uid
	 * @return user if found
	 * @throws Exception
	 */
	public static IUser getUser(String uid) throws Exception {
		return new DBUser(uid, 0);
	}

	/**
	 * Get user from email
	 * @param email
	 * @return user if found
	 * @throws Exception
	 */
	public static IUser getUserFromEmail(String email) throws Exception {
		return new DBUser(email, 1);
	}

	public static IUser getUserFromUserName(String userName) throws Exception {
		return new DBUser(userName, 2);
	}
}
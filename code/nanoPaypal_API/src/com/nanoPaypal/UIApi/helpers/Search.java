package com.nanoPaypal.UIApi.helpers;

import com.nanoPaypal.user.impl.UserFactory;
import com.nanoPaypal.user.specs.IUser;

public class Search {

	/**
	 * Searches a user by his email
	 * @param emailId
	 * @return uid of the user if found, else null
	 */
	public static String searchUser(String emailId) {
		try {
			IUser user = UserFactory.getUserFromEmail(emailId);
			if (user != null)
				return user.getUID();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Searches a user by his userName
	 * @param userName of the user
	 * @return uid of the user if found, else null
	 */
	public static String getUid(String userName) {
		try {
			IUser user = UserFactory.getUserFromUserName(userName);
			if (user != null)
				return user.getUID();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

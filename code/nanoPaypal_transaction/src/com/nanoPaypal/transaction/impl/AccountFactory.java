package com.nanoPaypal.transaction.impl;

import com.nanoPaypal.transaction.spec.IAccount;
import com.nanoPaypal.user.specs.IUser;

public class AccountFactory {
	/**
	 * @param iUser
	 * @return the account of the user
	 */
	public static IAccount getAccount(IUser iUser) {
		return new DBAccount(iUser);
	}
}

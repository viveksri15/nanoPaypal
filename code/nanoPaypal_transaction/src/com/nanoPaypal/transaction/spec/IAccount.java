package com.nanoPaypal.transaction.spec;

import java.sql.SQLException;

/**
 * Account of the user
 * User can have multiple accounts
 * Accounts can also be fetched from Network etc besides database
 * 	//TODO: ideally, amount/currency should be in a separate class to support multiple currencies
 * @author vivek
 *
 */
public interface IAccount {
	public void initNewAccount() throws SQLException;
	public void initAccount();
	public int getAccountId();
	public float getFunds();
	String transferFund(int recipientAccountId, float amount) throws Exception;
}
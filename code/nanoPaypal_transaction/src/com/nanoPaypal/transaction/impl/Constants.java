package com.nanoPaypal.transaction.impl;

public class Constants {
	public static final int INITIAL_AMOUNT = 100;
	public static final String QUERY_getAccountDetails = "select accountId, balance from account where userName=?";
	public static final String QUERY_getAccountDetailsByID = "select userName, balance from account where accountId=?";
	public static final String QUERY_updateBalance = "update account set balance =?  where accountId=?";
	public static final String QUERY_insertBalance = "insert into account (userName, balance) values (?,?)";
	public static final String QUERY_addTransactionUser = "insert into recipient (uid, ruid) values (?,?)";
	public static final String QUERY_getTransactionUsers = "select distinct a.email from users a INNER JOIN recipient b ON b.ruid=a.uid where b.uid=?";
	public static final String QUERY_deleteTransactionUsers = "delete from recipient where uid=? and ruid=?";
}
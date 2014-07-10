package com.nanoPaypal.UIApi.helpers;

public class Constants {

	public static final String QUERY_getHistory = "select time, transactionId, otherUser, type, balance, amount from transactionRecord where uid=? order by time desc";
	public static final String QUERY_getHistoryAll = "select a.time, a.transactionId, a.otherUser, a.type, a.balance, a.amount, b.userName from transactionRecord a INNER JOIN users b on a.uid=b.uid order by time desc";
	public static final String QUERY_getUsers = "select userName from users";
}

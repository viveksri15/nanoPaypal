package com.nanoPaypal.transaction.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

import com.nanoPaypal.db.DBFactory;
import com.nanoPaypal.db.impl.sqlLite.DeleteDBOperation;
import com.nanoPaypal.db.impl.sqlLite.InsertDBOperation;
import com.nanoPaypal.db.impl.sqlLite.SelectDBOperation;
import com.nanoPaypal.db.specs.DatabaseConnection;
import com.nanoPaypal.transaction.spec.IAccount;

/**
 * Generic Transaction Manager
 * @author vivek
 *
 */
public class TransactionManager {

	//Returns only single object for each accountId

	private static Map<Integer, Semaphore> lockedAccounts = new HashMap<Integer, Semaphore>();
	private static Map<String, Set<Integer>> currentTransactions = new HashMap<String, Set<Integer>>();

	//ideally, only transactionId should be able to unlock the transaction
	@SuppressWarnings("unused")
	private String trasactionId;
	private int accountId;

	public TransactionManager(int accountId, String trasactionId)
			throws InterruptedException {
		Semaphore semaphore = lockedAccounts.get(accountId);
		if (semaphore == null) {
			synchronized (lockedAccounts) {
				semaphore = lockedAccounts.get(accountId);
				if (semaphore == null) {
					semaphore = new Semaphore(1);
					lockedAccounts.put(accountId, semaphore);
				}
			}
		}
		semaphore.acquire();

		Set<Integer> set = currentTransactions.get(trasactionId);
		if (set == null) {
			synchronized (currentTransactions) {
				set = currentTransactions.get(accountId);
				if (set == null) {
					set = new HashSet<>();
					currentTransactions.put(trasactionId, set);
				}
			}
		}
		synchronized (set) {
			set.add(accountId);
		}
		this.accountId = accountId;
		this.trasactionId = trasactionId;
	}

	public IAccount getAccount() {
		IAccount account = new DBAccount(accountId);
		return account;
	}

	public synchronized static void endTransaction(String transactionId) {
		Set<Integer> accounts = null;
		accounts = currentTransactions.remove(transactionId);
		if (accounts != null) {
			Iterator<Integer> iterator = accounts.iterator();
			while (iterator.hasNext()) {
				Semaphore semaphore = null;
				semaphore = lockedAccounts.remove(iterator.next());
				semaphore.release();
			}
		}
	}

	/**
	 * Add user for transaction
	 * @param uid1: Source user
	 * @param uid2: Add this user to source user
	 * @return true if added, false otherwise
	 */
	public static boolean addUser(String uid1, String uid2) {
		Object[] params = new Object[]{uid1, uid2};
		InsertDBOperation dbOperation = new InsertDBOperation(
				Constants.QUERY_addTransactionUser, params);
		DatabaseConnection connection = DBFactory.getDBConnection();
		try {
			return connection.execute(dbOperation);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Get users for transaction
	 * @param uid
	 * @return users for transaction
	 */
	public static List<String> getUsers(String uid) {
		Object[] params = new Object[]{uid};
		SelectDBOperation dbOperation = new SelectDBOperation(
				Constants.QUERY_getTransactionUsers, params);
		List<String> ret = new ArrayList<>();
		DatabaseConnection connection = DBFactory.getDBConnection();
		List<Map<String, Object>> data = connection.getData(dbOperation);
		if (data != null) {
			for (Map<String, Object> mp : data) {
				ret.add((String) mp.get("email"));
			}
		}
		return ret;
	}

	/**
	 * Delete user preventing future transaction between uid1 and uid2 user
	 * @param uid1
	 * @param uid2 delete this user from uid1's list of users
	 * @return true if deleted, false otherwise
	 */
	public static boolean deleteUsers(String uid1, String uid2) {
		Object[] params = new Object[]{uid1, uid2};
		DeleteDBOperation dbOperation = new DeleteDBOperation(
				Constants.QUERY_deleteTransactionUsers, params);
		DatabaseConnection connection = DBFactory.getDBConnection();
		try {
			return connection.execute(dbOperation);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}

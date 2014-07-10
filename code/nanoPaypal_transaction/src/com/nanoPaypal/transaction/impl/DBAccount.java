package com.nanoPaypal.transaction.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import com.nanoPaypal.db.impl.sqlLite.InsertDBOperation;
import com.nanoPaypal.db.impl.sqlLite.SQLiteConnection;
import com.nanoPaypal.db.impl.sqlLite.SelectDBOperation;
import com.nanoPaypal.db.impl.sqlLite.UpdateDBOperation;
import com.nanoPaypal.db.specs.INoResultDBOperation;
import com.nanoPaypal.transaction.spec.IAccount;
import com.nanoPaypal.user.specs.IUser;

/**
 * Account of the user from database
 * @author vivek
 *
 */
public class DBAccount implements IAccount {

	private IUser user;
	private int accountId;
	private float amount;
	public DBAccount(IUser iUser) {
		this.user = iUser;
		initAccount();
	}

	/*
	 * Used by TransactionManager
	 */
	DBAccount(int accountId) {

		this.accountId = accountId;
		Object[] params = new Object[]{accountId};
		String query = Constants.QUERY_getAccountDetailsByID;
		SelectDBOperation dbOperation = new SelectDBOperation(query, params);
		SQLiteConnection connection = new SQLiteConnection();
		List<Map<String, Object>> data = connection.getData(dbOperation);
		if (data != null && data.size() > 0) {
			amount = Float.parseFloat(data.get(0).get("balance") + "");
		}

		//user is null
	}

	@Override
	public void initAccount() {
		Object[] params = new Object[]{user.getUserName()};
		String query = Constants.QUERY_getAccountDetails;
		SelectDBOperation dbOperation = new SelectDBOperation(query, params);
		SQLiteConnection connection = new SQLiteConnection();
		List<Map<String, Object>> data = connection.getData(dbOperation);
		if (data != null && data.size() > 0) {
			accountId = (int) data.get(0).get("accountId");
			amount = Float.parseFloat(data.get(0).get("balance") + "");
			System.out.println(accountId + " " + amount);
		}
	}

	@Override
	public void initNewAccount() throws SQLException {
		setFunds(Constants.INITIAL_AMOUNT);
	}

	private void setFunds(int initialAmount) throws SQLException {
		String query = Constants.QUERY_insertBalance;
		Object[] params1 = new Object[]{user.getUserName(), initialAmount};
		InsertDBOperation dbOperation1 = new InsertDBOperation(query, params1);
		SQLiteConnection connection = new SQLiteConnection();
		connection.execute(dbOperation1);
		initAccount();
	}

	@Override
	public int getAccountId() {
		return accountId;
	}

	@Override
	public float getFunds() {
		return amount;
	}

	/**
	 * Atomic operation to transfer funds. Ensures that accounts whose funds are getting transferred are locked, and properly released, ensuring consistency in funds
	 */
	@Override
	public String transferFund(int recipientAccountId,
			float amountFromThisToRecipient) throws NoSuchAlgorithmException,
			InterruptedException, InsufficientBalanceException, SQLException {
		String transactionId = accountId + recipientAccountId
				+ System.currentTimeMillis() + "";
		try {
			if (amountFromThisToRecipient <= 0)
				return null;
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			transactionId = (new HexBinaryAdapter()).marshal(md5
					.digest(transactionId.getBytes()));

			TransactionManager thisTransactionManager = new TransactionManager(
					this.accountId, transactionId);

			TransactionManager rTransactionManager = new TransactionManager(
					recipientAccountId, transactionId);

			IAccount thisAccount = thisTransactionManager.getAccount();
			IAccount rAccount = rTransactionManager.getAccount();

			float thisBalance = thisAccount.getFunds();
			float rBalance = rAccount.getFunds();

			thisBalance -= amountFromThisToRecipient;
			if (thisBalance < 0)
				throw new InsufficientBalanceException();
			rBalance += amountFromThisToRecipient;

			String query = Constants.QUERY_updateBalance;
			Object[] params1 = new Object[]{thisBalance, accountId};
			UpdateDBOperation dbOperation1 = new UpdateDBOperation(query,
					params1);

			Object[] params2 = new Object[]{rBalance, recipientAccountId};
			UpdateDBOperation dbOperation2 = new UpdateDBOperation(query,
					params2);

			List<INoResultDBOperation> dbOperations = new ArrayList<>();
			dbOperations.add(dbOperation1);
			dbOperations.add(dbOperation2);

			SQLiteConnection connection = new SQLiteConnection();
			connection.execute(dbOperations);
			return transactionId;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			TransactionManager.endTransaction(transactionId);
		}
	}
}

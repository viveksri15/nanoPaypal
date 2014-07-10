package com.nanoPaypal.user.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import com.nanoPaypal.db.DBFactory;
import com.nanoPaypal.db.impl.sqlLite.InsertDBOperation;
import com.nanoPaypal.db.impl.sqlLite.SelectDBOperation;
import com.nanoPaypal.db.impl.sqlLite.UpdateDBOperation;
import com.nanoPaypal.db.specs.DatabaseConnection;
import com.nanoPaypal.user.impl.exceptions.BadUserNamePasswordException;
import com.nanoPaypal.user.impl.exceptions.UserExistsException;
import com.nanoPaypal.user.specs.IContact;
import com.nanoPaypal.user.specs.IUser;

/**
 * User initialized from database
 * @author vivek
 *
 */
public class DBUser implements IUser {

	private String userName, uid;
	Set<IContact> contacts = new HashSet<>();
	Set<ROLE> roles = new HashSet<>();

	public DBUser(String userName, String password)
			throws BadUserNamePasswordException, NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		password = (new HexBinaryAdapter()).marshal(md5.digest(password
				.getBytes()));

		Object[] params = new Object[]{userName, password};
		DatabaseConnection dbConnection = DBFactory.getDBConnection();
		String query = Constants.QUERY_loginUser;
		SelectDBOperation dbOperation = new SelectDBOperation(query, params);
		List<Map<String, Object>> data = dbConnection.getData(dbOperation);
		if (data != null && data.size() > 0) {
			String userName1 = (String) data.get(0).get("userName");
			if (userName1 == null)
				throw new BadUserNamePasswordException();
			this.userName = userName1;
			this.uid = (String) data.get(0).get("uid");
			initUser((String) data.get(0).get("email"));
		}
	}
	private void initUser(String email) {
		IContact contact = new ContactCard(CONTACT_TYPE.EMAIL, email);
		contacts.add(contact);
		addRole(ROLE.USER);

		Object[] params = new Object[]{userName};
		DatabaseConnection dbConnection = DBFactory.getDBConnection();
		String query = Constants.QUERY_getRoles;
		SelectDBOperation dbOperation = new SelectDBOperation(query, params);
		List<Map<String, Object>> data = dbConnection.getData(dbOperation);

		ROLE[] roleValues = ROLE.values();
		Map<Integer, ROLE> roles = new HashMap<>();
		for (ROLE role : roleValues) {
			roles.put(role.getRoleId(), role);
		}
		if (data != null && data.size() > 0) {
			for (Map<String, Object> map : data) {
				int roleId = (int) map.get("roleId");
				addRole(roles.get(roleId));
			}
		}
	}
	public DBUser(String userName, String password, String email)
			throws Exception {

		checkUser(userName);
		checkEmail(email);
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		password = (new HexBinaryAdapter()).marshal(md5.digest(password
				.getBytes()));

		DatabaseConnection dbConnection = DBFactory.getDBConnection();
		this.uid = (new HexBinaryAdapter()).marshal(md5
				.digest((userName + System.currentTimeMillis()).getBytes()));
		Object[] params = new Object[]{userName, password, email, uid};
		String query = Constants.QUERY_createUser;
		InsertDBOperation dbOperation = new InsertDBOperation(query, params);
		dbConnection.execute(dbOperation);
		this.userName = userName;

		initUser(email);
		setRole(ROLE.USER);
		setRole(ROLE.TRANSACTION_USER);
		setRole(ROLE.LOGGEDIN_USER);
	}

	public DBUser(String param, int i) {
		Object[] params = new Object[]{param};
		DatabaseConnection dbConnection = DBFactory.getDBConnection();
		String query = Constants.QUERY_getUser;
		if (i == 1)
			query = Constants.QUERY_getUserByEmail;
		if (i == 2)
			query = Constants.QUERY_getUserByUserName;
		SelectDBOperation dbOperation = new SelectDBOperation(query, params);
		List<Map<String, Object>> data = dbConnection.getData(dbOperation);
		if (data != null && data.size() > 0) {
			String userName1 = (String) data.get(0).get("userName");
			this.userName = userName1;
			this.uid = (String) data.get(0).get("uid");
			initUser((String) data.get(0).get("email"));
		}
	}

	private static void checkUser(String userName) throws UserExistsException {
		Object[] params = new Object[]{userName};
		DatabaseConnection dbConnection = DBFactory.getDBConnection();
		String query = Constants.QUERY_checkUser;
		SelectDBOperation dbOperation = new SelectDBOperation(query, params);
		List<Map<String, Object>> data = dbConnection.getData(dbOperation);
		if (data != null && data.size() > 0) {
			throw new UserExistsException();
		}
	}

	private static void checkEmail(String email) throws UserExistsException {
		Object[] params = new Object[]{email};
		DatabaseConnection dbConnection = DBFactory.getDBConnection();
		String query = Constants.QUERY_checkEmail;
		SelectDBOperation dbOperation = new SelectDBOperation(query, params);
		List<Map<String, Object>> data = dbConnection.getData(dbOperation);
		if (data != null && data.size() > 0) {
			throw new UserExistsException();
		}
	}

	@Override
	public Set<ROLE> getRoles() {
		return roles;
	}
	@Override
	public String getUserName() {
		return userName;
	}

	@Override
	public Set<IContact> getContacts() {
		return contacts;
	}

	@Override
	public void addRole(ROLE iRole) {
		roles.add(iRole);
	}

	@Override
	public void setRole(ROLE iRole) {
		Object[] params = new Object[]{userName, iRole.getRoleId()};
		DatabaseConnection dbConnection = DBFactory.getDBConnection();
		String query = Constants.QUERY_insertRole;
		InsertDBOperation dbOperation = new InsertDBOperation(query, params);
		boolean done = false;
		try {
			done = dbConnection.execute(dbOperation);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (done)
			addRole(iRole);
	}
	@Override
	public String getUID() {
		return uid;
	}
	@Override
	public boolean setPassword(String password) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
			return false;
		}
		password = (new HexBinaryAdapter()).marshal(md5.digest(password
				.getBytes()));

		Object[] params = new Object[]{password, uid};
		UpdateDBOperation dbOperation = new UpdateDBOperation(
				Constants.QUERY_changePassword, params);
		DatabaseConnection connection = DBFactory.getDBConnection();
		try {
			return connection.execute(dbOperation);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}

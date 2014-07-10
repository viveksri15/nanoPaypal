package com.nanoPaypal.UIApi.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.nanoPaypal.db.DBFactory;
import com.nanoPaypal.db.impl.sqlLite.SelectDBOperation;
import com.nanoPaypal.db.specs.DatabaseConnection;

public class AdminHelper {
	
	/**
	 * Used to get users in the system
	 * @return list of users
	 */
	public static List<String> getAllUsers() {
		List<String> users = new ArrayList<>();
		String query = Constants.QUERY_getUsers;
		Object[] params = new Object[]{};
		SelectDBOperation dbOperation = new SelectDBOperation(query, params);
		DatabaseConnection connection = DBFactory.getDBConnection();
		List<Map<String, Object>> data = connection.getData(dbOperation);
		if (data != null) {
			for (Map<String, Object> mp : data) {
				users.add((String) mp.get("userName"));
			}
		}
		return users;
	}
}

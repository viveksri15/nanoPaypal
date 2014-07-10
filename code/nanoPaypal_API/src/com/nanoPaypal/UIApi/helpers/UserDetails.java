package com.nanoPaypal.UIApi.helpers;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.nanoPaypal.db.DBFactory;
import com.nanoPaypal.db.impl.sqlLite.SelectDBOperation;
import com.nanoPaypal.db.specs.DatabaseConnection;

public class UserDetails {
	
	/**
	 * Used to get user's transaction history
	 * @param userId
	 * @return User's history. The first element of the list contains the header. Subsequent elements contain the data
	 */
	public static List<List<Object>> getDetails(String userId) {
		Object[] params = new Object[]{userId};
		String query = Constants.QUERY_getHistory;
		SelectDBOperation dbOperation = new SelectDBOperation(query, params);
		DatabaseConnection connection = DBFactory.getDBConnection();
		List<Map<String, Object>> data = connection.getData(dbOperation);
		System.out.println("data=" + data);
		if (data == null) {
			return null;
		}
		List<List<Object>> result = new ArrayList<>();
		List<Object> header = new ArrayList<>();
		header.add("Date");
		header.add("transactionId");
		header.add("other party");
		header.add("type");
		header.add("balance");
		header.add("amount");

		result.add(header);

		for (Map<String, Object> map : data) {
			List<Object> dt = new ArrayList<>();
			dt.add(map.get("time"));
			dt.add(map.get("transactionId"));
			dt.add(map.get("otherUser"));
			String type = (String) map.get("type");
			if (type.equals("D"))
				type = "Debit";
			else
				type = "Credit";
			dt.add(type);

			DecimalFormat df = new DecimalFormat("0.00##");

			dt.add(df.format(Float.parseFloat(map.get("balance") + "")));
			dt.add(df.format(Float.parseFloat(map.get("amount") + "")));
			result.add(dt);
		}

		return result;
	}

	/**
	 * Used to get eveyone's transaction history
	 * @return All history. The first element of the list contains the header. Subsequent elements contain the data
	 */
	public static List<List<Object>> getDetails() {
		Object[] params = new Object[]{};
		String query = Constants.QUERY_getHistoryAll;
		SelectDBOperation dbOperation = new SelectDBOperation(query, params);
		DatabaseConnection connection = DBFactory.getDBConnection();
		List<Map<String, Object>> data = connection.getData(dbOperation);
		System.out.println("data=" + data);
		if (data == null) {
			return null;
		}
		List<List<Object>> result = new ArrayList<>();
		List<Object> header = new ArrayList<>();
		header.add("Date");
		header.add("transactionId");
		header.add("party 1");
		header.add("party 2");
		header.add("type");
		header.add("balance");
		header.add("amount");

		result.add(header);

		for (Map<String, Object> map : data) {
			List<Object> dt = new ArrayList<>();
			dt.add(map.get("time"));
			dt.add(map.get("transactionId"));
			dt.add(map.get("userName"));
			dt.add(map.get("otherUser"));
			String type = (String) map.get("type");
			if (type.equals("D"))
				type = "Debit";
			else
				type = "Credit";
			dt.add(type);

			DecimalFormat df = new DecimalFormat("0.00##");

			dt.add(df.format(Float.parseFloat(map.get("balance") + "")));
			dt.add(df.format(Float.parseFloat(map.get("amount") + "")));
			result.add(dt);
		}

		return result;
	}
}

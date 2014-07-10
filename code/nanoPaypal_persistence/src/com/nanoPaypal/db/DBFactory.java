package com.nanoPaypal.db;

import com.nanoPaypal.db.impl.sqlLite.SQLiteConnection;
import com.nanoPaypal.db.specs.DatabaseConnection;

public class DBFactory {
	/**
	 * 
	 * @return database connection object
	 */
	public static DatabaseConnection getDBConnection() {
		return new SQLiteConnection();
	}
}

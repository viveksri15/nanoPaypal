package com.nanoPaypal.logging;

import java.sql.SQLException;

import com.nanoPaypal.db.DBFactory;
import com.nanoPaypal.db.impl.sqlLite.InsertDBOperation;
import com.nanoPaypal.db.specs.DatabaseConnection;

/**
 * Log transactions in database
 * @author vivek
 */
public abstract class DBLogger implements ILogger {

	protected abstract String getQuery();
	protected abstract Object[] getParams();

	@Override
	public boolean log() {
		InsertDBOperation dbOperation = new InsertDBOperation(getQuery(),
				getParams());
		DatabaseConnection connection = DBFactory.getDBConnection();
		try {
			return connection.execute(dbOperation);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

}

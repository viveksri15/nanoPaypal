package com.nanoPaypal.db.specs;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class DatabaseConnection {

	/**
	 * Ensures given operations run as one atomic operation
	 * @param databaseOperation
	 * @return true if all operations succeed, and false if an operation (and hence all operations) fail
	 * @throws SQLException
	 */
	public boolean execute(List<INoResultDBOperation> databaseOperation)
			throws SQLException {
		Connection connection = null;
		try {
			connection = initConnection();
			boolean ret = true;
			for (INoResultDBOperation dbOperation : databaseOperation) {
				ret = dbOperation.execute(connection);
				if (!ret) {
					connection.rollback();
					throw new SQLException();
				}
			}
			if (ret) {
				connection.commit();
				return true;
			}
		} catch (SQLException e) {
			if (connection != null)
				connection.rollback();
			throw e;
		} finally {
			closeConnection(connection);
		}
		return false;
	};

	public boolean execute(INoResultDBOperation databaseOperation)
			throws SQLException {
		List<INoResultDBOperation> list = new ArrayList<>();
		list.add(databaseOperation);
		return execute(list);
	};

	public List<Map<String, Object>> getData(
			IResultDBOperation databaseOperation) {
		Connection connection = null;
		List<Map<String, Object>> ret = null;
		try {
			connection = initConnection();
			ret = databaseOperation.execute(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection(connection);
		}
		return ret;
	};

	protected abstract Connection initConnection() throws SQLException;
	protected abstract void closeConnection(Connection connection);
}
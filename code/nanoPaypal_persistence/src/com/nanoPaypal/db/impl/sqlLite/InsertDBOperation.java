package com.nanoPaypal.db.impl.sqlLite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.nanoPaypal.db.specs.INoResultDBOperation;

/**
 * Insert operation in the database
 * @author vivek
 */
public class InsertDBOperation implements INoResultDBOperation {

	String query;
	Object[] params;
	public InsertDBOperation(String query, Object[] params) {
		this.query = query;
		this.params = params;
	}

	@Override
	public boolean execute(Connection connection) throws SQLException {
		boolean execute = false;
		try {
			PreparedStatement stmt = connection.prepareStatement(query);
			for (int i = 0; i < params.length; i++)
				stmt.setObject(i + 1, params[i]);
			int count = stmt.executeUpdate();
			if (count > 0)
				execute = true;
			System.out.println(execute);
			stmt.close();
		} catch (Exception e) {
			throw e;
		}
		return execute;
	}
}

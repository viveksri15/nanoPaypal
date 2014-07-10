package com.nanoPaypal.db.impl.sqlLite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nanoPaypal.db.specs.IResultDBOperation;

/**
 * Select operation in the database
 * @author vivek
 */
public class SelectDBOperation implements IResultDBOperation {

	String query;
	Object[] params;
	public SelectDBOperation(String query, Object[] params) {
		this.query = query;
		this.params = params;
	}

	/**
	 * @return result, where rows are represented in list element, and maps denote key-value pair of column-data of a given row.
	 */
	@Override
	public List<Map<String, Object>> execute(Connection connection)
			throws SQLException {
		PreparedStatement stmt = connection.prepareStatement(query);
		for (int i = 0; i < params.length; i++)
			stmt.setObject(i + 1, params[i]);
		ResultSet rset = stmt.executeQuery();
		try {
			if (rset != null) {
				List<Map<String, Object>> result = new ArrayList<>();
				int numcols = rset.getMetaData().getColumnCount();
				while (rset.next()) {
					Map<String, Object> map = new HashMap<String, Object>();
					for (int i = 1; i <= numcols; i++) {
						map.put(rset.getMetaData().getColumnName(i),
								rset.getObject(i));
					}
					result.add(map);
				}
				return result;
			}
		} finally {
			rset.close();
			stmt.close();
		}
		return null;
	}
}
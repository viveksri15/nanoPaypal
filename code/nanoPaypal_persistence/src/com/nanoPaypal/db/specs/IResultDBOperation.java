package com.nanoPaypal.db.specs;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface IResultDBOperation {
	public List<Map<String, Object>> execute(Connection connection)
			throws SQLException;
}

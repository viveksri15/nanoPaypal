package com.nanoPaypal.db.specs;

import java.sql.Connection;
import java.sql.SQLException;

public interface INoResultDBOperation {

	boolean execute(Connection connection) throws SQLException;
}
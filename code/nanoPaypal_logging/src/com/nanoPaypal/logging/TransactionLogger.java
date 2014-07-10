package com.nanoPaypal.logging;

/**
 * Transaction Logger
 * @author vivek
 *
 */
public class TransactionLogger extends DBLogger {

	private String query;
	private Object[] params;
	public TransactionLogger(String query, Object[] params) {
		this.query = query;
		this.params = params;
	}
	protected String getQuery() {
		return query;
	}
	protected Object[] getParams() {
		return params;
	}
}

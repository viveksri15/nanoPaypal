package com.nanoPaypal.logging;

/**
 * Log Errors in a file
 * @author vivek
 */
class ErrorLogger extends FileLogger {

	public ErrorLogger() {
		super("errors");
	}
	public ErrorLogger(String trackType) {
		super(trackType);
	}
}

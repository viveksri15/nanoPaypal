package com.nanoPaypal.logging;

/**
 * Log errors
 * @author vivek
 */
public class ErrorLoggerFactory {
	public static FileLogger getInstance() {
		return new ErrorLogger();
	};
}

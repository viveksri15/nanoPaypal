package com.nanoPaypal.user.impl;

public class Constants {
	public static final String QUERY_loginUser = "select userName, email, uid from users where userName=? and password=?";
	public static final String QUERY_getRoles = "select distinct roleId from roles where userName=?";
	public static final String QUERY_insertRole = "insert into roles(userName, roleId) values (?,?)";
	public static final String QUERY_getUser = "select userName, email, uid from users where uid=?";
	public static final String QUERY_getUserByEmail = "select userName, email, uid from users where email=?";
	public static final String QUERY_changePassword = "update users set password=? where uid=?";
	public static final String QUERY_getUserByUserName = "select userName, email, uid from users where userName=?";
	public static String QUERY_createUser = "insert into users(userName, password, email, uid) values (?, ?, ?, ?)";
	public static String QUERY_checkUser = "select userName from users where userName = ?";
	public static String QUERY_checkEmail = "select email from users where email = ?";
}

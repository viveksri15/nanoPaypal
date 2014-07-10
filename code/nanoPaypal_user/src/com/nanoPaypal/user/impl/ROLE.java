package com.nanoPaypal.user.impl;

public enum ROLE {
	USER(0), LOGGEDIN_USER(1), TRANSACTION_USER(2), ADMINISTRATOR(3);
	private int roleId;
	ROLE(int id) {
		this.roleId = id;
	}
	public int getRoleId() {
		return roleId;
	}
}
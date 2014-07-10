package com.nanoPaypal.user.specs;

import java.util.Set;

import com.nanoPaypal.user.impl.ROLE;

/**
 * Subclassing this will give the application support of initializing user from network etc.
 * @author vivek
 *
 */
public interface IUser {
	/**
	 * Get roles a particular user can have
	 * @return roles
	 */
	public Set<ROLE> getRoles();
	
	/**
	 * Add a role
	 * @param iRole
	 */
	public void addRole(ROLE iRole);
	
	/**
	 * Get all the contcts of the user
	 * A user can have multiple contacts
	 * @return contacts
	 */
	public Set<IContact> getContacts();
	
	/**
	 * 
	 * @return userName of the user
	 */
	public String getUserName();
	
	/**
	 * 
	 * @return unique Id of the user
	 */
	public String getUID();
	
	public void setRole(ROLE iRole);
	public boolean setPassword(String password);
}
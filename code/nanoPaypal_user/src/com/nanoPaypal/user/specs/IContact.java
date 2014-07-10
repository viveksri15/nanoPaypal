package com.nanoPaypal.user.specs;

import com.nanoPaypal.user.impl.CONTACT_TYPE;

/**
 * Subclassing this can give feature of getting contact from network etc.
 * Also, a user can have multiple contacts
 * @author vivek
 *
 */
public interface IContact {
	public CONTACT_TYPE getContactType();
	public String getValue();
}

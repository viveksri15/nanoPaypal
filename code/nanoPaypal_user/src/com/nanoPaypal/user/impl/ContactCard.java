package com.nanoPaypal.user.impl;

import com.nanoPaypal.user.specs.IContact;
/**
 * A contact card of the user
 * @author vivek
 *
 */
public class ContactCard implements IContact {

	private CONTACT_TYPE contact_TYPE;
	private String value;

	public ContactCard(CONTACT_TYPE contact_TYPE, String value) {
		this.contact_TYPE = contact_TYPE;
		this.value = value;
	}

	@Override
	public CONTACT_TYPE getContactType() {
		return contact_TYPE;
	}

	@Override
	public String getValue() {
		return value;
	}

}

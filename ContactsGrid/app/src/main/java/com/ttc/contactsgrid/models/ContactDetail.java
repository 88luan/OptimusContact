/**
 * 
 */
package com.ttc.contactsgrid.models;

/**
 * @author OPTIMUS
 * 
 */
public class ContactDetail {
	
	/**
	 * type of Phone or Email. Such as: Home or Work or Mobile
	 */
	private final int type;

	/**
	 * value of Phone or Email such as: 0992143 or saaad@gmail.com
	 */
	private final String value;
	
	private String name;

	public ContactDetail(String value, int type) {
		this.type = type;
		this.value = value;
	}

	public int getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}


}

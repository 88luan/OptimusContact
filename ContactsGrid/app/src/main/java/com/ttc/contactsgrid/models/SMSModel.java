package com.ttc.contactsgrid.models;

import java.util.Date;

import android.text.format.DateFormat;

/**
 * This class represents SMS.
 * 
 * @author itcuties
 * 
 */
public class SMSModel {

	// Number from witch the sms was send
	private String number;
	// SMS text body
	private String body;

	private String type;

	private String id;

	private String thread_id;

	private String contact_id;

	private String time;

	public SMSModel() {

	}

	public SMSModel(String numbeR, String message, String typE) {
		number = numbeR;
		body = message;
		type = typE;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the thread_id
	 */
	public String getThread_id() {
		return thread_id;
	}

	/**
	 * @param thread_id
	 *            the thread_id to set
	 */
	public void setThread_id(String thread_id) {
		this.thread_id = thread_id;
	}

	/**
	 * @return the time
	 */
	public String getTime() {
		return time;
	}

	/**
	 * @param time
	 *            the time to set
	 */
	public void setTime(String time) {
		Long timestamp = Long.parseLong(time);
		this.time = (String) DateFormat.format("h:mm:ss aa - EEEE, dd/MM",
				new Date(timestamp));
	}

	/**
	 * @return the contact_id
	 */
	public String getContact_id() {
		return contact_id;
	}

	/**
	 * @param contact_id
	 *            the contact_id to set
	 */
	public void setContact_id(String contact_id) {
		this.contact_id = contact_id;
	}

}

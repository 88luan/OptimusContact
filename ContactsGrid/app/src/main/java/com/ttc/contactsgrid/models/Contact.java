/**
 * 
 */
package com.ttc.contactsgrid.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author OPTIMUS
 * 
 */
public class Contact implements Parcelable{

	// implement from Parcelable//////////////////

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(contactId);
		out.writeString(name);
		out.writeString(photoUri);
	}

	private void readFromParcel(Parcel in) {
		contactId = in.readString();
		name = in.readString();
		photoUri = in.readString();
	}

	// this is used to regenerate your object. All Parcelables must have a
	// CREATOR that implements these two methods
	public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
		public Contact createFromParcel(Parcel in) {
			return new Contact(in);
		}

		public Contact[] newArray(int size) {
			return new Contact[size];
		}
	};

	// example constructor that takes a Parcel and gives you an object populated
	// with it's values
	private Contact(Parcel in) {
		readFromParcel(in);
	}
	// endregion///////////////////////////////

	private String contactId;
	private String name;
	/**
	 * Uri of Photo
	 */
	private String photoUri;

	public Contact(String contactId, String name, String photoUri) {
		this.name = name;
		this.photoUri = photoUri;
		this.contactId = contactId;
	}

	public String getContactId() {
		return contactId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhotoUri() {
		return photoUri;
	}

	public void setPhotoUri(String photoUri) {
		this.photoUri = photoUri;
	}

}

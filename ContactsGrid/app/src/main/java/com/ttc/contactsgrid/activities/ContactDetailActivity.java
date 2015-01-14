/**
 * 
 */
package com.ttc.contactsgrid.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ttc.contactsgrid.R;
import com.ttc.contactsgrid.adapters.ListAdapterContactDetail;
import com.ttc.contactsgrid.models.ContactDetail;

/**
 * @author OPTIMUS
 * 
 */
public class ContactDetailActivity extends Activity {

	private ListView mListViewPhone;
	private ListView mListViewEmail;
	private List<ContactDetail> mListContactNumberDetails;
	private List<ContactDetail> mListContactEmailDetails;
	private ImageView mPhoto;
	private TextView mName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		
		// Declare variables
		mListViewPhone = (ListView) findViewById(R.id.listView_number_detail);
		mListViewEmail = (ListView) findViewById(R.id.listView_email_detail);
		mPhoto = (ImageView) findViewById(R.id.photoDetail);
		mName = (TextView) findViewById(R.id.nameDetail);
		mListContactNumberDetails = new ArrayList<ContactDetail>();
		mListContactEmailDetails = new ArrayList<ContactDetail>();

		// Get bundle contain Contact ID
		Bundle myBundle = getIntent().getExtras();
		String contactId = myBundle.getString("keyContactId");
		String name = myBundle.getString("keyName");
		String stringUriPhoto = myBundle.getString("keyUri");

		// Set Name contact
		mName.setText(name);
		// SetPhoto contact
		if (stringUriPhoto != null) {
			Uri uriPhoto = Uri.parse(stringUriPhoto);
			mPhoto.setImageURI(uriPhoto); 
		}

		queryAllPhoneNumbersForContact(contactId, mListContactNumberDetails);
		queryAllEmailAddressesForContact(contactId, mListContactEmailDetails);

		mListViewPhone.setAdapter(new ListAdapterContactDetail(this,
				mListContactNumberDetails));
		mListViewEmail.setAdapter(new ListAdapterContactDetail(this,
				mListContactEmailDetails));
	}

	/**
	 * Get all Phone number from contact ID and add into ListPhoneDetail
	 * 
	 * @param contactId
	 * @param listContactPhoneDetail
	 */
	public void queryAllPhoneNumbersForContact(String contactId,
			List<ContactDetail> listContactPhoneDetail) {
		final String[] projection = new String[] { Phone.NUMBER, Phone.TYPE };

		@SuppressWarnings("deprecation")
		final Cursor phone = managedQuery(Phone.CONTENT_URI, projection,
				Data.CONTACT_ID + "=?", new String[] { contactId }, null);

		if (phone.moveToFirst()) {
			final int contactNumberColumnIndex = phone
					.getColumnIndex(Phone.NUMBER);
			final int contactTypeColumnIndex = phone.getColumnIndex(Phone.TYPE);

			while (!phone.isAfterLast()) {
				final String number = phone.getString(contactNumberColumnIndex);
				final int type = phone.getInt(contactTypeColumnIndex);

				listContactPhoneDetail.add(new ContactDetail(number, Phone
						.getTypeLabelResource(type)));
				phone.moveToNext();
			}

		}
	}

	/**
	 * Get all Email Address from contact ID and add into ListEmailDetail
	 * 
	 * @param contactId
	 * @param listContactEmailDetail
	 */
	public void queryAllEmailAddressesForContact(String contactId,
			List<ContactDetail> listContactEmailDetail) {
		final String[] projection = new String[] { Email.DATA, Email.TYPE };

		@SuppressWarnings("deprecation")
		final Cursor email = managedQuery(Email.CONTENT_URI, projection,
				Data.CONTACT_ID + "=?", new String[] { contactId }, null);

		if (email.moveToFirst()) {
			final int contactEmailColumnIndex = email
					.getColumnIndex(Email.DATA);
			final int contactTypeColumnIndex = email.getColumnIndex(Email.TYPE);

			while (!email.isAfterLast()) {
				final String address = email.getString(contactEmailColumnIndex);
				final int type = email.getInt(contactTypeColumnIndex);
				listContactEmailDetail.add(new ContactDetail(address, Email
						.getTypeLabelResource(type)));
				email.moveToNext();
			}

		}
	}
}

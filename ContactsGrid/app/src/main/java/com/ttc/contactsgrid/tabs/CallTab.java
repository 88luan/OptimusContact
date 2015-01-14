/**
 * 
 */
package com.ttc.contactsgrid.tabs;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.ttc.contactsgrid.R;
import com.ttc.contactsgrid.adapters.SpinnerAdapter;
import com.ttc.contactsgrid.models.Contact;
import com.ttc.contactsgrid.models.ContactDetail;
import com.ttc.contactsgrid.utils.MyConstant;
import com.ttc.contactsgrid.utils.MyPreference;

/**
 * @author OPTIMUS
 * 
 */
public class CallTab extends SherlockFragment {
	// of ABS/////////////
	@Override
	public SherlockFragmentActivity getSherlockActivity() {
		return super.getSherlockActivity();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		setUserVisibleHint(true);
	}

	// endregion/////////

	private MyPreference myPreference;
	private Spinner mSpinner;
	private String mContactId;
	private ImageView mPhoto;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Get the view from call_tab.xml
		View view = inflater.inflate(R.layout.call_tab, container, false);
		Button btCall = (Button) view.findViewById(R.id.button_call);
		mSpinner = (Spinner) view.findViewById(R.id.spinner_phone_call);
		TextView name = (TextView) view.findViewById(R.id.tvNameCall);
		mPhoto = (ImageView) view.findViewById(R.id.imageViewPhoTo);

		mContactId = getContact().getContactId();
		name.setText(getContact().getName());
		setPhoto();

		myPreference = MyPreference.getInstance(getSherlockActivity());

		addItemsOnSpinner(mSpinner, mContactId);
		// get last Phone Number selected and set to spinner
		mSpinner.setSelection(getLastNumberSelection());

		btCall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				call(getPhoneNumberSelection(mSpinner));
			}
		});

		// handler event select item on Spinner
		mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				int mySave = mSpinner.getSelectedItemPosition();
				myPreference.putInt(MyConstant.CallId + mContactId, mySave);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		return view;
	}

	private void setPhoto() {
		String uriPhoto = getContact().getPhotoUri();

		if (uriPhoto == null) {
			mPhoto.setImageResource(R.drawable.person);
		} else {
			mPhoto.setImageURI(Uri.parse(uriPhoto));
		}
	}

	// get last phone number selected
	private int getLastNumberSelection() {
		int indexNumberNeedSelect = 0;
		indexNumberNeedSelect = myPreference.getInt(
				MyConstant.CallId + mContactId, 0);

		return indexNumberNeedSelect;
	}

	// get current phone number selected
	public String getPhoneNumberSelection(Spinner spinner) {
		ContactDetail contactDetail = (ContactDetail) spinner.getSelectedItem();
		return String.valueOf(contactDetail.getValue());
	}

	// add items into spinner dynamically
	public void addItemsOnSpinner(Spinner spinner, String contactId) {

		List<ContactDetail> listPhone = new ArrayList<ContactDetail>();
		if(contactId!=null){ 
			// this contact available in phone
			listPhone = queryAllPhoneNumbersForContact(contactId);
		} else {
			listPhone.add(new ContactDetail(getSherlockActivity().getIntent()
					.getStringExtra("number"), CommonDataKinds.Phone.TYPE_MOBILE));
		}
		
		SpinnerAdapter dataAdapter = new SpinnerAdapter(getSherlockActivity(),
				listPhone);
		spinner.setAdapter(dataAdapter);

	}

	/**
	 * Get all Phone from contact ID and return a List
	 * 
	 * @param contactId
	 */
	public List<ContactDetail> queryAllPhoneNumbersForContact(String contactId) {
		List<ContactDetail> listContactPhoneDetail = new ArrayList<ContactDetail>();
		final String[] projection = new String[] { Phone.NUMBER, Phone.TYPE };

		@SuppressWarnings("deprecation")
		final Cursor phone = getSherlockActivity().managedQuery(
				Phone.CONTENT_URI, projection, Data.CONTACT_ID + "=?",
				new String[] { contactId }, null);

		if (phone.moveToFirst()) {
			final int contactNumberColumnIndex = phone
					.getColumnIndex(Phone.NUMBER);
			final int contactTypeColumnIndex = phone.getColumnIndex(Phone.TYPE);

			while (!phone.isAfterLast()) {
				final String number = phone.getString(contactNumberColumnIndex)
						.replaceAll("\\s", "");// remove all white space in
												// String
				final int type = phone.getInt(contactTypeColumnIndex);

				listContactPhoneDetail.add(new ContactDetail(number, Phone
						.getTypeLabelResource(type)));
				phone.moveToNext();
			}

		}
		return listContactPhoneDetail;
	}
	
	/**
	 * Get all Email Address from contact ID and return a List
	 * 
	 * @param contactId
	 */
	public List<ContactDetail> queryAllEmailAddressesForContact(String contactId) {
		List<ContactDetail> listContactEmailDetail = new ArrayList<ContactDetail>();
		
		final String[] projection = new String[] { Email.DATA, Email.TYPE };

		final Cursor email = getSherlockActivity().managedQuery(Email.CONTENT_URI, projection,
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
		return listContactEmailDetail;
	}

	public Contact getContact() {
		Contact contact = getSherlockActivity().getIntent().getParcelableExtra(
				MyConstant.Contact);
		if (contact == null) {
			int fromNotification = getSherlockActivity().getIntent()
					.getIntExtra(MyConstant.FromNotif, 0);
			if (fromNotification == 1) {
				String number = getSherlockActivity().getIntent()
						.getStringExtra("number");
				try {
					contact = getContactByNumber(number);
				} catch (Exception e) {
				}
			}
		}
		return contact;
	}
	
	/**
	 * Get Contact ID, Name, Photo by Phone Number
	 * @param number
	 * @return Contact
	 */
	private Contact getContactByNumber(String number) {
		Contact contact = null;
		
	    Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
	    String[] selector = new String[] {
	    		ContactsContract.PhoneLookup._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME, 
                ContactsContract.PhoneLookup.PHOTO_URI };
	    
	    Cursor contactLookup = getSherlockActivity().getContentResolver().query(uri, selector, null, null, null);

	    int indexName = contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME);
	    String name = null;
	    int indexContactId = contactLookup.getColumnIndex(ContactsContract.Data._ID);
	    String id = null;
	    int indexPhoto = contactLookup.getColumnIndex(ContactsContract.Data.PHOTO_URI);
	    String photo = null;
	    try {
	        if (contactLookup != null && contactLookup.moveToNext()) {
	            name = contactLookup.getString(indexName);
	            id = contactLookup.getString(indexContactId);
	            photo = contactLookup.getString(indexPhoto);
	        }
	        contact = new Contact(id, name, photo);
	    } finally {
	        if (contactLookup != null) {
	            contactLookup.close();
	        }
	    }

	    return contact;
	}

	public void call(final String number) {
		
		new AlertDialog.Builder(getSherlockActivity())
	    .setMessage("Are you call to this number?")
	    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	        	
	        	String phoneCallUri = "tel:" + number;
	    		Intent phoneCallIntent = new Intent(Intent.ACTION_CALL,
	    				Uri.parse(phoneCallUri));
	    		startActivity(phoneCallIntent);
	    		
	        }
	     })
	    .setNegativeButton("No", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // do nothing
	        }
	     })
	     .show();
		
	}

	public String getEndOfPhoneNumber(String number) {

		String result = number;
		if (number.charAt(0) == '0') {
			StringBuilder builder1 = new StringBuilder(number);
			builder1.deleteCharAt(0);
			result = builder1.toString();
		}
		if (number.contains("+84")) {
			StringBuilder builder2 = new StringBuilder(number);
			builder2.delete(0, 3);
			result = builder2.toString();
		}

		return result;
	}

}

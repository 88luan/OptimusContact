/**
 * 
 */
package com.ttc.contactsgrid.tabs;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.ttc.contactsgrid.R;
import com.ttc.contactsgrid.adapters.SmsAdapter;
import com.ttc.contactsgrid.models.ContactDetail;
import com.ttc.contactsgrid.models.SMSModel;
import com.ttc.contactsgrid.utils.MyConstant;
import com.ttc.contactsgrid.utils.MyPreference;

/**
 * @author OPTIMUS
 * 
 */
public class SmsTab extends CallTab implements OnClickListener {

	public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	IntentFilter filter = new IntentFilter(SMS_RECEIVED);

	public static List<SMSModel> mListSMS = new ArrayList<SMSModel>();
	private ListView mListView;
	SmsAdapter mAdapter;
	Button btSend, btCall, btNew;
	EditText etMessage;
	Spinner mSpinner;
	String mPhoneNumber;
	String mEndOfPhoneNumber;
	MyPreference mMyPreference;
	private String mContactId;

	// #region of ABS
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

	// #endregion

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Get the view from sms_tab.xml
		View view = inflater.inflate(R.layout.sms_tab, container, false);

		mListView = (ListView) view.findViewById(R.id.listViewSMS);
		btSend = (Button) view.findViewById(R.id.button_sent);
		btCall = (Button) view.findViewById(R.id.button_call_sms);
		btNew = (Button) view.findViewById(R.id.button_new_sms);
		etMessage = (EditText) view.findViewById(R.id.editText_body);
		TextView tvName = (TextView) view.findViewById(R.id.tvNameSms);
		mSpinner = (Spinner) view.findViewById(R.id.spinner_phone_sms);

		mMyPreference = MyPreference.getInstance(getSherlockActivity());
		mContactId = getContact().getContactId();
		tvName.setText(getContact().getName());

		addItemsOnSpinner(mSpinner, mContactId);
		// get last Phone Number selected and set to spinner
		mSpinner.setSelection(getIndexLastNumberSelected());

		mPhoneNumber = getPhoneNumberSelection(mSpinner);
		mEndOfPhoneNumber = getEndOfPhoneNumber(mPhoneNumber);

		mListSMS = readSMS(mEndOfPhoneNumber);

		// Set smsList in the ListAdapter
		mAdapter = new SmsAdapter(getSherlockActivity(), mListSMS);

		mListView.setAdapter(mAdapter);

		btSend.setOnClickListener(this);
		btCall.setOnClickListener(this);
		btNew.setOnClickListener(this);

		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int positionSMS, long id) {

				showDialogSmsOption(positionSMS);

				return false;
			}
		});

		mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				int mySave = mSpinner.getSelectedItemPosition();
				mMyPreference.putInt(MyConstant.SMSId + mContactId, mySave);

				refreshListViewSMS();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		getSherlockActivity().registerReceiver(receiver_SMS, filter);
	}

	@Override
	public void onPause() {
		super.onPause();
		getSherlockActivity().unregisterReceiver(receiver_SMS);
	}

	private void refreshListViewSMS() {
		mPhoneNumber = getPhoneNumberSelection(mSpinner);
		mEndOfPhoneNumber = getEndOfPhoneNumber(mPhoneNumber);
		mListSMS.clear();
		mListSMS.addAll(readSMS(mEndOfPhoneNumber));
		mAdapter.notifyDataSetChanged();
	}

	private int getIndexLastNumberSelected() {
		int indexNumberNeedSelect = 0;

		String numberFromNofitication = null;
		numberFromNofitication = getSherlockActivity().getIntent()
				.getStringExtra("number");

		if (numberFromNofitication == null) {
			// get index from Shared Preference
			indexNumberNeedSelect = mMyPreference.getInt(MyConstant.SMSId
					+ mContactId, 0);
		} else if (numberFromNofitication != null && mContactId != null) {
			// this contact available in phone
			// get index from List Number
			List<ContactDetail> listDetails = new ArrayList<ContactDetail>();
			listDetails = queryAllPhoneNumbersForContact(mContactId);
			for (int i = 0; i < listDetails.size(); i++) {
				if (numberFromNofitication
						.equals(listDetails.get(i).getValue())) {
					indexNumberNeedSelect = i;
				}

			}
		} else if (numberFromNofitication != null && mContactId == null) {
			indexNumberNeedSelect = 0;
			mSpinner.setClickable(false);
		}

		return indexNumberNeedSelect;
	}

	private BroadcastReceiver receiver_SMS = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(SMS_RECEIVED)) {

				// get SMS received//////////////////////
				Bundle bundle = intent.getExtras();
				SmsMessage[] msgs = null;
				String strPhoneNo = "";
				String strBody = "";

				if (bundle != null) {
					// ---retrieve the SMS message received---
					Object[] pdus = (Object[]) bundle.get("pdus");
					msgs = new SmsMessage[pdus.length];
					for (int i = 0; i < msgs.length; i++) {
						msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
						strPhoneNo += msgs[i].getOriginatingAddress();

						strBody += msgs[i].getMessageBody().toString();
					}
				}
				// end region///////////////////
				
				if (strPhoneNo.endsWith(mEndOfPhoneNumber)) {
					refreshListViewSMS();
					mListSMS.add(new SMSModel(strPhoneNo, strBody, "1"));
					mAdapter.notifyDataSetChanged();
				}

			}
		}
	};
	
//	private SMSModel readNewSMS(String endOfPhoneNumber) {
//		SMSModel sms = new SMSModel();
//		
//		Uri uri = Uri.parse("content://sms/inbox");
//		Cursor cursor = getSherlockActivity().managedQuery(uri, null,
//				null, null, null);
//		cursor.moveToFirst();
//		
//		String cursorPhoneNo = cursor.getString(
//				cursor.getColumnIndexOrThrow("address")).toString();
//		if (cursorPhoneNo.endsWith(endOfPhoneNumber)) {
//			
//			sms.setNumber(cursor.getString(
//					cursor.getColumnIndexOrThrow("address")).toString());
//			sms.setId(cursor.getString(
//					cursor.getColumnIndexOrThrow("_id")).toString());
//			sms.setThread_id(cursor.getString(cursor
//					.getColumnIndexOrThrow("thread_id")));
//			sms.setBody(cursor.getString(
//					cursor.getColumnIndexOrThrow("body")).toString());
//			sms.setTime(cursor.getString(
//					cursor.getColumnIndexOrThrow("date")).toString());
//			sms.setType(cursor.getString(
//					cursor.getColumnIndexOrThrow("type")).toString());
//		}
//		return sms;
//	}

	private List<SMSModel> readSMS(String endOfPhoneNumber) {
		List<SMSModel> listSMS = new ArrayList<SMSModel>();

		Uri uri = Uri.parse("content://sms");

		String sortOrder = "date";
		Cursor cursor = getSherlockActivity().managedQuery(uri, null,
				null, null, sortOrder);

		// Read the sms data and store it in the list
		while (cursor.moveToNext()) {
			String cursorPhoneNo = cursor.getString(
					cursor.getColumnIndexOrThrow("address")).toString();
			if (cursorPhoneNo.endsWith(endOfPhoneNumber)) {
				SMSModel sms = new SMSModel();
				sms.setNumber(cursor.getString(
						cursor.getColumnIndexOrThrow("address")).toString());
				sms.setId(cursor.getString(
						cursor.getColumnIndexOrThrow("_id")).toString());
				sms.setThread_id(cursor.getString(cursor
						.getColumnIndexOrThrow("thread_id")));
				sms.setBody(cursor.getString(
						cursor.getColumnIndexOrThrow("body")).toString());
				sms.setTime(cursor.getString(
						cursor.getColumnIndexOrThrow("date")).toString());
				sms.setType(cursor.getString(
						cursor.getColumnIndexOrThrow("type")).toString());
				listSMS.add(sms);
			}
		}
		return listSMS;
	}

	public void sendSMS(String stringPhone, String stringMessage) {

		String SENT = "SMS_SENT";
		String DELIVERED = "SMS_DELIVERED";

		PendingIntent sentPI = PendingIntent.getBroadcast(
				getSherlockActivity(), 0, new Intent(SENT), 0);

		PendingIntent deliveredPI = PendingIntent.getBroadcast(
				getSherlockActivity(), 0, new Intent(DELIVERED), 0);

		// ---when the SMS has been sent---
		getSherlockActivity().registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(getSherlockActivity(), "SMS sent",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					Toast.makeText(getSherlockActivity(), "Generic failure",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					Toast.makeText(getSherlockActivity(), "No service",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					Toast.makeText(getSherlockActivity(), "Null PDU",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					Toast.makeText(getSherlockActivity(), "Radio off",
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}, new IntentFilter(SENT));

		// ---when the SMS has been delivered---
		getSherlockActivity().registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(getSherlockActivity(), "SMS delivered",
							Toast.LENGTH_SHORT).show();
					break;
				case Activity.RESULT_CANCELED:
					Toast.makeText(getSherlockActivity(), "SMS not delivered",
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}, new IntentFilter(DELIVERED));

		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(stringPhone, null, stringMessage, sentPI,
				deliveredPI);
	}

	public void sendLongSMS(String stringPhone, String stringMessage) {

		String SENT = "SMS_SENT";
		String DELIVERED = "SMS_DELIVERED";

		PendingIntent sentPI = PendingIntent.getBroadcast(
				getSherlockActivity(), 0, new Intent(SENT), 0);

		PendingIntent deliveredPI = PendingIntent.getBroadcast(
				getSherlockActivity(), 0, new Intent(DELIVERED), 0);

		// ---when the SMS has been sent---
		getSherlockActivity().registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(getSherlockActivity(), "SMS sent",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					Toast.makeText(getSherlockActivity(), "Generic failure",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					Toast.makeText(getSherlockActivity(), "No service",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					Toast.makeText(getSherlockActivity(), "Null PDU",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					Toast.makeText(getSherlockActivity(), "Radio off",
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}, new IntentFilter(SENT));

		// ---when the SMS has been delivered---
		getSherlockActivity().registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(getSherlockActivity(), "SMS delivered",
							Toast.LENGTH_SHORT).show();
					break;
				case Activity.RESULT_CANCELED:
					Toast.makeText(getSherlockActivity(), "SMS not delivered",
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}, new IntentFilter(DELIVERED));

		SmsManager smsManager = SmsManager.getDefault();
		ArrayList<String> parts = smsManager.divideMessage(stringMessage);

		ArrayList<PendingIntent> sentPIs = new ArrayList<PendingIntent>();
		ArrayList<PendingIntent> deliveredPIs = new ArrayList<PendingIntent>();
		for (int i = 0; i < parts.size(); i++) {
			sentPIs.add(sentPI);
			deliveredPIs.add(deliveredPI);
		}

		smsManager.sendMultipartTextMessage(stringPhone, null, parts, sentPIs,
				deliveredPIs);

	}

	public void saveInSent(String stringPhone, String stringMessage) {
		ContentValues values = new ContentValues();

		values.put("address", stringPhone);

		values.put("body", stringMessage);

		getSherlockActivity().getContentResolver().insert(
				Uri.parse("content://sms/sent"), values);

	}
	
	@SuppressLint("InlinedApi")
	public void showDialogSmsNew() {

		final Dialog myDialog = new Dialog(getSherlockActivity(),
				android.R.style.Theme_DeviceDefault_Dialog);
		myDialog.setContentView(R.layout.dialog_new_sms);
		myDialog.setTitle("New Message");
		myDialog.show();
		
		final EditText editTextNumber;
		final EditText editTextMessage;
		Button buttonSend;
		editTextNumber = (EditText) myDialog.findViewById(R.id.editText_phone_number_New);
		editTextMessage = (EditText) myDialog.findViewById(R.id.editText_message_new);
		buttonSend = (Button) myDialog.findViewById(R.id.button_send_new);
		
		buttonSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String number, message;
				number= editTextNumber.getText().toString();
				message= editTextMessage.getText().toString();
				
				if (number.trim().length() == 0) {
					Toast.makeText(getSherlockActivity(),
							"Please enter your phone number.", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				if (message.trim().length() == 0) {
					Toast.makeText(getSherlockActivity(),
							"Please enter your message.", Toast.LENGTH_SHORT)
							.show();
					return;
				}

				if (message.trim().length() > 160) {
					sendLongSMS(number, message);
					// Save in database
					saveInSent(number, message);

				} else {
					sendSMS(number, message);
					// Save in database
					saveInSent(number, message);
				}
				
				myDialog.dismiss();
			}
		});
		
	}

	@SuppressLint("InlinedApi")
	public void showDialogSmsOption(final int positionSMS) {

		final Dialog myDialog = new Dialog(getSherlockActivity(),
				android.R.style.Theme_DeviceDefault_Dialog);
		myDialog.setContentView(R.layout.dialog_listview);
		myDialog.setTitle("Message Options");
		myDialog.show();

		SMSModel smsInfo = mListSMS.get(positionSMS);
		final String smsId = smsInfo.getId();
		final String smsThread_Id = smsInfo.getThread_id();
		final String smsPhoneNo = smsInfo.getNumber();
		final String smsBody = smsInfo.getBody();

		final String[] options = { "Resend", "Delete", "Delete All" };
		ListView listView;
		listView = (ListView) myDialog.findViewById(R.id.listView);
		ArrayAdapter<String> adapter;
		adapter = new ArrayAdapter<String>(getSherlockActivity(),
				android.R.layout.simple_expandable_list_item_1, options);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int positionOption, long id) {
				myDialog.dismiss();
				switch (positionOption) {
				case 0: // Resend
					reSendSms(smsPhoneNo, smsBody);
					break;
				case 1: // Delete
					if (deleteSms(smsId)) {
						Toast.makeText(getSherlockActivity(), "Deleted",
								Toast.LENGTH_SHORT).show();
						mListSMS.remove(positionSMS);
						mAdapter.notifyDataSetChanged();
					}
					break;
				case 2: // Delete All
					if (deleteConversation(smsThread_Id)) {
						Toast.makeText(getSherlockActivity(), "Deleted All",
								Toast.LENGTH_SHORT).show();
						mListSMS.clear();
						mAdapter.notifyDataSetChanged();
					}
					break;
				default:
					break;
				}
			}
		});

	}

	public void reSendSms(String phoneNo, String body) {
		if (body.trim().length() > 160) {
			sendLongSMS(phoneNo, body);
			// Save in database
			saveInSent(phoneNo, body);

		} else {
			sendSMS(phoneNo, body);
			// Save in database
			saveInSent(phoneNo, body);
		}
		refreshListViewSMS();
	}

	public boolean deleteSms(String smsId) {
		boolean isSmsDeleted = false;
		try {
			getSherlockActivity().getContentResolver().delete(
					Uri.parse("content://sms/" + smsId), null, null);
			isSmsDeleted = true;

		} catch (Exception ex) {
			isSmsDeleted = false;
		}
		return isSmsDeleted;
	}

	public boolean deleteConversation(String smsThread_Id) {
		boolean isSmsDeleted = false;
		try {
			getSherlockActivity().getContentResolver().delete(
					Uri.parse("content://sms/conversations/" + smsThread_Id),
					null, null);
			isSmsDeleted = true;

		} catch (Exception ex) {
			isSmsDeleted = false;
		}
		return isSmsDeleted;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.button_sent:

			String stringPhone = mPhoneNumber;
			String stringMessage = etMessage.getText().toString();

			if (stringMessage.trim().length() == 0) {
				Toast.makeText(getSherlockActivity(),
						"Please enter your message.", Toast.LENGTH_SHORT)
						.show();
				return;
			}

			if (stringMessage.trim().length() > 160) {
				sendLongSMS(stringPhone, stringMessage);
				// Save in database
				saveInSent(stringPhone, stringMessage);

			} else {
				sendSMS(stringPhone, stringMessage);
				// Save in database
				saveInSent(stringPhone, stringMessage);
			}

			etMessage.setText("");

			refreshListViewSMS();

			// hide virtual keyboard
			InputMethodManager imm = (InputMethodManager) getSherlockActivity()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(etMessage.getWindowToken(), 0);

			break;
		case R.id.button_call_sms:
			call(mPhoneNumber);
			break;
		case R.id.button_new_sms:
			showDialogSmsNew();
			break;
		default:
			break;
		}
	}
}

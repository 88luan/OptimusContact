/**
 * 
 */
package com.ttc.contactsgrid.tabs;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.ttc.contactsgrid.R;
import com.ttc.contactsgrid.activities.ContactDetailActivity;
import com.ttc.contactsgrid.activities.FunctionActivity;
import com.ttc.contactsgrid.activities.SelectActivity;
import com.ttc.contactsgrid.adapters.ListAdapterContact;
import com.ttc.contactsgrid.models.Contact;
import com.ttc.contactsgrid.utils.MyConstant;

/**
 * @author OPTIMUS
 * 
 */
public class AllContactTab extends SherlockFragment implements
		OnItemLongClickListener, OnItemClickListener, OnClickListener {

	// Declare variables
	private GridView mGridView;
	private List<Contact> mListContacts = new ArrayList<Contact>();;
	private ListAdapterContact mAdapter;
	private Button mButtonAdd, mButtonAddStar, mButtonDelete;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Get the view from all_contact_tab.xml
		View view = inflater
				.inflate(R.layout.all_contact_tab, container, false);

		mGridView = (GridView) view.findViewById(R.id.gridview_allcontacts);
		mButtonAdd = (Button) view.findViewById(R.id.add_contact);
		mButtonAddStar = (Button) view.findViewById(R.id.add_star_list);
		mButtonDelete = (Button) view.findViewById(R.id.delete_ct_list);

		new Loading().execute();

		mGridView.setOnItemLongClickListener(this);
		mGridView.setOnItemClickListener(this);

		mButtonAdd.setOnClickListener(this);
		mButtonAddStar.setOnClickListener(this);
		mButtonDelete.setOnClickListener(this);

		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// Check which request it is that we're responding to
		if (requestCode == MyConstant.ADD_CONTACT_REQUEST) {
			if (resultCode == getSherlockActivity().RESULT_OK) {
				Toast.makeText(getSherlockActivity(), "Added Successfully",
						Toast.LENGTH_SHORT).show();

				Uri contactUri = data.getData();
				Cursor cursor = getSherlockActivity().getContentResolver()
						.query(contactUri, null, null, null, null);
				cursor.moveToFirst();

				int contactId = cursor
						.getColumnIndex(ContactsContract.Data._ID);
				int contactName = cursor
						.getColumnIndex(ContactsContract.Data.DISPLAY_NAME);
				int contactPhoto = cursor
						.getColumnIndex(ContactsContract.Data.PHOTO_URI);

				String strId = cursor.getString(contactId);
				String strName = cursor.getString(contactName);
				String strPhoto = cursor.getString(contactPhoto);
				mListContacts.add(0, new Contact(strId, strName, strPhoto));
				mAdapter.notifyDataSetChanged();

				// //region// faster way
				// mListContacts.clear();
				// mListContacts.addAll(getAllContacts());
				// mAdapter.notifyDataSetChanged();
				// //endregion

			} else if (resultCode == getSherlockActivity().RESULT_CANCELED) {
				Toast.makeText(getSherlockActivity(),
						"Contact Adding Canceled", Toast.LENGTH_SHORT).show();
			}
		}

		if (requestCode == MyConstant.EDIT_CONTACT_REQUEST) {
			if (resultCode == getSherlockActivity().RESULT_OK) {
				Toast.makeText(getSherlockActivity(), "Edited Successfully",
						Toast.LENGTH_SHORT).show();

				mListContacts.clear();
				mListContacts.addAll(getAllContacts());
				mAdapter.notifyDataSetChanged();
				
				// When edit contact, send a intent that content
				// provider changed
				Intent myIntent = new Intent(Intent.ACTION_PROVIDER_CHANGED);
				// myIntent.putExtra(MyConstant.Edit, contact);
				getSherlockActivity().sendBroadcast(myIntent);

			} else if (resultCode == getSherlockActivity().RESULT_CANCELED) {
				Toast.makeText(getSherlockActivity(),
						"Editing Canceled", Toast.LENGTH_SHORT).show();
			}
		}

	}

	@Override
	public void onResume() {
		super.onResume();

		// register Broadcast Receiver
		getSherlockActivity().registerReceiver(MyBroadcastReveiver,
				new IntentFilter(Intent.ACTION_PROVIDER_CHANGED));
	}

	/**
	 * Obtains all contact list.
	 * 
	 * @return the contact list contacts.
	 */
	private List<Contact> getAllContacts() {
		// region// Run query
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP
				+ " = '1'";
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
				+ " COLLATE LOCALIZED ASC";

		Cursor cursor = getSherlockActivity().managedQuery(uri, null,
				selection, null, sortOrder);
		// endregion

		// region// populate List
		List<Contact> listContacts = new ArrayList<Contact>();

		int contactId = cursor.getColumnIndex(ContactsContract.Data._ID);
		int contactName = cursor
				.getColumnIndex(ContactsContract.Data.DISPLAY_NAME);
		int contactPhoto = cursor
				.getColumnIndex(ContactsContract.Data.PHOTO_URI);

		while (cursor.moveToNext()) {

			String strId = cursor.getString(contactId);
			String strName = cursor.getString(contactName);
			String strPhoto = cursor.getString(contactPhoto);
			listContacts.add(new Contact(strId, strName, strPhoto));

		}
		// endregion

		return listContacts;
	}

	/**
	 * Access All Contacts and Set into GridView
	 * 
	 * @author OPTIMUS
	 * 
	 */
	private class Loading extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;

		@Override
		protected void onPreExecute() {
			// Create Progress Dialog
			progress = new ProgressDialog(getSherlockActivity());
			progress.setTitle(null);
			progress.setMessage("Loading...");
			progress.setCancelable(false);
			progress.setIndeterminate(true);
			progress.show();
		}

		@SuppressWarnings("deprecation")
		protected Void doInBackground(Void... arg0) {

			mListContacts.addAll(getAllContacts());

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progress.dismiss();
			mAdapter = new ListAdapterContact(getSherlockActivity(),
					mListContacts);
			mGridView.setAdapter(mAdapter);

		}
	}

	/**
	 * Show Dialog Option
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View v,
			final int position, long id) {

		// Create Dialog
		final AlertDialog dialogDetails;
		LayoutInflater inflater = LayoutInflater.from(getSherlockActivity());
		View dialogview = inflater.inflate(R.layout.dialog_option, null);
		AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(
				getSherlockActivity());
		dialogbuilder.setTitle("Option");
		dialogbuilder.setView(dialogview);
		dialogDetails = dialogbuilder.create();
		dialogDetails.show();

		// implement functions
		viewDetailContact(dialogDetails, mListContacts, position);
		addStarredContact(dialogDetails, position);
		deleteContact(dialogDetails, mListContacts, position, mAdapter);
		editContact(dialogDetails, mListContacts, position, mAdapter);

		return false;
	}

	/**
	 * Go to Activity Function
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		Contact contact = mListContacts.get(position);

		Intent intent = new Intent(getSherlockActivity(),
				FunctionActivity.class);
		intent.putExtra(MyConstant.Contact, contact);
		startActivity(intent);

		// Toast.makeText(getSherlockActivity(),
		// "." + contact.getContactId() + ".", Toast.LENGTH_LONG).show();
	}

	/**
	 * Go to Activity Contact Detail
	 * 
	 * @param dialogDetails
	 * @param position
	 */
	protected void viewDetailContact(final AlertDialog dialogDetails,
			final List<Contact> listContact, final int position) {
		Button btDetail = (Button) dialogDetails.findViewById(R.id.btDetail);
		btDetail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialogDetails.dismiss();
				Contact contact = listContact.get(position);
				String contactId = contact.getContactId();
				String name = contact.getName();
				String uriPhoto = contact.getPhotoUri();

				// put Contact name, ID, Uriphoto into Bundle
				Bundle myBundle = new Bundle();
				myBundle.putString("keyContactId", contactId);
				myBundle.putString("keyName", name);
				if (uriPhoto != null) {
					myBundle.putString("keyUri", uriPhoto);
				}

				// Go to Contact Details
				Intent myIntent = new Intent(getSherlockActivity(),
						ContactDetailActivity.class);
				myIntent.putExtras(myBundle);
				startActivity(myIntent);
			}
		});
	}

	/**
	 * Mark a contact as favorite(Starred)
	 * 
	 * @param dialogDetails
	 * @param position
	 */
	private void addStarredContact(final AlertDialog dialogDetails,
			final int position) {
		Button btMarkStarred = (Button) dialogDetails
				.findViewById(R.id.btAddFavorite);
		btMarkStarred.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogDetails.dismiss();
				Contact contact = mListContacts.get(position);
				String id = contact.getContactId();

				ContentValues values = new ContentValues();
				String where = Contacts._ID + "= ?";
				String[] value = new String[] { id };
				// starred is 1,non-starred is 0
				values.put(Contacts.STARRED, 1); 
				// implement query
				getSherlockActivity().getContentResolver().update(
						Contacts.CONTENT_URI, values,
						where, value);

				// When add contact to favorite, send a intent that content
				// provider changed
				Intent myIntent = new Intent(Intent.ACTION_PROVIDER_CHANGED);
				// myIntent.putExtra(MyConstant.AddFavorite, contact);
				getSherlockActivity().sendBroadcast(myIntent);
			}
		});
	}

	protected void addContact() {
		Intent i = new Intent(Intent.ACTION_INSERT);
		i.setType(Contacts.CONTENT_TYPE);
		if (Integer.valueOf(Build.VERSION.SDK) > 14) // Fix for 4.0.3++
			i.putExtra("finishActivityOnSaveCompleted", true);
		startActivityForResult(i, MyConstant.ADD_CONTACT_REQUEST);
	}

	protected void editContact(final AlertDialog dialogDetails,
			final List<Contact> listContacts, final int position,
			final ListAdapterContact adapter) {
		Button btEdit = (Button) dialogDetails.findViewById(R.id.btEdit);
		btEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogDetails.dismiss();
				
				Contact contact = listContacts.get(position);

				Intent editIntent = new Intent(Intent.ACTION_EDIT);
				editIntent.setData(Uri
						.parse(ContactsContract.Contacts.CONTENT_URI + "/"
								+ contact.getContactId()));

				editIntent.putExtra("finishActivityOnSaveCompleted", true);
				startActivityForResult(editIntent,
						MyConstant.EDIT_CONTACT_REQUEST);
				
			}
		});
	}

	protected void deleteContact(final AlertDialog dialogDetails,
			final List<Contact> listContacts, final int position,
			final ListAdapterContact adapter) {
		Button btdelete = (Button) dialogDetails.findViewById(R.id.btDelete);
		btdelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogDetails.dismiss();
				Contact contact = listContacts.get(position);
				String id = contact.getContactId();

				ContentResolver cr = getSherlockActivity().getContentResolver();
				String where = ContactsContract.Data._ID + "=?";
				String[] value = new String[] { id };

				ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
				ops.add(ContentProviderOperation
						.newDelete(ContactsContract.RawContacts.CONTENT_URI)
						.withSelection(where, value).build());
				try {
					cr.applyBatch(ContactsContract.AUTHORITY, ops);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (OperationApplicationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// When add contact to favorite, send a intent that content
				// provider changed
				Intent myIntent = new Intent(Intent.ACTION_PROVIDER_CHANGED);
				// myIntent.putExtra(MyConstant.Deleted, contact);
				getSherlockActivity().sendBroadcast(myIntent);

				listContacts.remove(position);
				// update View
				adapter.notifyDataSetChanged();

			}
		});
	}

	/**
	 * Create BroadcastReceiver in order to catch event when has
	 * PROVIDER_CHANGED
	 */
	private BroadcastReceiver MyBroadcastReveiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			// catch event when delete a contact
			// try {
			// Contact ct = (Contact) intent
			// .getParcelableExtra(MyConstant.Deleted);
			// String id = ct.getContactId();
			// for (int i = 0; i < mListContacts.size(); i++) {
			// if (mListContacts.get(i).getContactId().equals(id)) {
			// Toast.makeText(context, ct.getName() + " is Deleted",
			// Toast.LENGTH_SHORT).show();
			// mListContacts.remove(i);
			// mAdapter.notifyDataSetChanged();
			// }
			// }
			// } catch (Exception e) {
			// // TODO: handle exception
			// }
			try {
				mListContacts.clear();
				mListContacts.addAll(getAllContacts());
				mAdapter.notifyDataSetChanged();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

	};

	// /**
	// * Get Contact ID (String) from Phone number
	// *
	// * @param phoneNumber
	// * @return String
	// */
	// public String fetchContactIdFromPhoneNumber(String phoneNumber) {
	// // TODO Auto-generated method stub
	//
	// Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
	// Uri.encode(phoneNumber));
	// Cursor cFetch = getSherlockActivity().getContentResolver().query(uri,
	// new String[] { PhoneLookup.DISPLAY_NAME, PhoneLookup._ID },
	// null, null, null);
	//
	// String contactId = "";
	//
	// if (cFetch.moveToFirst()) {
	//
	// cFetch.moveToFirst();
	//
	// contactId = cFetch
	// .getString(cFetch.getColumnIndex(PhoneLookup._ID));
	//
	// }
	//
	// return contactId;
	//
	// }
	//
	// /**
	// * Get Uri of Photo Person
	// *
	// * @param contactId
	// * @return Uri of Photo Person
	// */
	// @SuppressLint("InlinedApi")
	// public Uri getPhotoUri(long contactId) {
	// ContentResolver contentResolver = getSherlockActivity()
	// .getContentResolver();
	//
	// try {
	// Cursor cursor = contentResolver
	// .query(ContactsContract.Data.CONTENT_URI,
	// null,
	// ContactsContract.Data.CONTACT_ID
	// + "="
	// + contactId
	// + " AND "
	//
	// + ContactsContract.Data.MIMETYPE
	// + "='"
	// + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE
	// + "'", null, null);
	//
	// if (cursor != null) {
	// if (!cursor.moveToFirst()) {
	// return null; // no photo
	// }
	// } else {
	// return null; // error in cursor process
	// }
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// return null;
	// }
	//
	// Uri person = ContentUris.withAppendedId(
	// ContactsContract.Contacts.CONTENT_URI, contactId);
	// return Uri.withAppendedPath(person,
	// ContactsContract.Contacts.Photo.DISPLAY_PHOTO); // full-size
	// }

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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.add_contact:
			addContact();
			
			break;
		case R.id.add_star_list:
			Intent intent1 = new Intent(getSherlockActivity(), SelectActivity.class);
			intent1.putParcelableArrayListExtra(MyConstant.ListContactFromGridView, (ArrayList<? extends Parcelable>) mListContacts);
			intent1.putExtra(MyConstant.AllTabAddStar, R.id.add_star_list);
			startActivity(intent1);
			
			break;
		case R.id.delete_ct_list:
			Intent intent2 = new Intent(getSherlockActivity(), SelectActivity.class);
			intent2.putParcelableArrayListExtra(MyConstant.ListContactFromGridView, (ArrayList<? extends Parcelable>) mListContacts);
			intent2.putExtra(MyConstant.AllTabAddStar, R.id.delete_ct_list);
			startActivity(intent2);
			
			break;
		default:
			break;
		}
	}

}

package com.ttc.contactsgrid.tabs;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.ttc.contactsgrid.R;
import com.ttc.contactsgrid.activities.FunctionActivity;
import com.ttc.contactsgrid.activities.SelectActivity;
import com.ttc.contactsgrid.adapters.ListAdapterContact;
import com.ttc.contactsgrid.models.Contact;
import com.ttc.contactsgrid.utils.MyConstant;

public class FavoriteContactTab extends AllContactTab {

	private GridView mGridView;
	private List<Contact> mListContacts = new ArrayList<Contact>();
	private ListAdapterContact mAdapter;
	private Button mButtonRemoveStar, mButtonDelete;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Get the view from favorite_contact_tab.xml
		View view = inflater.inflate(R.layout.favorite_contact_tab, container,
				false);

		mGridView = (GridView) view
				.findViewById(R.id.gridview_favoritecontacts);
		mButtonRemoveStar = (Button) view.findViewById(R.id.remove_star_list);
		mButtonDelete = (Button) view
				.findViewById(R.id.delete_contact_list_bar_star);

		new Loading().execute();

		mGridView.setOnItemLongClickListener(this);
		mGridView.setOnItemClickListener(this);

		mButtonRemoveStar.setOnClickListener(this);
		mButtonDelete.setOnClickListener(this);

		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getSherlockActivity().registerReceiver(MyBroadcastReceiver,
				new IntentFilter(Intent.ACTION_PROVIDER_CHANGED));
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Check which request it is that we're responding to

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

	/**
	 * Obtains the favorite contact list for the currently selected account.
	 * 
	 * @return A cursor for for accessing the contact list.
	 */
	private List<Contact> getAllContacts() {
		// region// Run query
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		String selection = "starred=?";
		String[] selectionArgs = new String[] { "1" };
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
				+ " COLLATE LOCALIZED ASC";

		Cursor cursor = getSherlockActivity().managedQuery(uri, null,
				selection, selectionArgs, sortOrder);
		// endregion

		// region//populate to List
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
	 * Access Favorite Contacts and Set into GridView
	 * 
	 * @author OPTIMUS
	 * 
	 */
	private class Loading extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress;

		@Override
		protected void onPreExecute() {
			progress = new ProgressDialog(getSherlockActivity());
			progress.setTitle(null);
			progress.setMessage("Loading...");
			progress.setCancelable(false);
			progress.setIndeterminate(true);
			progress.show();
		}

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
	 * Create BroadcastReceiver in order to catch event when has
	 * PROVIDER_CHANGED
	 */
	private BroadcastReceiver MyBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			// catch event when delete a contact
			// try {
			// Contact ctD = (Contact) intent
			// .getParcelableExtra(MyConstant.Deleted);
			// String id = ctD.getContactId();
			// for (int i = 0; i < mListContacts.size(); i++) {
			// if (mListContacts.get(i).getContactId().equals(id)) {
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

			// catch event when add a contact as Favorite
			// try {
			// Contact ctF = (Contact) intent
			// .getParcelableExtra(MyConstant.AddFavorite);
			// String id = ctF.getContactId();
			//
			// if (id != null) {
			// boolean flag = false;
			// for (int i = 0; i < mListContacts.size(); i++) {
			// if (mListContacts.get(i).getContactId().equals(id)) {
			// flag = true;
			// }
			// }
			// if (flag == false) {
			// Toast.makeText(context,
			// ctF.getName() + " is Added to Favorite",
			// Toast.LENGTH_SHORT).show();
			// mListContacts.add(ctF);
			// mAdapter.notifyDataSetChanged();
			// }
			// }
			// } catch (Exception e) {
			// // TODO: handle exception
			// }

		}
	};

	/**
	 * Remove form List of Favorite Contact
	 * 
	 * @param dialogDetails
	 * @param position
	 */
	private void removeStarredContact(final AlertDialog dialogDetails,
			final int position) {
		Button btMarkStarred = (Button) dialogDetails
				.findViewById(R.id.btremoveFavorite);
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
				values.put(Contacts.STARRED, 0);

				getSherlockActivity().getContentResolver().update(
						Contacts.CONTENT_URI, values,
						where, value);

				mListContacts.remove(position);
				// update View
				mAdapter.notifyDataSetChanged();
			}
		});
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
		View dialogview = inflater.inflate(R.layout.favorite_dialog_option,
				null);
		AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(
				getSherlockActivity());
		dialogbuilder.setTitle("Option");
		dialogbuilder.setView(dialogview);
		dialogDetails = dialogbuilder.create();
		dialogDetails.show();

		// implement functions
		removeStarredContact(dialogDetails, position);
		deleteContact(dialogDetails, mListContacts, position, mAdapter);
		editContact(dialogDetails, mListContacts, position, mAdapter);
		viewDetailContact(dialogDetails, mListContacts, position);
		
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

		// Toast.makeText(getSherlockActivity(), contact.getContactId(),
		// Toast.LENGTH_LONG).show();

	}

	// @SuppressLint("InlinedApi")
	// void dialogPickNumber(String[] strings, String name) {
	// // Create Dialog
	// final Dialog myDialog = new Dialog(getSherlockActivity(),
	// android.R.style.Theme_DeviceDefault_Dialog);
	// myDialog.setContentView(R.layout.dialog_listview);
	// myDialog.setTitle("Select Phone Number");
	// myDialog.show();
	//
	// final String[] arrayPhoneNumber = strings;
	// final String name_ = name;
	// ListView mListViewMain;
	// mListViewMain = (ListView) myDialog.findViewById(R.id.listView);
	// ArrayAdapter<String> adapterMain;
	// adapterMain = new ArrayAdapter<String>(getSherlockActivity(),
	// android.R.layout.simple_expandable_list_item_1,
	// arrayPhoneNumber);
	//
	// mListViewMain.setAdapter(adapterMain);
	// mListViewMain.setOnItemClickListener(new OnItemClickListener() {
	//
	// @Override
	// public void onItemClick(AdapterView<?> parent, View view,
	// int position, long id) {
	// myDialog.dismiss();
	// String number = arrayPhoneNumber[position];
	//
	// Intent intent1 = new Intent(getSherlockActivity(),
	// FunctionActivity.class);
	// intent1.putExtra("phone", number);
	// intent1.putExtra("name", name_);
	// startActivity(intent1);
	// }
	// });
	//
	// }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.remove_star_list:
			Intent intent1 = new Intent(getSherlockActivity(),
					SelectActivity.class);
			intent1.putParcelableArrayListExtra(
					MyConstant.ListContactFromGridView,
					(ArrayList<? extends Parcelable>) mListContacts);
			intent1.putExtra(MyConstant.AllTabAddStar, R.id.remove_star_list);
			startActivity(intent1);

			break;
		case R.id.delete_contact_list_bar_star:
			Intent intent2 = new Intent(getSherlockActivity(),
					SelectActivity.class);
			intent2.putParcelableArrayListExtra(
					MyConstant.ListContactFromGridView,
					(ArrayList<? extends Parcelable>) mListContacts);
			intent2.putExtra(MyConstant.AllTabAddStar, R.id.delete_contact_list_bar_star);
			startActivity(intent2);

			break;
		default:
			break;
		}
	}

}
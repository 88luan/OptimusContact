package com.ttc.contactsgrid.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ttc.contactsgrid.R;
import com.ttc.contactsgrid.models.Contact;
import com.ttc.contactsgrid.utils.MyConstant;

public class SelectActivity extends Activity {

	private boolean listSelected[];
	private List<Contact> mListContact = new ArrayList<Contact>();
	private GridView mGridView;
	private MyAdapter myAdapter;
	private Button mButtonSelectAll, mButtonClear, mButtonAccept;
	int whichButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_muti_select);
		
		whichButton = getIntent().getIntExtra(MyConstant.AllTabAddStar, 0);
		mGridView = (GridView) findViewById(R.id.gridview_selectcontacts);
		setColumnGridView();
		mButtonSelectAll = (Button) findViewById(R.id.select_all);
		mButtonClear = (Button) findViewById(R.id.clear);
		mButtonAccept = (Button) findViewById(R.id.accept);
		
		mListContact.addAll(getList());
		listSelected = new boolean[mListContact.size()];
		myAdapter = new MyAdapter(getApplicationContext(), mListContact);
		mGridView.setAdapter(myAdapter);

		mButtonSelectAll.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				final int len = listSelected.length;
				int cnt = 0;
				for (int i = 0; i < len; i++) {
					listSelected[i] = true;
					if (listSelected[i]) {
						cnt++;
					}
				}
				Toast.makeText(getApplicationContext(),
						"You've selected " + cnt + " contacts",
						Toast.LENGTH_LONG).show();
				myAdapter.notifyDataSetChanged();
			}
		});

		mButtonClear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final int len = listSelected.length;
				for (int i = 0; i < len; i++) {
					listSelected[i] = false;
				}

				myAdapter.notifyDataSetChanged();
			}
		});

		mButtonAccept.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				switch (whichButton) {
				case R.id.add_star_list:
					addStarredMutiContact();
					break;
				case R.id.remove_star_list:
					removeStarredMutiContact();
					break;
				case R.id.delete_ct_list:
					deleteMutiContact();
					break;
				case R.id.delete_contact_list_bar_star:
					deleteMutiContact();
					break;
					
				default:
					break;
				}

				finish();
			}
		});
	}

	List<Contact> getList() {
		return getIntent().getParcelableArrayListExtra(
				MyConstant.ListContactFromGridView);
	}
	
	private void setColumnGridView(){
		if (whichButton == R.id.remove_star_list || whichButton == R.id.delete_contact_list_bar_star) {
			mGridView.setNumColumns(2);
		}
	}

	private void addStarredMutiContact() {
		for (int j = 0; j < listSelected.length; j++) {
			if (listSelected[j] == true) {
				Contact contact = mListContact.get(j);
				String id = contact.getContactId();

				ContentValues values = new ContentValues();
				String where = Contacts._ID + "= ?";
				String[] value = new String[] { id };
				// starred is 1,non-starred is 0
				values.put(Contacts.STARRED, 1); 
				// implement query
				getContentResolver().update(Contacts.CONTENT_URI, values,
						where, value);
			}
		}

		// When add contact to favorite, send a intent that content
		// provider changed
		Intent myIntent = new Intent(Intent.ACTION_PROVIDER_CHANGED);
		sendBroadcast(myIntent);
	}

	private void removeStarredMutiContact() {
		for (int j = 0; j < listSelected.length; j++) {
			if (listSelected[j] == true) {
				Contact contact = mListContact.get(j);
				String id = contact.getContactId();

				ContentValues values = new ContentValues();
				String where = Contacts._ID + "= ?";
				String[] value = new String[] { id };
				// starred is 1,non-starred is 0
				values.put(Contacts.STARRED, 0); 
				// query
				getContentResolver().update(
						Contacts.CONTENT_URI, values,
						where, value);

			}
		}

		// When remove star contact, send a intent that content
		// provider changed
		Intent myIntent = new Intent(Intent.ACTION_PROVIDER_CHANGED);
		sendBroadcast(myIntent);
	}

	private void deleteMutiContact() {
		for (int j = 0; j < listSelected.length; j++) {
			if (listSelected[j] == true) {
				Contact contact = mListContact.get(j);
				String id = contact.getContactId();

				ContentResolver cr = getContentResolver();
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
			}
		}
		// When delete contact, send a intent that content
		// provider changed
		Intent myIntent = new Intent(Intent.ACTION_PROVIDER_CHANGED);
		sendBroadcast(myIntent);

	}

	private class MyAdapter extends ArrayAdapter<Contact> {
		private List<Contact> mListContact = new ArrayList<Contact>();
		private Context mContext;

		public MyAdapter(Context context, List<Contact> listcontact) {
			super(context, R.layout.grid_item_select, listcontact);
			// TODO Auto-generated constructor stub
			mContext = context;
			mListContact = listcontact;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;

			if (convertView == null) {

				LayoutInflater inflater = LayoutInflater.from(mContext);
				convertView = inflater.inflate(R.layout.grid_item_select,
						parent, false);

				holder = new ViewHolder();
				holder.photo = (ImageView) convertView
						.findViewById(R.id.picture_select);
				holder.checkbox = (CheckBox) convertView
						.findViewById(R.id.checkBox);
				holder.name = (TextView) convertView
						.findViewById(R.id.name_select);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Contact contact = (Contact) mListContact.get(position);
			holder.checkbox.setId(position);
			holder.checkbox.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					// TODO Auto-generated method stub
					CheckBox cb = (CheckBox) v;
					int id = cb.getId();
					if (listSelected[id]) {
						cb.setChecked(false);
						listSelected[id] = false;
					} else {
						cb.setChecked(true);
						listSelected[id] = true;
					}
				}
			});

			holder.name.setText(contact.getName());
			String uriPhoto = contact.getPhotoUri();

			if (uriPhoto == null) {
				holder.photo.setImageResource(R.drawable.person);
			} else {
				holder.photo.setImageURI(Uri.parse(uriPhoto));
			}
			holder.checkbox.setChecked(listSelected[position]);
			holder.id = position;

			return convertView;
		}
	}

	class ViewHolder {
		ImageView photo;
		TextView name;
		CheckBox checkbox;
		int id;
	}

}

/**
 * 
 */
package com.ttc.contactsgrid.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ttc.contactsgrid.R;
import com.ttc.contactsgrid.models.Contact;

/**
 * @author OPTIMUS
 * 
 */
public class ListAdapterContact extends ArrayAdapter<Contact> {

	private Context mContext;
	private List<Contact> mListContact = new ArrayList<Contact>();

	public ListAdapterContact(Context context, List<Contact> listcontact) {
		super(context, R.layout.grid_item, listcontact);
		// TODO Auto-generated constructor stub
		mContext = context;
		mListContact = listcontact;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View rowView, ViewGroup parent) {
		ContactViewHolder contactHolder = null;

		if (rowView == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			rowView = inflater.inflate(R.layout.grid_item, parent, false);

			contactHolder = new ContactViewHolder();
			contactHolder.name = (TextView) rowView.findViewById(R.id.name);
			contactHolder.photo = (ImageView) rowView
					.findViewById(R.id.picture);

			rowView.setTag(contactHolder);
		}

		contactHolder = (ContactViewHolder) rowView.getTag();

		Contact contact = (Contact) mListContact.get(position);
		contactHolder.name.setText(contact.getName());
		String uriPhoto = contact.getPhotoUri();
		
		if (uriPhoto==null) {
			contactHolder.photo.setImageResource(R.drawable.icon_person);
		} else {
			contactHolder.photo.setImageURI(Uri.parse(uriPhoto));
		}

		return rowView;
	}

	protected class ContactViewHolder {
		protected TextView name;
		protected ImageView photo;
	}

}

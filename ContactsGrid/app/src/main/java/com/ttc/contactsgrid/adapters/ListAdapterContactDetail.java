package com.ttc.contactsgrid.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ttc.contactsgrid.R;
import com.ttc.contactsgrid.models.ContactDetail;

public class ListAdapterContactDetail extends ArrayAdapter<ContactDetail> {

	private List<ContactDetail> mListContact = new ArrayList<ContactDetail>();
	private Context mContext;

	public ListAdapterContactDetail(Context context, List<ContactDetail> list) {
		super(context, R.layout.contact_details_item, list);
		this.mContext = context;
		this.mListContact = list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getRowView(position, convertView, parent);
	}
	
	public View getRowView(int position, View convertView, ViewGroup parent) {

		ContactDetail contactDetail = mListContact.get(position);
		ViewHolder holder;
		
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(R.layout.contact_details_item, null);

			holder = new ViewHolder();
			holder.type = (TextView) convertView
					.findViewById(R.id.type);
			holder.value = (TextView) convertView.findViewById(R.id.value);

			convertView.setTag(holder);
		}

		holder = (ViewHolder) convertView.getTag();

		holder.value.setText(contactDetail.getValue());
		holder.type.setText(contactDetail.getType());
		
		return convertView;
		
	}
	
	class ViewHolder {
		TextView value;
		TextView type;
	}
}

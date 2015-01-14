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

public class SpinnerAdapter extends ListAdapterContactDetail {

	private List<ContactDetail> mListContact = new ArrayList<ContactDetail>();
	private Context mContext;

	public SpinnerAdapter(Context context, List<ContactDetail> list) {
		super(context, list);
		this.mContext = context;
		this.mListContact = list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ContactDetail contactDetail = mListContact.get(position);
		ViewHolder holder;
		
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(R.layout.spinner_phone, null);

			holder = new ViewHolder();

			holder.value = (TextView) convertView.findViewById(R.id.value_spinner);

			convertView.setTag(holder);
		}

		holder = (ViewHolder) convertView.getTag();

		holder.value.setText(contactDetail.getValue());
		
		return convertView;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getRowView(position, convertView, parent);
	}
	
}

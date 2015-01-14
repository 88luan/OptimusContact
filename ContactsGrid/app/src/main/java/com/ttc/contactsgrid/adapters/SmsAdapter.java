package com.ttc.contactsgrid.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ttc.contactsgrid.R;
import com.ttc.contactsgrid.models.SMSModel;

/**
 * List adapter for storing SMS data
 * 
 * @author itcuties
 * 
 */
public class SmsAdapter extends ArrayAdapter<SMSModel> {

	// List context
	private Context context;
	// List values
	private List<SMSModel> smsList = new ArrayList<SMSModel>();

	public SmsAdapter(Context context, List<SMSModel> smsList) {
		super(context, R.layout.sms_item, smsList);
		this.context = context;
		this.smsList = smsList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SMSModel smsInfo = (SMSModel) smsList.get(position);
		
		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.sms_item, null);

			holder = new ViewHolder();
			holder.time = (TextView) convertView
					.findViewById(R.id.smsNumberText);
			holder.body = (TextView) convertView.findViewById(R.id.smsBodyText);
			holder.layout = (LinearLayout) convertView
					.findViewById(R.id.layout);

			convertView.setTag(holder);
		}

		holder = (ViewHolder) convertView.getTag();

		holder.layout.setGravity(smsInfo.getType().equals("1") ? Gravity.LEFT
				: Gravity.RIGHT);
		holder.time.setText(smsInfo.getTime());
		holder.body
				.setBackgroundResource(smsInfo.getType().equals("1") ? R.drawable.receive2
						: R.drawable.sent2);
		holder.body.setText(smsInfo.getBody());
		
		return convertView;
	}
	
	class ViewHolder {
		TextView time;
		TextView body;
		LinearLayout layout;
	}
}

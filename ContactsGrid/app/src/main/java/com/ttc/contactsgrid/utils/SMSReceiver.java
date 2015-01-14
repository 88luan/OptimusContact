package com.ttc.contactsgrid.utils;

import com.ttc.contactsgrid.R;
import com.ttc.contactsgrid.activities.FunctionActivity;
import com.ttc.contactsgrid.activities.ListContactsActivity;
import com.ttc.contactsgrid.models.SMSModel;
import com.ttc.contactsgrid.tabs.AllContactTab;
import com.ttc.contactsgrid.tabs.FavoriteContactTab;
import com.ttc.contactsgrid.tabs.SmsTab;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent.getAction().equals(SmsTab.SMS_RECEIVED)) {
			
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
		
			String number = standardizedNumber(strPhoneNo);
			String name = getDisplayNameByNumber(context, number);
			if (name==null) {
				name=number;
			}
			
			NotificationManager notifManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            Notification notif = new Notification(
            		R.drawable.notification, //icon
            		name, //text popup
                    System.currentTimeMillis());
            notif.defaults |= Notification.DEFAULT_SOUND;
            notif.defaults |= Notification.DEFAULT_VIBRATE;
            notif.defaults |= Notification.DEFAULT_LIGHTS;
            
            // The notification will be canceled when clicked by the user...
            notif.flags |= Notification.FLAG_AUTO_CANCEL;
            
            // ...but we still need to provide and intent; an empty one will
            // suffice. Alter for your own app's requirement.
            
            Intent notificationIntent = new Intent(context, FunctionActivity.class);
            notificationIntent.putExtra(MyConstant.FromNotif, 1);
            notificationIntent.putExtra("number", number);
            
            PendingIntent pi = PendingIntent.getActivity(context, 0,
                    notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
            notif.setLatestEventInfo(context, 
            		name, //title
            		strBody, //body
            		pi);
            
            notifManager.notify(0, notif);
		}
	}
	
	private String getDisplayNameByNumber(Context context, String number) {
	    Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
	   
	    Cursor contactLookup = context.getContentResolver().query(uri, new String[] {ContactsContract.PhoneLookup._ID,
	                                            ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);

	    int indexName = contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME);
	    String name = null;
	    try {
	        if (contactLookup != null && contactLookup.moveToNext()) {
	            name = contactLookup.getString(indexName);
	        }
	    } finally {
	        if (contactLookup != null) {
	            contactLookup.close();
	        }
	    }

	    return name;
	}
	
	private String standardizedNumber(String number){

		String result = number;
		if (number.contains("+84")) {
			StringBuilder builder = new StringBuilder(number);
			builder.delete(0, 3);
			builder.insert(0, '0');
			result = builder.toString();
		}

		return result;
	}

}

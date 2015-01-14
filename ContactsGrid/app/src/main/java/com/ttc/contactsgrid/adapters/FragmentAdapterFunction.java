/**
 * 
 */
package com.ttc.contactsgrid.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ttc.contactsgrid.tabs.CallTab;
import com.ttc.contactsgrid.tabs.SmsTab;

/**
 * @author OPTIMUS
 *
 */
public class FragmentAdapterFunction extends FragmentPagerAdapter {
	
	// Declare the number of ViewPager pages
		final int PAGE_COUNT = 2;

	public FragmentAdapterFunction(FragmentManager fm) {
		super(fm); 
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
	 */
	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub
		switch (position) {

		// Open FragmentTab1.java
		case 0:
			CallTab callTab = new CallTab();
			return callTab;

			// Open FragmentTab2.java
		case 1:
			SmsTab smsTab = new SmsTab();
			return smsTab;

		}
		return null;
	}


	/* (non-Javadoc)
	 * @see android.support.v4.view.PagerAdapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return PAGE_COUNT;
	}

}

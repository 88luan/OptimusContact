/**
 * 
 */
package com.ttc.contactsgrid.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.Window;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.ttc.contactsgrid.R;
import com.ttc.contactsgrid.adapters.FragmentAdapterListContacts;

/**
 * @author OPTIMUS
 *
 */
public class ListContactsActivity extends SherlockFragmentActivity {
	    
		// Declare Variables
		ActionBar mActionBar;
		ViewPager mPager;
		Tab tab;

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			// Get the view from activity_main.xml
			setContentView(R.layout.activity_listcontacts);

			// Set No Title Bar
			getSupportActionBar().setDisplayOptions(Window.FEATURE_NO_TITLE);
			
			// Activate Navigation Mode Tabs
			mActionBar = getSupportActionBar();
			mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
			// Locate ViewPager in activity_main.xml
			mPager = (ViewPager) findViewById(R.id.listcontacts);

			// Activate Fragment Manager
			FragmentManager fm = getSupportFragmentManager();

			// Capture ViewPager page swipes
			ViewPager.SimpleOnPageChangeListener ViewPagerListener = new ViewPager.SimpleOnPageChangeListener() {
				@Override
				public void onPageSelected(int position) {
					super.onPageSelected(position);
					// Find the ViewPager Position
					mActionBar.setSelectedNavigationItem(position);
				}
			};

			mPager.setOnPageChangeListener(ViewPagerListener);
			
			// Locate the adapter class called ViewPagerAdapter.java
			FragmentAdapterListContacts viewpageradapter = new FragmentAdapterListContacts(
					fm);
			// Set the View Pager Adapter into ViewPager
			mPager.setAdapter(viewpageradapter);

			// Capture tab button clicks
			ActionBar.TabListener tabListener = new ActionBar.TabListener() {

				@Override
				public void onTabSelected(Tab tab, FragmentTransaction ft) {
					// Pass the position on tab click to ViewPager
					mPager.setCurrentItem(tab.getPosition());
				}

				@Override
				public void onTabUnselected(Tab tab, FragmentTransaction ft) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onTabReselected(Tab tab, FragmentTransaction ft) {
					// TODO Auto-generated method stub
				}
			};

			// Create first Tab
			tab = mActionBar.newTab().setIcon(R.drawable.star)
					.setText("Favorite").setTabListener(tabListener);
			mActionBar.addTab(tab);

			// Create second Tab
			tab = mActionBar.newTab().setIcon(R.drawable.all_contact)
					.setText("All Contacts").setTabListener(tabListener);
			mActionBar.addTab(tab);


		}
}

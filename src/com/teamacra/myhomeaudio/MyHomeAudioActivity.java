package com.teamacra.myhomeaudio;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class MyHomeAudioActivity extends TabActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        addTabs();
    }
    
    /**
     * Add tabs to the main activity.
     */
    private void addTabs() {
    	Resources resources = getResources();
        TabHost tabHost = getTabHost();
        
        // The "my" tab
        Intent myIntent = new Intent().setClass(this, MyActivity.class);
        TabHost.TabSpec myTabSpec;
        myTabSpec = tabHost.newTabSpec("my")
        		.setIndicator("My")
        		.setContent(myIntent);
        tabHost.addTab(myTabSpec);
        
        // The "users" tab
        Intent usersIntent = new Intent().setClass(this, UsersActivity.class);
        TabHost.TabSpec usersTabSpec;
        usersTabSpec = tabHost.newTabSpec("users")
        		.setIndicator("Users")
        		.setContent(usersIntent);
        tabHost.addTab(usersTabSpec);
        
        // The "rooms" tab
        Intent roomsIntent = new Intent().setClass(this, RoomsActivity.class);
        TabHost.TabSpec roomsTabSpec;
        roomsTabSpec = tabHost.newTabSpec("rooms")
        		.setIndicator("Rooms")
        		.setContent(roomsIntent);
        tabHost.addTab(roomsTabSpec);
        
        // The "settings" tab
        Intent settingsIntent = new Intent().setClass(this, SettingsActivity.class);
        TabHost.TabSpec settingsTabSpec;
        settingsTabSpec = tabHost.newTabSpec("settings")
        		.setIndicator("Settings")
        		.setContent(settingsIntent);
        tabHost.addTab(settingsTabSpec);
        
    }
}
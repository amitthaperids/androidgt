package com.example.androidwidgetsample;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

public class WidgetSettings extends Activity {

	private SharedPreferences mSharedPref;
	private Button saveSettings;
	private EditText recieverName;
	private EditText recieverMobile;
	private EditText recieverEmailAddress;
	private RadioGroup mRadioGroup;
	private EditText mTimeInterval;
	private EditText mRecieverRelation;
	private EditText mDeviceUserName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		saveSettings = (Button)findViewById(R.id.saveSettings);
		recieverName = (EditText)findViewById(R.id.receiver_name);
		recieverMobile = (EditText)findViewById(R.id.receiver_number_hint);
		recieverEmailAddress = (EditText)findViewById(R.id.receiver_email_hint);
		mRadioGroup = (RadioGroup)findViewById(R.id.radioGroup);
		mTimeInterval = (EditText)findViewById(R.id.receiver_timeInterval_hint);
		mRecieverRelation = (EditText)findViewById(R.id.receiver_relation_hint);
		mDeviceUserName = (EditText)findViewById(R.id.sender_name);
		
		mSharedPref = this.getSharedPreferences(this.getPackageName(), Context.MODE_PRIVATE);
		
		recieverName.setText(mSharedPref.getString(getApplicationContext().getResources().getString(R.string.receiverLabel), ""));
		recieverMobile.setText(mSharedPref.getString(getApplicationContext().getResources().getString(R.string.receiverNumberLabel), ""));
		recieverEmailAddress.setText(mSharedPref.getString(getApplicationContext().getResources().getString(R.string.receiverEmailLabel), ""));
		mTimeInterval.setText(mSharedPref.getString(getApplicationContext().getResources().getString(R.string.receivertimeIntervalLabel), ""));
		mRecieverRelation.setText(mSharedPref.getString(getApplicationContext().getResources().getString(R.string.receiverrelationLabel), ""));
		mDeviceUserName.setText(mSharedPref.getString(getApplicationContext().getResources().getString(R.string.senderLabel), ""));
		
		if(mSharedPref.getString(getApplicationContext().getResources().getString(R.string.app_activate_status), "").equals(getApplicationContext().getResources().getString(R.string.app_activate))){
			mRadioGroup.check(R.id.activateApp);
		}
		else{
			mRadioGroup.check(R.id.deactivateApp);
		}
		
		saveSettings.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Pattern emailPttern = Pattern
						.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
				Matcher emailMatcher = emailPttern.matcher(recieverEmailAddress.getText().toString().trim().toLowerCase());
				if(recieverName.getText().toString().trim().length()<1){
					Toast.makeText(getApplicationContext(), "Receiver name should not be empty.", Toast.LENGTH_SHORT).show();
					recieverName.requestFocus();
				}
				else if(mDeviceUserName.getText().toString().trim().length()<1){
					Toast.makeText(getApplicationContext(), "Device user name should not be empty.", Toast.LENGTH_SHORT).show();
					mDeviceUserName.requestFocus();
				}else if(recieverMobile.getText().toString().trim().length()<1){
						Toast.makeText(getApplicationContext(), "Mobile number should not be empty.", Toast.LENGTH_SHORT).show();
						recieverMobile.requestFocus();
				}
				else if(recieverEmailAddress.getText().toString().trim().length()<1){
					Toast.makeText(getApplicationContext(), "Receiver Email Address should not be empty.", Toast.LENGTH_SHORT).show();
					recieverEmailAddress.requestFocus();
				}
				else if(mTimeInterval.getText().toString().trim().length()<1){
					Toast.makeText(getApplicationContext(), "Time interval should not be empty.", Toast.LENGTH_SHORT).show();
					mTimeInterval.requestFocus();
				}
				else if(mRecieverRelation.getText().toString().trim().length()<1){
					Toast.makeText(getApplicationContext(), "Relation with receiver should not be empty.", Toast.LENGTH_SHORT).show();
					mRecieverRelation.requestFocus();
				}
				else if(!emailMatcher.matches())
				{
					Toast.makeText(getApplicationContext(), "Enter valid email id.", Toast.LENGTH_SHORT).show();
					mRecieverRelation.requestFocus();
				}
				else{
					Editor mPrefEditor = mSharedPref.edit();
					mPrefEditor.putString(getApplicationContext().getResources().getString(R.string.receiverLabel), recieverName.getText().toString());
					mPrefEditor.putString(getApplicationContext().getResources().getString(R.string.receiverNumberLabel), recieverMobile.getText().toString());
					mPrefEditor.putString(getApplicationContext().getResources().getString(R.string.receiverEmailLabel), recieverEmailAddress.getText().toString());
					mPrefEditor.putString(getApplicationContext().getResources().getString(R.string.receivertimeIntervalLabel), mTimeInterval.getText().toString());
					mPrefEditor.putString(getApplicationContext().getResources().getString(R.string.receiverrelationLabel), mRecieverRelation.getText().toString());
					mPrefEditor.putString(getApplicationContext().getResources().getString(R.string.senderLabel), mDeviceUserName.getText().toString());
					
					int checkedRadioButton = mRadioGroup.getCheckedRadioButtonId();
					switch (checkedRadioButton) {
					  case R.id.activateApp : mPrefEditor.putString(getApplicationContext().getResources().getString(R.string.app_activate_status), getApplicationContext().getResources().getString(R.string.app_activate));
					                   	              break;
					  case R.id.deactivateApp : mPrefEditor.putString(getApplicationContext().getResources().getString(R.string.app_activate_status), getApplicationContext().getResources().getString(R.string.app_deactivate));
							                      break;
					}
					mPrefEditor.commit();
					
					AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(v.getContext());
					ComponentName thisWidget = new ComponentName(v.getContext(), MyWidgetProvider.class);
					int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);				
					if(MyWidgetProvider.timer != null){
						MyWidgetProvider.timer.cancel();
						Log.e("TimerCancelled", "CANCEL");
					}
					Intent intent = new Intent(getApplicationContext(),MyWidgetProvider.class);
					intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
	
					// Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
					// since it seems the onUpdate() is only fired on that:
					int[] ids = allWidgetIds;
					intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
					sendBroadcast(intent);
					finish();
				}
			}
		});
		
	}
		
}

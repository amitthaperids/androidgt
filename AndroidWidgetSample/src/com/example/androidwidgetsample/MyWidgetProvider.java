package com.example.androidwidgetsample;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.RemoteViews;

public class MyWidgetProvider extends AppWidgetProvider {

	private SharedPreferences mAppSharedPref;
	public static Timer timer;
	private boolean isStopped = false;
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		Log.e("///////////////", "ON DELETED CALLED");
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		Log.e("///////////////", "ON DISABLED CALLED");
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		Log.e("///////////////", "ON ENABLED CALLED");
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		Log.e("///////////////", "ON RECIEVE CALLED");
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.e("///////////////", "ON UPDATE CALLED");
		super.onUpdate(context, appWidgetManager, appWidgetIds);						
		
		doTask(context, appWidgetManager, null, false);
		mAppSharedPref = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);	
		if(mAppSharedPref.getString(context.getResources().getString(R.string.app_activate_status), context.getResources().getString(R.string.app_deactivate)).equals(context.getResources().getString(R.string.app_activate))){
			SwitchOnGPS(context);
			GPSTracker mGPS = new GPSTracker(context);
			if(mGPS.canGetLocation){
				Log.e("CAN GET LOCATION", "CAN GET LOCATION");	
			}else{
				Log.e("CAN NOT GET LOCATION", "CAN NOT GET LOCATION");
			}			
			isStopped = false;
			MyWidgetProvider.timer = null;
			MyWidgetProvider.timer = new Timer();			
			Log.e("Seconds", "=" + Integer.parseInt(mAppSharedPref.getString(context.getResources().getString(R.string.receivertimeIntervalLabel),"10")) * 1000);
			MyWidgetProvider.timer.scheduleAtFixedRate(new SendMessage(context, appWidgetManager,mGPS), 1000, Integer.parseInt(mAppSharedPref.getString(context.getResources().getString(R.string.receivertimeIntervalLabel),"10")) * 1000);
		}
		else{
			Log.e("App Not Activated", "Not Activated");
			isStopped = true;
			SwitchOFFGPS(context);
		}
		
			
		
//			String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

//		    if(!provider.contains("gps")){ //if gps is disabled
//		        final Intent poke = new Intent();
//		        poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider"); 
//		        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
//		        poke.setData(Uri.parse("3")); 
//		        context.sendBroadcast(poke);
//		    }
		    
//		    String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
//
//		    if(provider.contains("gps")){ //if gps is enabled
//		        final Intent poke = new Intent();
//		        poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
//		        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
//		        poke.setData(Uri.parse("3")); 
//		        context.sendBroadcast(poke);
//		    }
//		}		
	}
	
	private void SwitchOnGPS(Context context){
		String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

	    if(!provider.contains("gps")){ //if gps is disabled
	        final Intent poke = new Intent();
	        poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider"); 
	        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
	        poke.setData(Uri.parse("3")); 
	        context.sendBroadcast(poke);
	    }
	}
	
	private void SwitchOFFGPS(Context context){
		String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		
		if(provider.contains("gps")){ //if gps is enabled
			final Intent poke = new Intent();
			poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
			poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
			poke.setData(Uri.parse("3")); 
			context.sendBroadcast(poke);
		}
	}
	
	public class SendMessage extends TimerTask{
		Context mContext;
		RemoteViews mRemoteViews;
		ComponentName mComponentName;
		AppWidgetManager mAppWidgetManager;
		GPSTracker mGpx;
		
		public SendMessage(Context ctx, AppWidgetManager appWidgetManager, GPSTracker gpx){
			this.mContext = ctx;
			this.mRemoteViews = new RemoteViews(ctx.getPackageName(), R.layout.widget_layout);
			this.mComponentName = new ComponentName(ctx, MyWidgetProvider.class);
			this.mAppWidgetManager = appWidgetManager;
//			this.mGpx = gpx;
			SwitchOnGPS(ctx);
			mGpx = new GPSTracker(ctx);
			if(mGpx.canGetLocation){
				Log.e("CAN GET LOCATION", "CAN GET LOCATION");
				Log.e("Latitude", "=" + gpx.getLatitude());
			}else{
				Log.e("CAN NOT GET LOCATION", "CAN NOT GET LOCATION");
			}
			
		}
		@Override
		public void run() {
			Log.e("RUN METHOD", "WORKS");	
			
			if(mAppSharedPref.getString(this.mContext.getResources().getString(R.string.app_activate_status), this.mContext.getResources().getString(R.string.app_deactivate)).equals(this.mContext.getResources().getString(R.string.app_activate))){
				isStopped = false;	
//				if(period != Integer.parseInt(mAppSharedPref.getString(this.mContext.getResources().getString(R.string.receivertimeIntervalLabel),"10")) * 1000){
//					this.cancel();
//					Log.e("RUN METHOD", "RESCHEDULE");
//					timer = null;
//					timer = new Timer();
//					timer.scheduleAtFixedRate(new SendMessage(this.mContext, this.mAppWidgetManager, this.mGpx), Integer.parseInt(mAppSharedPref.getString(mContext.getResources().getString(R.string.receivertimeIntervalLabel),"10")) * 1000, Integer.parseInt(mAppSharedPref.getString(this.mContext.getResources().getString(R.string.receivertimeIntervalLabel),"10")) * 1000);
////					this.scheduleAtFixedRate(new SendMessage(this.mContext, this.mAppWidgetManager, this.mGpx), Integer.parseInt(mAppSharedPref.getString(mContext.getResources().getString(R.string.receivertimeIntervalLabel),"10")) * 1000, Integer.parseInt(mAppSharedPref.getString(this.mContext.getResources().getString(R.string.receivertimeIntervalLabel),"10")) * 1000);
//					period = Integer.parseInt(mAppSharedPref.getString(this.mContext.getResources().getString(R.string.receivertimeIntervalLabel),"10")) * 1000;
//				}
//				else{
//					
//				}	
				SwitchOnGPS(mContext);
				mGpx.getLocation();				
				doTask(mContext, mAppWidgetManager,mGpx,true);
//				SwitchOFFGPS(mContext);
			}
			else{
				Log.e("RUN METHOD", "App Not Activated");
				isStopped = true;
				this.cancel();
				MyWidgetProvider.timer = null;				
			}
		}
		@Override
		public boolean cancel() {
			Log.e("TIMER TASK CANCEL", "CALLED");
			MyWidgetProvider.timer = null;			
			return super.cancel();
		}
		
		
		
		
		
	}
	public void doTask(Context mContext, AppWidgetManager mAppWidgetManager, GPSTracker mGpx, boolean isSendMessage){
		//Get All ID'S
		ComponentName thisWidget = new ComponentName(mContext, MyWidgetProvider.class);
		int[] allWidgetIds = mAppWidgetManager.getAppWidgetIds(thisWidget);
		SharedPreferences geoPrefs = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
		if(!isStopped){
			for(int widgetId : allWidgetIds){			
				//Create Some random data
				int number = (new Random().nextInt(100));
				RemoteViews remoteViews = 	new RemoteViews(mContext.getPackageName(),R.layout.widget_layout);
	
				//Set the text
				remoteViews.setTextViewText(R.id.update, String.valueOf(number));
				
				//Register the onClickListener
				Intent callSettingActivity = new Intent(mContext,WidgetSettings.class);
				PendingIntent mPendingIntent = PendingIntent.getActivity(mContext, 0, callSettingActivity, 0);
				
				//Register the onClickListener
				Intent intent = new Intent(mContext,MyWidgetProvider.class);
				intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
				intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);
				
				PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				remoteViews.setOnClickPendingIntent(R.id.update, pendingIntent);
				remoteViews.setOnClickPendingIntent(R.id.settings, mPendingIntent);		
				if(isSendMessage){
					try{
						Log.e("Latitude", "=" + geoPrefs.getString("Latitude", "2.2"));
						Log.e("Longitude", "=" + geoPrefs.getString("Longitude", "2.2"));
						double latitude = Double.parseDouble(geoPrefs.getString("Latitude", "2.2"));
						double longitude = Double.parseDouble(geoPrefs.getString("Longitude", "2.2"));
//						latitude = 30.669740676879883;
//						longitude = 76.7194595336914;
						Log.e("Timer Called", "True");
						SmsManager smsManager = SmsManager.getDefault();
						Geocoder geocoder;
						List<Address> addresses = new ArrayList<Address>();
						geocoder = new Geocoder(mContext, Locale.getDefault());
						try {
							addresses = geocoder.getFromLocation(latitude, longitude, 10);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						String mailSubject = mContext.getResources().getString(R.string.mailSubject);
						mailSubject = mailSubject.replace("device_username", geoPrefs.getString(mContext.getResources().getString(R.string.senderLabel), ""));
						String gmailUserName = mContext.getResources().getString(R.string.mailSenderAddress);
						String gmailUserPassword = mContext.getResources().getString(R.string.mailSenderPassword);
						String gmailSenderName = geoPrefs.getString(mContext.getResources().getString(R.string.senderLabel), "");
						String gmailRecieverName = geoPrefs.getString(mContext.getResources().getString(R.string.receiverEmailLabel), "No Email Found");
						   
						if(addresses != null) {		
							try{
							   Address returnedAddress = addresses.get(0);
							   StringBuilder strReturnedAddress = new StringBuilder("Address:\n");
							   for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
							    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
							   }
//							   Log.e("Address", "=" + strReturnedAddress);
							   Log.e("Reciever", geoPrefs.getString(mContext.getResources().getString(R.string.receiverNumberLabel), "123"));
							   
							   String message = mContext.getResources().getString(R.string.SMSContent);
							   message = message.replace("receiver_name", geoPrefs.getString(mContext.getResources().getString(R.string.receiverLabel), "guest"));
							   message = message.replace("relation_name", geoPrefs.getString(mContext.getResources().getString(R.string.receiverrelationLabel), "guest"));
							   message = message.replace("current_address", strReturnedAddress.toString());
							   
							   
							   smsManager.sendTextMessage(geoPrefs.getString(mContext.getResources().getString(R.string.receiverNumberLabel), "123"), null, message, null, null);
							   
							  Log.e("Message", message);
							   if(!gmailRecieverName.equals("No Email Found")){
								   GMailSender sender = new GMailSender(gmailUserName, gmailUserPassword);
					                try {
					                    sender.sendMail(mailSubject,
					                            message,
					                            gmailSenderName,
					                            gmailRecieverName);
					                } catch (Exception e) {
					                    Log.e("SendMail", e.getMessage(), e);
					                }
							   }
							   else{
								   Log.e("EMAIL", gmailRecieverName);
							   }
							   
//							   SendMail mailer = new SendMail("vishal.ids1@gmail.com","mani.aimt@gmail.com","Current Location", message, "<b>HtmlBody</b>");
//							   try {
//							       mailer.sendAuthenticated();
//							   } catch (Exception e) {
//							       Log.e("Exception", "Failed sending email.", e);
//							   }
							}
							catch (IndexOutOfBoundsException e) {
								e.printStackTrace();
								String message = mContext.getResources().getString(R.string.SMSContentLatLong);
								message = message.replace("receiver_name", geoPrefs.getString(mContext.getResources().getString(R.string.receiverLabel), "guest"));
								message = message.replace("relation_name", geoPrefs.getString(mContext.getResources().getString(R.string.receiverrelationLabel), "guest"));
								message = message.replace("current_latitude", ""+latitude);
								message = message.replace("current_longitude", ""+longitude);
								smsManager.sendTextMessage(geoPrefs.getString(mContext.getResources().getString(R.string.receiverNumberLabel), "123"), null, message, null, null);
								Log.e("Reciever", geoPrefs.getString(mContext.getResources().getString(R.string.receiverNumberLabel), "123"));
								Log.e("Message", message);
								
								Log.e("Message", message);
								   if(!gmailRecieverName.equals("No Email Found")){
									   GMailSender sender = new GMailSender(gmailUserName, gmailUserPassword);
						                try {
						                    sender.sendMail(mailSubject,
						                            message,
						                            gmailSenderName,
						                            gmailRecieverName);
						                } catch (Exception ex) {
						                    Log.e("SendMail", ex.getMessage(), ex);
						                }
								   }
								   else{
									   Log.e("EMAIL", gmailRecieverName);
								   }
							}
							   
						}
						else{
							smsManager.sendTextMessage(geoPrefs.getString(mContext.getResources().getString(R.string.receiverNumberLabel), "123"), null, mContext.getResources().getString(R.string.SMSContentAddressNotFound), null, null);
							Log.e("Reciever", geoPrefs.getString(mContext.getResources().getString(R.string.receiverNumberLabel), "123"));
							Log.e("Message", geoPrefs.getString(mContext.getResources().getString(R.string.SMSContentAddressNotFound), "123"));
						}
					}
					catch (NumberFormatException e) {
						e.printStackTrace();
					}

				}
				mAppWidgetManager.updateAppWidget(widgetId, remoteViews);
			}
		}
	}
}

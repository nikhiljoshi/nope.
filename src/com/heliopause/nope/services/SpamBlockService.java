package com.heliopause.nope.services;

import android.app.Notification;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

public class SpamBlockService extends Service {

	// Debugging constants
	private static final boolean DEBUG = true;
	private static final String TAG = SpamBlockService.class.getSimpleName();

	public static TextReceiver receiver = new TextReceiver();
	SharedPreferences prefs;
	static ContentResolver cr = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		if (DEBUG) {
			Log.d(TAG, "SpamBlock service onCreate; registering receiver");
		}
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(99999999);
		registerReceiver(receiver, filter);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (DEBUG) {
			Log.d(TAG, "SpamBlock service onDestroy; unregistering receiver");
		}

		unregisterReceiver(receiver);

	}

	@Override
	public void onStart(Intent intent, int startid) {
		if (DEBUG) {
			Log.d(TAG,
					"SpamBlock service onStart; starting forground notification");
		}
		startForeground(startid, new Notification());
	}
}
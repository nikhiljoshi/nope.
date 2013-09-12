package com.heliopause.nope.services;

import com.heliopause.nope.Constants;
import com.heliopause.nope.database.BlockItemTable;
import com.heliopause.nope.database.DatabaseHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallReceiver extends BroadcastReceiver {

	// Debug constants
	private static final boolean DEBUG = true;
	private static final String TAG = CallReceiver.class.getSimpleName();

	private SQLiteDatabase db;
	private DatabaseHelper helper;
	private Context context;

	@Override
	public void onReceive(Context context, Intent intent) {

		// Set the context
		this.context = context;

		// Check is the listener is disabled
		SharedPreferences prefs = context.getSharedPreferences(
				Constants.SETTINGS_PREFS, Context.MODE_PRIVATE);
		boolean turnedOn = prefs.getBoolean(Constants.CALL_BLOCK_SERVICE_STATUS,
				true);
		if (!turnedOn) {
			return;
		}

		getHelper();
		db = helper.getReadableDatabase();

		// Get the action if the intent isn't null.
		String action = (intent == null) ? null : intent.getAction();

		if (action.equalsIgnoreCase("android.intent.action.PHONE_STATE")) {
			if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
					TelephonyManager.EXTRA_STATE_RINGING)) {
				if (isOnBlockList(intent
						.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER))) {
					if (DEBUG)
						Log.d(TAG, "Phone number is on block list!");
				} else {
					if (DEBUG)
						Log.d(TAG,
								"Phone number was not detected on block list");
				}
			}
		}

	}

	private void getHelper() {
		if (helper == null) {
			helper = new DatabaseHelper(context);
			if (DEBUG) {
				Log.d(TAG,
						"Creating a new instance of the database helper object");
			}
		} else {
			if (DEBUG) {
				Log.d(TAG, "Using existing database helper");
			}
		}
	}

	private boolean isOnBlockList(String incomingNum) {

		Cursor c = db.rawQuery("SELECT * FROM "
				+ BlockItemTable.CALLBLOCK_TABLE_NAME + " WHERE "
				+ BlockItemTable.COLUMN_NUMBER + "=?;",
				new String[] { incomingNum });
		if (c != null) {
			return true;
		} else {
			return false;
		}

	}
}

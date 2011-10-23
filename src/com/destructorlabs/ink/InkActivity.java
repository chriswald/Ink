package com.destructorlabs.ink;

import android.app.Activity;
import android.os.Bundle;

public class InkActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.drawing);
	}
}
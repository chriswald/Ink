package com.destructorlabs.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.destructorlabs.ink.R;

public class InformDialog extends Dialog implements OnClickListener {

	public InformDialog(Context context, String title, String text) {
		super(context);
		this.setTitle(title);

		TextView tv = (TextView) this.findViewById(R.id.dialog_text);
		tv.setText(text);

		Button b = (Button) this.findViewById(R.id.ok_button);
		b.setOnClickListener(this);
	}

	public void onClick(View v) {
		this.dismiss();
	}

}

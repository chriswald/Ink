/**
 *  _______    __    __   ________   __________    _______
 * /\   __ \  /\ \  /\ \ /\  ____ \ /\____  ___\  /\  ____\
 * \ \ \_/\_\ \ \ \_\_\ \\ \ \___\ \\/___/\ \__/  \ \ \___/
 *  \ \ \\/_/  \ \  ____ \\ \  ___ <     \ \ \     \ \____`\
 *   \ \ \   __ \ \ \__/\ \\ \ \ /\ \     \ \ \     \/___/\ \
 *    \ \ \__\ \ \ \ \ \ \ \\ \ \\ \ \    _\_\ \____   __\_\ \
 *     \ \______\ \ \_\ \ \_\\ \_\\ \_\  /\_________\ /\______\
 *      \/______/  \/_/  \/_/ \/_/ \/_/  \/_________/ \/______/
 *             __      __    ________    __        ______
 *            /\ \    /\ \  /\  ____ \  /\ \      /\  ___`,
 *            \ \ \   \ \ \ \ \ \__/\ \ \ \ \     \ \ \_/\ \
 *             \ \ \   \ \ \ \ \ \_\_\ \ \ \ \     \ \ \\ \ \
 *              \ \ \  _\ \ \ \ \  ____ \ \ \ \     \ \ \\ \ \
 *               \ \ \_\ \_\ \ \ \ \__/\ \ \ \ \_____\ \ \\_\ \
 *                \ \_________\ \ \_\ \ \_\ \ \______\\ \_____/
 *                 \/_________/  \/_/  \/_/  \/______/ \/____/
 *
 *         ->Ink
 *         ->Developed By Christopher J. Wald
 *         ->Copyright *Year* (c) All Rights Reserved
 *
 *
 * @author  	Christopher J. Wald
 * @date    	Oct 16, 2011
 * @project 	Ink
 * @file    	SettingsActivity.java
 * @description *Description*
 * @license:
 *
 * 	Redistribution and use in source and binary forms, with or without
 * 	modification, are permitted provided that the following conditions
 * 	are met:
 *
 *	- Redistributions of source code must retain the above copyright
 *	  notice, this list of conditions and the following disclaimer.
 *
 *	- Redistributions in binary form must reproduce the above copyright
 *	  notice, this list of conditions and the following disclaimer in the
 *	  documentation and/or other materials provided with the distribution.
 *
 *	- The name of Christopher J. Wald may not be used to endorse or promote
 *	  products derived from this software without specific prior written
 *	  permission.
 *
 * 	THIS SOFTWARE IS PRIVIDED BY THE COPYRIGHT HOLDER "AS IS" AND ANY
 * 	EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * 	IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * 	ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER BE LIABLE FOR ANY
 * 	DIRECT, INDIRECT, INCIDENTAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING
 * 	BUT NOT LIMITED TO UNDESIRED ACTION, LOSS OF SECURITY, LOSS OF DATA, LOSS OF
 * 	SLEEP,  LOSS OF HAIR, OR EXPLOSIONS). USE AT YOUR OWN RISK.
 */
package com.destructorlabs.ink;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.destructorlabs.pkg.Shape;

public class SettingsActivity extends Activity{
	Vector<String> files = new Vector<String>();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.settings);

		Button button = (Button) this.findViewById(R.id.save_button);
		final EditText edit = (EditText) this.findViewById(R.id.save_text);
		final TextView tv = (TextView) this.findViewById(R.id.load_text);

		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				if (!edit.getText().toString().equals("")) {
					FileOutputStream fos;
					ObjectOutputStream oos;

					try {
						fos = SettingsActivity.this.openFileOutput(edit.getText().toString(), Context.MODE_PRIVATE);
						oos = new ObjectOutputStream(fos);

					} catch (Exception e) {
						tv.setText("-Could not create file-\n" + e.toString());
						return;
					}

					try {
						Vector<Shape> shapes = DrawView.getShapesForSave();
						for (Shape s : shapes) {
							oos.writeObject(s);
						}
						oos.close();

					} catch (NullPointerException e) {
						tv.setText(e.toString());
						return;
					} catch (IOException e) {
						tv.setText(e.toString());
						return;
					} catch (Exception e) {
						tv.setText(e.toString());
						return;
					}
				}
			}
		});
	}
}

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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

public class SettingsActivity extends ListActivity{

	private enum InputType{TYPE, CENTER, CORNERS, NONE};
	InputType type = InputType.NONE;
	private List<String> item = null;
	private String root;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.settings);
		this.root = this.getFilesDir() + "/";

		Button button = (Button) this.findViewById(R.id.save_button);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				//SettingsActivity.this.onButtonClick();
				SettingsActivity.this.getDir(SettingsActivity.this.root);
			}
		});

		this.getDir(this.root);
	}


	/*@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		this.loadFromFile(this.root + this.item.get(position) + ".ink");
	}*/


	/*private void onButtonClick(){
		final EditText edit = (EditText) this.findViewById(R.id.save_text);

		if (!edit.getText().toString().equals("")) {
			String filename = edit.getText().toString();
			FileOutputStream fos;

			try{
				fos = SettingsActivity.this.openFileOutput(filename + ".ink", Context.MODE_PRIVATE);
				Vector<Shape> shapes = DrawView.getShapesForSave();
				for (Shape s : shapes){
					fos.write(s.toString().getBytes());
				}
				fos.close();
				edit.setText("");

			} catch (IOException e){}
		}
	}*/

	private void getDir(String dirPath){
		this.item = new ArrayList<String>();

		File f = new File(dirPath);
		File[] files = f.listFiles();

		if (files == null)
			return;

		for(int i=0; i < files.length; i++)	{
			File file = files[i];

			if (!file.isDirectory() && file.getName().endsWith(".ink")){
				String name = file.getName();
				name = name.substring(0, name.length() - 4);
				this.item.add(name);
			}

		}

		ArrayAdapter<String> fileList =	new ArrayAdapter<String>(this, R.layout.row, this.item);
		this.setListAdapter(fileList);
	}


	/*private void loadFromFile(String filename){
		if (!filename.endsWith(".ink"))
			filename += ".ink";

		Vector<Shape> shapes = new Vector<Shape>();

		boolean first_shape = true;
		String line = "";
		FileInputStream fis;

		ShapeType st = null;
		Corner c = null;
		Vector<Corner> cs = new Vector<Corner>();

		try {
			fis = new FileInputStream(filename);
			Scanner scan = new Scanner(fis);

			DrawView.getShapesForSave().removeAllElements();

			while (scan.hasNext()){
				line = scan.nextLine();

				if (line.startsWith("@SHAPE")) {
					if (!first_shape)
						shapes.add(Shape.makeShapeFromSave(st, c, cs));
					first_shape = false;
				} else if (line.startsWith("#SHAPETYPE")) {
					this.type = InputType.TYPE;
				} else if (line.startsWith("#CENTER")) {
					this.type = InputType.CENTER;
				} else if (line.startsWith("#CORNERS")) {
					this.type = InputType.CORNERS;
				} else {
					switch (this.type){
						case TYPE:
							st = (line.contains("CIRCLE") ? ShapeType.CIRCLE : ShapeType.POLYGON);
							break;
						case CENTER:
							String[] tokens = line.split(" ");
							c = new Corner(new Point(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1])), Shape.DEFAULT_RADIUS);
							break;
						case CORNERS:
							String[] tokens1 = line.split(" ");
							cs.add(new Corner(new Point(Integer.parseInt(tokens1[0]), Integer.parseInt(tokens1[1])), Shape.DEFAULT_RADIUS));
							break;
						default:
							break;
					}
				}
			}

			if (st != null  &&  c != null)
				shapes.add(Shape.makeShapeFromSave(st, c, cs));

			DrawView.addShapes(shapes);

		} catch (IOException e){}
	}*/
}

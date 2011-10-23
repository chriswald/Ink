package com.destructorlabs.ink;

import java.util.Vector;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class FileActivity extends ListActivity{
	public Vector<String> filenames = new Vector<String>();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.settings);

		for (int i = 0; i < 10; i ++) {
			this.filenames.add("-" + i);
		}

		String[] list = this.fileList();
		for (String s : list) {
			this.filenames.add(s);
		}

		this.setListAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, this.filenames.toArray()));
	}
}

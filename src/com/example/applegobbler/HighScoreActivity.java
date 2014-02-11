package com.example.applegobbler;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;

import com.example.model.HighScoreHelper;
import com.example.model.SQLiteDBHelper;

public class HighScoreActivity extends ListActivity {

	private ArrayList<DBEntry> listOfScores;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_high_score);
		
		HighScoreHelper hsHelper = new HighScoreHelper(this);
		
		listOfScores = hsHelper.getAllEvents();
		ArrayList<DBEntry> hs = new ArrayList<DBEntry>();
		int arrSize = listOfScores.size();
		while(hs.size() < arrSize)
		{
			int max = -1, maxIndex = 0;
			for(int i=0; i<listOfScores.size(); i++)
			{
				if(Integer.valueOf(listOfScores.get(i).score) > max)
				{
					max = Integer.valueOf(listOfScores.get(i).score);
					maxIndex = i;
				}
			}
			hs.add(listOfScores.get(maxIndex));
			listOfScores.remove(maxIndex);
		}
		ArrayList<String> list = new ArrayList<String>();		
		for(int i=0; i<hs.size(); i++)
		{
			list.add(hs.get(i).initial + "                    " + hs.get(i).score + "                    " + hs.get(i).diff);
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, list);
		setListAdapter(adapter);
	}	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.high_score, menu);
		return true;
	}

}

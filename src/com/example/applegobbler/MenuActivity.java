package com.example.applegobbler;

import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.game.model.Game;



public class MenuActivity extends Activity {

	private Button settingsButton;
	private Button newGameButton;
	private Button highScoreButton;
	private EditText init;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		
		init = (EditText) findViewById(R.id.editText1);
		settingsButton =  (Button) findViewById(R.id.settings_button);
		settingsButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent iSettings = new Intent(MenuActivity.this, SettingsActivity.class);
				startActivity(iSettings);
			}
		});
		newGameButton =  (Button) findViewById(R.id.new_game_button);
		newGameButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String prefs[] = getPrefs();
				if(prefs[0].equals("NULL"))
				{
					Toast.makeText(getApplicationContext(), "Must Specify Difficulty", Toast.LENGTH_SHORT).show();
				}	
				else
				{
					Integer difficulty = Integer.valueOf(prefs[0]);
					String initial = init.getEditableText().toString();
					initial = initial.toUpperCase();
					if(initial.length() != 2)
					{
						Toast.makeText(getApplicationContext(), "Must Specify Initial", Toast.LENGTH_SHORT).show();	
					}
					else
					{
						Intent game = new Intent(MenuActivity.this, Game.class);
						game.putExtra("diff", difficulty);
						game.putExtra("initial", initial);
						startActivity(game);
					}
				}
			}
		});
		
		highScoreButton = (Button) findViewById(R.id.high_score_button);
		highScoreButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent highScore = new Intent(MenuActivity.this, HighScoreActivity.class);
				startActivity(highScore);
			}
		});
	}

	private String[] getPrefs()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Resources res = getResources();
		
		String difficultyListPref = prefs.getString("difficulty_pref", "NULL");
		String navListPref = prefs.getString("navigation_pref", "NULL");
		
		String value[] = {difficultyListPref, navListPref};
		return value;

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}
	
}

package com.game.model;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.sound.SoundEngine;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class Game extends Activity
{
	protected CCGLSurfaceView _glSurfaceView;
	private int difficulty;
	private String initial;
	
	private int onPauseApplesEaten;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		_glSurfaceView = new CCGLSurfaceView(this);
		
		Bundle extras = this.getIntent().getExtras();
		difficulty = extras.getInt("diff");
		initial = extras.getString("initial");
		
		setContentView(_glSurfaceView);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		CCDirector.sharedDirector().attachInView(_glSurfaceView);
		
		CCDirector.sharedDirector().setDeviceOrientation(CCDirector.kCCDeviceOrientationLandscapeLeft);
		
		CCDirector.sharedDirector().setDisplayFPS(false);
		
		CCDirector.sharedDirector().setAnimationInterval(1.0f / 60.0f);
		
		GameLayer.setDiff(difficulty);
		GameLayer.setInitial(initial);
		GameLayer.setContext(this);
		CCScene scene = GameLayer.scene();
		
		CCDirector.sharedDirector().runWithScene(scene);
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		onPauseApplesEaten = GameLayer.getApplesEaten();	
		SoundEngine.sharedEngine().pauseSound() ;
		CCDirector.sharedDirector().pause();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		GameLayer.setApplesEaten(onPauseApplesEaten);
		CCDirector.sharedDirector().resume();
	}
	
	@Override
	public void onStop()
	{
		super.onStop();
		
		CCDirector.sharedDirector().end();
	}
}
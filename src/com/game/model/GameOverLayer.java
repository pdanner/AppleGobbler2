package com.game.model;

import org.cocos2d.layers.CCColorLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.sound.SoundEngine;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4B;

import com.example.applegobbler.R;

import android.content.Context;
import android.view.MotionEvent;

public class GameOverLayer extends CCColorLayer
{
	protected CCLabel _label;
	private int numTouches;
	
	public static CCScene scene(String message)
	{
		CCScene scene = CCScene.node();
		GameOverLayer layer = new GameOverLayer(ccColor4B.ccc4(3, 214, 34, 255));
		
		layer.getLabel().setString(message);
		
		scene.addChild(layer);
		
		return scene;
	}
	
	public CCLabel getLabel()
	{
		return _label;
	}
	
	protected GameOverLayer(ccColor4B color)
	{
		super(color);
		
		Context context = CCDirector.sharedDirector().getActivity();
		SoundEngine.sharedEngine().playSound(context, R.raw.game_over, false);
		
		this.setIsTouchEnabled(true);
		
		numTouches++;
		
		CGSize winSize = CCDirector.sharedDirector().displaySize();
		
		_label = CCLabel.makeLabel("Won't See Me", "DroidSans", 42);
		_label.setColor(ccColor3B.ccYELLOW);
		_label.setPosition(winSize.width / 2.0f, winSize.height / 3.0f);
		
		CCSprite gameOver = new CCSprite("game_over.png");
		gameOver.setPosition(winSize.getWidth() / 2.0f, winSize.getHeight() / 1.5f);
		addChild(gameOver);
		addChild(_label);
	}
	
	public void gameOverDone()
	{
		CCDirector.sharedDirector().replaceScene(GameLayer.scene());
	}
	
	@Override
	public boolean ccTouchesEnded(MotionEvent event)
	{
		if(numTouches >= 2)
		{
			numTouches = 0;
			gameOverDone();
		}
		else
		{
			numTouches++;
		}
		
		
		return true;
	}
}

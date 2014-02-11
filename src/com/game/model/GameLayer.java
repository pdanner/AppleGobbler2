package com.game.model;

import java.util.ArrayList;
import java.util.Random;

import org.cocos2d.layers.CCColorLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.sound.SoundEngine;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor4B;

import android.content.Context;
import android.view.MotionEvent;

import com.example.applegobbler.R;
import com.example.model.HighScoreHelper;

public class GameLayer extends CCColorLayer
{
	protected ArrayList<CCSprite> apples;
	protected ArrayList<CCSprite> walls;
	protected ArrayList<CCSprite> powerupsSpeed;
	protected ArrayList<CCSprite> powerupsPoints;
	protected ArrayList<CCSprite> powerupsWalls;

	private ArrayList<CGRect> wallRects;
	private CGRect appleRect;
	protected static int applesEaten;
	
	private static final int NO_MOVE = -1;
	private static final int MOVING_UP = 0;
	private static final int MOVING_RIGHT = 1;
	private static final int MOVING_DOWN = 2;
	private static final int MOVING_LEFT = 3;
	
	private static final int SPEED_SLOW = 2;
	private static final int SPEED_MEDIUM = 5;
	private static final int SPEED_FAST = 10;
	private static final int SPEED_KING_COBRA = 20;
	
	private static final int FLING_RIGHT = 0;
	private static final int FLING_LEFT = 1;
	private static final int FLING_UP = 3;
	private static final int FLING_DOWN = 4;
	
	private static final int DIFFICULTY_EASY = 0;
	private static final int DIFFICULTY_MEDIUM = 1;
	private static final int DIFFICULTY_HARD = 2;
	private static final int DIFFICULTY_KING_COBRA = 3;
	
	private static final int SPEED_POWERUP = 0;
	private static final int POINT_POWERUP = 1;
	private static final int WALL_POWERUP = 2;
	
	private double time;
	private double powerupSpeedDisplayTime;
	private double powerupPointsDisplayTime;
	private double powerupWallsDisplayTime;
	
	private boolean timeRunning = false;
	private boolean speedDisplay = false;
	private boolean pointsDisplay = false;
	private boolean wallsDisplay = false;
	
	private boolean wallsVisible = false;
	private double wallsVisibleTime;	
	private boolean pointsVisible = false;
	private double pointsVisibleTime;
	private boolean speedVisible = false;
	private double speedVisibleTime;
	
	private int playerMoving;
	private int nextMove;
	private int currentSpeed;
	
	private CCSprite player;
	
	private int firstTouchX;
	private int firstTouchY;
	
	private static int difficulty;
	private static String initial;	
	private static Context context;
	
	public static CCScene scene()
	{
		CCScene scene = CCScene.node();
		CCColorLayer layer = new GameLayer(ccColor4B.ccc4(153, 153, 153, 255));
		
		scene.addChild(layer);
		
		return scene;
	}
	
	public static void setDiff(int diff)
	{
		difficulty = diff;
	}
	
	public static void setInitial(String i)
	{
		initial = i;
	}
	
	public static void setContext(Context c)
	{
		context = c;
	}
	
	private void setUpWalls()
	{
		for(int i=0; i<10*difficulty; i++)
		{
			CCSprite wall = CCSprite.sprite("wall.png");
			Random rand = new Random();
			// Determine where to spawn the target along the Y axis
			CGSize winSize = CCDirector.sharedDirector().displaySize();
			int minY = (int)(0);
			int maxY = (int)(winSize.height);
			int rangeY = maxY - minY;
			
			int minX = (int)(0);
			int maxX = (int)(winSize.width);
			int rangeX = maxX - minX;
			
			wall.setPosition(rand.nextInt(rangeX)+ minX, rand.nextInt(rangeY) + minY);
			CGRect wallRect = CGRect.make(wall.getPosition().x - (wall.getContentSize().width),
					wall.getPosition().y - (wall.getContentSize().height),
					wall.getContentSize().width,
					wall.getContentSize().height);
			wallRects.add(wallRect);
			
			addChild(wall);
			walls.add(wall);
		}
		
			
	}
		
	protected GameLayer(ccColor4B color)
	{
		super(color);
		
		this.setIsTouchEnabled(true);
		
		apples = new ArrayList<CCSprite>();
		walls = new ArrayList<CCSprite>();
		powerupsSpeed = new ArrayList<CCSprite>();
		powerupsPoints = new ArrayList<CCSprite>();
		powerupsWalls = new ArrayList<CCSprite>();
		wallRects = new ArrayList<CGRect>();
		applesEaten = 0;
		
		CGSize winSize = CCDirector.sharedDirector().displaySize();
		player = CCSprite.sprite("head.png");
		playerMoving = NO_MOVE;
		nextMove = NO_MOVE;
		switch(difficulty)
		{
		case DIFFICULTY_EASY: currentSpeed = SPEED_SLOW;
			break;
		case DIFFICULTY_MEDIUM: currentSpeed = SPEED_MEDIUM;
			break;
		case DIFFICULTY_HARD: currentSpeed = SPEED_FAST;
			break;
		case DIFFICULTY_KING_COBRA: currentSpeed = SPEED_KING_COBRA;
			break;
		}
		setUpWalls();
		boolean foundPosition = false;
		player.setPosition(CGPoint.ccp(winSize.width / 2.0f, winSize.height / 2.0f));
		Random rand = new Random();

		while(!foundPosition)
		{
			boolean intersected = false;
			CGRect playerRect = CGRect.make(player.getPosition().x - (player.getContentSize().width),
					player.getPosition().y - (player.getContentSize().height),
					player.getContentSize().width,
					player.getContentSize().height);
			for (CGRect wall : wallRects)
			{
				
				if (CGRect.intersects(playerRect, wall))
				{
					player.setPosition(rand.nextInt((int)winSize.getWidth()), rand.nextInt((int)winSize.getHeight()));
					intersected = true;
				}
					
			}
			if(!intersected)
			{
				foundPosition = true;
			}
			
		}
		
		
		addChild(player);
		addTarget();
		
		// Handle sound
		Context context = CCDirector.sharedDirector().getActivity();
		SoundEngine.sharedEngine().playSound(context, R.raw.background_snowy_hill, true);
		
		this.schedule("update");
	}
	
	@Override
	public boolean ccTouchesBegan(MotionEvent event) {
		// Choose one of the touches to work with
		CGPoint location = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
				
		firstTouchX = (int) location.x;
		firstTouchY = (int) location.y;
		
		return true;
	}
	
	@Override
	public boolean ccTouchesEnded(MotionEvent event)
	{
		// Choose one of the touches to work with
		CGPoint location = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
		
		// Set up initial location of projectile
		float secondTouchX = location.x;
		float secondTouchY = location.y;
		
		int fling;
		// RIGHT FLING
		if(secondTouchX > firstTouchX)
		{
			if(secondTouchX - firstTouchX > Math.abs(secondTouchY - firstTouchY))
			{
				fling = FLING_RIGHT;
			}
			else
			{
				// FLING UP
				if(secondTouchY > firstTouchY)
				{
					fling = FLING_UP;
				}
				else
				{
					fling = FLING_DOWN;
				}
			}
		}
		else
		{
			if(Math.abs(secondTouchX - firstTouchX) > Math.abs(secondTouchY - firstTouchY))
			{
				fling = FLING_LEFT;
			}
			else
			{
				// FLING UP
				if(secondTouchY > firstTouchY)
				{
					fling = FLING_UP;
				}
				else
				{
					fling = FLING_DOWN;
				}
			}
		}
				
		// Turn right
		if((playerMoving == MOVING_UP || playerMoving == MOVING_DOWN) && fling == FLING_RIGHT)
		{
			nextMove = MOVING_RIGHT;
		}
		// Turn Left
		else if((playerMoving == MOVING_UP || playerMoving == MOVING_DOWN) && fling == FLING_LEFT)
		{
			nextMove = MOVING_LEFT;
		}
		// Turn down
		else if((playerMoving == MOVING_LEFT || playerMoving == MOVING_RIGHT) && fling == FLING_DOWN)
		{
			nextMove = MOVING_DOWN;
		}
		// Turn up
		else if((playerMoving == MOVING_LEFT || playerMoving == MOVING_RIGHT) && fling == FLING_UP)
		{
			nextMove = MOVING_UP;
		}
		else if(playerMoving == NO_MOVE)
		{
			if(fling == FLING_RIGHT)
			{
				nextMove = MOVING_RIGHT;
			}
			// Turn Left
			else if(fling == FLING_LEFT)
			{
				nextMove = MOVING_LEFT;
			}
			// Turn down
			else if(fling == FLING_DOWN)
			{
				nextMove = MOVING_DOWN;
			}
			// Turn up
			else if(fling == FLING_UP)
			{
				nextMove = MOVING_UP;
			}
		}
		else
		{
			nextMove = NO_MOVE;
		}		
		return true;
	}
	
	public void gameLogic(float dt)
	{
		addTarget();
	}
	
	public void update(float dt)
	{
		boolean intersected = false;
		float playerX = player.getPosition().x;
		float playerY = player.getPosition().y;
		
		if(timeRunning && System.currentTimeMillis() > time + 5000)
		{
			switch(difficulty)
			{
			case DIFFICULTY_EASY: currentSpeed = SPEED_SLOW;
				break;
			case DIFFICULTY_MEDIUM: currentSpeed = SPEED_MEDIUM;
				break;
			case DIFFICULTY_HARD: currentSpeed = SPEED_FAST;
				break;
			case DIFFICULTY_KING_COBRA: currentSpeed = SPEED_KING_COBRA;
				break;
			}
			timeRunning = false;
		}
		if(speedDisplay && System.currentTimeMillis() > powerupSpeedDisplayTime + 5000)
		{
			speedDisplay = false;
			for(CCSprite p : powerupsSpeed)
			{
				powerupsSpeed.remove(p);
				removeChild(p, true);				
			}			
		}
		if(pointsDisplay && System.currentTimeMillis() > powerupPointsDisplayTime + 5000)
		{
			pointsDisplay = false;
			for(CCSprite p : powerupsPoints)
			{
				powerupsPoints.remove(p);
				removeChild(p, true);				
			}			
		}
		if(wallsDisplay && System.currentTimeMillis() > powerupWallsDisplayTime + 5000)
		{
			wallsDisplay = false;
			for(CCSprite p : powerupsWalls)
			{
				powerupsWalls.remove(p);
				removeChild(p, true);				
			}			
		}
		if(System.currentTimeMillis() < powerupSpeedDisplayTime + 4500)
		{
			if(System.currentTimeMillis() > speedVisibleTime + 500)
			{
				speedVisibleTime = System.currentTimeMillis();
				if(speedVisible)
				{
					powerupsSpeed.get(0).setVisible(false);
					speedVisible = false;
				}
				else
				{
					powerupsSpeed.get(0).setVisible(true);
					speedVisible = true;
				}
			}
			
		}
		if(System.currentTimeMillis() < powerupPointsDisplayTime + 4500)
		{
			if(System.currentTimeMillis() > pointsVisibleTime + 500)
			{
				pointsVisibleTime = System.currentTimeMillis();
				if(pointsVisible)
				{
					powerupsPoints.get(0).setVisible(false);
					pointsVisible = false;
				}
				else
				{
					powerupsPoints.get(0).setVisible(true);
					pointsVisible = true;
				}
			}
			
		}
		if(System.currentTimeMillis() < powerupWallsDisplayTime + 4500)
		{
			if(System.currentTimeMillis() > wallsVisibleTime + 500)
			{
				wallsVisibleTime = System.currentTimeMillis();
				if(wallsVisible)
				{
					powerupsWalls.get(0).setVisible(false);
					wallsVisible = false;
				}
				else
				{
					powerupsWalls.get(0).setVisible(true);
					wallsVisible = true;
				}
			}
			
		}
		
		
		
		CGSize winSize = CCDirector.sharedDirector().displaySize();
		int maxY = (int)(winSize.height);
		int maxX = (int)(winSize.width);
		
		if(playerX >= maxX || playerX <= 0 || playerY >= maxY || playerY <= 0)
		{
			gameOver(applesEaten);
			CCDirector.sharedDirector().replaceScene(GameOverLayer.scene("Game Over! Score:" + applesEaten + 
					"\n Tap Screen Twice to Play Again"));
			
		}
		if(playerMoving == NO_MOVE)
		{
			if(nextMove == MOVING_LEFT)
			{
				playerMoving = MOVING_LEFT;
				player.setPosition(playerX - currentSpeed, playerY);
				nextMove = NO_MOVE;
			}
			else if(nextMove == MOVING_RIGHT)
			{
				playerMoving = MOVING_RIGHT;
				player.setPosition(playerX + currentSpeed, playerY);
				nextMove = NO_MOVE;
			}
			else if(nextMove == MOVING_UP)
			{
				playerMoving = MOVING_UP;
				player.setPosition(playerX, playerY + currentSpeed);
				nextMove = NO_MOVE;
			}
			else if(nextMove == MOVING_DOWN)
			{
				playerMoving = MOVING_DOWN;
				player.setPosition(playerX, playerY - currentSpeed);
				nextMove = NO_MOVE;
			}
		}		
		else if(playerMoving == MOVING_UP)
		{
			if(nextMove == MOVING_LEFT)
			{
				playerMoving = MOVING_LEFT;
				player.setPosition(playerX - currentSpeed, playerY);
				nextMove = NO_MOVE;
			}
			else if(nextMove == MOVING_RIGHT)
			{
				playerMoving = MOVING_RIGHT;
				player.setPosition(playerX + currentSpeed, playerY);
				nextMove = NO_MOVE;
			}
			else
			{
				player.setPosition(playerX, playerY + currentSpeed);
			}
		}
		else if(playerMoving == MOVING_DOWN)
		{
			if(nextMove == MOVING_LEFT)
			{
				playerMoving = MOVING_LEFT;
				player.setPosition(playerX - currentSpeed, playerY);
				nextMove = NO_MOVE;
			}
			else if(nextMove == MOVING_RIGHT)
			{
				playerMoving = MOVING_RIGHT;
				player.setPosition(playerX + currentSpeed, playerY);
				nextMove = NO_MOVE;
			}
			else
			{
				player.setPosition(playerX, playerY - currentSpeed);
			}
		}
		else if(playerMoving == MOVING_LEFT)
		{
			if(nextMove == MOVING_UP)
			{
				playerMoving = MOVING_UP;
				player.setPosition(playerX, playerY + currentSpeed);
				nextMove = NO_MOVE;
			}
			else if(nextMove == MOVING_DOWN)
			{
				playerMoving = MOVING_DOWN;
				player.setPosition(playerX, playerY - currentSpeed);
				nextMove = NO_MOVE;
			}
			else
			{
				player.setPosition(playerX - currentSpeed, playerY);
			}
		}
		else if(playerMoving == MOVING_RIGHT)
		{
			if(nextMove == MOVING_UP)
			{
				playerMoving = MOVING_UP;
				player.setPosition(playerX, playerY + currentSpeed);
				nextMove = NO_MOVE;
			}
			else if(nextMove == MOVING_DOWN)
			{
				playerMoving = MOVING_DOWN;
				player.setPosition(playerX, playerY - currentSpeed);
				nextMove = NO_MOVE;
			}
			else
			{
				player.setPosition(playerX + currentSpeed, playerY);
			}
		}
		else if(nextMove == NO_MOVE)
		{
			if(playerMoving == MOVING_UP)
			{
				player.setPosition(playerX, playerY + currentSpeed);
			}
			else if(playerMoving == MOVING_DOWN)
			{
				player.setPosition(playerX, playerY - currentSpeed);
			}
			else if(playerMoving == MOVING_LEFT)
			{
				player.setPosition(playerX - currentSpeed, playerY);
			}
			else if(playerMoving == MOVING_RIGHT)
			{
				player.setPosition(playerX + currentSpeed, playerY);
			}
		}
		
		CGRect playerRect = CGRect.make(player.getPosition().x - (player.getContentSize().width),
				player.getPosition().y - (player.getContentSize().height),
				player.getContentSize().width,
				player.getContentSize().height);
		
		ArrayList<CCSprite> powerupsToDelete = new ArrayList<CCSprite>();
			
		if (CGRect.intersects(playerRect, appleRect))
		{
			
			removeChild(apples.get(0), true);
			apples.remove(apples.get(0));
			applesEaten++;
			intersected = true;
		}
		
		for (CGRect wall : wallRects)
		{
			
			if (CGRect.intersects(playerRect, wall))
			{
				gameOver(applesEaten);
				CCDirector.sharedDirector().replaceScene(GameOverLayer.scene("Game Over! Score:" + applesEaten + 
						"\n" + "Tap Screen Twice to Play Again"));
				
				intersected = true;
			}
				
		}
		
		for (CCSprite powerup : powerupsSpeed)
		{
			CGRect targetRect = CGRect.make(powerup.getPosition().x - (powerup.getContentSize().width),
											powerup.getPosition().y - (powerup.getContentSize().height),
											powerup.getContentSize().width,
											powerup.getContentSize().height);
			
			if (CGRect.intersects(playerRect, targetRect))
			{
				switch(difficulty)
				{
				case DIFFICULTY_EASY: currentSpeed = SPEED_FAST;
					break;
				case DIFFICULTY_MEDIUM: currentSpeed = 3;
					break;
				case DIFFICULTY_HARD: currentSpeed = 7;
					break;
				case DIFFICULTY_KING_COBRA: currentSpeed = 2;
					break;
				}
				time = System.currentTimeMillis();
				timeRunning = true;
				powerupsToDelete.add(powerup);
			}
				
		}
		
		for (CCSprite powerup : powerupsPoints)
		{
			CGRect targetRect = CGRect.make(powerup.getPosition().x - (powerup.getContentSize().width),
											powerup.getPosition().y - (powerup.getContentSize().height),
											powerup.getContentSize().width,
											powerup.getContentSize().height);
			
			if (CGRect.intersects(playerRect, targetRect))
			{
				applesEaten += 4;
				powerupsPoints.remove(powerup);
				removeChild(powerup, true);
			}
				
		}
		
		for (CCSprite powerup : powerupsWalls)
		{
			CGRect targetRect = CGRect.make(powerup.getPosition().x - (powerup.getContentSize().width),
											powerup.getPosition().y - (powerup.getContentSize().height),
											powerup.getContentSize().width,
											powerup.getContentSize().height);
			
			if (CGRect.intersects(playerRect, targetRect))
			{
				//Delete 5 walls
				for(int i=0; i<5; i++)
				{
					if(i < walls.size())
					{
						removeChild(walls.get(i), true);
						wallRects.remove(i);
						walls.remove(i);
					}
					
				}
				powerupsWalls.remove(powerup);
				removeChild(powerup, true);
				
			}

				
		}
		for (CCSprite powerup : powerupsToDelete)
		{
			powerupsSpeed.remove(powerup);
			removeChild(powerup, true);
			
		}		
		if(intersected)
		{
			addTarget();
			
		}
	}
	
	private void gameOver(int applesEaten)
	{
		SoundEngine.sharedEngine().pauseSound();
		HighScoreHelper hsHelper = new HighScoreHelper(context);
		if(applesEaten > 0)
		{
			switch(difficulty)
			{
			case DIFFICULTY_EASY: hsHelper.addEntry(initial, applesEaten, "EASY");
				break;
			case DIFFICULTY_MEDIUM: hsHelper.addEntry(initial, applesEaten, "MEDIUM");
				break;
			case DIFFICULTY_HARD: hsHelper.addEntry(initial, applesEaten, "HARD");
				break;
			case DIFFICULTY_KING_COBRA: hsHelper.addEntry(initial, applesEaten, "KING COBRA");
				break;		
			}
			
		}
	}
	protected void addTarget()
	{
		Random rand = new Random();
		CCSprite target = CCSprite.sprite("apple.png");
		CCSprite wall = CCSprite.sprite("wall.png");
		
		// Determine where to spawn the target along the Y axis
		CGSize winSize = CCDirector.sharedDirector().displaySize();
		int minY = (int)(target.getContentSize().height);
		int maxY = (int)(winSize.height - target.getContentSize().height);
		int rangeY = maxY - minY;
		
		int minX = (int)(target.getContentSize().width);
		int maxX = (int)(winSize.width - target.getContentSize().width);
		int rangeX = maxX - minX;
		
		wall.setPosition(rand.nextInt(rangeX)+ minX, rand.nextInt(rangeY) + minY);
		CGRect wallRect = CGRect.make(wall.getPosition().x - (wall.getContentSize().width),
				wall.getPosition().y - (wall.getContentSize().height),
				wall.getContentSize().width,
				wall.getContentSize().height);
		wallRects.add(wallRect);
		addChild(wall);
	
		boolean foundSpot = false;
		int actualX = 0, actualY = 0;
		while(!foundSpot)
		{
			actualX = rand.nextInt(rangeX) + minX;
			actualY = rand.nextInt(rangeY) + minY;
			boolean conflicts = false;
			
			target.setPosition(actualX, actualY);
			appleRect = CGRect.make(target.getPosition().x - (target.getContentSize().width),
					target.getPosition().y - (target.getContentSize().height),
					target.getContentSize().width,
					target.getContentSize().height);
			
			for (CGRect wallArr : wallRects)
			{
				if (CGRect.intersects(appleRect, wallArr))
				{
					conflicts = true;
					break;
				}
					
			}
			if(!conflicts)
			{
				foundSpot = true;
			}
		}
		

		addChild(target);
		
		target.setTag(1);
		wall.setTag(1);
		walls.add(wall);
		apples.add(target);	
		
		int randNum = rand.nextInt(20);
		if(randNum == SPEED_POWERUP)
		{
			if(powerupsSpeed.size() < 1)
			{
				CCSprite speedPowerup = CCSprite.sprite("apple_speed.png");
				speedPowerup.setPosition(rand.nextInt(rangeX)+ minX, rand.nextInt(rangeY) + minY);
				addChild(speedPowerup);
				speedPowerup.setTag(1);
				powerupsSpeed.add(speedPowerup);	
				powerupSpeedDisplayTime = System.currentTimeMillis();
				speedDisplay = true;
				speedVisibleTime = System.currentTimeMillis();
			}
			
		}
		else if(randNum == POINT_POWERUP)
		{
			if(powerupsPoints.size() < 1)
			{
				CCSprite pointsPowerup = CCSprite.sprite("apple_points.png");
				pointsPowerup.setPosition(rand.nextInt(rangeX)+ minX, rand.nextInt(rangeY) + minY);
				addChild(pointsPowerup);
				pointsPowerup.setTag(1);
				powerupsPoints.add(pointsPowerup);	
				powerupPointsDisplayTime = System.currentTimeMillis();
				pointsDisplay = true;
				pointsVisibleTime = System.currentTimeMillis();
			}
		}
		else if(randNum == WALL_POWERUP)
		{
			if(powerupsWalls.size() < 1)
			{
				CCSprite wallPowerup = CCSprite.sprite("delete_wall.png");
				wallPowerup.setPosition(rand.nextInt(rangeX)+ minX, rand.nextInt(rangeY) + minY);
				addChild(wallPowerup);
				wallPowerup.setTag(1);
				powerupsWalls.add(wallPowerup);	
				powerupWallsDisplayTime = System.currentTimeMillis();
				wallsDisplay = true;
				wallsVisibleTime = System.currentTimeMillis();
			}
		}

	}
	
	protected static int getApplesEaten()
	{
		return applesEaten;
	}
	
	protected static void setApplesEaten(int ae)
	{
		applesEaten = ae;
	}
	
}

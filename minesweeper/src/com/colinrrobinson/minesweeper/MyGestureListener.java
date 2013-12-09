package com.colinrrobinson.minesweeper;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;

public class MyGestureListener implements GestureListener {

	boolean longPressStatus = false;
	
	Minesweeper game;
	
	public void setGame(Minesweeper g) {
		this.game = g;
	}
	
	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		longPressStatus = false;
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		if ( !longPressStatus )
			game.processTouch();
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		System.out.println("long press");
		longPressStatus = true;
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
			Vector2 pointer1, Vector2 pointer2) {
		// TODO Auto-generated method stub
		return false;
	}

}

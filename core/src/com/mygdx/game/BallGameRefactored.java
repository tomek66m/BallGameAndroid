package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;

public class BallGameRefactored extends ApplicationAdapter {
	GameEngine _gameEngine;
	//AssetManager _assetManager;

	
	@Override
	public void create () {

		// init game engine
		_gameEngine = new GameEngine(0.0f, 0.0f, 10.0f,
				0.0f, 0.0f, 10.0f,
				1.0f, 300.0f);

		// init assets loading
		_gameEngine.loadAssets("ball05scale30ZupXforwardobj.obj",
				"track30ZupXforwardobj.obj",
				"box04scaleZupXforwardobj.obj",
				"testSkyboxSwamp.jpg",
				"arial.fnt"
		);

		_gameEngine.getmAssetManager().finishLoading();

		_gameEngine.setmGameState(GameState.DONELOADING);

		//
		GameEngine.mInitalBallPosition = new Vector3(0.0f, 0.9f, 0.0f);
		_gameEngine.initObjects(
				new Vector3(0.0f, 0.0f, 0.0f), // first path position
				20,
				new Vector3(0.0f, 0.0f, 10.0f), // first box position
				80);
		_gameEngine.setmGameState(GameState.PLAYING);
		//
	}

	@Override
	public void render () {

		// INPUT
		if(_gameEngine.getmGameState() == GameState.PLAYING)
			_gameEngine.updateForPlaying();

		else if(_gameEngine.getmGameState() == GameState.GAMEOVER)
			_gameEngine.updateForGameOver();


		// DRAW
		_gameEngine.draw();
	}
	
	@Override
	public void dispose ()
	{
		//_assetManager.dispose();
	}
}

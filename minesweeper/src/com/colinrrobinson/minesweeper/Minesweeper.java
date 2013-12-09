package com.colinrrobinson.minesweeper;

import java.util.HashMap;
import java.util.Map.Entry;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class Minesweeper extends Game {

	OrthographicCamera camera;
	SpriteBatch batch;
	ShapeRenderer shapeRenderer;

	HashMap<Integer, Rectangle> tiles;
	int[][] tileGrid;
	int[][] mineGrid;
	
	Texture coveredTexture;
	Texture blankTexture;
	Texture flagQuestionTexture;
	Texture flagTexture;
	Texture mineTexture;
	Texture bangTexture;
	Texture oneTexture;
	Texture twoTexture;
	Texture threeTexture;
	
	// tile width and height
	float width = 64;
	float height = 64;
	int grid_x = 10;
	int grid_y = 10;
	
	@Override
	public void create() {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 640, 640);

		batch = new SpriteBatch();

		coveredTexture = new Texture(Gdx.files.internal("covered.png"));
		blankTexture = new Texture(Gdx.files.internal("blank.png"));
		flagQuestionTexture = new Texture(
				Gdx.files.internal("flag_question.png"));
		flagTexture = new Texture(Gdx.files.internal("flag.png"));
		mineTexture = new Texture(Gdx.files.internal("mine.png"));
		bangTexture = new Texture(Gdx.files.internal("bang.png"));

		oneTexture = new Texture(Gdx.files.internal("one.png"));
		twoTexture = new Texture(Gdx.files.internal("two.png"));
		threeTexture = new Texture(Gdx.files.internal("three.png"));
		
		shapeRenderer = new ShapeRenderer();

		tiles = new HashMap<Integer, Rectangle>();
		tileGrid = new int[10][10];
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				initTiles(i, j);
			}
		}
		
		mineGrid = generateMines(10, 10, 12);
		
		MyGestureListener mgl = new MyGestureListener();
		mgl.setGame(this);
		GestureDetector gd = new GestureDetector(mgl);
		Gdx.input.setInputProcessor(gd);
	}

	private void initTiles(int i, int j) {
		Rectangle tile = new Rectangle();
		tile.x = 0 + i * 64;
		tile.y = 0 + j * 64;
		tile.width = 64;
		tile.height = 64;

		Integer size = tiles.size();
		tiles.put(size, tile);
		tileGrid[i][j] = 0;
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0.8f, 0.8f, 0.8f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		camera.update();
		batch.setProjectionMatrix(camera.combined);

		batch.begin();
		for (Entry<Integer, Rectangle> entry : tiles.entrySet()) {
			Integer key = entry.getKey();
			Rectangle tile = entry.getValue();

			int row = (int) key % 10;
			int col = (int) Math.floor(key / 10);
			int state = tileGrid[col][row];

			Texture texture = coveredTexture;
			if (state == 0) {
				texture = coveredTexture;
			} else if (state == 1) {
				texture = blankTexture;
			} else if (state == 2) {
				texture = flagQuestionTexture;
			} else if (state == 3) {
				texture = flagTexture;
			} else if (state == 4) {
				texture = mineTexture;
			} else if (state == 5) {
				texture = bangTexture;
			} else if (state == 6) {
				texture = oneTexture;
			} else if (state == 7) {
				texture = twoTexture;
			} else if (state == 8) {
				texture = threeTexture;
			}
			batch.draw(texture, tile.x, tile.y, 64, 64);
		}
		batch.end();

		drawGrid();

		// process user input
		/*if (Gdx.input.isTouched()) {
			processTouch();
		}*/
	}

	public void processTouch() {
		Vector3 touchPos = new Vector3();
		touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(touchPos);

		int col = (int) Math.floor(touchPos.x / 64);
		int row = (int) Math.floor(touchPos.y / 64);
		
		showTile(col, row);
		if (isWinning()) {
			System.out.println("YOU WIN");
		}
		
		if (isMine(col, row)) {
			System.out.println("LOSER");
			showMines();
			tileGrid[col][row] = 5;
		} else {
			if (isEmpty(col, row)) {
				doOpenNeighbours(col, row);
			} else {
				if (isWinning()) {
					System.out.println("YOU WIN");
				}
			}
		}
		
		printGrid(tileGrid);
		printGrid(mineGrid);
	}

	private void drawGrid() {
		float x;
		float y;
		Gdx.gl10.glLineWidth(4 / camera.zoom); // line width
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(Color.BLACK);
		for (int i = 0; i <= 10; i++) {
			for (int j = 0; j <= 10; j++) {
				x = 0 + i * 64;
				y = 0 + j * 64;
				shapeRenderer.line(x, y, x, y + height);
				shapeRenderer.line(x, y, x + width, y);

			}
		}
		shapeRenderer.end();
	}

	private void printGrid(int[][] grid) {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				System.out.printf("%d ", grid[i][j]);
			}
			System.out.println();
		}
		System.out.println();
	}

	private void showTile(int x, int y) {
		int value = mineGrid[x][y];
		if ( value == 1 ) {
			tileGrid[x][y] = 6;
		} else if ( value == 2 ) {
			tileGrid[x][y] = 7;
		} else if ( value == 3 ) {
			tileGrid[x][y] = 8;
		} else {
			tileGrid[x][y] = 1;
		}
	}
	
	@Override
	public void dispose() {
		batch.dispose();
		blankTexture.dispose();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
	
	/**
	 * taken from http://jsbin.com/usisu3/6/edit
	 */
	
	public int[][] generateMines(int grid_x, int grid_y, int mine_count) {
		
		// make grid and populate with 0s
		int[][] grid = new int[10][10];
		for (int n=0; n<grid_x; n++) {
			for (int m=0; m<grid_y; m++) {
				grid[n][m] = 0;
			}
		}
		
		int mine_value = -(mine_count * 2), mine_x, mine_y;
		
		// Fill the grid with mines and mine information
		for (int k=0; k<mine_count; k++) {
			while (true) {
				mine_x = (int) Math.floor(Math.random()*grid_x);
				mine_y = (int) Math.floor(Math.random()*grid_y);

				// TODO : add more randomness and strategies here

				if (0 <= grid[mine_x][mine_y]) {
					break;
				}
			}
			for (int n=-1; n<2; n++) {
				for (int m=-1; m<2; m++) {
					if (0 == n && 0 == m) {
						grid[mine_x][mine_y] = mine_value;
					} else if (between(mine_x+n,0,grid_x-1) && between(mine_y+m,0,grid_y-1)) {
						grid[mine_x + n][mine_y + m]++;
					}
				}
			}
		}
	    
		return grid;
	}
	
	public boolean between(int v, int a, int b) {
		return (v>=a) && (v<=b);
	}
	
	public boolean isMine(int x, int y) {
		return (0 > mineGrid[x][y]);
	}
	
	public boolean isEmpty(int x, int y) {
		return (0 == mineGrid[x][y]);
	}
	
	public boolean isHidden(int x, int y) {
		return (0 == tileGrid[x][y]);
	}
	
	public boolean isWinning() {
		int hiddenTiles = 0;
		
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 10; y++) {
				if ( !isMine(x,y) && isHidden(x,y) ) {
					hiddenTiles++;
				}
			}
		}
		
		return (0 == hiddenTiles);
	}
	
	int show_neighbour_count = 0;
	public void doOpenNeighbours(int n, int m) {
		if (!isEmpty(n,m))
			return;

	    for (int x=-1; x<2; x++) {
	    	for (int y=-1; y<2; y++) {
	    		if (between(x+n,0,grid_x-1) && between(y+m,0,grid_y-1) && !isMine(x+n,y+m) && isHidden(x+n,y+m) ) {
	    			showTile(x+n, y+m);
	    			show_neighbour_count++;
	    			doOpenNeighbours(x+n, y+m);
	    			if (--show_neighbour_count == 0) {
	    				if (isWinning()) {
	    					// do winning
	    					System.out.println("YOU WIN");
	    				}
	    			}
	    		}
	    	}
	    }
	}
	
	public void showMines() {
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 10; y++) {
				if (isMine(x,y)) {
					tileGrid[x][y] = 4;
				}
			}
		}
	}
}

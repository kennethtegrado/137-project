/***********************************************************
* This GameTimer class is a subclass of the AnimationTimer.
* This is where most of the activity in the game is created.
* It includes spawning, moving and rendering of all the
* entities.
*
* @author Quim Ramos
* @created_date 2022-12-22
*
***********************************************************/

package MainGameStage;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
//import javafx.scene.text.Font;
//import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;

import java.util.ArrayList;
//import java.util.Random;

class GameTimer extends AnimationTimer{
	private GraphicsContext gc;
	private Player player;
	private ArrayList<Bullet> bullet;
	private ArrayList<Wall> wall;
	private ArrayList<Bush> bush;
	private ArrayList<Water> water;
	private ArrayList<Metal> metal;
	private ArrayList<Steel> steel;
	private String currentFacing;
	private StackPane stage;
	private Scene scene;
	private int change;
	private long startChanging;
	private AnimationTimer animationTimer;
	private double bgOffsetX = 0; // Initial X offset for the background image
	private ClientConnection connection;
	private ArrayList<Player> players;

	public static int PLAYER_SIZE = 32;
	public static int SPRITE_SIZE = 35;
	private static boolean fireBullet;

	public final static int START_MAP_WIDTH = 55;
	public final static int START_MAP_HEIGHT = 50;
	public final static int END_MAP_WIDTH = 1141;
	public final static int END_MAP_HEIGHT = 752;
	public final static double BULLET_SPEED = 3;

	private Image game_bg;
	private Image up;
	private Image left;
	private Image down;
	private Image right;
	private String username;

	GameTimer(StackPane stage, Scene scene, GraphicsContext gc, ClientConnection connection) {
		this.game_bg = new Image(getClass().getResourceAsStream("/images/gameBg.png"));
		this.up = new Image(getClass().getResourceAsStream("/images/tank-up.png"), GameTimer.PLAYER_SIZE, GameTimer.PLAYER_SIZE, false, false);
		this.left = new Image(getClass().getResourceAsStream("/images/tank-left.png"), GameTimer.PLAYER_SIZE, GameTimer.PLAYER_SIZE, false, false);
		this.down  = new Image(getClass().getResourceAsStream("/images/tank-down.png"), GameTimer.PLAYER_SIZE, GameTimer.PLAYER_SIZE, false, false);
		this.right = new Image(getClass().getResourceAsStream("/images/tank-right.png"), GameTimer.PLAYER_SIZE, GameTimer.PLAYER_SIZE, false, false);

		this.stage = stage;
		this.gc = gc;
		this.scene = scene;
		this.gc.drawImage(game_bg, 0, 0);
		this.scene.setFill(Color.BLACK);
		this.bullet = new ArrayList<Bullet>();
		this.wall = new ArrayList<Wall>();
		this.bush = new ArrayList<Bush>();
		this.water = new ArrayList<Water>();
		this.metal = new ArrayList<Metal>();
		this.steel = new ArrayList<Steel>();
		this.currentFacing = "up";
		this.change = 1;
		this.prepareActionHandlers();
		// this.player = new Player("Tank");
		this.initializeMap();
		this.connection = connection;
	}

	@Override
	public void handle(long currentNanoTime) {
		this.gc.drawImage(game_bg, 0, 0);

		// render players
		int paddingT = 230;
		int paddingR = 250;
		int paddingL = 1170;
		for (Player player: players) {
			player.render(gc);
			Label infoLabel = new Label(player.getName());
			Label infoLabel2 = new Label("");
			for (int i=1; i<=player.getHealth(); i++) {
				infoLabel2.setText(infoLabel2.getText() + "❤");
			}
			infoLabel.setPadding(new Insets(paddingT, 0, 0, paddingL));
			infoLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
			infoLabel.setTextFill(Color.WHITE);
			infoLabel2.setPadding(new Insets(paddingT, paddingR, 0, 0));
			infoLabel2.setFont(Font.font("Arial", FontWeight.BOLD, 16));
			infoLabel2.setTextFill(Color.RED);
			this.stage.getChildren().addAll(infoLabel, infoLabel2);
			StackPane.setAlignment(infoLabel, Pos.TOP_CENTER);
			StackPane.setAlignment(infoLabel2, Pos.TOP_RIGHT);
			paddingT += 18;
		}

		for (Steel steel: this.steel) {
			steel.render(this.gc);
		}
		
		this.movePlayers();
		
		this.renderMap(currentNanoTime);

		for (Bullet fire: this.bullet) {
			this.moveBullet(fire);
		}
		this.checkBulletPlayerCollision();

		for (Player player: players)
		if (player.getIsAlive() == false) {
			players.remove(player);
		}

		if (players.size() == 1) {
			if (this != null) {
				this.stop();
				this.gameOver();
			}
		}
	}
	
	private void movePlayers() {
		for (Player player: players) {
			this.checkWaterCollision(player);
			this.checkWallCollision(player);
			this.checkMetalCollision(player);
			this.checkSteelCollision(player);
			
				if (player.goLeft) {
					if (player.getXPos() <= GameTimer.END_MAP_WIDTH && player.getXPos() > GameTimer.START_MAP_WIDTH) {
						player.setXPos(player.getXPos() - player.getSpeed());
					}
					player.loadImage(left);
					player.currentFacing = "left";
				} else if (player.goRight) {
						if (player.getXPos()+GameTimer.PLAYER_SIZE < GameTimer.END_MAP_WIDTH && player.getXPos() >= GameTimer.START_MAP_WIDTH) {
							player.setXPos(player.getXPos() + player.getSpeed());
						}
					player.loadImage(right);
					player.currentFacing = "right";
				} else if (player.goUp) {
						if (player.getYPos() <= GameTimer.END_MAP_HEIGHT && player.getYPos() > GameTimer.START_MAP_HEIGHT) {
							player.setYPos(player.getYPos() - player.getSpeed());
						}
					player.loadImage(up);
					player.currentFacing = "up";
				} else if (player.goDown) {
						if (player.getYPos()+GameTimer.PLAYER_SIZE < GameTimer.END_MAP_HEIGHT && player.getYPos() >= GameTimer.START_MAP_HEIGHT) {
							player.setYPos(player.getYPos() + player.getSpeed());
						}
					player.loadImage(down);
					player.currentFacing = "down";
				}
			
			player.render(this.gc);
		}
	}

	private void drawGameOver(GraphicsContext gc) {
		// Use an AnimationTimer to continuously redraw the background
		gc.clearRect(0, 0, Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT);

		// Draw background image
		Image bg = new Image(getClass().getResourceAsStream("/images/gameOverScreen.gif"), 1500, 800, false, false);

		// Calculate the new offset based on time or player position
		// For example, you can use time to make it scroll automatically
		bgOffsetX -= 1; // Adjust the scrolling speed as needed
		
		// Draw the background image twice to create the scrolling effect
		gc.drawImage(bg, bgOffsetX, 0);
		gc.drawImage(bg, bgOffsetX + bg.getWidth(), 0);

		// If the first image is out of view, reset the offset
		if (bgOffsetX <= -bg.getWidth()) {
				bgOffsetX = 0;
		}
	}

	private void gameOver() {
		stage.getChildren().remove(1);
    animationTimer = new AnimationTimer() {
			@Override
			public void handle(long currentNanoTime) {
					drawGameOver(gc);
			}
		};
		animationTimer.start();
	}

	void initializeMap() {
		boolean isAlternateX = true;
		boolean isAlternateY = true;
		int a = 0;
		for (int i=GameTimer.START_MAP_WIDTH; i+GameTimer.SPRITE_SIZE < GameTimer.END_MAP_WIDTH; i = i + GameTimer.SPRITE_SIZE) {
			if (i == GameTimer.START_MAP_WIDTH || i+GameTimer.SPRITE_SIZE*2 > GameTimer.END_MAP_WIDTH) {
				for (int j=GameTimer.START_MAP_HEIGHT; j+GameTimer.SPRITE_SIZE < GameTimer.END_MAP_HEIGHT; j = j + GameTimer.SPRITE_SIZE) {
					//if (j == GameTimer.START_MAP_HEIGHT || j+GameTimer.SPRITE_SIZE*2 > GameTimer.END_MAP_HEIGHT) {
						Bush newBush = new Bush(i, j);
						this.bush.add(newBush);
					//}
				}
				continue;
			}
			if (isAlternateX) {
				for (int j=GameTimer.START_MAP_HEIGHT+GameTimer.SPRITE_SIZE; j+GameTimer.SPRITE_SIZE*2 < GameTimer.END_MAP_HEIGHT; j = j + GameTimer.SPRITE_SIZE) {
					if (isAlternateY) {
						if (a == 0) {
							Water newWater = new Water(i, j);
							this.water.add(newWater);
							a = 1;
						} else if (a == 1) {
							Wall newWall = new Wall(i, j);
							this.wall.add(newWall);
							a = 2;
						} else if (a == 2) {
							Metal newMetal = new Metal(i, j);
							this.metal.add(newMetal);
							a = 0; 
						}
					}
					isAlternateY = !isAlternateY;
				}
			}

			Bush newBush1 = new Bush(i, 715);
			this.bush.add(newBush1);

			Bush newBush2 = new Bush(i, GameTimer.START_MAP_HEIGHT);
			this.bush.add(newBush2);
			isAlternateX = !isAlternateX;
		}
	}

	void renderMap(long currentNanoTime) {
		for (int i = 0; i < this.wall.size(); i++) {
			Wall wall = this.wall.get(i);
			if (wall.getHealth() > 0) {
				wall.render(this.gc);
			} else {
				this.wall.remove(i);
			}
		}

		for (Bush bush: this.bush) {
			bush.render(this.gc, this.change);
		}
		
		for (Water water: this.water) {
			water.render(this.gc, this.change);
		}

		double spawnElapsedTime = (currentNanoTime - this.startChanging) / 1000000000.0;
		if(spawnElapsedTime > 1.5) {
			this.change = 2;
			this.startChanging = System.nanoTime();
		} else if (spawnElapsedTime > 1) {
			this.change = 3;
		} else if (spawnElapsedTime > 0.5) {
			this.change = 1;
		}

		for (Water water: this.water) {
			water.render(this.gc, this.change);
		}

		for (Metal metal: this.metal) {
			metal.render(this.gc);
		}
	}

	void checkWallCollision(Player player) {
		for (Wall wall: this.wall) {
			if (player.currentFacing == "up") {
				boolean hasCollisionX = (player.getXPos() > wall.getXPos() 
																&& player.getXPos() < wall.getXPos()+GameTimer.PLAYER_SIZE)
																|| (player.getXPos()+GameTimer.PLAYER_SIZE > wall.getXPos()
																&& player.getXPos()+GameTimer.PLAYER_SIZE < wall.getXPos()+GameTimer.PLAYER_SIZE);
				boolean hasCollisionY = (player.getYPos() < wall.getYPos()+GameTimer.PLAYER_SIZE
																&& player.getYPos()+GameTimer.PLAYER_SIZE > wall.getYPos()+GameTimer.PLAYER_SIZE);
				if (hasCollisionY && hasCollisionX) {
					player.goUp = false;
					player.setYPos(player.getYPos()+1);
				}
			} else if (player.currentFacing == "down") {
				boolean hasCollisionX = (player.getXPos() > wall.getXPos() 
																&& player.getXPos() < wall.getXPos()+GameTimer.PLAYER_SIZE)
																|| (player.getXPos()+GameTimer.PLAYER_SIZE > wall.getXPos()
																&& player.getXPos()+GameTimer.PLAYER_SIZE < wall.getXPos()+GameTimer.PLAYER_SIZE);
				boolean hasCollisionY = (player.getYPos()+GameTimer.PLAYER_SIZE > wall.getYPos()
																&& player.getYPos() < wall.getYPos());
				if (hasCollisionY && hasCollisionX) {
					player.goDown = false;
					player.setYPos(player.getYPos()-1);
				}
			} else if (player.currentFacing == "left") {
				boolean hasCollisionY = (player.getYPos() > wall.getYPos() 
																&& player.getYPos() <=wall.getYPos()+GameTimer.PLAYER_SIZE)
																|| (player.getYPos()+GameTimer.PLAYER_SIZE > wall.getYPos()
																&& player.getYPos()+GameTimer.PLAYER_SIZE < wall.getYPos()+GameTimer.PLAYER_SIZE);
				boolean hasCollisionX = (player.getXPos() < wall.getXPos()+GameTimer.PLAYER_SIZE
																&& player.getXPos()+GameTimer.PLAYER_SIZE > wall.getXPos()+GameTimer.PLAYER_SIZE);
				if (hasCollisionX && hasCollisionY) {
					player.goLeft = false;
					player.setXPos(player.getXPos()+2);
				}
			} else if (player.currentFacing == "right") {
				boolean hasCollisionY = (player.getYPos() > wall.getYPos() 
																&& player.getYPos() < wall.getYPos()+GameTimer.PLAYER_SIZE)
																|| (player.getYPos()+GameTimer.PLAYER_SIZE > wall.getYPos()
																&& player.getYPos()+GameTimer.PLAYER_SIZE < wall.getYPos()+GameTimer.PLAYER_SIZE);
				boolean hasCollisionX = (player.getXPos()+GameTimer.PLAYER_SIZE > wall.getXPos()
																&& player.getXPos() < wall.getXPos());
				if (hasCollisionY && hasCollisionX) {
					player.goRight = false;
					player.setXPos(player.getXPos()-1);
				}
			}

			for (int i = 0; i < this.bullet.size(); i++) {
				Bullet bullet = this.bullet.get(i);
				if (wall.collidesWith(bullet)) {
					this.bullet.remove(i);
					wall.setHealth();
					break;
				}
			}
		}
	}

	void checkWaterCollision(Player player) {
		for (Water water: this.water) {
			if (player.currentFacing == "up") {
				boolean hasCollisionX = (player.getXPos() > water.getXPos() 
																&& player.getXPos() < water.getXPos()+GameTimer.PLAYER_SIZE)
																|| (player.getXPos()+GameTimer.PLAYER_SIZE > water.getXPos()
																&& player.getXPos()+GameTimer.PLAYER_SIZE < water.getXPos()+GameTimer.PLAYER_SIZE);
				boolean hasCollisionY = (player.getYPos() < water.getYPos()+GameTimer.PLAYER_SIZE
																&& player.getYPos()+GameTimer.PLAYER_SIZE > water.getYPos()+GameTimer.PLAYER_SIZE);
				if (hasCollisionY && hasCollisionX) {
					player.goUp = false;
					player.setYPos(player.getYPos()+1);
				}
			} else if (player.currentFacing == "down") {
				boolean hasCollisionX = (player.getXPos() > water.getXPos() 
																&& player.getXPos() < water.getXPos()+GameTimer.PLAYER_SIZE)
																|| (player.getXPos()+GameTimer.PLAYER_SIZE > water.getXPos()
																&& player.getXPos()+GameTimer.PLAYER_SIZE < water.getXPos()+GameTimer.PLAYER_SIZE);
				boolean hasCollisionY = (player.getYPos()+GameTimer.PLAYER_SIZE > water.getYPos()
																&& player.getYPos() < water.getYPos());
				if (hasCollisionY && hasCollisionX) {
					player.goDown = false;
					player.setYPos(player.getYPos()-1);
				}
			} else if (player.currentFacing == "left") {
				boolean hasCollisionY = (player.getYPos() > water.getYPos() 
																&& player.getYPos() < water.getYPos()+GameTimer.PLAYER_SIZE)
																|| (player.getYPos()+GameTimer.PLAYER_SIZE > water.getYPos()
																&& player.getYPos()+GameTimer.PLAYER_SIZE < water.getYPos()+GameTimer.PLAYER_SIZE);
				boolean hasCollisionX = (player.getXPos() < water.getXPos()+GameTimer.PLAYER_SIZE
																&& player.getXPos()+GameTimer.PLAYER_SIZE > water.getXPos()+GameTimer.PLAYER_SIZE);
				if (hasCollisionX && hasCollisionY) {
					player.goLeft = false;
					player.setXPos(player.getXPos()+2);
				}
			} else if (player.currentFacing == "right") {
				boolean hasCollisionY = (player.getYPos() > water.getYPos() 
																&& player.getYPos() < water.getYPos()+GameTimer.PLAYER_SIZE)
																|| (player.getYPos()+GameTimer.PLAYER_SIZE > water.getYPos()
																&& player.getYPos()+GameTimer.PLAYER_SIZE < water.getYPos()+GameTimer.PLAYER_SIZE);
				boolean hasCollisionX = (player.getXPos()+GameTimer.PLAYER_SIZE > water.getXPos()
																&& player.getXPos() < water.getXPos());
				if (hasCollisionY && hasCollisionX) {
					player.goRight = false;
					player.setXPos(player.getXPos()-1);
				}
			}
		}
	}

	void checkMetalCollision(Player player) {
		for (Metal metal: this.metal) {
			if (player.currentFacing == "up") {
				boolean hasCollisionX = (player.getXPos() > metal.getXPos() 
																&& player.getXPos() < metal.getXPos()+GameTimer.PLAYER_SIZE)
																|| (player.getXPos()+GameTimer.PLAYER_SIZE > metal.getXPos()
																&& player.getXPos()+GameTimer.PLAYER_SIZE < metal.getXPos()+GameTimer.PLAYER_SIZE);
				boolean hasCollisionY = (player.getYPos() < metal.getYPos()+GameTimer.PLAYER_SIZE
																&& player.getYPos()+GameTimer.PLAYER_SIZE > metal.getYPos()+GameTimer.PLAYER_SIZE);
				if (hasCollisionY && hasCollisionX) {
					player.goUp = false;
					player.setYPos(player.getYPos()+1);
				}
			} else if (player.currentFacing == "down") {
				boolean hasCollisionX = (player.getXPos() > metal.getXPos() 
																&& player.getXPos() < metal.getXPos()+GameTimer.PLAYER_SIZE)
																|| (player.getXPos()+GameTimer.PLAYER_SIZE > metal.getXPos()
																&& player.getXPos()+GameTimer.PLAYER_SIZE < metal.getXPos()+GameTimer.PLAYER_SIZE);
				boolean hasCollisionY = (player.getYPos()+GameTimer.PLAYER_SIZE > metal.getYPos()
																&& player.getYPos() < metal.getYPos());
				if (hasCollisionY && hasCollisionX) {
					player.goDown = false;
					player.setYPos(player.getYPos()-1);
				}
			} else if (player.currentFacing == "left") {
				boolean hasCollisionY = (player.getYPos() > metal.getYPos() 
																&& player.getYPos() < metal.getYPos()+GameTimer.PLAYER_SIZE)
																|| (player.getYPos()+GameTimer.PLAYER_SIZE > metal.getYPos()
																&& player.getYPos()+GameTimer.PLAYER_SIZE < metal.getYPos()+GameTimer.PLAYER_SIZE);
				boolean hasCollisionX = (player.getXPos() < metal.getXPos()+GameTimer.PLAYER_SIZE
																&& player.getXPos()+GameTimer.PLAYER_SIZE > metal.getXPos()+GameTimer.PLAYER_SIZE);
				if (hasCollisionX && hasCollisionY) {
					player.goLeft = false;
					player.setXPos(player.getXPos()+2);
				}
			} else if (player.currentFacing == "right") {
				boolean hasCollisionY = (player.getYPos() > metal.getYPos() 
																&& player.getYPos() < metal.getYPos()+GameTimer.PLAYER_SIZE)
																|| (player.getYPos()+GameTimer.PLAYER_SIZE > metal.getYPos()
																&& player.getYPos()+GameTimer.PLAYER_SIZE < metal.getYPos()+GameTimer.PLAYER_SIZE);
				boolean hasCollisionX = (player.getXPos()+GameTimer.PLAYER_SIZE > metal.getXPos()
																&& player.getXPos() < metal.getXPos());
				if (hasCollisionY && hasCollisionX) {
					player.goRight = false;
					player.setXPos(player.getXPos()-1);
				}
			}

			for (int i = 0; i < this.bullet.size(); i++) {
				Bullet bullet = this.bullet.get(i);
				if (metal.collidesWith(bullet)) {
					this.bullet.remove(i);
					break;
				}
			}
		}
	}

	void checkSteelCollision(Player player) {
		for (Steel steel: this.steel) {
			if (steel.collidesWith(player)) {
				player.setSpeed(2);
				break;
			} else {
				player.setSpeed(1);
			}
		}
	}

	void checkBulletPlayerCollision() {
		for (int i = 0; i < this.bullet.size(); i++) {
			Bullet bullet = this.bullet.get(i);
			for (Player player: players)
			if (player.collidesWith(bullet)) {
				this.bullet.remove(i);
				player.setHealth();
				player.setIsAlive();
				break;
			}
		}
	}

	public void handleKeyPress(String username, String code, double d, double e) {
		Player player = null;

		for (Player x: players) 
			if (x.getName().equals(username)) player = x;
		player.setXPos(d);
		player.setYPos(e);
		if(code.equals("A")) {
			player.goLeft = true;
		}else if(code.equals("D")) {
			player.goRight = true;
		}else if(code.equals("W")) {
			player.goUp = true;
		}else if(code.equals("S")) {
			player.goDown = true;
		}else if(code.equals("SPACE")) {
			player.isFiring = true;
			fireBullet(player);
		}
	}

	public void handleKeyRelease(String username, String code, double d, double e) {
		Player player = null;

		for (Player x: players) 
			if (x.getName().equals(username)) player = x;

		player.setXPos(d);
		player.setYPos(e);
		if(code.equals("A")) {
			player.goLeft = false;
		}else if(code.equals("D")) {
			player.goRight = false;
		}else if(code.equals("W")) {
			player.goUp = false;
		}else if(code.equals("S")) {
			player.goDown = false;	
		}else if(code.equals("SPACE")) {
			player.isFiring = false;
		}
	}

	private void prepareActionHandlers() {	// method for the player controls
		// Duration firingInterval = Duration.millis(500);
		// 	Timeline firing = new Timeline(
		// 		new KeyFrame(Duration.ZERO, event -> fireBullet()),
		// 		new KeyFrame(firingInterval));
		// 	firing.setCycleCount(Animation.INDEFINITE);

    	this.scene.setOnKeyPressed(new EventHandler<KeyEvent>()
        {
            public void handle(KeyEvent e)
            {
                String code = e.getCode().toString();
				if (code.equals("A") || code.equals("S") || code.equals("W") || code.equals("D") || code.equals("SPACE")) {
					connection.pressKey(code + " " + player.getXPos() + " " + player.getYPos());
				}
            }
        });
    	this.scene.setOnKeyReleased(new EventHandler<KeyEvent>()
        {
            public void handle(KeyEvent e)
            {
                String code = e.getCode().toString();
				
				if (code.equals("A") || code.equals("S") || code.equals("W") || code.equals("D") || code.equals("SPACE"))
					connection.releaseKey(code + " " + player.getXPos() + " " + player.getYPos());
            }
        });
    }

	private void fireBullet(Player player) {
		//long currentTime = System.currentTimeMillis();
		//if (currentTime - player.getLastBulletFired() > player.getFireRate()) {
			Bullet fire = new Bullet(0, 0, "");
			if (player.currentFacing == "up") {
				fire.setDirection(player.currentFacing);
				fire.setVisible(true);
				fire.setXPos(player.getXPos()+GameTimer.PLAYER_SIZE/2-1);
				fire.setYPos(player.getYPos()-5);
			} else if (player.currentFacing == "down") {
				fire.setDirection(player.currentFacing);
				fire.setVisible(true);
				fire.setXPos(player.getXPos()+GameTimer.PLAYER_SIZE/2-1);
				fire.setYPos(player.getYPos()+GameTimer.PLAYER_SIZE+2);
			} else if (player.currentFacing == "left") {
				fire.setDirection(player.currentFacing);
				fire.setVisible(true);
				fire.setXPos(player.getXPos()-5);
				fire.setYPos(player.getYPos()+GameTimer.PLAYER_SIZE/2-1);
			} else if (player.currentFacing == "right") {
				fire.setDirection(player.currentFacing);
				fire.setVisible(true);
				fire.setXPos(player.getXPos()+GameTimer.PLAYER_SIZE+2);
				fire.setYPos(player.getYPos()+GameTimer.PLAYER_SIZE/2-1);
			}
			this.bullet.add(fire);
			player.isFiring = false;
		// 	player.setLastBulletFired(currentTime);
    // }
	}

	private void moveBullet(Bullet fire) {
		if (fire.getDirection() == "up") {
			if (fire.getYPos() > GameTimer.START_MAP_HEIGHT) {
				fire.setYPos(fire.getYPos()-GameTimer.BULLET_SPEED);
			} else {
				fire.setVisible(false);
			}
		} else if (fire.getDirection() == "down") {
			if (fire.getYPos() < GameTimer.END_MAP_HEIGHT-GameTimer.BULLET_SPEED*2) {
				fire.setYPos(fire.getYPos()+GameTimer.BULLET_SPEED);
			} else {
				fire.setVisible(false);
			}
		} else if (fire.getDirection() == "left") {
			if (fire.getXPos() > GameTimer.START_MAP_WIDTH) {
				fire.setXPos(fire.getXPos()-GameTimer.BULLET_SPEED);
			} else {
				fire.setVisible(false);
			}
		} else if (fire.getDirection() == "right") {
			if (fire.getXPos() < GameTimer.END_MAP_WIDTH-GameTimer.BULLET_SPEED*2) {
				fire.setXPos(fire.getXPos()+GameTimer.BULLET_SPEED);
			} else {
				fire.setVisible(false);
			}
		}

		if (fire.getVisible()) {
			fire.render(this.gc);
		}
	}


	public void setPlayers(ArrayList<String> userIds) {
		this.players = new ArrayList<>();
		int i = 0;
		Integer []x = {55, 1105, 55, 1105};
		Integer[]y = {50, 50, 715, 715};
		for (String userId: userIds) {
			Player player = new Player(userId, x[i], y[i]);
			if (userId.equals(this.username)) this.player = player;
			players.add(player);
			i++;
		}
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
/***********************************************************
* This Player class is a subclass of Blobs. It instantiates
* the player blob. Most of the methods in this class are
* getters and setters for the other classes to use.
*
* @author Quim Ramos
* @created_date 2022-12-22
*
***********************************************************/

package MainGameStage;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

class Player {
	private String name;
	private double xPos;
	private double yPos;
	private double fireRate;
	private double lastBulletFired;
	private double speed;
	private double health;
	private boolean isAlive;
	private Image player = new Image(getClass().getResourceAsStream("/images/tank-up.png"), GameTimer.PLAYER_SIZE, GameTimer.PLAYER_SIZE, false, false);
	private final static double INITIAL_X = 55;
	private final static double INITIAL_Y = 50;

	Player (String name){
		this.name = name;
		this.xPos = INITIAL_X;
		this.yPos = INITIAL_Y;
		this.fireRate = 800;
		this.lastBulletFired = 0;
		this.speed = 1;
		this.health = 3;
		this.isAlive = true;
	}

	String getName(){
		return this.name;
	}

	double getXPos() {
		return this.xPos;
	}

	double getYPos() {
		return this.yPos;
	}

	Image getPlayer() {
		return this.player;
	}

	void setXPos(double newXPos) {
		this.xPos = newXPos;
	}
	
	void setYPos(double newYPos) {
		this.yPos = newYPos;
	}

	double getFireRate() {
		return this.fireRate;
	}

	void setFireRate(double newFireRate) {
		this.fireRate = newFireRate;
	}

	double getLastBulletFired() {
		return this.lastBulletFired;
	}

	void setLastBulletFired(double newBulletFired) {
		this.lastBulletFired = newBulletFired;
	}

	double getSpeed() {
		return this.speed;
	}

	void setSpeed(double newSpeed) {
		this.speed = newSpeed;
	}

	double getHealth() {
		return this.health;
	}

	void setHealth() {
		this.health--;
	}

	boolean getIsAlive() {
		return this.isAlive;
	}

	void setIsAlive() {
		if (this.health == 0) this.isAlive = false;
	}

	void loadImage(Image newImage) {
		this.player = newImage;
	}

	void render(GraphicsContext gc) {
		gc.drawImage(this.player, this.xPos, this.yPos);
	}

	public Rectangle2D getBounds(){
		return new Rectangle2D(this.xPos - this.player.getHeight()/2, this.yPos - this.player.getHeight()/2, this.player.getWidth(), this.player.getHeight());
	}

	protected boolean collidesWith(Bullet rect2)	{
		Rectangle2D rectangle1 = this.getBounds();
		Rectangle2D rectangle2 = rect2.getBounds();

		return rectangle1.intersects(rectangle2);
	}
}
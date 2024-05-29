package MainGameStage;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Bullet {
	private double xPos;
	private double yPos;
	private Image bullet = new Image(getClass().getResourceAsStream("/images/bullet.png"), 5, 5, false, false);
  private boolean visible;
  private String direction;

	Bullet (double xPos, double yPos, String direction){
		this.xPos = xPos;
		this.yPos = yPos;
    this.visible = false;
    this.direction = direction;
	}

  double getXPos() {
		return this.xPos;
	}

	double getYPos() {
		return this.yPos;
	}

  void setXPos(double newXPos) {
		this.xPos = newXPos;
	}
	
	void setYPos(double newYPos) {
		this.yPos = newYPos;
	}

  boolean getVisible() {
    return this.visible;
  }

  void setVisible(boolean newVisible) {
    this.visible = newVisible;
  }

  String getDirection() {
    return this.direction;
  }

  void setDirection(String newDirection) {
    this.direction = newDirection;
  }

  void render(GraphicsContext gc) {
		gc.drawImage(this.bullet, this.xPos, this.yPos);
	}

  public Rectangle2D getBounds(){
		return new Rectangle2D(this.xPos - this.bullet.getHeight()/2 - 15, this.yPos - this.bullet.getHeight()/2 - 15, this.bullet.getWidth(), this.bullet.getHeight());
	}
}

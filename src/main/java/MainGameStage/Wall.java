package MainGameStage;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Wall {
  private double xPos;
	private double yPos;
  private Image wall = new Image(getClass().getResourceAsStream("/images/wall-brick.png"), GameTimer.SPRITE_SIZE, GameTimer.SPRITE_SIZE, false, false);
  private Image wallHealth4 = new Image(getClass().getResourceAsStream("/images/wall-brick-4.png"), GameTimer.SPRITE_SIZE, GameTimer.SPRITE_SIZE, false, false);
  private Image wallHealth3 = new Image(getClass().getResourceAsStream("/images/wall-brick-3.png"), GameTimer.SPRITE_SIZE, GameTimer.SPRITE_SIZE, false, false);
  private Image wallHealth2 = new Image(getClass().getResourceAsStream("/images/wall-brick-2.png"), GameTimer.SPRITE_SIZE, GameTimer.SPRITE_SIZE, false, false);
  private Image wallHealth1 = new Image(getClass().getResourceAsStream("/images/wall-brick-1.png"), GameTimer.SPRITE_SIZE, GameTimer.SPRITE_SIZE, false, false);
  private boolean visible;
  private int health;

	Wall (double xPos, double yPos){
		this.xPos = xPos;
		this.yPos = yPos;
    this.visible = true;
    this.health = 5;
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

  void loadImage(Image newImage) {
		this.wall = newImage;
	}

  int getHealth () {
    return this.health;
  }

  void setHealth () {
    this.health--;
    switch (this.health) {
      case 4:
        this.loadImage(wallHealth4);
        break;
      case 3:
        this.loadImage(wallHealth3);
        break;
      case 2:
        this.loadImage(wallHealth2);
        break;
      case 1:
        this.loadImage(wallHealth1);
        break;
      default:
        break;
    }
  }

  void render(GraphicsContext gc) {
		gc.drawImage(this.wall, this.xPos, this.yPos);
	}

  private Rectangle2D getBounds(){
		return new Rectangle2D(this.xPos - this.wall.getHeight()/2, this.yPos - this.wall.getHeight()/2, this.wall.getWidth(), this.wall.getHeight());
	}

	protected boolean collidesWith(Bullet rect2)	{
		Rectangle2D rectangle1 = this.getBounds();
		Rectangle2D rectangle2 = rect2.getBounds();
		
		return rectangle1.intersects(rectangle2);
	}
}

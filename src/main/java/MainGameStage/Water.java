package MainGameStage;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Water {
  private double xPos;
	private double yPos;
  private Image water = new Image(getClass().getResourceAsStream("/images/water.png"), GameTimer.SPRITE_SIZE, GameTimer.SPRITE_SIZE, false, false);
  private Image water1 = new Image(getClass().getResourceAsStream("/images/water-1.png"), GameTimer.SPRITE_SIZE, GameTimer.SPRITE_SIZE, false, false);
  private Image water2 = new Image(getClass().getResourceAsStream("/images/water-2.png"), GameTimer.SPRITE_SIZE, GameTimer.SPRITE_SIZE, false, false);

	Water (double xPos, double yPos){
		this.xPos = xPos;
		this.yPos = yPos;
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

  void loadImage(Image newImage) {
		this.water = newImage;
	}

  void render(GraphicsContext gc, int change) {
    if (change == 1) {
      gc.drawImage(this.water, this.xPos, this.yPos);
    } else if (change == 2) {
      gc.drawImage(this.water1, this.xPos, this.yPos);
    } else {
      gc.drawImage(this.water2, this.xPos, this.yPos);
    }
	}
}

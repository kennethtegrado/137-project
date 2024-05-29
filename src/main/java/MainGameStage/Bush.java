package MainGameStage;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Bush {
  private double xPos;
	private double yPos;
  private Image bush = new Image(getClass().getResourceAsStream("/images/bush.png"), GameTimer.SPRITE_SIZE, GameTimer.SPRITE_SIZE, false, false);
  private Image bush1 = new Image(getClass().getResourceAsStream("/images/bush-1.png"), GameTimer.SPRITE_SIZE, GameTimer.SPRITE_SIZE, false, false);
  private Image bush2 = new Image(getClass().getResourceAsStream("/images/bush-2.png"), GameTimer.SPRITE_SIZE, GameTimer.SPRITE_SIZE, false, false);

	Bush (double xPos, double yPos){
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
		this.bush = newImage;
	}

  void render(GraphicsContext gc, int change) {
    if (change == 1) {
      gc.drawImage(this.bush, this.xPos, this.yPos);
    } else if (change == 2) {
      gc.drawImage(this.bush1, this.xPos, this.yPos);
    } else {
      gc.drawImage(this.bush2, this.xPos, this.yPos);
    }
	}
}

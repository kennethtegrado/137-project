package MainGameStage;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Metal {
  private double xPos;
	private double yPos;
    private Image metal = new Image(getClass().getResourceAsStream("/images/metal.png"), GameTimer.SPRITE_SIZE, GameTimer.SPRITE_SIZE, false, false);

	Metal (double xPos, double yPos){
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
		this.metal = newImage;
	}

  void render(GraphicsContext gc) {
		gc.drawImage(this.metal, this.xPos, this.yPos);
	}

  private Rectangle2D getBounds(){
		return new Rectangle2D(this.xPos - this.metal.getHeight()/2, this.yPos - this.metal.getHeight()/2, this.metal.getWidth(), this.metal.getHeight());
	}

	protected boolean collidesWith(Bullet rect2)	{
		Rectangle2D rectangle1 = this.getBounds();
		Rectangle2D rectangle2 = rect2.getBounds();
		
		return rectangle1.intersects(rectangle2);
	}
}

package weapon;

import java.awt.Color;

import display.StdDraw;
import display.Vector2;

/**
 * A projectile is shot by a weapon at a position and 
 * follows the direction provided at a constant speed.
 */
public abstract class Projectile {
	
	protected 		double cSpeed;	// The constant speed
	protected 		double x;		// The X position
	protected 		double y;		// The Y position
	protected final double width;	// The width
	protected final double height;	// The height
	protected 		double xSpeed;	// The current x speed
	protected 		double ySpeed;	// The current y speed
	protected 		Color	color;	// The color 
	protected 		int damage;		// The amount of damage the projectile does
	
	/**
	 * Creates a projectile of the provided dimensions.
	 * @param width of the projectile
	 * @param height of the projectile
	 */
	protected Projectile(double width, double height) {
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Moves the projectile by the time provided.
	 * @param time elapsed time
	 */
	public void step(double time) {
		x += xSpeed*time;
		y += ySpeed*time;
	}
	
	/**
	 * Draws the projectile.
	 */
	public void draw() {
		StdDraw.setPenColor(color);
		StdDraw.filledCircle(x, y, width/2);
		StdDraw.setPenColor(StdDraw.BLACK);
	}

	/**
	 * Sets the position of the projectile. 
	 * @param x X position
	 * @param y Y position
	 */
	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Checks whether the projectile is out of the screen.
	 * @return whether the projectile is out of the screen
	 */
	public boolean isOutOfScreen() {
		return x > 1 || y > 1 || x < 0 || y < 0;
	}
	
	/**
	 * Checks whether the projectile is out of a provided rectangle.
	 * @param xCenter X center of the rectangle
	 * @param yCenter Y center of the rectangle
	 * @param halfWidth half of the width of the rectangle
	 * @param halfHeight half of the height of the rectangle
	 * @return whether the projectile is out of the rectangle
	 */
	public boolean isOutOfRectangle(double xCenter, double yCenter, double halfWidth, double halfHeight) {
		return x < xCenter-halfWidth || x > xCenter+halfWidth || y < yCenter-halfHeight || y > yCenter+halfHeight;
	}
	
	/**
	 * Checks whether the projectile is out of a provided circle.
	 * @param xCenter X center of the circle
	 * @param yCenter Y center of the circle
	 * @param radius the radius of the circle
	 * @return whether the projectile is out of the circle
	 */
	public boolean isOutOfCircle(double xCenter, double yCenter, double radius) {
		return !(Math.sqrt((x-xCenter)*(x-xCenter)+(y-yCenter)*(y-yCenter)) < radius);
	}
	

	/**
	 * Given a location to target, compute the direction of the projectile.
	 * @param targetPos the target to aim at
	 */
	public void computeDirection(Vector2<Double> targetPos) {
		double norm = Math.sqrt(Math.pow((targetPos.getX() - x),2) + Math.pow(targetPos.getY() - y, 2));
		this.xSpeed = (targetPos.getX() - x)/norm;
		this.ySpeed = (targetPos.getY() - y)/norm;
		this.xSpeed *= cSpeed;
		this.ySpeed *= cSpeed;
	}
	
	/**
	 * Gives the damage the projectile does.
	 * @return the damage
	 */
	public int getDamage() {
		return damage;
	}

	/**
	 * @return the x
	 */
	public double getX() {
		return x;
	}


	/**
	 * @return the y
	 */
	public double getY() {
		return y;
	}

	
}

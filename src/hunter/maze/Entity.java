package hunter.maze;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

/**
 * An entity represents any element that appears in the game. The
 * entity is responsible for resolving collisions and movement
 * based on a set of properties defined either by subclass or externally.
 * 
 * Note that doubles are used for positions. This may seem strange
 * given that pixels locations are integers. However, using double means
 * that an entity can move a partial pixel. It doesn't of course mean that
 * they will be display half way through a pixel but allows us not lose
 * accuracy as we move.
 */
public abstract class Entity {
	/** The game in which the entity exists */
	private Game game;
	/** The current x location of this entity */ 
	protected double x;
	/** The current y location of this entity */
	protected double y;
	//The direction the entity is facing
	protected double vector;
	/** The sprite that represents this entity */
	protected Sprite sprite;
	/** The current speed of this entity horizontally (pixels/sec) */
	protected double dx;
	/** The current speed of this entity vertically (pixels/sec) */
	protected double dy;
	/** The rectangle used for this entity during collisions  resolution */
	private Rectangle me = new Rectangle();
	/** The rectangle used for other entities during collision resolution */
	private Rectangle him = new Rectangle();
	
	/**
	 * Construct a entity based on a sprite image and a location.
	 * 
	 * @param ref The reference to the image to be displayed for this entity
 	 * @param x The initial x location of this entity
	 * @param y The initial y location of this entity
	 */
	public Entity(String ref,int x,int y,int vector) {
		this.sprite = SpriteStore.get().getSprite(ref);
		this.x = x;
		this.y = y;
		this.vector = vector;
	}
	
	/**
	 * Request that this entity move itself based on a certain amount
	 * of time passing.
	 * 
	 * @param delta The amount of time that has passed in milliseconds
	 */
	public void move(long delta) {
		// update the location of the entity based on move speeds
		x += (delta * dx) / 1000;
		y += (delta * dy) / 1000;
	}
	
	/* function to change sprite's image
	 * used when player entity moves
	 */
	public void setSpriteImage(String ref) {
		this.sprite = SpriteStore.get().getSprite(ref);
	}
	
	/**
	 * Set the horizontal speed of this entity
	 * 
	 * @param dx The horizontal speed of this entity (pixels/sec)
	 */
	public void setHorizontalMovement(double dx) {
		this.dx = dx;
	}

	/**
	 * Set the vertical speed of this entity
	 * 
	 * @param dx The vertical speed of this entity (pixels/sec)
	 */
	public void setVerticalMovement(double dy) {
		this.dy = dy;
	}
	
	public void setVector(double vector) {
		this.vector = vector;
	}
	
	/**
	 * Get the horizontal speed of this entity
	 * 
	 * @return The horizontal speed of this entity (pixels/sec)
	 */
	public double getHorizontalMovement() {
		return dx;
	}

	/**
	 * Get the vertical speed of this entity
	 * 
	 * @return The vertical speed of this entity (pixels/sec)
	 */
	public double getVerticalMovement() {
		return dy;
	}
	
	public double getVector() {
		return vector;
	}
	
	/**
	 * Draw this entity to the graphics context provided
	 * 
	 * @param g The graphics context on which to draw
	 */
	public void draw(Graphics g) {
		sprite.draw(g,(int) x,(int) y);
	}
	
	/**
	 * Do the logic associated with this entity. This method
	 * will be called periodically based on game events
	 */
	public void doLogic() {
	}
	
	/**
	 * Get the x location of this entity
	 * 
	 * @return The x location of this entity
	 */
	public int getX() {
		return (int) x;
	}

	/**
	 * Get the y location of this entity
	 * 
	 * @return The y location of this entity
	 */
	public int getY() {
		return (int) y;
	}
	
	/**
	 * Check if this entity collides with another.
	 * 
	 * @param other The other entity to check collision against
	 * @return True if the entities collide with each other
	 */
	public boolean collidesWith(Entity other) {
		/*if(other instanceof WallEntity && this instanceof PlayerEntity) {
			System.out.printf("me: %s %s %s %s other: %s %s %s %s\n",(int) x,(int) y,sprite.getWidth(),sprite.getHeight(),(int) other.x,(int) other.y,other.sprite.getWidth(),other.sprite.getHeight());
		}*/
		me.setBounds((int) x,(int) y,sprite.getWidth(),sprite.getHeight());
		him.setBounds((int) other.x,(int) other.y,other.sprite.getWidth(),other.sprite.getHeight());

		return me.intersects(him);
	}
	public boolean collidesWith(Wall other) {
		/*if(other instanceof WallEntity && this instanceof PlayerEntity) {
			System.out.printf("me: %s %s %s %s other: %s %s %s %s\n",(int) x,(int) y,sprite.getWidth(),sprite.getHeight(),(int) other.x,(int) other.y,other.sprite.getWidth(),other.sprite.getHeight());
		}*/
		me.setBounds((int) x,(int) y,sprite.getWidth(),sprite.getHeight());
		him.setBounds((int) other.x,(int) other.y,other.sprite.getWidth(),other.sprite.getHeight());

		return me.intersects(him);
	}
	
	/* logic to see if movement would collide entities
	** first used to reset player movement if moving away from a wall
	*/
	public boolean wouldCollideWith(Wall other) {
		me.setBounds((int) x+(int)dx,(int) y+(int)dy,sprite.getWidth(),sprite.getHeight());
		him.setBounds((int) other.x,(int) other.y,other.sprite.getWidth(),other.sprite.getHeight());

		return me.intersects(him);
	}
	
	/**
	 * Notification that this entity collided with another.
	 * 
	 * @param other The entity with which this entity collided.
	 */
	public abstract void collidedWith(Entity other);
}
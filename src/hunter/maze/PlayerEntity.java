package hunter.maze;

import hunter.maze.AlienEntity;
import hunter.maze.Entity;
import hunter.maze.Game;

import java.awt.Rectangle;
import java.util.ArrayList;

/**The entity that represents the players ship**/
public class PlayerEntity extends Entity {
	/** The game in which the ship exists */
	private Game game;
	/** The rectangle used for this entity during collisions  resolution */
	private Rectangle me = new Rectangle();
	/** The rectangle used for other entities during collision resolution */
	private Rectangle him = new Rectangle();
	double playerx,playery;
	
	/**
	 * Create a new entity to represent the players ship
	 *  
	 * @param game The game in which the ship is being created
	 * @param ref The reference to the sprite to show for the ship
	 * @param x The initial x location of the player's ship
	 * @param y The initial y location of the player's ship
	 */
	public PlayerEntity(Game game,String ref,int x,int y,int vector) {
		super(ref,x,y,vector);
		this.game = game;
	}
	
	/**
	 * Request that the ship move itself based on an elapsed amount of
	 * time
	 * 
	 * @param delta The time that has elapsed since last move (ms)
	 */
	public void move(long delta) {

		// check walls to make sure player's movement wont intersect wall
		ArrayList thewalls = game.getWalls();
		for(int i=0; i<thewalls.size();i++) {
			Wall other = (Wall)thewalls.get(i);
			
			playerx = x + ((delta * dx) / 1000);
			playery = y + ((delta * dy) / 1000);
			
			me.setBounds((int)playerx,(int)playery,sprite.getWidth(),sprite.getHeight());
			him.setBounds((int)other.x,(int)other.y,other.sprite.getWidth(),other.sprite.getHeight());

			if(me.intersects(him)){
				System.out.println("movement declined, player intersected wall");
				return;
			}
			
		}
		
		// if we're moving left and have reached the left hand side
		// of the screen, don't move
		if ((dx < 0) && (x < 10)) {
			return;
		}
		// if we're moving right and have reached the right hand side
		// of the screen, don't move 768,640
		if ((dx > 0) && (x > 736)) {
			return;
		}

		if ((dy < 0) && (y < 10)) {
			return;
		}

		if ((dy > 0) && (y > 608)) {
			return;
		}
		
		super.move(delta);
		//super.move(1);
	}
	
	/**
	 * Notification that the player's ship has collided with something
	 * 
	 * @param other The entity with which the ship has collided
	 */
	public void collidedWith(Entity other) {
		// if its an alien, notify the game that the player
		// is dead
		if (other instanceof AlienEntity) {
			game.notifyDeath();
		}
	}
}
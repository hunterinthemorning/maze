package hunter.maze;

import java.awt.Rectangle;
import java.util.ArrayList;

/**
 * An entity which represents one of our space invader aliens.
 */
public class AlienEntity extends Entity {
	/** The speed at which the alien moves horizontally */
	private double moveSpeed = 75;
	/** The game in which the entity exists */
	private Game game;
	/** The rectangle used for this entity during collisions  resolution */
	private Rectangle me = new Rectangle();
	/** The rectangle used for other entities during collision resolution */
	private Rectangle him = new Rectangle();
	double playerx,playery;
	
	/**
	 * Create a new alien entity
	 * 
	 * @param game The game in which this entity is being created
	 * @param ref The sprite which should be displayed for this alien
	 * @param x The initial x location of this alien
	 * @param y The initial y location of this alien
	 */
	public AlienEntity(Game game,String ref,int x,int y,int vector) {
		super(ref,x,y,vector);
		
		this.game = game;
		dx = -moveSpeed;
		dy = moveSpeed;
	}

	/**
	 * Request that this alien moved based on time elapsed
	 * 
	 * @param delta The time that has elapsed since last move
	 */
	public void move(long delta) {
		ArrayList thewalls = game.getWalls();
		for(int i=0; i<thewalls.size();i++) {
			Wall other = (Wall)thewalls.get(i);
			
			playerx = x + ((delta * dx) / 1000);
			playery = y + ((delta * dy) / 1000);
			
			me.setBounds((int)playerx,(int)playery,sprite.getWidth(),sprite.getHeight());
			him.setBounds((int)other.x,(int)other.y,other.sprite.getWidth(),other.sprite.getHeight());

			if(me.intersects(him)){
				//System.out.println("movement reversed, alien intersected wall");
				reverseDirection();
				return;
			}
			
		}
		// if we have reached the left hand side of the screen and
		// are moving left then request a logic update 
		if ((dx < 0) && (x < 10)) {
			game.updateLogic();
		}
		// and vice vesa, if we have reached the right hand side of 

		// the screen and are moving right, request a logic update

		if ((dx > 0) && (x > 750)) {
			game.updateLogic();
		}
		
		if ((dy < 0) && (y < 10)) {
			game.updateLogic();
		}
		
		if ((dy > 0) && (y > 570)) {
			game.updateLogic();
		}
		
		// proceed with normal move

		super.move(delta);
	}
	
	/* function for when alien hits a wall
	 * reverse dy if 
	 * reverse dx if
	 */
	private void reverseDirection() {
		dy = -dy;
		return;
	}
	
	/**
	 * Update the game logic related to aliens
	 */
	public void doLogic() {
		/* Original Logic
		// swap over horizontal movement and move down the

		// screen a bit

		dx = -dx;
		y += 10;
		
		// if we've reached the bottom of the screen then the player

		// dies

		if (y > 570) {
			game.notifyDeath();
		}
		*/
		
		//New logic with alien entities moving seperate paths
		// if we're moving left and have reached the left hand side
		// of the screen, reverse horizontal movement
		if ((dx < 0) && (x < 10)) {
			dx = -dx;
			return;
		}
		// if we're moving right and have reached the right hand side
		// of the screen, reverse horizontal movement
		if ((dx > 0) && (x > 760)) {
			dx = -dx;
			return;
		}
		// if we're moving down and have reached the bottom
		// of the screen, reverse vertical movement
		if ((dy < 0) && (y < 10)) {
			dy = -dy;
			return;
		}
		// if we're moving up and have reached the top
		// of the screen, reverse vertical movement
		if ((dy > 0) && (y > 570)) {
			dy = -dy;
			return;
		}
	}
	
	/**
	 * Notification that this alien has collided with another entity
	 * 
	 * @param other The other entity
	 */
	public void collidedWith(Entity other) {
		// collisions with aliens are handled elsewhere

	}
}
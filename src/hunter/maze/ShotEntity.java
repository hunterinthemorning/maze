package hunter.maze;

/**
 * An entity representing a shot fired by the player's ship
 * 
 * @author Kevin Glass
 */
public class ShotEntity extends Entity {
	/** The vertical speed at which the players shot moves */
	private double moveSpeed = -300;
	/** The game in which this entity exists */
	private Game game;
	/** True if this shot has been "used", i.e. its hit something */
	private boolean used = false;
	
	/**
	 * Create a new shot from the player
	 * 
	 * @param game The game in which the shot has been created
	 * @param sprite The sprite representing this shot
	 * @param x The initial x location of the shot
	 * @param y The initial y location of the shot
	 */
	public ShotEntity(Game game,String sprite,int x,int y, int vector) {
		super(sprite,x,y,vector);
		
		this.game = game;
		
		dy = moveSpeed;
		//added for shot entity to travel in the direction the player is moving
		//prior to, shot entity only traveled in the -y direction
		dx = moveSpeed;
	}

	/**
	 * Request that this shot moved based on time elapsed
	 * 
	 * @param delta The time that has elapsed since last move
	 */
	public void move(long delta) {
		// proceed with normal move

		super.move(delta);
		
		// if we shot off the screen, remove ourselves
		if (y < 10 || y > 570 || x < 10 || x > 760) {
			game.removeEntity(this);
		}
	}
	
	public double getShotSpeed() {
		return moveSpeed;
	}
	
	/**
	 * Notification that this shot has collided with another
	 * entity
	 * 
	 * @parma other The other entity with which we've collided
	 */
	public void collidedWith(Entity other) {
		// prevents double kills, if we've already hit something,

		// don't collide

		if (used) {
			return;
		}
		
		// if we've hit an alien, kill it!

		if (other instanceof AlienEntity) {
			// remove the affected entities

			game.removeEntity(this);
			game.removeEntity(other);
			
			// notify the game that the alien has been killed

			game.notifyAlienKilled();
			used = true;
		}
	}
}
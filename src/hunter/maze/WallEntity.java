package hunter.maze;

public class WallEntity extends Entity {
	private Game game;

	public WallEntity(Game game,String ref,int x,int y,int vector) {
		super(ref,x,y,vector);
		this.game = game;
	}
	
	//Implement of abstract method from Entity class
	public void collidedWith(Entity other) {
		//Tell running game cycle that an entity has collided with a wall
		if(other instanceof PlayerEntity) {
			game.playerOnWall(other);
		}
		// when a shot hits wall, destroy it
		if(other instanceof ShotEntity) {
			game.removeEntity(other);
		}
		return;
	}
}
package hunter.maze;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Game extends Canvas {
	/** The strategy that allows us to use accelerate page flipping */
	private BufferStrategy strategy;
	/** True if the game is currently "running", i.e. the game loop is looping */
	private boolean gameRunning = true;
	/** The list of all the entities that exist in our game */
	private ArrayList entities = new ArrayList();
	// the list of walls
	private ArrayList wallentities = new ArrayList();
	/** The list of entities that need to be removed from the game this loop */
	private ArrayList removeList = new ArrayList();
	/** The entity representing the player */
	private Entity player;
	/** The speed at which the player's ship should move (pixels/sec) */
	private double moveSpeed = 300;
	/** The time at which last fired a shot */
	private long lastFire = 0;
	/** The interval between our players shot (ms) */
	private long firingInterval = 250;
	/** The number of aliens left on the screen */
	private int alienCount;
	// the entity that represents a wall
	private Wall wall;
	
	/** The message to display which waiting for a key press */
	private String message = "";
	/** True if we're holding up game play until a key has been pressed */
	private boolean waitingForKeyPress = true;
	/** True if the left cursor key is currently pressed */
	private boolean leftPressed = false;
	/** True if the right cursor key is currently pressed */
	private boolean rightPressed = false;
	/** True if the up cursor key is currently pressed */
	private boolean upPressed = false;
	/** True if the down cursor key is currently pressed */
	private boolean downPressed = false;
	/** True if we are firing */
	private boolean firePressed = false;
	/** True if game logic needs to be applied this loop, normally as a result of a game event */
	private boolean logicRequiredThisLoop = false;
	

	private int count = 0;
	
	/**
	 * Construct our game and set it running.
	 */
	public Game() {
		// create a frame to contain our game
		JFrame container = new JFrame("Maze");
		
		// get hold the content of the frame and set up the resolution of the game
		JPanel panel = (JPanel) container.getContentPane();
		panel.setPreferredSize(new Dimension(768,640));
		panel.setLayout(null);
		
		// setup our canvas size and put it into the content of the frame
		setBounds(0,0,768,640);
		panel.add(this);
		
		// Tell AWT not to bother repainting our canvas since we're
		// going to do that our self in accelerated mode
		setIgnoreRepaint(true);
		
		// finally make the window visible 
		container.pack();
		container.setResizable(false);
		container.setVisible(true);
		
		// add a listener to respond to the user closing the window. If they
		// do we'd like to exit the game
		container.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		// add a key input system (defined below) to our canvas
		// so we can respond to key pressed
		addKeyListener(new KeyInputHandler());
		
		// request the focus so key events come to us
		requestFocus();

		// create the buffering strategy which will allow AWT
		// to manage our accelerated graphics
		createBufferStrategy(2);
		strategy = getBufferStrategy();
		
		// initialize the entities in our game so there's something
		// to see at startup
		initEntities();
	}
	
	/**
	 * Start a fresh game, this should clear out any old data and
	 * create a new set.
	 */
	private void startGame() {
		// clear out any existing entities and initialize a new set
		entities.clear();
		initEntities();
		
		// blank out any keyboard settings we might currently have
		leftPressed = false;
		rightPressed = false;
		
		upPressed = false;
		downPressed = false;
		
		firePressed = false;
	}
	
	/**
	 * Initialize the starting state of the entities (ship and aliens). Each
	 * entity will be added to the overall list of entities in the game.
	 */
	private void initEntities() {
		String[][] themap = {
				{"wall","wall","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","wall","wall"},
				{"wall","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","wall"},
				{"null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null"},
				{"null","null","null","wall","null","null","null","wall","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null"},
				{"null","null","null","wall","null","null","null","wall","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null"},
				{"null","null","null","wall","null","null","null","wall","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null"},
				{"null","null","null","wall","null","null","null","wall","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null"},
				{"null","null","null","wall","null","null","null","wall","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null"},
				{"null","null","null","wall","null","null","null","wall","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null"},
				{"null","null","null","wall","null","null","null","wall","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null"},
				{"null","null","null","wall","null","null","null","wall","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null"},
				{"null","null","null","wall","null","null","null","wall","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null"},
				{"null","null","null","wall","null","null","null","wall","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null"},
				{"null","null","null","wall","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null"},
				{"null","null","null","wall","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null"},
				{"null","null","null","wall","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null"},
				{"null","null","null","wall","wall","wall","wall","wall","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null"},
				{"null","null","null","null","null","null","null","null","null","null","null","null","wall","null","null","null","null","null","null","null","null","null","null","null"},
				{"wall","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","null","wall"},
				{"wall","wall","null","null","null","null","null","null","null","null","null","null","ship","null","null","null","null","null","null","null","null","null","wall","wall"}
			};
	
		for(int x = 0; x < 24; x++) {
			for(int y = 0; y < 20; y++) {
				//if(x == 12 && y == 19) {
				if(themap[y][x] == "ship") {
					player = new PlayerEntity(this,"sprites/ship.gif",32*x,32*y,0);
					entities.add(player);
				} else if(themap[y][x] == "wall") {
					wall = new Wall("sprites/wall.png",32*x,32*y);
					wallentities.add(wall);
				}
			}
		}
		
		
		// create the player ship and place it roughly in the center of the screen
		//player = new PlayerEntity(this,"sprites/ship.gif",370,570,0);
		//entities.add(player);
		
		// create a block of aliens (5 rows, by 12 aliens, spaced evenly)
		alienCount = 0;
		Entity alien = new AlienEntity(this,"sprites/alien.gif",100,30,0);
		entities.add(alien);
		alienCount++;
		/*for (int row=0;row<5;row++) {
			for (int x=0;x<1;x++) {
				Entity alien = new AlienEntity(this,"sprites/alien.gif",100+(x*50),(50)+row*30);
				entities.add(alien);
				alienCount++;
			}
		}*/
		
	}
	
	/**
	 * Notification from a game entity that the logic of the game
	 * should be run at the next opportunity (normally as a result of some
	 * game event)
	 */
	public void updateLogic() {
		logicRequiredThisLoop = true;
	}
	
	// get wall entities
	public ArrayList getWalls() {
		return wallentities;
	}
	
	/**
	 * Remove an entity from the game. The entity removed will
	 * no longer move or be drawn.
	 * 
	 * @param entity The entity that should be removed
	 */
	public void removeEntity(Entity entity) {
		removeList.add(entity);
	}
	
	/**
	 * Notification that the player has died. 
	 */
	public void notifyDeath() {
		message = "Oh no! They got you, try again?";
		waitingForKeyPress = true;
	}

	/**
	 * Notification that the player has won since all the aliens
	 * are dead.
	 */
	public void notifyWin() {
		message = "Well done! You Win!";
		waitingForKeyPress = true;
	}
	
	/**
	 * Notification that an alien has been killed
	 */
	public void notifyAlienKilled() {
		// reduce the alien count, if there are none left, the player has won!
		alienCount--;
		
		if (alienCount == 0) {
			notifyWin();
		}
		// if there are still some aliens left then they all need to get faster, so
		// speed up all the existing aliens
		for (int i=0;i<entities.size();i++) {
			Entity entity = (Entity) entities.get(i);
			
			if (entity instanceof AlienEntity) {
				// speed up by 2%
				entity.setHorizontalMovement(entity.getHorizontalMovement() * 1.02);
			}
		}
	}
	
	/**
	 * Attempt to fire a shot from the player. Its called "try"
	 * since we must first check that the player can fire at this 
	 * point, i.e. has he/she waited long enough between shots
	 */
	public void tryToFire() {
		// check that we have waiting long enough to fire
		if (System.currentTimeMillis() - lastFire < firingInterval) {
			return;
		}
		//variable to hold which sprite image to show
		String s = "sprites/shot.gif";;
		int sx = player.getX();
		int sy = player.getY();
		int dx = 0;
		int dy = 0;
		//change the direction of the shot entity based off of the direction the player is facing
		switch ((int)player.getVector()) {
			case 0:
				dx = 0;
				dy = -1;
				sx += 10;
				sy -= 30;
				//s = "sprites/shot.gif";
				break;
			case 45:
				dx = 1;
				dy = -1;
				sx += 30;
				sy -= 30;
				//s = "sprites/shot.gif";
				break;
			case 90:
				dx = 1;
				dy = 0;
				sx += 30;
				s = "sprites/shot90.gif";
				break;
			case 135:
				dx = 1;
				dy = 1;
				sx += 30;
				sy += 30;
				//s = "sprites/shot.gif";
				break;
			case 180:
				dx = 0;
				dy = 1;
				sy += 30;
				sx += 10;
				s = "sprites/shot180.gif";
				break;
			case 225:
				dx = -1;
				dy = 1;
				sx -= 30;
				sy += 30;
				//s = "sprites/shot.gif";
				break;
			case 270:
				dx = -1;
				dy = 0;
				sx -= 30;
				s = "sprites/shot270.gif";
				break;
			case 315:
				dx = -1;
				dy = -1;
				sx -= 30;
				sy -= 30;
				//s = "sprites/shot.gif";
				break;
		}
		// if we waited long enough, create the shot entity, and record the time.
		lastFire = System.currentTimeMillis();
		ShotEntity shot = new ShotEntity(this,s,sx,sy,(int)player.getVector(),dx,dy);
				
		entities.add(shot);
	}
	
	/**
	 * The main game loop. This loop is running during all game
	 * play as is responsible for the following activities:
	 * <p>
	 * - Working out the speed of the game loop to update moves
	 * - Moving the game entities
	 * - Drawing the screen contents (entities, text)
	 * - Updating game events
	 * - Checking Input
	 * <p>
	 */
	public void gameLoop() {
		long lastLoopTime = System.currentTimeMillis();
		
		// keep looping round until the game ends
		while (gameRunning) {

			// work out how long its been since the last update, this
			// will be used to calculate how far the entities should
			// move this loop
			long delta = System.currentTimeMillis() - lastLoopTime;
			lastLoopTime = System.currentTimeMillis();
			
			// Get hold of a graphics context for the accelerated 
			// surface and blank it out
			Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
			g.setColor(Color.black);
			g.fillRect(0,0,768,640);
			
			// draw all walls in the game
			for (int i=0;i<wallentities.size();i++) {
				Wall entity = (Wall) wallentities.get(i);
				entity.draw(g);
			}

			// cycle round drawing all the entities we have in the game
			for (int i=0;i<entities.size();i++) {
				Entity entity = (Entity) entities.get(i);
				entity.draw(g);
			}
			
			// cycle round asking each entity to move itself
			if (!waitingForKeyPress) {
				for (int i=0;i<entities.size();i++) {
					Entity entity = (Entity) entities.get(i);
					entity.move(delta);
				}
			}
			
			// brute force collisions, compare every entity against
			// every other entity. If any of them collide notify 
			// both entities that the collision has occurred
			for (int p=0;p<entities.size();p++) {
				Entity me = (Entity) entities.get(p);
				for (int s=p+1;s<entities.size();s++) {
					Entity him = (Entity) entities.get(s);
					
					if (me.collidesWith(him)) {
						me.collidedWith(him);
						him.collidedWith(me);
					}
				}
				
			}
			
			// remove any entity that has been marked for clear up
			entities.removeAll(removeList);
			removeList.clear();

			// if a game event has indicated that game logic should
			// be resolved, cycle round every entity requesting that
			// their personal logic should be considered.
			if (logicRequiredThisLoop) {
				for (int i=0;i<entities.size();i++) {
					Entity entity = (Entity) entities.get(i);
					entity.doLogic();
				}
				
				logicRequiredThisLoop = false;
			}
			
			// if we're waiting for an "any key" press then draw the 
			// current message 
			if (waitingForKeyPress) {
				g.setColor(Color.white);
				g.drawString(message,(800-g.getFontMetrics().stringWidth(message))/2,250);
				g.drawString("Press any key",(800-g.getFontMetrics().stringWidth("Press any key"))/2,300);
			}
			
			// finally, we've completed drawing so clear up the graphic
			// and flip the buffer over
			g.dispose();
			strategy.show();
			
			// resolve the movement of the ship. First assume the ship 
			// isn't moving. If either cursor key is pressed then
			// update the movement appropriately
			player.setHorizontalMovement(0);
			player.setVerticalMovement(0);
			
			//Check for any movement of player, Options are:
			//315 degrees
			if ((upPressed) && (leftPressed)) {
				player.setHorizontalMovement(-moveSpeed*0.75);
				player.setVerticalMovement(-moveSpeed*0.75);
				player.setVector(315);
			}
			//45 degrees
			else if ((upPressed) && (rightPressed)) {
				player.setHorizontalMovement(moveSpeed*0.75);
				player.setVerticalMovement(-moveSpeed*0.75);
				player.setVector(45);
			}
			//135 degrees
			else if ((downPressed) && (rightPressed)) {
				player.setHorizontalMovement(moveSpeed*0.75);
				player.setVerticalMovement(moveSpeed*0.75);
				player.setVector(135);
			}
			//225 degrees
			else if ((downPressed) && (leftPressed)) {
				player.setHorizontalMovement(-moveSpeed*0.75);
				player.setVerticalMovement(moveSpeed*0.75);
				player.setVector(225);
			}
			//270 degrees
			else if ((leftPressed) && (!rightPressed)) {
				player.setSpriteImage("sprites/ship270.gif");
				player.setHorizontalMovement(-moveSpeed);
				player.setVector(270);
			//90 degrees
			} else if ((rightPressed) && (!leftPressed)) {
				player.setSpriteImage("sprites/ship90.gif");
				player.setHorizontalMovement(moveSpeed);
				player.setVector(90);
			//0 degrees
			} else if ((upPressed) && (!downPressed)) {
				player.setSpriteImage("sprites/ship.gif");
				player.setVerticalMovement(-moveSpeed);
				player.setVector(0);
			//180 degrees
			} else if ((downPressed) && (!upPressed)) {
				player.setSpriteImage("sprites/ship180.gif");
				player.setVerticalMovement(moveSpeed);
				player.setVector(180);
			}
			
			
			// if we're pressing fire, attempt to fire
			if (firePressed) {
				tryToFire();
			}
			
			// finally pause for a bit. Note: this should run us at about
			// 100 fps but on windows this might vary each loop due to
			// a bad implementation of timer
			try { Thread.sleep(10); } catch (Exception e) {}
		}
	}
	
	/**
	 * A class to handle keyboard input from the user. The class
	 * handles both dynamic input during game play, i.e. left/right 
	 * and shoot, and more static type input (i.e. press any key to
	 * continue)
	 * 
	 * This has been implemented as an inner class more through 
	 * habit then anything else. Its perfectly normal to implement
	 * this as separate class if slight less convenient.
	 */
	private class KeyInputHandler extends KeyAdapter {
		/** The number of key presses we've had while waiting for an "any key" press */
		private int pressCount = 1;
		
		/**
		 * Notification from AWT that a key has been pressed. Note that
		 * a key being pressed is equal to being pushed down but *NOT*
		 * released. Thats where keyTyped() comes in.
		 *
		 * @param e The details of the key that was pressed 
		 */
		public void keyPressed(KeyEvent e) {
			// if we're waiting for an "any key" typed then we don't 
			// want to do anything with just a "press"
			if (waitingForKeyPress) {
				return;
			}
			
			
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				leftPressed = true;
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				rightPressed = true;
			}
			if (e.getKeyCode() == KeyEvent.VK_UP) {
				upPressed = true;
			}
			if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				downPressed = true;
			}
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				firePressed = true;
			}
		} 
		
		/**
		 * Notification from AWT that a key has been released.
		 *
		 * @param e The details of the key that was released 
		 */
		public void keyReleased(KeyEvent e) {
			// if we're waiting for an "any key" typed then we don't 
			// want to do anything with just a "released"
			if (waitingForKeyPress) {
				return;
			}
			
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				leftPressed = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				rightPressed = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_UP) {
				upPressed = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				downPressed = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				firePressed = false;
			}
		}

		/**
		 * Notification from AWT that a key has been typed. Note that
		 * typing a key means to both press and then release it.
		 *
		 * @param e The details of the key that was typed. 
		 */
		public void keyTyped(KeyEvent e) {
			// if we're waiting for a "any key" type then
			// check if we've received any recently. We may
			// have had a keyType() event from the user releasing
			// the shoot or move keys, hence the use of the "pressCount"
			// counter.
			if (waitingForKeyPress) {
				if (pressCount == 1) {
					// since we've now received our key typed
					// event we can mark it as such and start 
					// our new game
					waitingForKeyPress = false;
					startGame();
					pressCount = 0;
				} else {
					pressCount++;
				}
			}
			
			// if we hit escape, then quit the game
			if (e.getKeyChar() == 27) {
				System.exit(0);
			}
		}
	}
	
	/**
	 * The entry point into the game. We'll simply create an
	 * instance of class which will start the display and game
	 * loop.
	 * 
	 * @param argv The arguments that are passed into our game
	 */
	public static void main(String argv[]) {
		Game g =new Game();

		// Start the main game loop, note: this method will not
		// return until the game has finished running. Hence we are
		// using the actual main thread to run the game.
		g.gameLoop();
	}
}
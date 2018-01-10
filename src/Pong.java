import acm.program.*;
import acm.graphics.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;

public class Pong extends GraphicsProgram {
	
	private static final long serialVersionUID = 1L;
	
	// Constants for Customisation
	
	// Window Size (Currently set to fill my screen)
	public static final int WINDOW_WIDTH = 1280;
	public static final int WINDOW_HEIGHT = 739;
	
	// Padding is the distance between the edge of the screen & play area
	public static final int PADDING = 30;
	
	// How often the positions of the objects refresh (in ms)
	public static final int REFRESH = 1;
	
	// Colour Settings
	public static final Color BACKGROUND_COLOUR = Color.black;
	public static final Color BORDER_COLOUR = Color.white;
	
	public static final Color PADDLE1_COLOUR = new Color(103, 216, 168);
	public static final String PADDLE1_COLOUR_STRING = "Green"; // Use Title Case
	public static final Color PADDLE2_COLOUR = new Color(214, 111, 107);
	public static final String PADDLE2_COLOUR_STRING = "Red"; // Use Title Case
	
	public static final Color BALL_COLOUR = Color.white;
	public static final Color GAMEOVER_COLOUR = Color.white;
	
	// Font Settings
	public static final Font GAME_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 30);
	
	// Sizes of paddle & ball
	public static final int PADDLE_WIDTH = 15;
	public static final int PADDLE_HEIGHT = 120;
	public static final int DIAMETER = 20;
	
	// PADDLE_MOVE indicates how much the paddle moves when you press W/S/UP/DOWN
	// Adjust the 10 at the end to change how fast the paddle moves
	public static final double PADDLE_MOVE = (WINDOW_HEIGHT - PADDLE_HEIGHT - PADDING * 2) / 10;
	
	
	// Game Variables
	// Indicates the direction of the 2 paddles
	public static String direction1 = "stop";
	public static String direction2 = "stop";
	
	// Set to false to restart the game
	public static boolean play = true;
	
	
	// Ball Velocity measured in pt per REFRESH ms
	public void setBallVelocity (GOval ball, double velX, double velY) {
		
		ball.setLocation(ball.getX() + velX,ball.getY() + velY);
		
	}
	
	// Keyboard listener
	// Current settings:
	// Player 1 (left): W = up, S = down
	// Player 2 (right): UP = up, DOWN = down
	// SPACE = New game
	public void keyPressed(KeyEvent key) {
		
		switch (key.getKeyCode()) {
		
			case KeyEvent.VK_W: 
				direction1 = "up";
				break;
				
			case KeyEvent.VK_S:
				direction1 = "down";
				break;
			
			case KeyEvent.VK_UP:
				direction2 = "up";
				break;
				
			case KeyEvent.VK_DOWN:
				direction2 = "down";
				break;
				
			case KeyEvent.VK_SPACE:
				play = false;
				break;
				
			default:
				break;
			
		}
		
	}
	
	public void init() {
		
		// Set size of window
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		
	}
	
	public void run() {
		
		// Key Listeners for keyPressed(KeyEvent key)
		addKeyListeners();
		
		
		// Background & Border Set Up
		GRect background = new GRect(WINDOW_WIDTH, WINDOW_HEIGHT);
		background.setFillColor(BACKGROUND_COLOUR);
		background.setFilled(true);
		add(background);
		
		GRect border = new GRect(PADDING, PADDING, WINDOW_WIDTH - PADDING*2, WINDOW_HEIGHT - PADDING*2);
		border.setColor(BORDER_COLOUR);
		add(border);
		
		
		// Paddle Set Up (Initially placed in the middle)
		GRect paddle1 = new GRect(PADDING, WINDOW_HEIGHT/2 - PADDLE_HEIGHT/2, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle1.setColor(PADDLE1_COLOUR);
		paddle1.setFilled(true);
		add(paddle1);
		
		GRect paddle2 = new GRect(WINDOW_WIDTH - PADDING - PADDLE_WIDTH, WINDOW_HEIGHT/2 - PADDLE_HEIGHT/2, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle2.setColor(PADDLE2_COLOUR);
		paddle2.setFilled(true);
		add(paddle2);
		
		
		// Ball Set Up (Middle of screen)
		GOval ball = new GOval(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2, DIAMETER, DIAMETER);
		ball.setColor(BALL_COLOUR);
		ball.setFilled(true);
		
		// Game Over Text
		GLabel gameOver = new GLabel("Game Over");
		gameOver.setColor(GAMEOVER_COLOUR);
		gameOver.setFont(GAME_FONT);
		gameOver.setLocation(WINDOW_WIDTH/2 - gameOver.getWidth()/2, WINDOW_HEIGHT/2 - PADDING);
		
		GLabel winner1 = new GLabel(PADDLE1_COLOUR_STRING + " Wins!");		
		winner1.setColor(PADDLE1_COLOUR);
		winner1.setFont(GAME_FONT);
		winner1.setLocation(WINDOW_WIDTH/2 - winner1.getWidth()/2, WINDOW_HEIGHT/2 + PADDING);
		
		GLabel winner2 = new GLabel(PADDLE2_COLOUR_STRING + " Wins!");
		winner2.setColor(PADDLE2_COLOUR);
		winner2.setFont(GAME_FONT);
		winner2.setLocation(WINDOW_WIDTH/2 - winner2.getWidth()/2, WINDOW_HEIGHT/2 + PADDING + 30);
		
		// The games will loop in here until the app is closed
		while (true) {
			
			// New Ball
			add(ball);
			
			// Initial Velocity of Ball (Total 1 pt per REFRESH ms)
			double angle = Math.random()*360;
			double velX = Math.sin(angle)*0.5;
			double velY = Math.cos(angle)*0.5;
			
			// Within here is one game
			while (true) {
				
				// Only occurs when you press space
				if (!play) break;
				
				
				// Ball Movement
				// Set up ball velocity
				setBallVelocity(ball, velX, velY);
				
				// If it collides with top or bottom border, its velocity will be reflected
				if (ball.getY() < PADDING || ball.getY() > WINDOW_HEIGHT - DIAMETER - PADDING)
					velY = -velY;
				
				// If it reaches left end
				if (ball.getX() < PADDING + PADDLE_WIDTH) {
					
					// Collides with paddle1
					if (ball.getY() + DIAMETER/2 > paddle1.getY() && ball.getY() + DIAMETER/2 < paddle1.getY() + PADDLE_HEIGHT) {
						
						// Finds maximum distance from edge to centre of paddle
						double distBallToPaddle = Math.max(ball.getY() + DIAMETER/2 - paddle1.getY(), paddle1.getY() + PADDLE_HEIGHT - ball.getY() - DIAMETER/2);
						
						// Horizontal speed increases as game progresses
						velX = -velX * 1.2;
						// Vertical speed increases, especially if paddle is closer to the edge
						velY = velY * (1 + distBallToPaddle/120);
						
					// Missed by paddle1 (paddle2 wins)
					} else {
						
						// Game Over Text
						add(gameOver);
						add(winner2);
						
						// Ball made stationary off-screen to prevent the winner text from changing before SPACE pressed
						ball.setLocation(WINDOW_HEIGHT + PADDING, WINDOW_WIDTH + PADDING);
						velX = 0;
						velY = 0;
						
					}
					
				// If it reaches right end
				} else if (ball.getX() + DIAMETER > WINDOW_WIDTH - PADDING - PADDLE_WIDTH) {
					
					// Collides with paddle2
					if (ball.getY() + DIAMETER/2 > paddle2.getY() && ball.getY() + DIAMETER/2 < paddle2.getY() + PADDLE_HEIGHT) {
						
						// Finds maximum distance from edge to centre of paddle
						double distBallToPaddle = Math.max(ball.getY() + DIAMETER/2 - paddle2.getY(), paddle2.getY() + PADDLE_HEIGHT - ball.getY() - DIAMETER/2);
						
						// Horizontal speed increases as game progresses
						velX = -velX * 1.2;
						// Vertical speed increases, especially if paddle is closer to the edge
						velY = velY * (1 + distBallToPaddle/120);
						
					// Missed by paddle2 (paddle1 wins)
					} else {
						
						// Game Over Text
						add(gameOver);
						add(winner1);
						
						// Ball made stationary off-screen to prevent the winner text from changing before SPACE pressed
						ball.setLocation(WINDOW_HEIGHT + PADDING, WINDOW_WIDTH + PADDING);
						velX = 0;
						velY = 0;
						
					}
					
				}
				
				
				// Paddle Movement (additional PADDLE_MOVE/2 to make up for minor inaccuracies in values)
				if (direction1.equals("up") && paddle1.getY() > PADDING + PADDLE_MOVE/2) {
					
					paddle1.setLocation(paddle1.getX(), paddle1.getY() - PADDLE_MOVE);
					direction1 = "stop";
					
				} else if (direction1.equals("down") && paddle1.getY() < WINDOW_HEIGHT - PADDLE_HEIGHT - PADDING - PADDLE_MOVE/2) {
					
					paddle1.setLocation(paddle1.getX(), paddle1.getY() + PADDLE_MOVE);
					direction1 = "stop";
					
				}
				
				if (direction2.equals("up") && paddle2.getY() >= PADDING + PADDLE_MOVE/2) {
					
					paddle2.setLocation(paddle2.getX(), paddle2.getY() - PADDLE_MOVE);
					direction2 = "stop";
					
				} else if (direction2.equals("down") && paddle2.getY() < WINDOW_HEIGHT - PADDLE_HEIGHT - PADDING - PADDLE_MOVE/2) {
					
					paddle2.setLocation(paddle2.getX(), paddle2.getY() + PADDLE_MOVE);
					direction2 = "stop";
					
				}
				
				// How often it refreshes
				pause(REFRESH);
				
			}
			
			// Reset Game Setup
			remove(gameOver);
			remove(winner1);
			remove(winner2);
			remove(ball);
			play = true;
			direction1 = "stop";
			direction2 = "stop";
			
			paddle1.setLocation(PADDING, WINDOW_HEIGHT/2 - PADDLE_HEIGHT/2);
			paddle2.setLocation(WINDOW_WIDTH - PADDING - PADDLE_WIDTH, WINDOW_HEIGHT/2 - PADDLE_HEIGHT/2);
			ball.setLocation(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2);
			
		}
		
	}

}
package main;
import java.awt.*;
import javax.swing.*;

import entity.Player;
import tile.TileHandler;

public class GamePanel extends JPanel implements Runnable{
	// S C R E E N _ S E T T I N G S
	final int originalTileSize = 16; // Tiles are 32x32.
	/* Since 16x16 tiles would look very small on the modern screens:
	 * 1280 x 960
	 * 1920 x 1080
	 * etc..
	 * We will have to up-scale it. But how?
	 * I've read that scaling is common when developing retro-style 2D games
	 */
	final int scale = 3;
	public final int tileSize = originalTileSize * scale; // 48x48 tile
	
	public final int maxScreenCol = 16;
	public final int maxScreenRow = 12;
	
	public final int screenWidth = tileSize * maxScreenCol;// 768 pixels / X
	public final int screenHeight = tileSize * maxScreenRow;// 576 pixels / X
	
	// W O R L D _ S E T T I N G S
	public final int maxWorldCol = 50;
	public final int maxWorldRow = 50;
	public final int worldWidth = tileSize * maxWorldCol;
	public final int worldHeight = tileSize * maxWorldRow;
	
	// F P S 
	int FPS = 60;
	/*gamePanel is implementing interface Runnable
	 * Which is used to create a thread. Starting the thread causes the object's (in this case: gamePanel)
	 * To be called in that separately executing thread
	 */

	//GamePanel constructor
	public GamePanel() {
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
		//I'm not sure if this is necessary but..}}
		this.setBackground(Color.black);
		//This is what Eng. Yasmeen pointed to, but it's not what we are aiming for.
		this.setDoubleBuffered(true);
		//This improves rendering performance
		this.addKeyListener(keyH);
		this.setFocusable(true);
		//So this game can
		
}
	TileHandler tileH = new TileHandler(this);
	KeyHandler keyH = new KeyHandler();
	public Player player = new Player(this,keyH);
	Thread gameThread;
	
	public void startGameThread() {
		gameThread = new Thread(this);
		//Thread constructor. This is how you instantiate a Thread.
		gameThread.start();
	}
	

	@Override
	//The core/heart of the game
	/*Update & redraw
	 * Update the information such as the character's position
	 * Draw: draw the screen with the updated information
	 * FPS times per second
	 */
	public void run() {
		
		double drawInterval = 1000000000/FPS;
			//0.016BAR seconds
		double nextDrawTime = System.nanoTime() + drawInterval;
			//This is the re-draw interval, the way to setup the existence of time
		
		while (gameThread != null) {
			//We use the JVM's high-res time source, in nanoseconds
			//This means that as long as this gameThread exists, it will keep on executing what's inside the run method's curly brackets
			update();
			repaint();
			
			
			try {
				
				double remainingTime = nextDrawTime - System.nanoTime();
					//We need to let the thread sleep for the remainingTime
				
				remainingTime = remainingTime/1000000;
				
				if(remainingTime < 0) {
					remainingTime = 0;
				}
				//I doubt this will happen in such a small-scale game
				
				Thread.sleep((long)remainingTime); 
				
				nextDrawTime += drawInterval;
				
			} catch (InterruptedException e) {
				e.printStackTrace();
				}	
		}
	}
	public void update() {
		player.update();
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		
		tileH.draw(g2);
		player.draw(g2);
		
		g2.dispose();
	}
}

//Research Delta/Accumulator method for the game-loop
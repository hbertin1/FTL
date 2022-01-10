package main;
import display.Button;
import display.StdDraw;
import display.Vector2;

/**
 * This class starts the game by creating the canvas
 * in which the game will be drawn in and the world as
 * well as the main loop of the game.
 */
public class Start {
	public static void main(String[] args) {

		// Creates the canvas of the game
		StdDraw.setCanvasSize(1000, 1000);

		// Enables double buffering to allow animation
		StdDraw.enableDoubleBuffering();

		// Creates the world
		World w = new World();

		// Game infinite loop
		while(w.player.getCurrentHull() > 0) {
			while(w.player.getCurrentHull() > 0) {

				// Clears the canvas of the previous frame
				StdDraw.clear();

				// Processes the key pressed during the last frame
				w.processKey();

				// Makes a step of the world
				w.step();

				// Draws the world to the canvas
				w.draw();

				//On affiche le niveau du joueur
				StdDraw.text(0.92, 0.03, "Level: " + w.getLevel());
				StdDraw.text(0.82, 0.96, "Press SPACE to see commands");
				
				// Shows the canvas to screen
				StdDraw.show();

				// Waits for 20 milliseconds before drawing next frame.
				StdDraw.pause(20);

				if(w.opponent.getCurrentHull() <= 0) { //Si le joueur détruit le vaisseau ennemie
					String reward = w.randReward(); //On genere une recompense aleatoire
					if(w.AllModulesLevelMax()) {// Si tous ces modules sont deja au level maximum
						StdDraw.clear();
						StdDraw.text(0.5, 0.8, "You won");
						StdDraw.text(0.5, 0.5, "Reward : " + reward);
						StdDraw.text(0.5, 0.3, "All your modules are level max");//On ne lui propose pas d'ameliorer ses modules
						StdDraw.show();
						long time = System.currentTimeMillis();
						while(System.currentTimeMillis() < time + 5000);// et la partie est lancee 5 secondes apres
					}
					else{//Sinon
						w.setReadyToPlay(false);
						while (!w.isReadyToPlay()) {
							StdDraw.clear();
							w.drawEnd();//Il peut choisir un module a ameliorer
							//La partie se lance quand le joueur a choisit quel module il souhaitait ameliorer
							StdDraw.text(0.5, 0.8, "You won");
							StdDraw.text(0.5,0.5, "Reward : " + reward);
							StdDraw.show();
							StdDraw.pause(20);
						}
					}
					w.nextLevel();//On ameliore le vaisseau ennemie

				}
			}
		}
		//Si le joueur a perdu contre l'ia, la partie s'arrete et on affiche "Game Over"
		StdDraw.clear();
		StdDraw.text(0.5, 0.5, "Game Over");;
		StdDraw.show();

	}
	

}
